import { Component } from '@angular/core';
import {
  AlandaTableColumnDefinition,
  AlandaTableLayout,
  Authorizations,
} from '@alanda/common';
import { UserAdapter } from '../../core/services/user.adapter';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';

@Component({
  selector: 'alanda-task-list',
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.scss'],
})
export class AlandaTaskListComponent {
  layouts$: Observable<AlandaTableLayout[]>;
  user$ = this.userAdapter.currentUser$;
  taskLayouts: AlandaTableLayout[];

  constructor(private userAdapter: UserAdapter) {
    const clickableTaskCell: AlandaTableColumnDefinition = {
      displayName: 'Task Name',
      name: 'Task Name',
      field: 'task.task_name',
    };

    const taskColumnDefs = {
      defaultColumnDefs: [
        clickableTaskCell,
        {
          displayName: 'Type',
          name: 'Type',
          width: 50,
          field: 'task.task_type',
        },
        { displayName: 'Object', name: 'Object', field: 'task.object_name' },
        {
          displayName: 'Cluster',
          name: 'Cluster',
          field: 'refObject.clusterIdName',
        },
        { displayName: 'Address', name: 'Address', field: 'refObject.address' },
        {
          displayName: 'Project ID',
          name: 'Project ID',
          field: 'project.projectId',
        },
        {
          displayName: 'Prio',
          name: 'Priority',
          field: 'project.priority',
          width: 70,
          template:
            "{'ng-prio': project != null, 'ng-prio-low': project != null && project.priority == 0, 'ng-prio-medium': project != null && project.priority == 1, 'ng-prio-high': project != null && project.priority == 2}",
        },
        {
          displayName: 'Project Tag',
          name: 'Project Tag',
          field: 'project.tag',
          cellTemplate:
            '<div class="ui-grid-cell-contents">{{grid.appScope.tagArrayToString(row.entity.project.tag)}}</div>',
        },
        { displayName: 'Assignee', name: 'Assignee', field: 'task.assignee' },
        { displayName: 'Action', name: 'Action', width: 120 },
        {
          displayName: 'Created',
          name: 'Created',
          field: 'task.created',
          width: 90,
        },
        { displayName: 'Due', name: 'Due', field: 'task.due', width: 90 },
      ],
      adminColumnDefs: [
        clickableTaskCell,
        {
          displayName: 'Type',
          name: 'Type',
          width: 80,
          field: 'task.task_type',
        },
        { displayName: 'Object', name: 'Object', field: 'task.object_name' },
        {
          displayName: 'Cluster',
          name: 'Cluster',
          field: 'refObject.clusterIdName',
        },
        { displayName: 'Address', name: 'Address', field: 'refObject.address' },
        {
          displayName: 'Project ID',
          name: 'Project ID',
          field: 'project.projectId',
        },
        {
          displayName: 'Prio',
          name: 'Priority',
          field: 'project.priority',
          width: 70,
          template:
            "{'ng-prio': project != null, 'ng-prio-low': project != null && project.priority == 0, 'ng-prio-medium': project != null && project.priority == 1, 'ng-prio-high': project != null && project.priority == 2}",
        },
        {
          displayName: 'Project Tag',
          name: 'Project Tag',
          field: 'project.tag',
          cellTemplate:
            '<div class="ui-grid-cell-contents">{{grid.appScope.tagArrayToString(row.entity.project.tag)}}</div>',
        },
        { displayName: 'Assignee', name: 'Assignee', field: 'task.assignee' },
        { displayName: 'Action', name: 'Action', width: 120 },
        {
          displayName: 'Created',
          name: 'Created',
          field: 'task.created',
          width: 100,
        },
        { displayName: 'Due', name: 'Due', field: 'task.due', width: 90 },
        {
          displayName: 'Candidate Group',
          name: 'Candidate Group',
          field: 'task.candidateGroups',
          filter: '!Administrator',
          width: 180,
        },
      ],
    };

    this.taskLayouts = [
      {
        name: 'default',
        displayName: 'Site',
        columnDefs: taskColumnDefs.defaultColumnDefs,
      },
      {
        name: 'admin',
        displayName: 'Admin',
        columnDefs: taskColumnDefs.adminColumnDefs,
      },
    ];

    this.layouts$ = this.user$.pipe(
      map((user) => {
        return this.taskLayouts.filter((layout) =>
          Authorizations.filterTaskListLayouts(layout, user),
        );
      }),
    );
  }
}
