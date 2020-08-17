import { Component } from '@angular/core';
import { AlandaTableLayout } from '@alanda/common';

@Component({
  selector: 'alanda-project-monitor',
  templateUrl: './project-monitor.component.html',
  styleUrls: ['./project-monitor.component.scss'],
})
export class AlandaProjectMonitorComponent {
  layouts: AlandaTableLayout[];

  constructor() {
    const columnDefAll = [
      {
        displayName: 'Project ID',
        name: 'Project ID',
        field: 'project.projectId',
      },
      { displayName: 'Title', name: 'Title', field: 'project.title' },
      {
        displayName: 'Project Type',
        name: 'Project Type',
        field: 'project.pmcProjectType.name',
      },
      { displayName: 'Sub Type', name: 'Sub Type', field: 'project.subtype' },
      {
        displayName: 'RefObjectId',
        name: 'RefObjectId',
        field: 'project.refObjectIdName',
      },
      {
        displayName: 'Start Date',
        name: 'startDate',
        field: 'project.createDate',
        width: 90,
      },
      {
        displayName: 'Due Date',
        name: 'Due Date',
        field: 'project.dueDate',
        width: 90,
      },
      {
        displayName: 'Prio',
        name: 'Prio',
        field: 'project.priority',
        width: '40',
        template:
          "{'ng-prio': true, 'ng-prio-low': project.priority == 0, 'ng-prio-medium': project.priority == 1, 'ng-prio-high': project.priority == 2}",
      },
      { displayName: 'Tag', name: 'Tag', field: 'project.tag' },
      {
        displayName: 'Status',
        name: 'Status',
        field: 'project.status',
        width: 110,
      },
    ];

    this.layouts = [
      {
        name: 'test',
        displayName: 'Test',
        filterOptions: {},
        columnDefs: columnDefAll,
      },
      {
        name: 'all',
        displayName: 'All',
        filterOptions: {},
        columnDefs: columnDefAll,
      },
      {
        name: 'foo',
        displayName: 'Foo',
        filterOptions: {},
        columnDefs: columnDefAll,
      },
      {
        name: 'bar',
        displayName: 'Bar',
        filterOptions: {},
        columnDefs: columnDefAll,
      },
    ];
  }
}
