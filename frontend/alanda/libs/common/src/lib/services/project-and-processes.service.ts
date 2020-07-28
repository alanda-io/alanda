import { Injectable } from '@angular/core';
import { TreeNode } from 'primeng/api/treenode';

import { AlandaProject } from '../shared/api/models/project';
import { AlandaProcess } from '../shared/api/models/process';
import { AlandaTask } from '../shared/api/models/task';

@Injectable({
  providedIn: 'root',
})
export class ProjectAndProcessesService {
  constructor() {}

  mapProjectToTreeNode(project: AlandaProject): TreeNode {
    return {
      data: {
        label: `${project.projectId} (${project.pmcProjectType.name} / ${project.title})`,
        refObject: project.refObjectIdName,
        assignee: project.ownerName,
        start: project.createDate,
        comment: project.comment,
      },
      children: project.processes.map((process) =>
        this.mapProcessToTreeNode(process),
      ),
      expanded: true,
    };
  }

  mapProcessToTreeNode(process: AlandaProcess): TreeNode {
    return {
      data: {
        label: process.label,
        refObject: process.processKey,
        start: process.startTime,
        end: process.endTime,
        comment: process.resultComment,
      },
      children: process.tasks.map((task) => this.mapTaskToTreeNode(task)),
      expanded: true,
    };
  }

  mapTaskToTreeNode(task: AlandaTask): TreeNode {
    return {
      data: {
        label: task.task_name,
        refObject: task.process_definition_key,
        assignee: task.assignee,
        comment: task.comment,
        routerLink: `/forms/${task.formKey}/${task.task_id}`,
      },
    };
  }
}
