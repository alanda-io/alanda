import { Injectable } from '@angular/core';
import { TreeNode } from 'primeng/api/treenode';
import { AlandaProject } from '../../api/models/project';
import { AlandaProcess } from '../../api/models/process';
import { AlandaTask } from '../../api/models/task';
import { AlandaProcessesAndTasks } from '../../api/models/processesAndTasks';
import { ProjectState } from '../../enums/projectState.enum';
import { Observable } from 'rxjs';
import { ProcessRelation } from '../../enums/processRelation.enum';
import { AlandaProjectApiService } from '../../api/projectApi.service';
import { uuid } from '../../utils/helper-functions';

export interface MappedAllowedProcesses {
  keyWithoutPhase: string;
  processKey: string;
  label: string;
  phase: string;
}

export interface MappedProcessesAndTasks {
  data: AlandaProcessesAndTasks,
  allowed: MappedAllowedProcesses[],
  active: AlandaProcess[]
}

export enum TreeNodeDataType {
  PROJECT,
  TASK,
  PROCESS,
  ACTIVITY,
  STARTPROCESS
};

export interface TreeNodeData {
  uuid: string;
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
}

@Injectable({
  providedIn: 'root',
})
export class XxxService {

  constructor(private readonly projectService: AlandaProjectApiService) { }

  mapProjectsToTreeNode(project: AlandaProject): TreeNode[] {
    const data: TreeNode[] = [];
    if (project.parents) {
      data.push({
        data: { label: `Parent Projects (${project.parents.length})` },
        children: project.parents.map(parent => Object.assign({}, parent, {processes: null})).map(parent => this.mapProjectToTreeNode(parent)),
      })
    }
    data.push(this.mapProjectToTreeNode(project));
    if (project.children) {
      data.push({
        data: { label: `Child Projects (${project.children.length})` },
        children: project.children.map(child => this.mapProjectToTreeNode(child)),
      })
    }
    return data;
  }

  mapProjectToTreeNode(project: AlandaProject): TreeNode {
    const data: TreeNodeData = {
      uuid: uuid(),
      label: `${project.projectId} (${project.pmcProjectType.name} / ${project.title})`,
      refObject: project.refObjectIdName,
      assignee: project.ownerName,
      start: new Date(project.createDate),
      comment: project.comment,
      routerLink: `/projectdetails/${project.projectId}`,
      type: TreeNodeDataType.PROJECT,
      project,
      status: project.status
    };
    return {
      data,
      children: project.processes ? project.processes.map(process => this.mapProcessToTreeNode(process)) : null
    };
  }

  mapProcessToTreeNode(process: AlandaProcess): TreeNode {
    const data: TreeNodeData = {
      uuid: uuid(),
      label: process.label,
      refObject: process.processKey,
      start: process.startTime,
      end: process.endTime,
      comment: process.resultComment,
      type: TreeNodeDataType.PROCESS,
      process,
      dropdown: process.status === ProjectState.NEW ? true : false,
      status: process.status
    };
    return {
      data,
      children: process.tasks ? process.tasks.map((task) => this.mapTaskToTreeNode(task)) : null
    };
  }

  mapTaskToTreeNode(task: AlandaTask): TreeNode {
    const data: TreeNodeData = {
      uuid: uuid(),
      label: task.task_name,
      refObject: task.process_definition_key,
      assignee: task.assignee,
      start: new Date(task.created),
      comment: task.comment,
      routerLink: `/forms/${task.formKey}/${task.task_id}`,
      type: task.actinst_type === 'task' ? TreeNodeDataType.TASK : TreeNodeDataType.ACTIVITY,
      task,
      status: task.state
    };
    return {
      data,
    };
  }

  getStartProcessDropdownAsTreeNode(): TreeNode {
    return { data : {
      uuid: uuid(),
      type: TreeNodeDataType.STARTPROCESS,
      dropdown: true
      }
    }
  }

  getProcessesAndTasksForProject(processesAndTasks: AlandaProcessesAndTasks): any {
    const result: MappedProcessesAndTasks = {
      data: processesAndTasks,
      allowed: [],
      active: []
    };
    const phaseNames = processesAndTasks.phaseNames;
    const processNameToPhaseMap = {}
    let allowedProcesses = processesAndTasks.allowed['default'];
    allowedProcesses = allowedProcesses.sort((itemA, itemB) => {return itemA.processName.localeCompare(itemB.processName)});
    for (const processDef of allowedProcesses) {
      processNameToPhaseMap[processDef.processDefinitionKey] = 'default'
      result.allowed.push({
        keyWithoutPhase: processDef.processDefinitionKey,
        processKey: processDef.processDefinitionKey,
        label: processDef.processName,
        phase: processNameToPhaseMap[processDef.processDefinitionKey]}
      );
    }
    for (let [phaseName, processes] of Object.entries(processesAndTasks.allowed)) {
      if (phaseName !== 'default') {
        processes = processes.sort((itemA, itemB) => {return itemA.processName.localeCompare(itemB.processName)});
        for (const processDef of processes) {
          processNameToPhaseMap[processDef.processDefinitionKey] = phaseName;
          result.allowed.push({
            keyWithoutPhase: processDef.processDefinitionKey,
            processKey: `${phaseName}:${processDef.processDefinitionKey}`,
            label: processDef.processName + ' (' + phaseNames[phaseName] + ')',
            phase: phaseName
          });
        }
      }
      let active = processesAndTasks.active
      active = active.map((item) => {
        item['processKeyWithoutPhase'] = item.processKey;
        if (item.phase == null && processNameToPhaseMap[item.processKey]) {
          item.phase = processNameToPhaseMap[item.processKey];
        }
        if (item.phase && item.phase !== 'default') {
          item.processKey=`${item.phase}:${item.processKey}`
        }
        return item;
      });
      result.active = active
    }
    return result;
  }

  startSubprocess(value: {data: TreeNodeData, parent: TreeNode}): Observable<AlandaProcess> {
    value.data.process.status = ProjectState.NEW;
    value.data.process.relation = ProcessRelation.CHILD;
    value.data.process.workDetails = '';
    value.data.process.businessObject = value.parent.data.project.refObjectIdName;
    return this.projectService.saveProjectProcess(value.parent.data.project.guid, value.data.process);
  }

}
