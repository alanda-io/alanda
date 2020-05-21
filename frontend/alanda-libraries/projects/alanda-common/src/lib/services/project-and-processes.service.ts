import { Injectable } from '@angular/core';
import { TreeNode } from 'primeng/api/treenode';
import { AlandaProject } from '../api/models/project';
import { AlandaProcess } from '../api/models/process';
import { AlandaTask } from '../api/models/task';
import { AlandaProjectApiService } from '../api/projectApi.service';

export interface TreeNodeData {
  label: string;
  refObject: string;
  assignee: string;
  start: any;
  end: any;
  comment: string;
  routerLink: string;
  type: string;
  id: any;
  value: any;
  project: AlandaProject;
}

@Injectable({
  providedIn: 'root'
})
export class ProjectAndProcessesService {

  constructor(private projectService: AlandaProjectApiService) { }

  mapProjectToTreeNode(project: AlandaProject, guid: number): TreeNode {
    const data: TreeNodeData = {
      label: `${project.projectId} (${project.pmcProjectType.name} / ${project.title})`,
      refObject: project.refObjectIdName,
      assignee: project.ownerName,
      start: project.createDate,
      end: null,
      comment: project.comment,
      routerLink: project.childrenIds.includes(guid) ? `/project/projectdetails/${project.projectId}` : null,
      type: project.childrenIds.includes(guid) ? 'parent-project' : 'project',
      id: project.guid,
      value: project,
      project,
    };
    return {
      data,
      children: project.childrenIds.includes(guid) ? null : project.processes.map(process => this.mapProcessToTreeNode(process, project)),
      expanded: true
    };
  }

  mapProcessToTreeNode(process: AlandaProcess, project: AlandaProject): TreeNode {
    const data: TreeNodeData = {
      label: process.label,
      refObject: process.processKey,
      assignee: null,
      start: process.startTime,
      end: process.endTime,
      comment: process.resultComment,
      routerLink: null,
      type: 'process',
      id: process.guid,
      value: process,
      project
    };
    return {
      data,
      children: process.tasks ? process.tasks.map(task => this.mapTaskToTreeNode(task, project)) : null,
      expanded: true
    };
  }

  mapTaskToTreeNode(task: AlandaTask, project: AlandaProject): TreeNode {
    const data: TreeNodeData = {
      label: task.task_name,
      refObject: task.process_definition_key,
      assignee: task.assignee,
      start: null,
      end: null,
      comment: task.comment,
      routerLink: `/forms/${task.formKey}/${task.task_id}`,
      type: task.actinst_type,
      id: task.task_id,
      value: task,
      project
    };
    return {
      data
    };
  }

  mapAllowedProcessesToTreeNode(processes: any[], project: AlandaProject): TreeNode {
    const data: TreeNodeData = {
      label: 'allowed-processes',
      refObject: null,
      assignee: null,
      start: null,
      end: null,
      comment: null,
      routerLink: null,
      type: 'allowed-processes',
      id: null,
      value: processes,
      project
    };
    return {
      data
    };
  }
}
