import {
  AlandaTableLayout,
  Authorizations,
  TableColumnType,
  TableType,
} from '@alanda/common';

const PROJECT_MONITOR_COLUMNS_DEFS = {
  allColumnDefs: [
    {
      displayName: 'Project Owner',
      name: 'project.additionalInfo.project_details_tenant',
      field: 'project.additionalInfo.project_details_tenant',
    },
    {
      displayName: 'Project ID',
      name: 'Project ID',
      field: 'project.projectId',
    },
    {
      displayName: 'Title',
      name: 'Title',
      field: 'project.title',
    },
    {
      displayName: 'Project Type',
      name: 'Project Type',
      field: 'project.pmcProjectType.name',
    },
    { displayName: 'Sub Type', name: 'Sub Type', field: 'project.subtype' },
    {
      displayName: 'RefObjectId',
      name: 'RefObjectId',
      field: 'project.refObjectDisplayName',
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
      width: 65,
      template:
        "{'priority': true, 'priority-low': project.priority == 2, 'priority-medium': project.priority == 1, 'priority-high': project.priority == 0}",
    },
    {
      displayName: 'Tag',
      name: 'Tag',
      field: 'project.tag',
      cellTemplate:
        '<div class="p-grid-cell-contents"><span>{{grid.appScope.tagArrayToString(row.entity.project.tag)}}</span></div>',
    },
    {
      displayName: 'Status',
      name: 'Status',
      field: 'project.status',
    },
  ],
  neubauUmbauColumnDefs: [
    {
      displayName: 'Site Owner',
      name: 'refObject.smSiteOwnerGroupDto.idName',
      field: 'refObject.smSiteOwnerGroupDto.idName',
      width: 90,
    },
    {
      displayName: 'Project Owner',
      name: 'project.additionalInfo.project_details_tenant',
      field: 'project.additionalInfo.project_details_tenant',
    },
    {
      displayName: 'Site',
      name: 'project.refObjectDisplayName',
      field: 'project.refObjectDisplayName',
    },
    {
      displayName: 'Project ID',
      name: 'Project ID',
      field: 'project.projectId',
    },
    { displayName: 'Sub Type', name: 'Sub Type', field: 'project.subtype' },
    {
      displayName: 'Reason',
      name: 'Reason',
      field: 'project.tag',
      cellTemplate:
        '<div class="p-grid-cell-contents"><span>{{grid.appScope.tagArrayToString(row.entity.project.tag)}}</span></div>',
    },
    {
      displayName: 'Prio',
      name: 'Prio',
      field: 'project.priority',
      width: 65,
      template:
        "{'priority': true, 'priority-low': project.priority == 2, 'priority-medium': project.priority == 1, 'priority-high': project.priority == 0}",
    },
    {
      displayName: 'Status',
      name: 'Status',
      field: 'project.status',
    },
    {
      displayName: 'Start Date',
      name: 'startDate',
      field: 'project.createDate',
      type: TableColumnType.DATE,
    },
    {
      displayName: 'Due Date',
      name: 'dueDate',
      field: 'project.dueDate',
      type: TableColumnType.DATE,
    },
    {
      displayName: 'Akqui GU',
      name: 'Akqui GU',
      field: 'contacts.akqui.fullName',
    },
    {
      displayName: 'Bau GU',
      name: 'Bau GU',
      field: 'contacts.baugu.fullName',
    },
  ],
  scheduledUmbauColumnDefs: [
    {
      displayName: 'Project Owner',
      name: 'project.additionalInfo.project_details_tenant',
      field: 'project.additionalInfo.project_details_tenant',
    },
    {
      displayName: 'Project ID',
      name: 'Project ID',
      field: 'project.projectId',
    },
    { displayName: 'Sub Type', name: 'Sub Type', field: 'project.subtype' },
    {
      displayName: 'Reason',
      name: 'Reason',
      field: 'project.tag',
      cellTemplate:
        '<div class="p-grid-cell-contents"><span>{{grid.appScope.tagArrayToString(row.entity.project.tag)}}</span></div>',
    },
    {
      displayName: 'Prio',
      name: 'Prio',
      field: 'project.priority',
      width: 65,
      template:
        "{'priority': true, 'priority-low': project.priority == 2, 'priority-medium': project.priority == 1, 'priority-high': project.priority == 0}",
    },
    {
      displayName: 'Scheduled DateDate',
      name: 'scheduledDate',
      field: 'project.milestonesMap.PROJECT_START_SCHEDULED.act',
    },
    {
      displayName: 'Due Date',
      name: 'dueDate',
      field: 'project.dueDate',
      type: TableColumnType.DATE,
    },
    {
      displayName: 'Site',
      name: 'RefObjectId',
      field: 'project.refObjectIdName',
    },
    {
      displayName: 'Akqui GU',
      name: 'Akqui GU',
      field: 'contacts.akqui.fullName',
    },
    {
      displayName: 'Bau GU',
      name: 'Bau GU',
      field: 'contacts.baugu.fullName',
    },
  ],
  txClusterColumnDefs: [
    {
      displayName: 'Project ID',
      name: 'Project ID',
      field: 'project.projectId',
    },
    {
      displayName: 'Project Name',
      name: 'Project Name',
      field: 'project.title',
    },
    { displayName: 'Links', name: 'Links', field: '' },
    {
      displayName: 'Created',
      name: 'Created',
      field: 'project.createDate',
      type: TableColumnType.DATE,
    },
    {
      displayName: 'Assignee',
      name: 'Assignee',
      field: 'project.assignee',
    },
    { displayName: 'Status', name: 'Status', field: 'project.status' },
  ],
  txSiteColumnDefs: [
    {
      displayName: 'Project ID',
      name: 'Project ID',
      field: 'project.projectId',
    },
    {
      displayName: 'Cluster Project Name',
      name: 'Cluster Project Name',
      field: 'project.additionalInfo.txcluster.title',
    },
    {
      displayName: 'Project Name',
      name: 'Project Name',
      field: 'project.title',
    },
    {
      displayName: 'Site ID',
      name: 'Site ID',
      field: 'project.refObjectIdName',
    },
    {
      displayName: 'Created',
      name: 'Created',
      field: 'project.createDate',
      type: TableColumnType.DATE,
    },
    {
      displayName: 'Assignee',
      name: 'Assignee',
      field: 'project.assignee',
    },
    { displayName: 'Status', name: 'Status', field: 'project.status' },
  ],
  txLinkColumnDefs: [
    {
      displayName: 'Project ID',
      name: 'Project ID',
      field: 'project.projectId',
    },
    {
      displayName: 'Cluster Project Name',
      name: 'Cluster Project Name',
      field: 'project.additionalInfo.txcluster.title',
    },
    {
      displayName: 'Project Name',
      name: 'Project Name',
      field: 'project.title',
    },
    {
      displayName: 'Link ID',
      name: 'Link ID',
      field: 'project.refObjectIdName',
    },
    {
      displayName: 'Created',
      name: 'Created',
      field: 'project.createDate',
      type: TableColumnType.DATE,
    },
    {
      displayName: 'Assignee',
      name: 'Assignee',
      field: 'project.assignee',
    },
    { displayName: 'Status', name: 'Status', field: 'project.status' },
  ],
  eventColumnDefs: [
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
      width: 65,
      template:
        "{'priority': true, 'priority-low': project.priority == 2, 'priority-medium': project.priority == 1, 'priority-high': project.priority == 0}",
    },
    {
      displayName: 'Status',
      name: 'Status',
      field: 'project.status',
    },
  ],
  abbauColumnDefs: [
    {
      displayName: 'Project Owner',
      name: 'project.additionalInfo.project_details_tenant',
      field: 'project.additionalInfo.project_details_tenant',
    },
    {
      displayName: 'Project ID',
      name: 'Project ID',
      field: 'project.projectId',
    },
    {
      displayName: 'Reason',
      name: 'Reason',
      field: 'project.tag',
      cellTemplate:
        '<div class="p-grid-cell-contents"><span>{{grid.appScope.tagArrayToString(row.entity.project.tag)}}</span></div>',
    },
    {
      displayName: 'Prio',
      name: 'Prio',
      field: 'project.priority',
      width: 65,
      template:
        "{'priority': true, 'priority-low': project.priority == 2, 'priority-medium': project.priority == 1, 'priority-high': project.priority == 0}",
    },
    {
      displayName: 'Status',
      name: 'Status',
      field: 'project.status',
    },
    {
      displayName: 'Project Start',
      name: 'startDate',
      field: 'project.createDate',
      type: TableColumnType.DATE,
    },
    {
      displayName: 'Project Due',
      name: 'Due Date',
      field: 'project.dueDate',
      type: TableColumnType.DATE,
    },
    {
      displayName: 'RefObjectId',
      name: 'RefObjectId',
      field: 'project.refObjectIdName',
    },
    {
      displayName: 'Abbau GU',
      name: 'Abbau GU',
      field: 'project.additionalInfo.role_baugu',
    },
    {
      displayName: 'Abbau Start',
      name: 'Abbau Start',
      field: 'project.additionalInfo.requestedStartDateStr',
      type: TableColumnType.DATE,
    },
    {
      displayName: 'Abbau End',
      name: 'Abbau End',
      field: 'project.additionalInfo.requestedEndDateStr',
      type: TableColumnType.DATE,
    },
    {
      displayName: 'Change ID',
      name: 'Change ID',
      field: 'project.additionalInfo.changeId',
    },
  ],
  requestColumnDefs: [
    {
      displayName: 'Request ID',
      name: 'Request ID',
      field: 'project.projectId',
    },
    {
      displayName: 'Status',
      name: 'Status',
      field: 'project.status',
    },
    {
      displayName: 'Customer',
      name: 'Customer',
      field: 'project.additionalInfo.companyName',
    },
    {
      displayName: 'Location A',
      name: 'Location A',
      field: 'refObject.locAAddress',
    },
    {
      displayName: 'Location B',
      name: 'Location B',
      field: 'refObject.locBAddress',
    },
    {
      displayName: 'Request Date',
      name: 'Request Date',
      field: 'project.createDate',
      type: TableColumnType.DATE,
    },
  ],
};

export const PROJECT_LAYOUTS = [
  {
    name: 'all',
    displayName: 'Alle',
    filterOptions: {},
    columnDefs: PROJECT_MONITOR_COLUMNS_DEFS.allColumnDefs,
  },
  {
    name: 'umbau',
    displayName: 'Umbau / Neubau',
    filterOptions: {
      'project.pmcProjectType.idName.raw': ['UMBAU', 'NB-SI'],
    },
    columnDefs: PROJECT_MONITOR_COLUMNS_DEFS.neubauUmbauColumnDefs,
  },
  {
    name: 'scheduled_umbau',
    displayName: 'Umbau (Scheduled)',
    filterOptions: { 'project.status.raw': ['SCHEDULED'] },
    columnDefs: PROJECT_MONITOR_COLUMNS_DEFS.scheduledUmbauColumnDefs,
  },
  {
    name: 'txcluster',
    displayName: 'TX Cluster',
    filterOptions: {
      'project.pmcProjectType.idName': {
        value: 'TX-CLUSTER-ORCHESTRATION',
        fieldType: 'raw',
      },
    },
    columnDefs: PROJECT_MONITOR_COLUMNS_DEFS.txClusterColumnDefs,
  },
  {
    name: 'txsite',
    displayName: 'TX Site',
    filterOptions: {
      'project.pmcProjectType.idName': {
        value: 'TX-SITE-ORCHESTRATION',
        fieldType: 'raw',
      },
    },
    columnDefs: PROJECT_MONITOR_COLUMNS_DEFS.txSiteColumnDefs,
  },
  {
    name: 'txlink',
    displayName: 'TX Link',
    filterOptions: {
      'project.pmcProjectType.idName': {
        value: 'TX-LINK-ORCHESTRATION',
        fieldType: 'raw',
      },
    },
    columnDefs: PROJECT_MONITOR_COLUMNS_DEFS.txLinkColumnDefs,
  },
  {
    name: 'event',
    displayName: 'Event',
    filterOptions: {
      'project.pmcProjectType.idName': {
        value: 'EVENT-EVENT',
        fieldType: 'raw',
      },
    },
    columnDefs: PROJECT_MONITOR_COLUMNS_DEFS.eventColumnDefs,
  },
  {
    name: 'refarming',
    displayName: 'Refarming',
    filterOptions: {
      'project.pmcProjectType.idName': {
        value: 'REFARMING-ORCHESTRATION',
        fieldType: 'raw',
      },
    },
    columnDefs: PROJECT_MONITOR_COLUMNS_DEFS.allColumnDefs,
  },
  {
    name: 'abbau',
    displayName: 'Abbau',
    filterOptions: {
      'project.pmcProjectType.idName.raw': ['ABBAU', 'DECONSTRUCTION'],
    },
    columnDefs: PROJECT_MONITOR_COLUMNS_DEFS.abbauColumnDefs,
  },
  {
    name: 'request',
    displayName: 'Request',
    filterOptions: {
      'project.pmcProjectType.idName': {
        value: 'REQUEST',
        fieldType: 'raw',
      },
    },
    columnDefs: PROJECT_MONITOR_COLUMNS_DEFS.requestColumnDefs,
  },
];
