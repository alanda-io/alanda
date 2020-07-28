import { Injectable } from '@angular/core';
import { TreeNode } from 'primeng/api/treenode';
import { AlandaProject } from '../../shared/api/models/project';
import { AlandaProcess } from '../../shared/api/models/process';
import { AlandaTask } from '../../shared/api/models/task';
import { ProjectState } from '../../enums/projectState.enum';

export interface TreeNodeData {
  label?: string;
  refObject?: string;
  assignee?: string;
  start?: Date;
  end?: Date;
  type?: string;
  comment?: string;
  routerLink?: string;
  relatedProject?: AlandaProject;
  value?: any;
}

@Injectable({
  providedIn: 'root',
})
export class ProjectAndProcessesService {
  constructor() {}

  mapProjectToTreeNode(
    project: AlandaProject,
    relatedProject: AlandaProject,
  ): TreeNode {
    const data: TreeNodeData = {
      label: `${project.projectId} (${project.pmcProjectType.name} / ${project.title})`,
      refObject: project.refObjectIdName,
      assignee: project.ownerName,
      start: new Date(project.createDate),
      comment: project.comment,
      routerLink: `/projectdetails/${project.projectId}`,
      type: this.getProjectRelation(project, relatedProject),
      value: project,
      relatedProject,
    };
    return {
      data,
      children:
        data.type !== 'parent'
          ? project.processes.map((process) =>
              this.mapProcessToTreeNode(process, project),
            )
          : null,
    };
  }

  mapProcessToTreeNode(
    process: AlandaProcess,
    project: AlandaProject,
  ): TreeNode {
    if (process.status === ProjectState.NEW) {
      return this.mapNewProcessToTreeNode(process, project);
    }
    const data: TreeNodeData = {
      label: process.label,
      refObject: process.processKey,
      start: process.startTime,
      end: process.endTime,
      comment: process.resultComment,
      type: 'process',
      value: process,
      relatedProject: project,
    };
    return {
      data,
      children: process.tasks
        ? process.tasks.map((task) => this.mapTaskToTreeNode(task, project))
        : null,
    };
  }

  mapTaskToTreeNode(task: AlandaTask, project: AlandaProject): TreeNode {
    const data: TreeNodeData = {
      label: task.task_name,
      refObject: task.process_definition_key,
      assignee: task.assignee,
      start: new Date(task.created),
      comment: task.comment,
      routerLink: `/forms/${task.formKey}/${task.task_id}`,
      type: task.actinst_type === 'task' ? 'task' : 'activity',
      value: task,
      relatedProject: project,
    };
    return {
      data,
    };
  }

  mapNewProcessToTreeNode(
    process: AlandaProcess,
    project: AlandaProject,
  ): TreeNode {
    const data: TreeNodeData = {
      type: 'new-process',
      value: process,
      relatedProject: project,
    };
    return {
      data,
    };
  }

  private getProjectRelation(
    project: AlandaProject,
    relatedProject: AlandaProject,
  ): string {
    if (project.childrenIds?.includes(relatedProject.guid)) {
      return 'parent';
    }
    if (project.parentIds?.includes(relatedProject.guid)) {
      return 'child';
    }
    return 'project';
  }
}
