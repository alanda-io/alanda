import { Injectable } from '@angular/core';
import { AlandaUser } from '../api/models/user';
import { Authorizations } from '../permissions/utils/permission-checks';
import { AlandaTableLayout } from '../api/models/tableLayout';

@Injectable()
export class AlandaMonitorAPIService {
  constructor() {}

  clickableTaskCell = {
    displayName: 'Task Name',
    name: 'Task Name',
    field: 'task.task_name',
  };

  taskColumnDefs = {
    defaultColumnDefs: [
      this.clickableTaskCell,
      { displayName: 'Type', name: 'Type', width: 50, field: 'task.task_type' },
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
      this.clickableTaskCell,
      { displayName: 'Type', name: 'Type', width: 80, field: 'task.task_type' },
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

  taskLayouts = {
    default: {
      name: 'default',
      displayName: 'Site',
      columnDefs: this.taskColumnDefs.defaultColumnDefs,
    },
    admin: {
      name: 'admin',
      displayName: 'Admin',
      columnDefs: this.taskColumnDefs.adminColumnDefs,
    },
  };

  public getTaskListLayouts(user: AlandaUser): AlandaTableLayout[] {
    let layouts: any[] = Object.keys(this.taskLayouts).map(
      (key) => this.taskLayouts[key],
    );
    layouts = layouts.filter((layout) => {
      if (Authorizations.hasRole('Admin', user)) {
        return layout;
      } else {
        return Authorizations.hasPermission(
          user,
          'task:layout:' + layout.name,
          null,
        );
      }
    });
    layouts.push(this.taskLayouts.default);
    return layouts;
  }
}
