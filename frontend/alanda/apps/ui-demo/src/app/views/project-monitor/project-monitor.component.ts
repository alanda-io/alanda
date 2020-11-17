import { Component } from '@angular/core';
import {
  AlandaTableColumnDefinition,
  AlandaTableLayout,
  TableColumnType,
} from '@alanda/common';

@Component({
  selector: 'alanda-project-monitor',
  templateUrl: './project-monitor.component.html',
  styleUrls: ['./project-monitor.component.scss'],
})
export class AlandaProjectMonitorComponent {
  layouts: AlandaTableLayout[];

  constructor() {
    const columnDefAll: AlandaTableColumnDefinition[] = [
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
        type: TableColumnType.DATE,
      },
      {
        displayName: 'Due Date',
        name: 'Due Date',
        field: 'project.dueDate',
        type: TableColumnType.DATE,
      },
      {
        displayName: 'Prio',
        name: 'Prio',
        field: 'project.priority',
        width: 75,
        template:
          "{'priority': true, 'priority-high': project.priority == 0, 'priority-medium': project.priority == 1, 'priority-low': project.priority == 2}",
      },
      { displayName: 'Tag', name: 'Tag', field: 'project.tag' },
      {
        displayName: 'Status',
        name: 'Status',
        field: 'project.status',
      },
    ];

    const columnDefSimple: AlandaTableColumnDefinition[] = [
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
      {
        displayName: 'Due Date',
        name: 'Due Date',
        field: 'project.dueDate',
        type: TableColumnType.DATE,
      },
      {
        displayName: 'Prio',
        name: 'Prio',
        field: 'project.priority',
        width: 75,
        template:
          "{'priority': true, 'priority-high': project.priority == 0, 'priority-medium': project.priority == 1, 'priority-low': project.priority == 2}",
      },
    ];

    const columnDefSpecial: AlandaTableColumnDefinition[] = [
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
      {
        displayName: 'Due Date',
        name: 'Due Date',
        field: 'project.dueDate',
        type: TableColumnType.DATE,
      },
      {
        displayName: 'Prio',
        name: 'Prio',
        field: 'project.priority',
        width: 75,
        template:
          "{'priority': true, 'priority-high': project.priority == 0, 'priority-medium': project.priority == 1, 'priority-low': project.priority == 2}",
      },
      {
        displayName: 'X',
        name: 'X',
      },
    ];

    this.layouts = [
      {
        name: 'all',
        displayName: 'All',
        columnDefs: columnDefAll,
        filterOptions: {},
      },
      {
        name: 'foo',
        displayName: 'Foo',
        columnDefs: columnDefAll,
        filterOptions: {},
      },
      {
        name: 'bar',
        displayName: 'Bar',
        columnDefs: columnDefSimple,
        filterOptions: {},
      },
      {
        name: 'specialEdit',
        displayName: 'Special Edit',
        columnDefs: columnDefSpecial,
        filterOptions: {},
      },
    ];
  }
}
