import { Injectable } from "@angular/core";
import { AuthorizationServiceNg } from "./authorization.service";
import { PmcUser } from "../../models/pmcUser";

@Injectable({
    providedIn: 'root'
})
export class TableAPIServiceNg {


    constructor(private authorizationService: AuthorizationServiceNg){};

    public getProjectMonitorLayouts(): any {
        /* if(this.authorizationService.isAuthorized('monitor:read:neubau-umbau-details')){
          this.projectMonitorLayouts.umbau.columnDefs.push(...[ 
            {
              displayName:'Details', 
              name: 'Details', 
              field: 'project.details'
            },
             {
              displayName:'Edit Details',  //TODO: check im monitor html ob es sich um das Feld Edit Details handelt und dann entsprechend buttons einfügen
              name: 'YAction', 
              field: ''
            },
          ]);
        } */
        return this.projectMonitorLayouts;
    }

    public getTaskListLayouts(user: PmcUser): any {
      let layouts: any[] = Object.keys(this.taskLayouts).map(key => this.taskLayouts[key])
      layouts = layouts.filter((layout) =>{
        if(this.authorizationService.hasPmcRole('Admin', user)){
          return layout;
        } else {
          return this.authorizationService.isAuthorized('task:layout:' + layout.name, null, user);
        }
      }); 
      layouts.push(this.taskLayouts.default);
      return layouts;
  }

    projectMonitorColumnDefs = {
        allColumnDefs : [
        {displayName:'Project ID', name: 'Project ID', field: 'project.projectId'},
        {displayName:'Title', name: 'Title', field: 'project.title'},
        {displayName:'Project Type', name: 'Project Type', field: 'project.pmcProjectType.name'},
        {displayName:'Sub Type', name: 'Sub Type', field: 'project.subtype'},
        {displayName:'RefObjectId', name: 'RefObjectId', field: 'project.refObjectIdName'},
        {displayName:'Start Date', name: 'startDate', field: 'project.createDate', width: 90},
        {displayName:'Due Date', name: 'Due Date', field: 'project.dueDate', width: 90},
        {displayName:'Prio', name: 'Prio', field: 'project.priority', width: '40', template: `{'ng-prio': true, 'ng-prio-low': project.priority == 0, 'ng-prio-medium': project.priority == 1, 'ng-prio-high': project.priority == 2}`},
        {displayName:'Tag', name: 'Tag', field: 'project.tag'},
        {displayName:'Status', name: 'Status', field: 'project.status', width: 110}
        ],
        /* neubauUmbauColumnDefs : [
        {displayName:'Project ID', name: 'Project ID', field: 'project.projectId'},
        {displayName:'Sub Type', name: 'Sub Type', field: 'project.subtype'},
        {displayName:'Reason', name: 'Reason', field: 'project.tag', cellTemplate: '<div class="ui-grid-cell-contents"><span>{{grid.appScope.tagArrayToString(row.entity.project.tag)}}</span></div>'},
        {displayName:'Prio', name: 'Prio', field: 'project.priority',  width: '*', maxWidth: 40, template: `{'ng-prio': true, 'ng-prio-low': project.priority == 0, 'ng-prio-medium': project.priority == 1, 'ng-prio-high': project.priority == 2}` },
        {displayName:'Status', name: 'Status', field: 'project.status', width: 60},
        {displayName:'Start Date', name: 'startDate', field: 'project.createDate', width: 90},
        {displayName:'Due Date', name: 'dueDate', field: 'project.dueDate', width: 90},
        {displayName:'Site', name: 'RefObjectId', field: 'project.refObjectIdName'},
        {displayName:'Akqui GU', name: 'Akqui GU', field: 'contacts.akqui.fullName'},
        {displayName:'Bau GU', name: 'Bau GU', field: 'contacts.baugu.fullName'}
        ],
        scheduledUmbauColumnDefs : [
        {displayName:'Project ID', name: 'Project ID', field: 'project.projectId'},
        {displayName:'Sub Type', name: 'Sub Type', field: 'project.subtype'},
        {displayName:'Reason', name: 'Reason', field: 'project.tag', cellTemplate: '<div class="ui-grid-cell-contents"><span>{{grid.appScope.tagArrayToString(row.entity.project.tag)}}</span></div>'},
        {displayName:'Prio', name: 'Prio', field: 'project.priority',  width: '*', maxWidth: 40,  template: `{'ng-prio': true, 'ng-prio-low': project.priority == 0, 'ng-prio-medium': project.priority == 1, 'ng-prio-high': project.priority == 2}`},
        {displayName:'Scheduled DateDate', name: 'scheduledDate', field: 'project.milestonesMap.PROJECT_START_SCHEDULED.act', width: 90},
        {displayName:'Due Date', name: 'dueDate', field: 'project.dueDate', width: 90},
        {displayName:'Site', name: 'RefObjectId', field: 'project.refObjectIdName'},
        {displayName:'Akqui GU', name: 'Akqui GU', field: 'contacts.akqui.fullName'},
        {displayName:'Bau GU', name: 'Bau GU', field: 'contacts.baugu.fullName'}
        ],
        txClusterColumnDefs : [
        {displayName:'Project ID', name: 'Project ID', field: 'project.projectId'},
        {displayName:'Project Name', name: 'Project Name', field: 'project.title'},
        {displayName:'Links', name: 'Links', field: ''},
        {displayName:'Created', name: 'Created', field: 'project.createDate'},
        {displayName:'Assignee', name: 'Assignee', field: 'project.assignee'},
        {displayName:'Status', name: 'Status', field: 'project.status'}
        ],
        txSiteColumnDefs : [
        {displayName:'Project ID', name: 'Project ID', field: 'project.projectId'},
        {displayName:'Cluster Project Name', name: 'Cluster Project Name', field: 'project.additionalInfo.txcluster.title'},
        {displayName:'Project Name', name: 'Project Name', field: 'project.title'},
        {displayName:'Site ID', name: 'Site ID', field: 'project.refObjectIdName'},
        {displayName:'Created', name: 'Created', field: 'project.createDate'},
        {displayName:'Assignee', name: 'Assignee', field: 'project.assignee'},
        {displayName:'Status', name: 'Status', field: 'project.status'}
        ],
        txLinkColumnDefs : [
        {displayName:'Project ID', name: 'Project ID', field: 'project.projectId'},
        {displayName:'Cluster Project Name', name: 'Cluster Project Name', field: 'project.additionalInfo.txcluster.title'},
        {displayName:'Project Name', name: 'Project Name', field: 'project.title'},
        {displayName:'Link ID', name: 'Link ID', field: 'project.refObjectIdName'},
        {displayName:'Created', name: 'Created', field: 'project.createDate'},
        {displayName:'Assignee', name: 'Assignee', field: 'project.assignee'},
        {displayName:'Status', name: 'Status', field: 'project.status'}
        ],
        eventColumnDefs : [
        {displayName:'Project ID', name: 'Project ID', field: 'project.projectId'},
        {displayName:'Title', name: 'Title', field: 'project.title'},
        {displayName:'Project Type', name: 'Project Type', field: 'project.pmcProjectType.name'},
        {displayName:'RefObjectId', name: 'RefObjectId', field: 'project.refObjectIdName'},
        {displayName:'Start Date', name: 'startDate', field: 'project.createDate', width: 90},
        {displayName:'Due Date', name: 'Due Date', field: 'project.dueDate', width: 90},
        {displayName:'Prio', name: 'Prio', field: 'project.priority',  width: '*', maxWidth: 40,  template: `{'ng-prio': true, 'ng-prio-low': project.priority == 0, 'ng-prio-medium': project.priority == 1, 'ng-prio-high': project.priority == 2}`},
        {displayName:'Status', name: 'Status', field: 'project.status', width: 110}
        ],
        abbauColumnDefs : [
        {displayName:'Project ID', name: 'Project ID', field: 'project.projectId'},
        {displayName:'Reason', name: 'Reason', field: 'project.tag', cellTemplate: '<div class="ui-grid-cell-contents"><span>{{grid.appScope.tagArrayToString(row.entity.project.tag)}}</span></div>'},
        {displayName:'Prio', name: 'Prio', field: 'project.priority',  width: '*', maxWidth: 40, template: `{'ng-prio': true, 'ng-prio-low': project.priority == 0, 'ng-prio-medium': project.priority == 1, 'ng-prio-high': project.priority == 2}` },
        {displayName:'Status', name: 'Status', field: 'project.status', width: 110},
        {displayName:'Project Start', name: 'startDate', field: 'project.createDate', width: 90},
        {displayName:'Project Due', name: 'Due Date', field: 'project.dueDate', width: 90},
        {displayName:'RefObjectId', name: 'RefObjectId', field: 'project.refObjectIdName'},
        {displayName:'Abbau GU', name: 'Abbau GU', field: 'project.additionalInfo.role_baugu'},
        {displayName:'Abbau Start', name: 'Abbau Start', field: 'project.additionalInfo.requestedStartDateStr'},
        {displayName:'Abbau End', name: 'Abbau End', field: 'project.additionalInfo.requestedEndDateStr'},
        {displayName:'Change ID', name: 'Change ID', field: 'project.additionalInfo.changeId'}
        ],
        requestColumnDefs : [
        {displayName:'Request ID', name: 'Request ID', field: 'project.projectId', width: 130},
        {displayName:'Status', name: 'Status', field: 'project.status', width: 130},
        {displayName:'Customer', name: 'Customer', field: 'project.additionalInfo.companyName', width: 350},
        {displayName:'Location A', name: 'Location A', field: 'refObject.locAAddress'},
        {displayName:'Location B', name: 'Location B', field: 'refObject.locBAddress'},
        {displayName:'Request Date', name: 'Request Date', field: 'project.createDate', width: 130}
        ] */
    }

    projectMonitorLayouts = {
        all: {
          name:'all', displayName: 'Alle',
          filterOptions: {},
          columnDefs: this.projectMonitorColumnDefs.allColumnDefs},
        /* umbau: {
          name:'umbau', displayName: 'Umbau / Neubau',
          filterOptions: {"project.pmcProjectType.idName.raw" : [ "UMBAU","NB-SI"]},
          columnDefs: this.projectMonitorColumnDefs.neubauUmbauColumnDefs},
        scheduled_umbau: {
          name:'scheduled_umbau', displayName: 'Umbau (Scheduled)',
          filterOptions: {"project.status.raw" : [ "SCHEDULED"]},
          columnDefs: this.projectMonitorColumnDefs.scheduledUmbauColumnDefs},
        txcluster: {
          name:'txcluster', displayName: 'TX Cluster',
          filterOptions: {"project.pmcProjectType.idName" : {value: "TX-CLUSTER-ORCHESTRATION", fieldType: "raw"}},
          columnDefs: this.projectMonitorColumnDefs.txClusterColumnDefs},
        txsite: {
          name:'txsite', displayName: 'TX Site',
          filterOptions: {"project.pmcProjectType.idName" : {value: "TX-SITE-ORCHESTRATION", fieldType: "raw"}},
          columnDefs: this.projectMonitorColumnDefs.txSiteColumnDefs},
        txlink: {
          name:'txlink',displayName: 'TX Link',
          filterOptions: {"project.pmcProjectType.idName" : {value: "TX-LINK-ORCHESTRATION", fieldType: "raw"}},
          columnDefs: this.projectMonitorColumnDefs.txLinkColumnDefs},
        event: {
          name:'event',displayName: 'Event',
          filterOptions: {"project.pmcProjectType.idName" : {value: "EVENT-EVENT", fieldType: "raw"}},
          columnDefs: this.projectMonitorColumnDefs.eventColumnDefs},
        refarming: {
          name:'refarming',displayName: 'Refarming',
          filterOptions: {"project.pmcProjectType.idName" : {value: "REFARMING-ORCHESTRATION", fieldType: "raw"}},
          columnDefs: this.projectMonitorColumnDefs.allColumnDefs},
        abbau: {
          name:'abbau',displayName: 'Abbau',
          filterOptions: {"project.pmcProjectType.idName" : {value: "ABBAU", fieldType: "raw"}},
          columnDefs: this.projectMonitorColumnDefs.abbauColumnDefs},
        request: {
          name:'request',displayName: 'Request',
          filterOptions: {"project.pmcProjectType.idName" : {value: "REQUEST", fieldType: "raw"}},
          columnDefs: this.projectMonitorColumnDefs.requestColumnDefs} */
      }

      clickableTaskCell = {
        displayName:'Task Name',
        name: 'Task Name',
        field: 'task.task_name'
      }

      taskColumnDefs = {
        defaultColumnDefs : [
          this.clickableTaskCell,
          {displayName:'Type', name: 'Type', width: 50, field: 'task.task_type'},
          {displayName:'Object', name: 'Object', field: 'task.object_name'},
          {displayName:'Cluster', name: 'Cluster', field: 'refObject.clusterIdName'},
          {displayName:'Address', name: 'Address', field: 'refObject.address'},
          {displayName:'Project ID', name: 'Project ID', field: 'project.projectId'},
          {displayName:'Prio', name: 'Priority', field: 'project.priority', width: 70, template: `{'ng-prio': project != null, 'ng-prio-low': project != null && project.priority == 0, 'ng-prio-medium': project != null && project.priority == 1, 'ng-prio-high': project != null && project.priority == 2}`},
          {displayName:'Project Tag', name: 'Project Tag', field: 'project.tag',cellTemplate: '<div class="ui-grid-cell-contents">{{grid.appScope.tagArrayToString(row.entity.project.tag)}}</div>'},
          {displayName:'Assignee', name: 'Assignee', field: 'task.assignee'},
          {displayName:'Action',name: 'Action', width: 120},
          {displayName:'Created', name: 'Created', field: 'task.created', width: 90},
          {displayName:'Due', name: 'Due', field: 'task.due', width: 90}
        ],
        adminColumnDefs : [
          this.clickableTaskCell,
          {displayName:'Type', name: 'Type', width: 80, field: 'task.task_type'},
          {displayName:'Object', name: 'Object', field: 'task.object_name'},
          {displayName:'Cluster', name: 'Cluster', field: 'refObject.clusterIdName'},
          {displayName:'Address', name: 'Address', field: 'refObject.address'},
          {displayName:'Project ID', name: 'Project ID', field: 'project.projectId'},
          {displayName:'Prio', name: 'Priority', field: 'project.priority', width: 70, template: `{'ng-prio': project != null, 'ng-prio-low': project != null && project.priority == 0, 'ng-prio-medium': project != null && project.priority == 1, 'ng-prio-high': project != null && project.priority == 2}` },
          {displayName:'Project Tag', name: 'Project Tag', field: 'project.tag',cellTemplate: '<div class="ui-grid-cell-contents">{{grid.appScope.tagArrayToString(row.entity.project.tag)}}</div>'},
          {displayName:'Assignee', name: 'Assignee', field: 'task.assignee'},
          {displayName:'Action',name: 'Action', width: 120},
          {displayName:'Created', name: 'Created', field: 'task.created', width: 100},
          {displayName:'Due', name: 'Due', field: 'task.due', width: 90},
          {displayName:'Candidate Group', name: 'Candidate Group', field: 'task.candidateGroups', filter: '!Administrator', width: 180}
        ],
        /* bbmColumnDefs : [
          this.clickableTaskCell,
          {displayName:'Type', name: 'Type', width: 50, field: 'task.task_type'},
          {displayName:'Object', name: 'Object', field: 'task.object_name'},
          {displayName:'Cable', name: 'Cable', field: 'refObject.cableName'},
          {displayName:'Link', name: 'Link', field: 'refObject.linkName'},
          {displayName:'Chain', name: 'Chain', field: 'refObject.chainName'},
          {displayName:'Project', name: 'Project', field: 'project.projectId'},
          {displayName:'Prio', name: 'Priority', field: 'project.priority', width: 70, template: `{'ng-prio': project != null, 'ng-prio-low': project != null && project.priority == 0, 'ng-prio-medium': project != null && project.priority == 1, 'ng-prio-high': project != null && project.priority == 2}` },
          {displayName:'Assignee', name: 'Assignee', field: 'task.assignee'},
          {displayName:'Action',name: 'Action', width: 120},
          {displayName:'Created', name: 'Created', field: 'task.created'},
          {displayName:'Due', name: 'Due', field: 'task.due'}
        ],
        bbmUmbauColumnDefs : [
          this.clickableTaskCell,
          {displayName:'Type', name: 'Type', width: 50, field: 'task.task_type'},
          {displayName:'Object', name: 'Object', field: 'task.object_name'},
          {displayName:'Project', name: 'Project', field: 'project.projectId'},
          {displayName:'Prio', name: 'Priority', field: 'project.priority', width: 70, template: `{'ng-prio': project != null, 'ng-prio-low': project != null && project.priority == 0, 'ng-prio-medium': project != null && project.priority == 1, 'ng-prio-high': project != null && project.priority == 2}` },
          {displayName:'Project Due', name: 'Project Due', field: 'project.dueDate'},
          {displayName:'Project Tag', name: 'Project Tag', field: 'project.tag',cellTemplate: '<div class="ui-grid-cell-contents"><span>{{grid.appScope.tagArrayToString(row.entity.project.tag)}}</span></div>'},
          {displayName:'Assignee', name: 'Assignee', field: 'task.assignee'},
          {displayName:'Action',name: 'Action', width: 120},
          {displayName:'Created', name: 'Created', field: 'task.created'},
          {displayName:'Details', name: 'Details', field: 'project.details'},
          {displayName: 'Responsible Groups', name: 'Responsible Groups', field: "task.candidateGroups", cellTemplate: '<div class="ui-grid-cell-contents"><span>{{grid.appScope.tagArrayToString(row.entity.task.candidateGroups | filter: "!Administrator")}}</span></div>', width: 180},
          {displayName:'X', name: 'X', cellTemplate: '<div class="ui-grid-cell-contents"><span ng-click="grid.appScope.editDetails(row)"><i class="fa fa-edit"></i></span></div>', enableCellEdit: false, width: 50,enableSorting:false,enableFiltering: false}
        ],
        banfColumnDefs : [
          this.clickableTaskCell,
          {displayName:'Type', name: 'Type', field: 'task.task_type'},
          {displayName:'Object', name: 'Object', field: 'task.object_name'},
          {displayName:'Title', name: 'Title', field: 'refObject.title'},
          {displayName:'Prio', name: 'BanfPriority', field: 'refObject.priority', width: 70, template: `{'ng-prio': project != null, 'ng-prio-low': project != null && project.priority == 0, 'ng-prio-medium': project != null && project.priority == 1, 'ng-prio-high': project != null && project.priority == 2}` },
          {displayName:'Cost Center', name: 'Cost Center', field: 'refObject.costcenterName'},
          {displayName:'PO', name: 'PO', field: 'refObject.poId'},
          {displayName:'PO Driver', name: 'PO Driver', field: 'refObject.poDriver.name'},
          {displayName:'Requestor', name: 'Requestor', field: 'refObject.requestorName'},
          {displayName:'Assignee', name: 'Assignee', field: 'task.assignee'},
          {displayName:'Action',name: 'Action', width: 120},
          {displayName:'Created', name: 'Created', field: 'task.created'},
          {displayName:'Due', name: 'Due', field: 'task.due'}
        ],
        transmissionColumnDefs : [
          this.clickableTaskCell,
          {displayName:'Type', name: 'Type', field: 'task.task_type'},
          {displayName:'Object', name: 'Object', field: 'task.object_name'},
          {displayName:'Site A', name: 'SiteA', field: 'refObject.stationA.site.idName'},
          {displayName:'Site B', name: 'SiteB', field: 'refObject.stationB.site.idName'},
          {displayName:'Project Type', name: 'Project Type Root', field: 'project.additionalInfo.rootparent.projectType'},
          {displayName:'Prio', name: 'Priority', field: 'project.priority', width: 70, template: `{'ng-prio': project != null, 'ng-prio-low': project != null && project.priority == 0, 'ng-prio-medium': project != null && project.priority == 1, 'ng-prio-high': project != null && project.priority == 2}` },
          {displayName:'Reason', name: 'Reason', field: 'project.tag',cellTemplate: '<div class="ui-grid-cell-contents"><span>{{grid.appScope.tagArrayToString(row.entity.project.additionalInfo.rootparent.tag)}}</span></div>'},
          {displayName:'Parent Project Name', name: 'TXCluster', field: 'project.additionalInfo.rootparent.title'},
          {displayName:'Assignee', name: 'Assignee', field: 'task.assignee'},
          {displayName:'Action',name: 'Action', width: 120},
          {displayName:'Created', name: 'Created', field: 'task.created'},
          {displayName:'Due', name: 'Due', field: 'task.due'}
        ],
        txclusterColumnDefs : [
          this.clickableTaskCell,
          {displayName:'Type', name: 'Type', field: 'task.task_type'},
          {displayName:'Object', name: 'Object', field: 'task.object_name'},
          {displayName:'Site A', name: 'SiteA', field: 'refObject.stationA.site.idName'},
          {displayName:'Site B', name: 'SiteB', field: 'refObject.stationB.site.idName'},
          {displayName:'Parent Project Name', name: 'TXCluster', field: 'project.additionalInfo.rootparent.title'},
          {displayName:'Prio', name: 'Priority', field: 'project.priority', width: 70, template: `{'ng-prio': project != null, 'ng-prio-low': project != null && project.priority == 0, 'ng-prio-medium': project != null && project.priority == 1, 'ng-prio-high': project != null && project.priority == 2}` },
          {displayName:'Assignee', name: 'Assignee', field: 'task.assignee'},
          {displayName:'Action',name: 'Action', width: 120},
          {displayName:'Created', name: 'Created', field: 'task.created'},
          {displayName:'Due', name: 'Due', field: 'task.due'}
        ],
        guCivilWorksColumnDefs : [
          this.clickableTaskCell,
          {displayName:'Type', name: 'Type', width: 50, field: 'task.task_type'},
          {displayName:'Object', name: 'Object', field: 'task.object_name'},
          {displayName:'Project Type', name: 'Project Type Sub', field: 'project.subtype'},
          {displayName:'Project ID', name: 'Project ID', field: 'project.projectId'},
          {displayName:'Project Tag', name: 'Project Tag', field: 'project.tag',cellTemplate: '<div class="ui-grid-cell-contents"><span>{{grid.appScope.tagArrayToString(row.entity.project.tag)}}</span></div>'},
          {displayName:'Prio', name: 'Priority', field: 'project.priority', width: 70, template: `{'ng-prio': project != null, 'ng-prio-low': project != null && project.priority == 0, 'ng-prio-medium': project != null && project.priority == 1, 'ng-prio-high': project != null && project.priority == 2}` },        
          {displayName:'Assignee', name: 'Assignee', field: 'task.assignee'},
          {displayName:'Action',name: 'Action', width: 120},
          {displayName:'Created', name: 'Created', field: 'task.created'},
          {displayName:'Due', name: 'Due', field: 'task.due'}
        ],
        tocColumnDefs : [
          {displayName:'TOC', name: 'TOC', field: 'project.additionalInfo.toc'},
          this.clickableTaskCell,
          {displayName:'Object', name: 'Object', field: 'task.object_name'},
          {displayName:'Cluster', name: 'Cluster', field: 'refObject.clusterIdName'},
          {displayName:'Address', name: 'Address', field: 'refObject.address'},
          {displayName:'Project Tag', name: 'Project Tag', field: 'project.tag',cellTemplate: '<div class="ui-grid-cell-contents"><span>{{grid.appScope.tagArrayToString(row.entity.project.tag)}}</span></div>'},
          {displayName:'Civil Works GU', name: 'Civil Works GU', field: 'project.additionalInfo.role_baugu'},
          {displayName:'Team', name: 'Team', field: 'project.additionalInfo.toc_team_name'},
          {displayName:'Prio', name: 'Priority', field: 'project.priority', width: 70, template: `{'ng-prio': project != null, 'ng-prio-low': project != null && project.priority == 0, 'ng-prio-medium': project != null && project.priority == 1, 'ng-prio-high': project != null && project.priority == 2}` },
          {displayName:'Phone', name: 'Phone', field: 'project.additionalInfo.toc_team_phone'},
          {displayName:'Integration date',name: 'Integration date', field: 'project.additionalInfo.toc_int_date'},
          {displayName:'Night', name: 'Night', field: 'project.additionalInfo.toc_night'},
          {displayName:'int. window plan', name: 'int window plan', cellTemplate: '<toc-integration-window project="row.entity.project"></toc-integration-window>'},
          {displayName:'Assignee', name: 'Assignee', field: 'task.assignee'},
          {displayName:'Action',name: 'Action', width: 120},
          {displayName:'Created', name: 'Created', field: 'task.created', width: 90}
        ],
        bbuColumnDefs : [
          this.clickableTaskCell,
          {displayName:'Type', name: 'Type', width: 50, field: 'task.task_type'},
          {displayName:'Object', name: 'Object', field: 'task.object_name'},
          {displayName:'RNC', name: 'RNC', field: 'refObject.rncId'},
          {displayName:'RNC-BW', name: 'RNC-BW', field: 'refObject.rncBw'},
          {displayName:'TRM-BW', name: 'TRM-BW', field: 'refObject.cacBw'},
          {displayName:'Prio', name: 'Priority', field: 'project.priority', width: 70, template: `{'ng-prio': project != null, 'ng-prio-low': project != null && project.priority == 0, 'ng-prio-medium': project != null && project.priority == 1, 'ng-prio-high': project != null && project.priority == 2}` },
          {displayName:'Shaping', name: 'Shaping', field: 'refObject.shaping'},
          {displayName:'Assignee', name: 'Assignee', field: 'task.assignee'},
          {displayName:'Action',name: 'Action', width: 120},
          {displayName:'Created', name: 'Created', field: 'task.created', width: 90},
          {displayName:'Due', name: 'Due', field: 'task.due'}
        ],
        abbauColumnDefs : [
          this.clickableTaskCell,
          {displayName:'Type', name: 'Type', width: 50, field: 'task.task_type'},
          {displayName:'Object', name: 'Object', field: 'task.object_name'},
          {displayName:'Cluster', name: 'Cluster', field: 'refObject.clusterIdName'},
          {displayName:'Project ID', name: 'Project ID', field: 'project.projectId'},
          {displayName:'Project Type', name: 'Project Type', field: 'project.pmcProjectType.name'},
          {displayName:'Project Tag', name: 'Project Tag', field: 'project.tag',cellTemplate: '<div class="ui-grid-cell-contents"><span>{{grid.appScope.tagArrayToString(row.entity.project.tag)}}</span></div>'},
          {displayName:'Prio', name: 'Priority', field: 'project.priority', width: 70, template: `{'ng-prio': project != null, 'ng-prio-low': project != null && project.priority == 0, 'ng-prio-medium': project != null && project.priority == 1, 'ng-prio-high': project != null && project.priority == 2}` },        
          {displayName:'Assignee', name: 'Assignee', field: 'task.assignee'},
          {displayName:'Action',name: 'Action', width: 120},
          {displayName:'Created', name: 'Created', field: 'task.created', width: 90},
          {displayName:'Due', name: 'Due', field: 'task.due'},
          {displayName:'Change ID', name: 'Change ID', field: 'project.additionalInfo.changeId'}
        ],
        requestColumnDefs : [
          this.clickableTaskCell,
          {displayName:'Customer', name: 'Customer', field: 'task.object_name', width: 350},
          {displayName:'Customer Address', name: 'Address', field: 'refObject.companyAddress', width: 400},
          {displayName:'LRT Reference', name: 'LRT Reference', field: 'project.title', width: 230},
          {displayName:'Prio', name: 'Priority', field: 'project.priority', width: 70, template: `{'ng-prio': project != null, 'ng-prio-low': project != null && project.priority == 0, 'ng-prio-medium': project != null && project.priority == 1, 'ng-prio-high': project != null && project.priority == 2}` },
          {displayName:'Assignee', name: 'Assignee', field: 'task.assignee', width: 200},
          {displayName:'Action',name: 'Action', width: 120},
          {displayName:'Created', name: 'Created', field: 'task.created', width: 130}
        ],
        bbuUpgradeColumnDefs : [
          this.clickableTaskCell,
          {displayName:'Type', name: 'Type', width: 50, field: 'task.task_type'},
          {displayName:'Object', name: 'Object', field: 'task.object_name'},
          {displayName:'Cluster', name: 'Cluster', field: 'refObject.clusterIdName'},
          {displayName:'Work Details', name: 'Work Details', field: 'task.comment'},
          {displayName:'Prio', name: 'Priority', field: 'project.priority', width: 70, template: `{'ng-prio': project != null, 'ng-prio-low': project != null && project.priority == 0, 'ng-prio-medium': project != null && project.priority == 1, 'ng-prio-high': project != null && project.priority == 2}` },
          {displayName:'Due CW', name: 'Due CW', field: 'project.additionalInfo.dueDateCw', width: 90},
          {displayName:'Assignee', name: 'Assignee', field: 'task.assignee'},
          {displayName:'Action',name: 'Action', width: 120},
          {displayName:'Created', name: 'Created', field: 'task.created', width: 90}
        ] */
      }
  
      taskLayouts={
        default: {name: 'default',displayName:'Site',columnDefs: this.taskColumnDefs.defaultColumnDefs},
        admin: {name: 'admin', displayName:'Admin',columnDefs: this.taskColumnDefs.adminColumnDefs},
        /* banf: {name: 'banf', displayName:'BANF',columnDefs: this.taskColumnDefs.banfColumnDefs},
        bbm: {name: 'bbm', displayName:'BBM',columnDefs: this.taskColumnDefs.bbmColumnDefs},
        bbmumbau: {name: 'bbmumbau', displayName:'Schnick Schnack Schabernak',columnDefs: this.taskColumnDefs.bbmUmbauColumnDefs},
        transmission: {name: 'transmission', displayName:'Transmission' ,columnDefs: this.taskColumnDefs.transmissionColumnDefs},
        txcluster: {
          name: 'txcluster', displayName:'TX Cluster' ,columnDefs: this.taskColumnDefs.txclusterColumnDefs,
          filterOptions: {"project.additionalInfo.rootparent.projectTypeIdName" : {value: "TX-CLUSTER-ORCHESTRATION", fieldType: "raw"}}
          },
        txsinglelink: {
          name: 'txsinglelink', displayName:'TX Single Link' ,columnDefs: this.taskColumnDefs.transmissionColumnDefs,
          filterOptions: {"project.additionalInfo.rootparent.projectTypeIdName" : {value: "!TX-CLUSTER-ORCHESTRATION", fieldType: "raw"}}
        },
        guCivilWorks: {name: 'guCivilWorks', displayName: 'GU - Civil Works', columnDefs: this.taskColumnDefs.guCivilWorksColumnDefs},
        toc: {name: 'toc', displayName: 'TOC Site', columnDefs: this.taskColumnDefs.tocColumnDefs},
        bbu: {name: 'bbu', displayName: 'BBU', columnDefs: this.taskColumnDefs.bbuColumnDefs},
        abbau: {name: 'abbau', displayName: 'Abbau', columnDefs: this.taskColumnDefs.abbauColumnDefs},
        request: {name: 'request', displayName: 'Request', columnDefs: this.taskColumnDefs.requestColumnDefs},
        bbuUpgrade: {name: 'bbuUpgrade', displayName: 'BBU Capacity Upgrade', columnDefs: this.taskColumnDefs.bbuUpgradeColumnDefs} */
      }
}


