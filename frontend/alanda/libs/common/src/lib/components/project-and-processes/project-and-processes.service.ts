import { Injectable } from '@angular/core';
import { TreeNode } from 'primeng/api/treenode';
import { AlandaProject } from '../../api/models/project';
import { AlandaProcess } from '../../api/models/process';
import { AlandaTask } from '../../api/models/task';
import { AlandaProcessesAndTasks } from '../../api/models/processesAndTasks';
import { ProjectState } from '../../enums/projectState.enum';
import { AlandaProjectApiService } from '../../api/projectApi.service';
import { uuid } from '../../utils/helper-functions';

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
  label?: string;
  refObject?: string;
  assignee?: string;
  start?: Date;
  end?: Date;
  comment?: string;
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
  constructor(private readonly projectService: AlandaProjectApiService) {}

  mapProjectsToTreeNode(project: AlandaProject): TreeNode[] {
    const data: TreeNode[] = [];
    if (project.parents) {
      data.push({
        data: { label: `Parent Projects (${project.parents.length})` },
        children: project.parents
          .map((parent) => Object.assign({}, parent, { processes: null }))
          .map((parent) => this.mapProjectToTreeNode(parent)),
      });
    }
    data.push(this.mapProjectToTreeNode(project));
    if (project.children) {
      data.push({
        data: { label: `Child Projects (${project.children.length})` },
        children: project.children.map((child) =>
          this.mapProjectToTreeNode(child),
        ),
      });
    }
    return data;
  }

  mapProjectToTreeNode(project: AlandaProject): TreeNode {
    let label = `${project.projectId} (${
      project.subType ? project.subType : project.pmcProjectType.name
    } / ${project.title})`;
    if (project.refObjectIdName) {
      label = label + `for (${project.refObjectIdName})`;
    }
    if (project.displayMetaInfo) {
      label = label + ` ${project.displayMetaInfo}`;
    }
    const id = uuid();
    const data: TreeNodeData = {
      id,
      label,
      refObject: project.refObjectIdName,
      assignee: project.ownerName,
      start: new Date(project.createDate),
      comment: project.comment,
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
    return {
      key: id,
      data,
      children: project.processes
        ? project.processes.map((process) =>
            this.mapProcessToTreeNode(process, project),
          )
        : null,
    };
  }

  mapProcessToTreeNode(
    process: AlandaProcess,
    relatedProject: AlandaProject,
  ): TreeNode {
    const papActions: PapActions[] =
      process.status === ProjectState.NEW
        ? ['REMOVE-SUBPROCESS', 'START-SUBPROCESS']
        : ['CANCEL-PROCESS'];
    if (this.showSubprocessConfigButton(process, relatedProject)) {
      papActions.push('CONFIGURE-PROCESS');
    }
    const id = uuid();
    const data: TreeNodeData = {
      id,
      label: process.label,
      refObject: process.processKey,
      start: process.startTime,
      end: process.endTime,
      comment: process.resultComment,
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

  mapTaskToTreeNode(task: AlandaTask): TreeNode {
    const id = uuid();
    const data: TreeNodeData = {
      id,
      label: task.task_name,
      refObject: task.process_definition_key,
      assignee: task.assignee,
      start: new Date(task.created),
      comment: task.comment,
      routerLink: `/forms/${task.formKey}/${task.task_id}`,
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

  getProcessesAndTasksForProject(
    processesAndTasks: AlandaProcessesAndTasks,
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
      result.allowed.push({
        keyWithoutPhase: processDef.processDefinitionKey,
        processKey: processDef.processDefinitionKey,
        label: processDef.processName,
        phase: processNameToPhaseMap[processDef.processDefinitionKey],
      });
    }
    for (let [phaseName, processes] of Object.entries(
      processesAndTasks.allowed,
    )) {
      if (phaseName !== 'default') {
        processes = processes.sort((itemA, itemB) => {
          return itemA.processName.localeCompare(itemB.processName);
        });
        for (const processDef of processes) {
          processNameToPhaseMap[processDef.processDefinitionKey] = phaseName;
          result.allowed.push({
            keyWithoutPhase: processDef.processDefinitionKey,
            processKey: `${phaseName}:${processDef.processDefinitionKey}`,
            label: processDef.processName + ' (' + phaseNames[phaseName] + ')',
            phase: phaseName,
          });
        }
      }
      let active = processesAndTasks.active;
      active = active.map((item) => {
        item['processKeyWithoutPhase'] = item.processKey;
        if (item.phase == null && processNameToPhaseMap[item.processKey]) {
          item.phase = processNameToPhaseMap[item.processKey];
        }
        if (item.phase && item.phase !== 'default') {
          item.processKey = `${item.phase}:${item.processKey}`;
        }
        return item;
      });
      result.active = active;
    }
    return result;
  }

  private showSubprocessConfigButton(
    process: AlandaProcess,
    relatedProject: AlandaProject,
  ): boolean {
    const projectTypeConfig = JSON.parse(
      relatedProject.pmcProjectType.configuration,
    );
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
        subprocessPropertiesTemplate[processKeyWithoutPhase]) ||
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
}
