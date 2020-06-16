import { Injectable } from '@angular/core';
import {Project} from '../../models/project';
import {TreeNode} from 'primeng/api';
import {Process} from '../../models/process';
import {ProjectState} from '../../enums/project-status.enum';
import {PmcTask} from '../../models/pmcTask';

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
  relatedProject: Project;
}

@Injectable({
  providedIn: 'root'
})
export class ProjectAndProcessesService {
  constructor() { }

  mapProjectToTreeNode(project: Project, relatedProject: Project): TreeNode {
    const data: TreeNodeData = {
      label: `${project.projectId} (${project.pmcProjectType.name} / ${project.title})`,
      refObject: project.refObjectIdName,
      assignee: project.ownerName,
      start: project.createDate,
      end: null,
      comment: project.comment,
      routerLink: `/projectdetails/${project.projectId}`,
      type: this.getProjectRelation(project, relatedProject),
      id: project.guid,
      value: project,
      relatedProject,
    };
    return {
      data,
      children: data.type !== 'parent' ? project.processes.map(process => this.mapProcessToTreeNode(process, project)) : null,
      expanded: data.type === 'project'
    };
  }

  mapProcessToTreeNode(process: Process, project: Project): TreeNode {
    if (process.status === ProjectState.NEW) {
      return this.mapNewProcessToTreeNode(process, project);
    }
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
      relatedProject: project
    };
    return {
      data,
      children: process.tasks ? process.tasks.map(task => this.mapTaskToTreeNode(task, project)) : null,
      expanded: true
    };
  }

  mapTaskToTreeNode(task: PmcTask, project: Project): TreeNode {
    const data: TreeNodeData = {
      label: task.task_name,
      refObject: task.process_definition_key,
      assignee: task.assignee,
      start: task.created,
      end: null,
      comment: task.comment,
      routerLink: `/forms/${task.formKey}/${task.task_id}`,
      type: task.actinst_type,
      id: task.task_id,
      value: task,
      relatedProject: project
    };
    return {
      data
    };
  }

  mapNewProcessToTreeNode(process: Process, project: Project): TreeNode {
    const data: TreeNodeData = {
      label: null,
      refObject: null,
      assignee: null,
      start: null,
      end: null,
      comment: null,
      routerLink: null,
      type: 'new-process',
      id: null,
      value: process,
      relatedProject: project
    };
    return {
      data
    };
  }

  /* mapNewProcessToTreeNode(projectNode: TreeNode, processes: AlandaProcess): TreeNode {
    const data: TreeNodeData = {
      label: null,
      refObject: null,
      assignee: null,
      start: null,
      end: null,
      comment: null,
      routerLink: null,
      type: 'new-process',
      id: null,
      value: processes,
      relatedProject: projectNode.data.value
    };
    projectNode.children ? projectNode.children.push({data}) : projectNode.children = [{data}];
    return projectNode;
  } */

  private getProjectRelation(project: Project, relatedProject: Project): string {
    if (project.childrenIds) {
      for (const id of project.childrenIds) {
        if (id === relatedProject.guid) {
          return 'parent';
        }
      }
    }
    if (project.parentIds) {
      for (const id of project.parentIds) {
        if (id === relatedProject.guid) {
          return 'child';
        }
      }
    }
    return 'project';
  }
}
