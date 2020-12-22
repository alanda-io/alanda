import { Injectable } from '@angular/core';
import { TreeNode } from 'primeng/api/treenode';
import { AlandaProject } from '../../api/models/project';
import { AlandaProcess } from '../../api/models/process';
import { AlandaTask } from '../../api/models/task';
import { AlandaProcessesAndTasks } from '../../api/models/processesAndTasks';
import { ProjectState } from '../../enums/projectState.enum';
import { uuid } from '../../utils/helper-functions';
import { AccessLevels, Authorizations } from '../../permissions';
import { AlandaUser } from '../../api/models/user';

const MAX_CHARACTERS_LENGTH = 25;

export interface MappedAllowedProcesses {
  keyWithoutPhase: string;
  processKey: string;
  label: string;
  phase: string;
}

export interface MappedProcessesAndTasks {
  data: AlandaProcessesAndTasks;
  allowed: MappedAllowedProcesses[];
  active: AlandaProcess[];
}

export enum TreeNodeDataType {
  PROJECT,
  TASK,
  PROCESS,
  ACTIVITY,
  STARTPROCESS,
}

export type PapActions =
  | 'RELATE-OPTIONS'
  | 'CANCEL-PROJECT'
  | 'CANCEL-PROCESS'
  | 'START-SUBPROCESS'
  | 'REMOVE-SUBPROCESS'
  | 'CONFIGURE-PROCESS';

export interface TreeNodeData {
  id?: string;
  guid?: number;
  label?: string;
  refObject?: string;
  assignee?: string;
  start?: Date;
  end?: Date;
  info?: string;
  routerLink?: string;
  type?: TreeNodeDataType;
  project?: AlandaProject;
  task?: AlandaTask;
  process?: AlandaProcess;
  dropdown?: boolean;
  status?: string;
  relatedProject?: AlandaProject;
  readOnly?: boolean;
  papActions?: PapActions[];
}

@Injectable()
export class AlandaProjectAndProcessesService {
  constructor() {}

  mapProjectsToTreeNode(project: AlandaProject, user: AlandaUser): TreeNode[] {
    const data: TreeNode[] = [];
    if (project.parents) {
      data.push({
        data: { label: `Parent Projects (${project.parents.length})` },
        children: project.parents
          .map((parent) => Object.assign({}, parent, { processes: null }))
          .map((parent) => this.mapProjectToTreeNode(parent, user)),
      });
    }
    data.push(this.mapProjectToTreeNode(project, user));
    if (project.children) {
      data.push({
        data: { label: `Child Projects (${project.children.length})` },
        children: project.children.map((child) =>
          this.mapProjectToTreeNode(child, user),
        ),
      });
    }
    return data;
  }

  mapProjectToTreeNode(project: AlandaProject, user: AlandaUser): TreeNode {
    const projectTitle = project.title?.length ? ` / ${project.title}` : '';
    let label = `${project.projectId} (${
      project.subtype ? project.subtype : project.pmcProjectType.name
    }${projectTitle})`;
    if (project.refObjectIdName) {
      label = label + ` for (${project.refObjectIdName})`;
    }
    if (project.displayMetaInfo) {
      label = label + ` ${project.displayMetaInfo}`;
    }

    const id = uuid();
    const { processDefsToHideAfterCompletion, processDefsToHide } = project
      .pmcProjectType?.configuration
      ? JSON.parse(project.pmcProjectType?.configuration)
      : { processDefsToHideAfterCompletion: [], processDefsToHide: [] };
    const data: TreeNodeData = {
      id,
      label,
      refObject: project.refObjectIdName,
      start: new Date(project.createDate),
      info: this.getLimitedText(project.details || ''),
      routerLink: `/projectdetails/${project.projectId}`,
      type: TreeNodeDataType.PROJECT,
      project,
      status: project.status,
      readOnly:
        project.status === ProjectState.CANCELED ||
        project.status === ProjectState.COMPLETED ||
        project.status === ProjectState.SUSPENDED,
      papActions: ['RELATE-OPTIONS', 'CANCEL-PROJECT'],
    };

    const children = [];
    project.processes?.map((process) => {
      if (process.relation === 'MAIN') {
        process.tasks?.map((task) =>
          children.push(this.mapTaskToTreeNode(task)),
        );
      } else {
        if (
          this.showProcessInTheTree(
            process,
            processDefsToHideAfterCompletion,
            processDefsToHide,
          )
        ) {
          children.push(this.mapProcessToTreeNode(process, project, user));
        }
      }
    });

    return {
      key: id,
      data,
      children,
    };
  }

  mapProcessToTreeNode(
    process: AlandaProcess,
    relatedProject: AlandaProject,
    user: AlandaUser,
  ): TreeNode {
    const papActions: PapActions[] = [];
    if (this.showRemoveSubProcessButton(process, relatedProject, user)) {
      papActions.push('REMOVE-SUBPROCESS');
    }
    if (this.showCancelSubProcessButton(process, relatedProject, user)) {
      papActions.push('CANCEL-PROCESS');
    }
    if (this.showSubprocessConfigButton(process, relatedProject)) {
      papActions.push('CONFIGURE-PROCESS');
    }
    if (this.showStartSubProcessButton(process, relatedProject, user)) {
      papActions.push('START-SUBPROCESS');
    }

    const id = uuid();
    const data: TreeNodeData = {
      id,
      guid: process.guid,
      label: process.label,
      refObject:
        process.businessObject !== relatedProject.refObjectIdName
          ? process.businessObject
          : '',
      start: process.startTime,
      end: process.endTime,
      info: this.getLimitedText(process.workDetails || ''),
      routerLink: `/finder/pio/${process.processInstanceId}`,
      type: TreeNodeDataType.PROCESS,
      process,
      dropdown: process.status === ProjectState.NEW ? true : false,
      status: process.status,
      relatedProject,
      readOnly:
        process.status === ProjectState.CANCELED ||
        process.status === ProjectState.COMPLETED ||
        process.status === ProjectState.SUSPENDED ||
        process.status === ProjectState.DELETED ||
        relatedProject.status === ProjectState.CANCELED ||
        relatedProject.status === ProjectState.COMPLETED ||
        relatedProject.status === ProjectState.SUSPENDED,
      papActions,
    };
    return {
      key: id,
      data,
      children: process.tasks
        ? process.tasks.map((task) => this.mapTaskToTreeNode(task))
        : null,
    };
  }

  private showStartSubProcessButton(
    process: AlandaProcess,
    relatedProject: AlandaProject,
    user: AlandaUser,
  ): boolean {
    const isStateNew = process.status === ProjectState.NEW;
    const noPhaseOrActivePhase =
      !process.phase ||
      process.phase === 'default' ||
      relatedProject?.phases?.find((p) => p.idName === process.phase)
        ?.active === true;
    return (
      isStateNew &&
      noPhaseOrActivePhase &&
      this.hasProcessPermission(
        process.phase,
        process.processKeyWithoutPhase,
        relatedProject.authBase,
        user,
        AccessLevels.start,
      )
    );
  }

  private showCancelSubProcessButton(
    process: AlandaProcess,
    relatedProject: AlandaProject,
    user: AlandaUser,
  ): boolean {
    const isStateActive = process.status === ProjectState.ACTIVE;
    const noPhaseOrActivePhase =
      !process.phase ||
      process.phase === 'default' ||
      relatedProject?.phases?.find((p) => p.idName === process.phase)
        ?.active === true;
    return (
      isStateActive &&
      noPhaseOrActivePhase &&
      this.hasProcessPermission(
        process.phase,
        process.processKeyWithoutPhase,
        relatedProject.authBase,
        user,
        AccessLevels.start,
      )
    );
  }

  private showRemoveSubProcessButton(
    process: AlandaProcess,
    relatedProject: AlandaProject,
    user: AlandaUser,
  ): boolean {
    const isStateNew = process.status === ProjectState.NEW;
    return (
      isStateNew &&
      this.hasProcessPermission(
        process.phase,
        process.processKeyWithoutPhase,
        relatedProject.authBase,
        user,
        AccessLevels.create,
      )
    );
  }

  private hasProcessPermission(
    phase: string,
    processKey: string,
    projectAuthBase: string,
    user: AlandaUser,
    accessLevel: string,
  ): boolean {
    // authBase contains all active phases of the project, reduce it to process related phase
    if (phase) {
      const re = /(#\{permissions\}:[A-Za-z0-9_,-]*:)([A-Za-z0-9_,-]*)(:.*)/;
      const result = projectAuthBase.match(re);
      if (result.length >= 3 && result[2].indexOf(phase) > -1) {
        projectAuthBase = projectAuthBase.replace(re, '$1' + phase + '$3');
      }
    }
    const perm = `proc:${projectAuthBase}:${processKey}`;
    return Authorizations.hasPermission(user, perm, accessLevel);
  }

  mapTaskToTreeNode(task: AlandaTask): TreeNode {
    const id = uuid();
    const data: TreeNodeData = {
      id,
      label: task.task_name,
      refObject: task.process_definition_key,
      assignee: task.assignee,
      start: new Date(task.created),
      info: this.getTaskInfo(task),
      routerLink:
        task.actinst_type === 'task'
          ? `/forms/${task.formKey}/${task.task_id}`
          : undefined,
      type:
        task.actinst_type === 'task'
          ? TreeNodeDataType.TASK
          : TreeNodeDataType.ACTIVITY,
      task,
      status: task.state,
    };
    return {
      key: id,
      data,
    };
  }

  getStartProcessDropdownAsTreeNode(relatedProject: AlandaProject): TreeNode {
    const id = uuid();
    return {
      data: {
        id,
        type: TreeNodeDataType.STARTPROCESS,
        dropdown: true,
        relatedProject,
        readOnly:
          relatedProject.status === ProjectState.CANCELED ||
          relatedProject.status === ProjectState.COMPLETED ||
          relatedProject.status === ProjectState.SUSPENDED,
      },
      key: id,
    };
  }

  getTaskInfo(task: AlandaTask): string {
    const candidateGroups = task?.candidateGroups?.reduce((acc, group) => {
      if (group !== 'Administrator') {
        acc += `${group}, `;
      }
      return acc;
    }, '');
    return task.actinst_type === 'task'
      ? `<b>Assignee:</b> ${
          task.assignee ? task.assignee : 'no assignee'
        }<br /> <b>Candidate Group:</b> ${
          task.candidateGroups?.length
            ? candidateGroups.substr(0, candidateGroups.length - 2)
            : '-'
        }`
      : '';
  }

  getLimitedText(property: string): string {
    if (property?.length > MAX_CHARACTERS_LENGTH) {
      property = `${property.substring(0, MAX_CHARACTERS_LENGTH)}...`;
    }
    return property;
  }

  getProcessesAndTasksForProject(
    processesAndTasks: AlandaProcessesAndTasks,
    project: AlandaProject,
    user: AlandaUser,
  ): any {
    const result: MappedProcessesAndTasks = {
      data: processesAndTasks,
      allowed: [],
      active: [],
    };
    const phaseNames = processesAndTasks.phaseNames;
    const processNameToPhaseMap = {};
    let allowedProcesses = processesAndTasks.allowed['default'];
    allowedProcesses = allowedProcesses.sort((itemA, itemB) => {
      return itemA.processName.localeCompare(itemB.processName);
    });
    for (const processDef of allowedProcesses) {
      processNameToPhaseMap[processDef.processDefinitionKey] = 'default';
      if (
        this.hasProcessPermission(
          null,
          processDef.processDefinitionKey,
          project.authBase,
          user,
          AccessLevels.create,
        )
      ) {
        result.allowed.push({
          keyWithoutPhase: processDef.processDefinitionKey,
          processKey: processDef.processDefinitionKey,
          label: processDef.processName,
          phase: processNameToPhaseMap[processDef.processDefinitionKey],
        });
      }
    }
    for (let [phaseName, processes] of Object.entries(
      processesAndTasks.allowed,
    )) {
      if (phaseName !== 'default') {
        processes = processes.sort((itemA, itemB) => {
          return itemA.processName.localeCompare(itemB.processName);
        });
        for (const processDef of processes) {
          if (
            this.hasProcessPermission(
              phaseName,
              processDef.processDefinitionKey,
              project.authBase,
              user,
              AccessLevels.create,
            )
          ) {
            processNameToPhaseMap[processDef.processDefinitionKey] = phaseName;
            result.allowed.push({
              keyWithoutPhase: processDef.processDefinitionKey,
              processKey: `${phaseName}:${processDef.processDefinitionKey}`,
              label:
                processDef.processName + ' (' + phaseNames[phaseName] + ')',
              phase: phaseName,
            });
          }
        }
      }
    }

    let active = processesAndTasks.active;
    active = active.map((item) => {
      const processKeyWithoutPhase = item?.processKey
        ? JSON.parse(JSON.stringify(item.processKey))
        : null;
      item['processKeyWithoutPhase'] = processKeyWithoutPhase;
      if (item.phase == null && processNameToPhaseMap[item.processKey]) {
        item.phase = processNameToPhaseMap[item.processKey];
      }
      if (item.phase && item.phase !== 'default') {
        item.processKey = `${item.phase}:${item.processKey}`;
      }
      return item;
    });
    result.active = active;

    return result;
  }

  private showSubprocessConfigButton(
    process: AlandaProcess,
    relatedProject: AlandaProject,
  ): boolean {
    const projectTypeConfig = relatedProject?.pmcProjectType?.configuration
      ? JSON.parse(relatedProject.pmcProjectType.configuration)
      : {};
    const subprocessPropertiesTemplate = {};
    const subprocessProperties = {};
    const subprocessPropertiesConfig = {};
    if (projectTypeConfig) {
      if (projectTypeConfig.subprocessProperties) {
        for (const propDef of projectTypeConfig.subprocessProperties) {
          subprocessPropertiesTemplate[propDef.processDefinitionKey] =
            propDef.propertiesTemplate;
          subprocessProperties[propDef.processDefinitionKey] =
            propDef.properties;
          subprocessPropertiesConfig[propDef.processDefinitionKey] = propDef;
        }
      }
    }

    const processKeyWithoutPhase = process['processKeyWithoutPhase'];

    if (
      (!subprocessProperties[processKeyWithoutPhase] &&
        !subprocessPropertiesTemplate[processKeyWithoutPhase]) ||
      !(
        process.status === ProjectState.ACTIVE ||
        process.status === ProjectState.NEW
      )
    ) {
      return false;
    }
    if (
      subprocessPropertiesConfig[processKeyWithoutPhase] &&
      subprocessPropertiesConfig[processKeyWithoutPhase].showButton
    ) {
      return subprocessPropertiesConfig[processKeyWithoutPhase].showButton;
    }
    return (
      (subprocessProperties[processKeyWithoutPhase] &&
        subprocessProperties[processKeyWithoutPhase].length > 0) ||
      subprocessPropertiesTemplate[processKeyWithoutPhase]
    );
  }

  showProcessInTheTree(
    process: AlandaProcess,
    processDefsToHideAfterCompletion: string[],
    processDefsToHide: string[],
  ): boolean {
    if (processDefsToHide?.includes(process?.processKey)) {
      return false;
    }

    if (
      process.status === ProjectState.COMPLETED &&
      processDefsToHideAfterCompletion?.includes(process?.processKey)
    ) {
      return false;
    }

    return true;
  }
}
