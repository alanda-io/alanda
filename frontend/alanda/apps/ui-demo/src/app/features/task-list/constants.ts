import { AlandaTableColumnDefinition, TableColumnType } from '@alanda/common';

const CLICKABLE_TASK_CELL: AlandaTableColumnDefinition = {
  displayName: 'Task Name',
  name: 'Task Name',
  field: 'task.task_name',
  width: 250,
  cellTemplate:
    '<div class="p-grid-cell-contents pmc-javascript-link" ng-click="grid.appScope.onRowDblClick(row, col)">{{ row.entity.task.task_name }}</div>',
};

const TASK_COLUMNS_DEFS = {
  defaultColumnDefs: [
    {
      displayName: 'Site Owner',
      name: 'refObject.smSiteOwnerGroupDto.idName',
      field: 'refObject.smSiteOwnerGroupDto.idName',
    },
    {
      displayName: 'Project Owner',
      name: 'project.additionalInfo.project_details_tenant',
      field: 'project.additionalInfo.project_details_tenant',
    },
    {
      displayName: 'SiteId',
      name: 'project.refObjectDisplayName',
      field: 'project.refObjectDisplayName',
    },
    CLICKABLE_TASK_CELL,
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
      displayName: 'Versandort',
      name: 'Site ID',
      field: 'refObject.baDestination.name',
    },
    {
      displayName: 'Prio',
      name: 'Priority',
      field: 'task.priority',
      width: 65,
      template:
        "{'priority': task.priority != null,'priority-low': task.priority == 2, 'priority-medium': task.priority == 1, 'priority-high': task.priority == 0}",
    },
    {
      displayName: 'Project Tag',
      name: 'Project Tag',
      field: 'project.tag',
      cellTemplate:
        '<div class="p-grid-cell-contents">{{grid.appScope.tagArrayToString(row.entity.project.tag)}}</div>',
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
    {
      displayName: 'Due',
      name: 'Due',
      field: 'task.due',
      type: TableColumnType.DATE,
    },
  ],
  adminColumnDefs: [
    {
      displayName: 'Site Owner',
      name: 'refObject.smSiteOwnerGroupDto.idName',
      field: 'refObject.smSiteOwnerGroupDto.idName',
    },
    {
      displayName: 'Project Owner',
      name: 'project.additionalInfo.project_details_tenant',
      field: 'project.additionalInfo.project_details_tenant',
    },
    {
      displayName: 'SiteId',
      name: 'project.refObjectDisplayName',
      field: 'project.refObjectDisplayName',
    },
    CLICKABLE_TASK_CELL,
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
      field: 'task.priority',
      width: 65,
      template:
        "{'priority': task.priority != null, 'priority-low': task.priority == 2, 'priority-medium': task.priority == 1, 'priority-high': task.priority == 0}",
    },
    {
      displayName: 'Project Tag',
      name: 'Project Tag',
      field: 'project.tag',
      cellTemplate:
        '<div class="p-grid-cell-contents">{{grid.appScope.tagArrayToString(row.entity.project.tag)}}</div>',
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
    { displayName: 'Due', name: 'Due', field: 'task.due' },
    {
      displayName: 'Candidate Group',
      name: 'Candidate Group',
      field: 'task.candidateGroups',
      cellTemplate:
        '<div class="p-grid-cell-contents"><span>{{grid.appScope.tagArrayToString(row.entity.task.candidateGroups | filter: "!Administrator")}}</span></div>',
    },
  ],
  bbmColumnDefs: [
    CLICKABLE_TASK_CELL,
    {
      displayName: 'Type',
      name: 'Type',
      field: 'task.task_type',
    },
    { displayName: 'Object', name: 'Object', field: 'task.object_name' },
    { displayName: 'Cable', name: 'Cable', field: 'refObject.cableName' },
    { displayName: 'Link', name: 'Link', field: 'refObject.linkName' },
    { displayName: 'Chain', name: 'Chain', field: 'refObject.chainName' },
    { displayName: 'Project', name: 'Project', field: 'project.projectId' },
    {
      displayName: 'Prio',
      name: 'Priority',
      field: 'task.priority',
      width: 65,
      template:
        "{'priority': task.priority != null, 'priority-low': task.priority == 2, 'priority-medium': task.priority == 1, 'priority-high': task.priority == 0}",
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
    {
      displayName: 'Due',
      name: 'Due',
      field: 'task.due',
      type: TableColumnType.DATE,
    },
  ],
  schnickSchnackColumnDefs: [
    {
      displayName: 'Site Owner',
      name: 'refObject.smSiteOwnerGroupDto.idName',
      field: 'refObject.smSiteOwnerGroupDto.idName',
    },
    {
      displayName: 'Project Owner',
      name: 'project.additionalInfo.project_details_tenant',
      field: 'project.additionalInfo.project_details_tenant',
    },
    {
      displayName: 'SiteId',
      name: 'project.refObjectDisplayName',
      field: 'project.refObjectDisplayName',
    },
    CLICKABLE_TASK_CELL,
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
      field: 'task.priority',
      width: 65,
      template:
        "{'priority-low': task.priority == 2, 'priority-medium': task.priority == 1, 'priority-high': task.priority == 0}",
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
        '<div class="p-grid-cell-contents"><span>{{grid.appScope.tagArrayToString(row.entity.project.tag)}}</span></div>',
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
        '<div class="p-grid-cell-contents"><span>{{grid.appScope.tagArrayToString(row.entity.task.candidateGroups | filter: "!Administrator")}}</span></div>',
    },
    {
      displayName: 'X',
      name: 'X',
    },
  ],
  banfColumnDefs: [
    CLICKABLE_TASK_CELL,
    { displayName: 'Type', name: 'Type', field: 'task.task_type' },
    { displayName: 'Object', name: 'Object', field: 'task.object_name' },
    { displayName: 'Title', name: 'Title', field: 'refObject.title' },
    {
      displayName: 'Site ID',
      name: 'Site ID',
      field: 'refObject.baDestination.name',
    },
    {
      displayName: 'Project ID',
      name: 'Project ID',
      field: 'refObject.project.projectId',
      sortable: false,
    },
    {
      displayName: 'Prio',
      name: 'BanfPriority',
      field: 'task.priority',
      template:
        "{'priority': task.priority != null, 'priority-low': task.priority == 2, 'priority-medium': task.priority == 1, 'priority-high': task.priority == 0}",
    },
    {
      displayName: 'Cost Center',
      name: 'Cost Center',
      field: 'refObject.costcenterName',
    },
    { displayName: 'PO', name: 'PO', field: 'refObject.poId' },
    {
      displayName: 'PO Driver',
      name: 'PO Driver',
      field: 'refObject.poDriver.name',
      sortable: false,
    },
    {
      displayName: 'Requestor',
      name: 'Requestor',
      field: 'refObject.requestorName',
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
    {
      displayName: 'Due',
      name: 'Due',
      field: 'task.due',
      type: TableColumnType.DATE,
    },
  ],
  transmissionColumnDefs: [
    CLICKABLE_TASK_CELL,
    { displayName: 'Type', name: 'Type', field: 'task.task_type' },
    { displayName: 'Object', name: 'Object', field: 'task.object_name' },
    {
      displayName: 'Site A',
      name: 'SiteA',
      field: 'refObject.stationA.site.idName',
    },
    {
      displayName: 'Site B',
      name: 'SiteB',
      field: 'refObject.stationB.site.idName',
    },
    {
      displayName: 'Project Type',
      name: 'Project Type Root',
      field: 'project.additionalInfo.rootparent.projectType',
    },
    {
      displayName: 'Prio',
      name: 'Priority',
      field: 'task.priority',
      width: 65,
      template:
        "{'priority': task.priority != null, 'priority-low': task.priority == 2, 'priority-medium': task.priority == 1, 'priority-high': task.priority == 0}",
    },
    {
      displayName: 'Reason',
      name: 'Reason',
      field: 'project.tag',
      cellTemplate:
        '<div class="p-grid-cell-contents"><span>{{grid.appScope.tagArrayToString(row.entity.project.additionalInfo.rootparent.tag)}}</span></div>',
    },
    {
      displayName: 'Parent Project Name',
      name: 'TXCluster',
      field: 'project.additionalInfo.rootparent.title',
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
    {
      displayName: 'Due',
      name: 'Due',
      field: 'task.due',
      type: TableColumnType.DATE,
    },
  ],
  txclusterColumnDefs: [
    CLICKABLE_TASK_CELL,
    { displayName: 'Type', name: 'Type', field: 'task.task_type' },
    { displayName: 'Object', name: 'Object', field: 'task.object_name' },
    {
      displayName: 'Site A',
      name: 'SiteA',
      field: 'refObject.stationA.site.idName',
    },
    {
      displayName: 'Site B',
      name: 'SiteB',
      field: 'refObject.stationB.site.idName',
    },
    {
      displayName: 'Parent Project Name',
      name: 'TXCluster',
      field: 'project.additionalInfo.rootparent.title',
    },
    {
      displayName: 'Prio',
      name: 'Priority',
      field: 'task.priority',
      width: 65,
      template:
        "{'priority': task.priority != null, 'priority-low': task.priority == 2, 'priority-medium': task.priority == 1, 'priority-high': task.priority == 0}",
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
    {
      displayName: 'Due',
      name: 'Due',
      field: 'task.due',
      type: TableColumnType.DATE,
    },
  ],
  guCivilWorksColumnDefs: [
    {
      displayName: 'Project Owner',
      name: 'project.additionalInfo.project_details_tenant',
      field: 'project.additionalInfo.project_details_tenant',
    },
    CLICKABLE_TASK_CELL,
    {
      displayName: 'Type',
      name: 'Type',
      field: 'task.task_type',
    },
    { displayName: 'Object', name: 'Object', field: 'task.object_name' },
    {
      displayName: 'Project Type',
      name: 'Project Type Sub',
      field: 'project.subtype',
    },
    {
      displayName: 'Project ID',
      name: 'Project ID',
      field: 'project.projectId',
    },
    {
      displayName: 'Project Tag',
      name: 'Project Tag',
      field: 'project.tag',
      cellTemplate:
        '<div class="p-grid-cell-contents"><span>{{grid.appScope.tagArrayToString(row.entity.project.tag)}}</span></div>',
    },
    {
      displayName: 'Prio',
      name: 'Priority',
      field: 'task.priority',
      width: 65,
      template:
        "{'priority': task.priority != null, 'priority-low': task.priority == 2, 'priority-medium': task.priority == 1, 'priority-high': task.priority == 0}",
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
    {
      displayName: 'Due',
      name: 'Due',
      field: 'task.due',
      type: TableColumnType.DATE,
    },
  ],
  tocColumnDefs: [
    {
      displayName: 'TOC',
      name: 'TOC',
      field: 'project.additionalInfo.toc',
    },
    CLICKABLE_TASK_CELL,
    { displayName: 'Object', name: 'Object', field: 'task.object_name' },
    {
      displayName: 'Cluster',
      name: 'Cluster',
      field: 'refObject.clusterIdName',
    },
    { displayName: 'Address', name: 'Address', field: 'refObject.address' },
    {
      displayName: 'Project Tag',
      name: 'Project Tag',
      field: 'project.tag',
      cellTemplate:
        '<div class="p-grid-cell-contents"><span>{{grid.appScope.tagArrayToString(row.entity.project.tag)}}</span></div>',
    },
    {
      displayName: 'Civil Works GU',
      name: 'Civil Works GU',
      field: 'project.additionalInfo.role_baugu',
    },
    {
      displayName: 'Team',
      name: 'Team',
      field: 'project.additionalInfo.toc_team_name',
    },
    {
      displayName: 'Prio',
      name: 'Priority',
      field: 'task.priority',
      width: 65,
      template:
        "{'priority': task.priority != null, 'priority-low': task.priority == 2, 'priority-medium': task.priority == 1, 'priority-high': task.priority == 0}",
    },
    {
      displayName: 'Phone',
      name: 'Phone',
      field: 'project.additionalInfo.toc_team_phone',
    },
    {
      displayName: 'Integration date',
      name: 'Integration date',
      field: 'project.additionalInfo.toc_int_date',
    },
    {
      displayName: 'Night',
      name: 'Night',
      field: 'project.additionalInfo.toc_night',
    },
    {
      displayName: 'int. window plan',
      name: 'int window plan',
      cellTemplate:
        '<toc-integration-window project="row.entity.project"></toc-integration-window>',
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
  ],
  bbuColumnDefs: [
    CLICKABLE_TASK_CELL,
    {
      displayName: 'Type',
      name: 'Type',
      field: 'task.task_type',
    },
    { displayName: 'Object', name: 'Object', field: 'task.object_name' },
    {
      displayName: 'RNC',
      name: 'RNC',
      field: 'refObject.rncId',
      sortable: false,
    },
    {
      displayName: 'RNC-BW',
      name: 'RNC-BW',
      field: 'refObject.rncBw',
      sortable: false,
    },
    {
      displayName: 'TRM-BW',
      name: 'TRM-BW',
      field: 'refObject.cacBw',
      sortable: false,
    },
    {
      displayName: 'Prio',
      name: 'Priority',
      field: 'task.priority',
      width: 65,
      template:
        "{'priority': task.priority != null, 'priority-low': task.priority == 2, 'priority-medium': task.priority == 1, 'priority-high': task.priority == 0}",
    },
    {
      displayName: 'Shaping',
      name: 'Shaping',
      field: 'refObject.shaping',
      sortable: false,
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
    {
      displayName: 'Due',
      name: 'Due',
      field: 'task.due',
      type: TableColumnType.DATE,
    },
  ],
  abbauColumnDefs: [
    {
      displayName: 'Site Owner',
      name: 'refObject.smSiteOwnerGroupDto.idName',
      field: 'refObject.smSiteOwnerGroupDto.idName',
    },
    {
      displayName: 'Project Owner',
      name: 'project.additionalInfo.project_details_tenant',
      field: 'project.additionalInfo.project_details_tenant',
    },
    {
      displayName: 'SiteId',
      name: 'project.refObjectDisplayName',
      field: 'project.refObjectDisplayName',
    },
    CLICKABLE_TASK_CELL,
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
    {
      displayName: 'Project ID',
      name: 'Project ID',
      field: 'project.projectId',
    },
    {
      displayName: 'Project Type',
      name: 'Project Type',
      field: 'project.pmcProjectType.name',
    },
    {
      displayName: 'Project Tag',
      name: 'Project Tag',
      field: 'project.tag',
      cellTemplate:
        '<div class="p-grid-cell-contents"><span>{{grid.appScope.tagArrayToString(row.entity.project.tag)}}</span></div>',
    },
    {
      displayName: 'Prio',
      name: 'Priority',
      field: 'task.priority',
      width: 65,
      template:
        "{'priority': task.priority != null, 'priority-low': task.priority == 2, 'priority-medium': task.priority == 1, 'priority-high': task.priority == 0}",
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
    {
      displayName: 'Due',
      name: 'Due',
      field: 'task.due',
      type: TableColumnType.DATE,
    },
    {
      displayName: 'Change ID',
      name: 'Change ID',
      field: 'project.additionalInfo.changeId',
    },
  ],
  requestColumnDefs: [
    CLICKABLE_TASK_CELL,
    {
      displayName: 'Customer',
      name: 'Customer',
      field: 'project.additionalInfo.companyName',
    },
    {
      displayName: 'Location A <-> Location B',
      name: 'RefObject IdName',
      field: 'task.object_name',
    },
    {
      displayName: 'Prio',
      name: 'Priority',
      field: 'task.priority',
      width: 65,
      template:
        "{'priority': task.priority != null, 'priority-low': task.priority == 2, 'priority-medium': task.priority == 1, 'priority-high': task.priority == 0}",
    },
    {
      displayName: 'Assignee',
      name: 'Assignee',
      field: 'task.assignee',
    },
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
  ],
  bbuUpgradeColumnDefs: [
    CLICKABLE_TASK_CELL,
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
    {
      displayName: 'Work Details',
      name: 'Work Details',
      field: 'task.comment',
    },
    {
      displayName: 'Prio',
      name: 'Priority',
      field: 'task.priority',
      width: 65,
      template:
        "{'priority': task.priority != null, 'priority-low': task.priority == 2, 'priority-medium': task.priority == 1, 'priority-high': task.priority == 0}",
    },
    {
      displayName: 'Due CW',
      name: 'Due CW',
      field: 'project.additionalInfo.dueDateCw',
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
  ],
  offerColumnDefs: [
    CLICKABLE_TASK_CELL,
    { displayName: 'Type', name: 'Type', field: 'task.task_type' },
    { displayName: 'Object', name: 'Object', field: 'task.object_name' },
    { displayName: 'Title', name: 'Title', field: 'refObject.title' },
    {
      displayName: 'Site ID',
      name: 'Site ID',
      field: 'refObject.baDestination.name',
    },
    {
      displayName: 'Project ID',
      name: 'Project ID',
      field: 'refObject.project.projectId',
      sortable: false,
    },
    {
      displayName: 'Prio',
      name: 'BanfPriority',
      field: 'task.priority',
      template:
        "{'priority': task.priority != null, 'priority-low': task.priority == 2, 'priority-medium': task.priority == 1, 'priority-high': task.priority == 0}",
    },
    {
      displayName: 'Cost Center',
      name: 'Cost Center',
      field: 'refObject.costcenterName',
    },
    { displayName: 'PO', name: 'PO', field: 'refObject.poId' },
    {
      displayName: 'PO Driver',
      name: 'PO Driver',
      field: 'refObject.poDriver.name',
      sortable: false,
    },
    {
      displayName: 'Requestor',
      name: 'Requestor',
      field: 'refObject.requestorName',
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
    {
      displayName: 'Due',
      name: 'Due',
      field: 'task.due',
      type: TableColumnType.DATE,
    },
  ],
};

export const TASK_LAYOUTS = [
  {
    name: 'default',
    displayName: 'Site',
    columnDefs: TASK_COLUMNS_DEFS.defaultColumnDefs,
  },
  {
    name: 'admin',
    displayName: 'Admin',
    columnDefs: TASK_COLUMNS_DEFS.adminColumnDefs,
  },
  {
    name: 'banf',
    displayName: 'BANF',
    columnDefs: TASK_COLUMNS_DEFS.banfColumnDefs,
  },
  {
    name: 'bbm',
    displayName: 'BBM',
    columnDefs: TASK_COLUMNS_DEFS.bbmColumnDefs,
  },
  {
    name: 'schnickSchnack',
    displayName: 'Schnick Schnack Schabernak',
    columnDefs: TASK_COLUMNS_DEFS.schnickSchnackColumnDefs,
  },
  {
    name: 'transmission',
    displayName: 'Transmission',
    columnDefs: TASK_COLUMNS_DEFS.transmissionColumnDefs,
  },
  {
    name: 'txcluster',
    displayName: 'TX Cluster',
    columnDefs: TASK_COLUMNS_DEFS.txclusterColumnDefs,
    filterOptions: {
      'project.additionalInfo.rootparent.projectTypeIdName': {
        value: 'TX-CLUSTER-ORCHESTRATION',
        fieldType: 'raw',
      },
    },
  },
  {
    name: 'txsinglelink',
    displayName: 'TX Single Link',
    columnDefs: TASK_COLUMNS_DEFS.transmissionColumnDefs,
    filterOptions: {
      'project.additionalInfo.rootparent.projectTypeIdName': {
        value: '!TX-CLUSTER-ORCHESTRATION',
        fieldType: 'raw',
      },
    },
  },
  {
    name: 'guCivilWorks',
    displayName: 'GU - Civil Works',
    columnDefs: TASK_COLUMNS_DEFS.guCivilWorksColumnDefs,
  },
  {
    name: 'toc',
    displayName: 'TOC Site',
    columnDefs: TASK_COLUMNS_DEFS.tocColumnDefs,
  },
  {
    name: 'bbu',
    displayName: 'BBU',
    columnDefs: TASK_COLUMNS_DEFS.bbuColumnDefs,
  },
  {
    name: 'abbau',
    displayName: 'Abbau',
    columnDefs: TASK_COLUMNS_DEFS.abbauColumnDefs,
  },
  {
    name: 'request',
    displayName: 'Request',
    columnDefs: TASK_COLUMNS_DEFS.requestColumnDefs,
  },
  {
    name: 'bbuUpgrade',
    displayName: 'BBU Capacity Upgrade',
    columnDefs: TASK_COLUMNS_DEFS.bbuUpgradeColumnDefs,
  },
  {
    name: 'offer',
    displayName: 'Offer',
    columnDefs: TASK_COLUMNS_DEFS.offerColumnDefs,
    filterOptions: {
      'task.process_definition_key': {
        value: 'purchase-to-pay',
        fieldType: 'raw',
      },
    },
  },
];
