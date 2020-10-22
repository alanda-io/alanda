import { Component } from '@angular/core';
import {
  AlandaTableColumnDefinition,
  AlandaTableLayout,
  Authorizations,
  TableType,
  TableColumnType,
} from '@alanda/common';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { UserStoreImpl } from '../../store/user';

@Component({
  selector: 'alanda-task-list',
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.scss'],
})
export class AlandaTaskListComponent {
  layouts$: Observable<AlandaTableLayout[]>;
  user$ = this.userStore.currentUser$;
  taskLayouts: AlandaTableLayout[];

  constructor(private userStore: UserStoreImpl) {
    const clickableTaskCell: AlandaTableColumnDefinition = {
      displayName: 'Task Name',
      name: 'Task Name',
      field: 'task.task_name',
      width: 220,
    };

    const taskColumnDefs = {
      defaultColumnDefs: [
        {
          displayName: 'Project Owner',
          name: 'project.additionalInfo.project_details_tenant',
          field: 'project.additionalInfo.project_details_tenant',
        },
        clickableTaskCell,
        {
          displayName: 'Type',
          name: 'Type',
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
          template:
            "{'p-text-center': true,'ng-prio': project != null, 'ng-prio-low': project != null && project.priority == 2, 'ng-prio-medium': project != null && project.priority == 1, 'ng-prio-high': project != null && project.priority == 0}",
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
          type: TableColumnType.DATE,
        },
        { displayName: 'Due', name: 'Due', field: 'task.due', width: 90 },
      ],
      adminColumnDefs: [
        clickableTaskCell,
        {
          displayName: 'Type',
          name: 'Type',
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
          type: TableColumnType.DATE,
        },
        { displayName: 'Due', name: 'Due', field: 'task.due', width: 90 },
        {
          displayName: 'Candidate Group',
          name: 'Candidate Group',
          field: 'task.candidateGroups',
          filter: '!Administrator',
        },
      ],
      schnickSchnackColumnDefs: [
        {
          displayName: 'Project Owner',
          name: 'project.additionalInfo.project_details_tenant',
          field: 'project.additionalInfo.project_details_tenant',
        },
        clickableTaskCell,
        {
          displayName: 'Type',
          name: 'Type',
          field: 'task.task_type',
        },
        { displayName: 'Object', name: 'Object', field: 'task.object_name' },
        { displayName: 'Project', name: 'Project', field: 'project.projectId' },
        {
          displayName: 'Prio',
          name: 'Priority',
          field: 'project.priority',
          template:
            "{'p-text-center': true, 'ng-prio': project != null, 'ng-prio-low': project != null && project.priority == 2, 'ng-prio-medium': project != null && project.priority == 1, 'ng-prio-high': project != null && project.priority == 0}",
        },
        {
          displayName: 'Project Due',
          name: 'Project Due',
          field: 'project.dueDate',
          type: TableColumnType.DATE,
        },
        {
          displayName: 'Project Tag',
          name: 'Project Tag',
          field: 'project.tag',
          cellTemplate:
            '<div class="ui-grid-cell-contents"><span>{{grid.appScope.tagArrayToString(row.entity.project.tag)}}</span></div>',
        },
        { displayName: 'Assignee', name: 'Assignee', field: 'task.assignee' },
        {
          displayName: 'Action',
          name: 'Action',
          // cellTemplate: require('pmc-cockpit-base/app/core/task/list/views/taskactions.template.html'),
        },
        {
          displayName: 'Created',
          name: 'Created',
          field: 'task.created',
          type: TableColumnType.DATE,
        },
        { displayName: 'Details', name: 'Details', field: 'project.details' },
        {
          displayName: 'Responsible Groups',
          name: 'Responsible Groups',
          field: 'task.candidateGroups',
          cellTemplate:
            '<div class="ui-grid-cell-contents"><span>{{grid.appScope.tagArrayToString(row.entity.task.candidateGroups | filter: "!Administrator")}}</span></div>',
        },
        {
          displayName: 'X',
          name: 'X',
          cellTemplate:
            '<div class="ui-grid-cell-contents"><span ng-click="grid.appScope.editDetails(row)"><i class="pi pi-pencil"></i></span></div>',
          enableCellEdit: false,
          enableSorting: false,
          enableFiltering: false,
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
      {
        name: 'admin',
        displayName: 'Admin',
        columnDefs: taskColumnDefs.adminColumnDefs,
      },
      {
        name: 'schnickSchnack',
        displayName: 'Schnick Schnack Schabernak',
        columnDefs: taskColumnDefs.schnickSchnackColumnDefs,
      },
    ];

    this.layouts$ = this.user$.pipe(
      map((user) => {
        return this.taskLayouts.filter((layout) =>
          Authorizations.hasPermissionForTableLayout(
            layout,
            user,
            TableType.TASK,
          ),
        );
      }),
    );
  }
}
