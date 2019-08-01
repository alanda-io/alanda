import { Component, OnInit, Input } from '@angular/core';
import { RefObjectService } from '../../services/rest/refobject.service';
import { MessageService } from 'primeng/components/common/messageservice';
import { DocumentServiceNg } from '../../services/rest/document.service';
import { ExtendedTreeNode } from './shared/tree-node.model';
import { SimpleDocument } from './shared/simple-document.model';
@Component({
  selector: 'attachments-component',
  templateUrl: './attachments.component.html',
  providers: [RefObjectService, MessageService]
})
export class AttachmentsComponent implements OnInit {

  @Input() mappings: string;
  @Input() project?: any;
  @Input() task?: any;
  @Input() pid?: string;

  panelShown: boolean = false;
  uploaderUrl: string;
  showUpload: boolean;
  downloadAllUrl: string;
  fileCount: number;
  data: any = {};
  loadingInProgress: boolean;
  treeNode: ExtendedTreeNode[] = [];
  currentFiles: SimpleDocument[]; //passed to attachments-list

  constructor(private documentService: DocumentServiceNg, private messageService: MessageService) {}

  ngOnInit() {

    //////////////////////////////////////////////////////////////////////
    //Für Tests im Libraries Projekt

/*     let umbauProject = {
      "guid": 1044711,
      "version": 4,
      "projectId": "P19.000038",
      "tag": [
        "TX-Erweiterung"
      ],
      "title": "Sharing test",
      "status": "ACTIVE",
      "priority": 0,
      "refObjectIdName": "110010A",
      "refObjectType": "SI",
      "projectType": "Umbau Process",
      "projectTypeIdName": "UMBAU",
      "subType": null,
      "dueDate": "2019-03-14",
      "pmcProjectType": {
        "guid": 7,
        "idName": "UMBAU",
        "name": "Umbau Process",
        "docuConfigs": null,
        "allowedTags": "Ersterrichtung,RX-Erweiterung,TX-Erweiterung,Umbau wegen Brandschutzvorschriften,Mängelbehebung,Behördliche Vorgaben,Umbau des Nutzungsgebers am Objekt,Kündigung durch Nutzungsgeber,Anrainerbeschwerden,Umbau wegen Sharingpartner,Coverage,Capacity,Umbauten exORA Site auf Grund Mängel,Ersatzstandort,Umbau wegen BBM Projekt,E-Band Swap,TX Capacity Upgrade,TX Reconstruction / Dismantling,3rd Party and Business,Pilot,Link Neubau,Sonstiges",
        "allowedProcesses": null,
        "readRights": "admin,baukoo,acquicoonew,acquicoore,rom,transregio,radioregio,prop,emvu,bbmcentral,radiozentral,ran_back_integration,changeman,zte_conf_file_prep,transzentral,umbau-reader",
        "writeRights": "admin,acquicoore",
        "createRights": "admin,baukoo,acquicoonew,acquicoore,rom,transregio,radioregio",
        "deleteRights": "admin",
        "readRightGroups": [
          "admin",
          "baukoo",
          "acquicoonew",
          "acquicoore",
          "rom",
          "transregio",
          "radioregio",
          "prop",
          "emvu",
          "bbmcentral",
          "radiozentral",
          "ran_back_integration",
          "changeman",
          "zte_conf_file_prep",
          "transzentral",
          "umbau-reader"
        ],
        "writeRightGroups": [
          "admin",
          "acquicoore"
        ],
        "createRightGroups": [
          "admin",
          "baukoo",
          "acquicoonew",
          "acquicoore",
          "rom",
          "transregio",
          "radioregio"
        ],
        "deleteRightGroups": [
          "admin"
        ],
        "allowedTagList": [
          "Ersterrichtung",
          "RX-Erweiterung",
          "TX-Erweiterung",
          "Umbau wegen Brandschutzvorschriften",
          "Mängelbehebung",
          "Behördliche Vorgaben",
          "Umbau des Nutzungsgebers am Objekt",
          "Kündigung durch Nutzungsgeber",
          "Anrainerbeschwerden",
          "Umbau wegen Sharingpartner",
          "Coverage",
          "Capacity",
          "Umbauten exORA Site auf Grund Mängel",
          "Ersatzstandort",
          "Umbau wegen BBM Projekt",
          "E-Band Swap",
          "TX Capacity Upgrade",
          "TX Reconstruction / Dismantling",
          "3rd Party and Business",
          "Pilot",
          "Link Neubau",
          "Sonstiges"
        ],
        "processDefinitions": [],
        "allowedSubtypes": "Umbau-Intern,Umbau-Extern,Site Modification 900 MHz,Site Modification LTE18,LTE26/Capacity,LTE26MM/Capacity,Weasel,Business Kunde Neubau,Business Kunde Umbau,Eagle,Ersatzstandort",
        "allowedSubtypeList": [
          "Umbau-Intern",
          "Umbau-Extern",
          "Site Modification 900 MHz",
          "Site Modification LTE18",
          "LTE26/Capacity",
          "LTE26MM/Capacity",
          "Weasel",
          "Business Kunde Neubau",
          "Business Kunde Umbau",
          "Eagle",
          "Ersatzstandort"
        ],
        "startProcess": "acqui-orchestration",
        "allowedMilestones": [],
        "objectType": "SI",
        "additionalProperties": null,
        "configuration": "{\"startSubprocessVariable\":\"acquicheck\",\"startSubprocessMessageName\":\"acquicheck-requested\",\"guiHiddenRoles\":\"akqui,baugu,transgu,baukoo,h3aacqui\",\"subprocessProperties\":[{\"processDefinitionKey\":\"lease-contr-nego\",\"propertiesTemplate\":\"nsa/views/pmc.process.properties.h3aacqui.html\",\"properties\":[{\"propertyName\":\"contract_type\",\"typ\":\"dropdown\",\"displayName\":\"Contract Type\",\"defaultValue\":\"\",\"values\":[{\"value\":\"normal\",\"displayName\":\"Normal\"},{\"value\":\"frame_contract\",\"displayName\":\"Frame Contract\"}]}]},{\"processDefinitionKey\":\"plan-and-perform-pb\",\"properties\":[{\"propertyName\":\"skip_tx_radio\",\"typ\":\"dropdown\",\"displayName\":\"Skip TX/Radio inputs\",\"defaultValue\":\"\",\"values\":[{\"value\":\"yes\",\"displayName\":\"Yes\"},{\"value\":\"no\",\"displayName\":\"No\"}]}]},{\"processDefinitionKey\":\"create-ap\",\"properties\":[{\"propertyName\":\"skip_tx_radio\",\"typ\":\"dropdown\",\"displayName\":\"Skip TX/Radio inputs\",\"defaultValue\":\"\",\"values\":[{\"value\":\"yes\",\"displayName\":\"Yes\"},{\"value\":\"no\",\"displayName\":\"No\"}]}]},{\"processDefinitionKey\":\"legal-check-contract\",\"propertiesTemplate\":\"nsa/views/pmc.process.properties.h3aacqui.html\",\"properties\":[]},{\"processDefinitionKey\":\"manage-site-owner\",\"propertiesTemplate\":\"nsa/views/pmc.process.properties.h3aacqui.html\",\"properties\":[]},{\"processDefinitionKey\":\"auth-check-a-plan-app\",\"propertiesTemplate\":\"nsa/views/pmc.process.properties.h3aacqui.html\",\"properties\":[]},{\"processDefinitionKey\":\"create-sar\",\"propertiesTemplate\":\"nsa/views/pmc.process.properties.h3aacqui.html\",\"properties\":[]},{\"processDefinitionKey\":\"create-bp\",\"propertiesTemplate\":\"nsa/views/pmc.process.properties.bp.html\",\"properties\":[{\"description\":\"Before starting the BP creation process please define who needs to provide information for BP creation. Note that everyone who provides input needs to approve the BP.\",\"propertyName\":\"bpConfig_requiredApprovalFromAcquisition\",\"typ\":\"dropdown\",\"displayName\":\"Acquisition\",\"defaultValue\":\"true\",\"values\":[{\"value\":\"true\",\"displayName\":\"Yes\"},{\"value\":\"false\",\"displayName\":\"No\"}]},{\"propertyName\":\"bpConfig_requiredApprovalFromConstruction\",\"typ\":\"dropdown\",\"displayName\":\"Construction\",\"defaultValue\":\"true\",\"values\":[{\"value\":\"true\",\"displayName\":\"Yes\"},{\"value\":\"false\",\"displayName\":\"No\"}]},{\"propertyName\":\"bpConfig_requiredApprovalFromRadio\",\"typ\":\"dropdown\",\"displayName\":\"Radio\",\"defaultValue\":\"true\",\"values\":[{\"value\":\"true\",\"displayName\":\"Yes\"},{\"value\":\"false\",\"displayName\":\"No\"}]},{\"propertyName\":\"bpConfig_requiredApprovalFromTransmission\",\"typ\":\"dropdown\",\"displayName\":\"Transmission\",\"defaultValue\":\"true\",\"values\":[{\"value\":\"true\",\"displayName\":\"Yes\"},{\"value\":\"false\",\"displayName\":\"No\"}]},{\"propertyName\":\"bpConfig_requiredApprovalFromSharing\",\"typ\":\"dropdown\",\"displayName\":\"Sharing\",\"defaultValue\":\"true\",\"values\":[{\"value\":\"true\",\"displayName\":\"Yes\"},{\"value\":\"false\",\"displayName\":\"No\"}]},{\"description\":\"If the BANF for BP should be created and goods receipted automatically please select yes for Automatic BANF.\",\"propertyName\":\"bpConfig_automaticBanf\",\"typ\":\"dropdown\",\"displayName\":\"Automatically create BANF\",\"defaultValue\":\"false\",\"values\":[{\"value\":\"true\",\"displayName\":\"Yes\"},{\"value\":\"false\",\"displayName\":\"No\"}]}]}],\"processDefsToHideAfterCompletion\":[\"check-phase-partner\",\"bp-assign-partner\"],\"processDefsToHide\":[\"start-site-checks\",\"tx-aggregation-planning-recon\",\"tx-aggregation-delivery-recon\",\"bbu-ne-assignment-check-recon\"],\"obligatoryDueDate\":true}",
        "phases": [
          {
            "guid": 1,
            "idName": "ACQUISITION",
            "displayName": "Acquisition",
            "allowedProcesses": [
              "active-sharing-request",
              "legal-check-contract",
              "plan-and-perform-pb",
              "create-ep",
              "auth-check-a-plan-app",
              "create-ap",
              "check-acqui-docu",
              "create-bp",
              "misc-activities",
              "create-sar",
              "create-statik",
              "request-tx-project-task",
              "check-sharing-docu",
              "approve-rsz",
              "approve-sp",
              "business-customer-mw-pb-order"
            ],
            "prepareRights": [
              "radioregio"
            ],
            "writeRights": [
              "acquicoore",
              "admin"
            ]
          },
          {
            "guid": 2,
            "idName": "CIVIL-WORKS",
            "displayName": "Civil Works",
            "allowedProcesses": [
              "site-construction"
            ],
            "prepareRights": [
              "radioregio"
            ],
            "writeRights": [
              "acquicoore",
              "admin"
            ]
          },
          {
            "guid": 3,
            "idName": "INTEGRATION",
            "displayName": "Integration",
            "allowedProcesses": [
              "create-bp",
              "passive-sharing-site-acceptance",
              "pre-docu",
              "site-based-ne-integration",
              "create-aat",
              "site-integration",
              "alarm-checks"
            ],
            "prepareRights": [
              "radioregio"
            ],
            "writeRights": [
              "acquicoore",
              "admin"
            ]
          },
          {
            "guid": 7,
            "idName": "LEGAL_CHECK",
            "displayName": "Legal check and contract ammendment",
            "allowedProcesses": [
              "lease-contr-nego",
              "manage-site-owner"
            ],
            "prepareRights": [],
            "writeRights": []
          },
          {
            "guid": 11,
            "idName": "SHARING",
            "displayName": "Sharing",
            "allowedProcesses": [
              "approve-rsz",
              "approve-sp",
              "sharing-pre-offer"
            ],
            "prepareRights": [
              "acquicoonew",
              "acquicoore",
              "rom",
              "baukoo",
              "transregio",
              "radioregio",
              "site-solution",
              "shakoo",
              "rolloutco",
              "shabl",
              "rom"
            ],
            "writeRights": [
              "shakoo",
              "rolloutco",
              "shabl",
              "rom"
            ]
          },
          {
            "guid": 9,
            "idName": "TRANSMISSION",
            "displayName": "Transmission",
            "allowedProcesses": [],
            "prepareRights": [],
            "writeRights": []
          }
        ],
        "detailsTemplate": "nsa/views/pmc.project.details.umbau.html",
        "propertiesTemplate": "nsa/views/pmc.project.properties.umbau.html",
        "creationPropertiesTemplate": null
      },
      "subtype": "Business Kunde Neubau",
      "parents": null,
      "children": null,
      "processes": [
        {
          "guid": 1044732,
          "version": 0,
          "processInstanceId": "39bb1a93-5282-11e9-b743-02420a0d0004",
          "parentExecutionId": null,
          "status": "ACTIVE",
          "relation": "MAIN",
          "workDetails": null,
          "processKey": "acqui-orchestration",
          "businessObject": null,
          "label": "Site Reconstruction Orchestration",
          "phase": null,
          "tasks": null,
          "resultStatus": null,
          "resultComment": null,
          "customRefObject": null,
          "startTime": null,
          "endTime": null,
          "processDefinitionId": null,
          "activeOrSuspended": true
        },
        {
          "guid": 1044742,
          "version": 3,
          "processInstanceId": "9b5b0410-5282-11e9-b743-02420a0d0004",
          "parentExecutionId": "9b533bce-5282-11e9-b743-02420a0d0004",
          "status": "ACTIVE",
          "relation": "CHILD",
          "workDetails": null,
          "processKey": "approve-rsz",
          "businessObject": "110010A",
          "label": "Approve Rotstiftzeichnung",
          "phase": "SHARING",
          "tasks": null,
          "resultStatus": null,
          "resultComment": null,
          "customRefObject": null,
          "startTime": null,
          "endTime": null,
          "processDefinitionId": null,
          "activeOrSuspended": true
        }
      ],
      "customerProjectId": 203077713,
      "ownerId": 2178106132,
      "details": null,
      "guStatus": null,
      "comment": "Sharing test",
      "risk": null,
      "refObjectId": 77675,
      "milestones": null,
      "phases": [
        {
          "idName": "ACQUISITION",
          "enabled": false,
          "active": false,
          "startDate": null,
          "endDate": null,
          "frozen": false
        },
        {
          "idName": "CIVIL-WORKS",
          "enabled": false,
          "active": false,
          "startDate": null,
          "endDate": null,
          "frozen": false
        },
        {
          "idName": "INTEGRATION",
          "enabled": null,
          "active": false,
          "startDate": null,
          "endDate": null,
          "frozen": false
        },
        {
          "idName": "LEGAL_CHECK",
          "enabled": null,
          "active": false,
          "startDate": null,
          "endDate": null,
          "frozen": false
        },
        {
          "idName": "TRANSMISSION",
          "enabled": false,
          "active": false,
          "startDate": null,
          "endDate": null,
          "frozen": false
        },
        {
          "idName": "SHARING",
          "enabled": true,
          "active": true,
          "startDate": "2019-03-30",
          "endDate": null,
          "frozen": true
        }
      ],
      "history": null,
      "displayMetaInfo": null,
      "properties": null,
      "propertiesMap": null,
      "milestonesMap": null,
      "createDate": "2019-03-30",
      "createUser": 2178106132,
      "ownerName": "Yannick Koitzsch",
      "updateDate": "2019-03-30",
      "updateUser": 1,
      "parentIds": null,
      "childrenIds": null,
      "additionalInfo": null,
      "resultStatus": null,
      "resultComment": null,
      "authBase": "#{permissions}:UMBAU:SHARING:ACTIVE:NULL:baugualpine:integrationeqos:NULL",
      "humanReadableId": "P19.000038 (guid=1044711)",
      "running": true
    }; */
    //this.project = umbauProject;
    //this.pid = "9b5b0410-5282-11e9-b743-02420a0d0004";    
    //this.pid = "f80526ae-52a1-11e9-b11b-02420a0d0004"; //banf pid
    //let jsonTask= {"assignee":"Paul Weldler","created":"2019-03-30","due":null,"formKey":"approve-rsz/manage-rsz-modification","priority":null,"description":null,"comment":null,"pmcProjectGuid":1044711,"candidateGroups":["Administrator","Sharing Coordinator"],"candidateGroupIds":[14,30],"state":"ACTIVE","task_id":"90ef3893-5287-11e9-b743-02420a0d0004","task_type":"SI","task_name":"Manage RSZ modification","object_name":"110010A","object_id":77675,"assignee_id":"2507824","execution_id":"9b5b0410-5282-11e9-b743-02420a0d0004","follow_up":null,"process_definition_id":"approve-rsz:3:b655a396-5239-11e9-b743-02420a0d0004","process_instance_id":"9b5b0410-5282-11e9-b743-02420a0d0004","process_name":"Approve Rotstiftzeichnung","process_definition_key":"approve-rsz","process_package_key":"39bb1a93-5282-11e9-b743-02420a0d0004","suspension_state":false};
    //this.task = jsonTask; 

    if(!this.mappings){
      //this.mappings = "AcquiDoc,SI,SA"
    } 
    //////////////////////////////////////////////////////////////////////

    this.fileCount = 0;
    this.data.mappings = this.mappings;
    if(this.project){
      this.data.refObjectType = 'project';
      this.data.guid = this.project.guid;
      this.data.idName = this.project.projectId;
    }  
    else if(this.task){
      console.log("thistask", this.task);
      this.data.refObjectType = this.task.task_type;
      this.data.idName = this.task.object_name;
      this.data.guid = this.task.object_id;
    }
    else if(this.pid){
      this.data.refObjectType='process';
      this.data.guid = this.pid;
    }

    this.loadTreeConfig();
  }
  

  loadTreeConfig(){
    this.documentService.loadTree(this.data.refObjectType, this.data.guid, true, this.data.mappings).subscribe(
      res => {
        res.name = this.data.idName;
        let temp = [];
        if(res.virtual){
          temp = res.children;
        }
        else temp = [res];
        this.data.selectedNode = temp[0];
        this.refreshUrls();
        this.fileCount = this.checkFileCount(temp);
        this.treeNode = res.children;
        for(let node of this.treeNode){
          this.setupTreeNode(node);
        }

        this.loadFolderContent();
      },
      error => this.messageService.add({severity:'error', summary:'Load Tree Config', detail: error.message}));
  }  

  setupTreeNode(node: ExtendedTreeNode){
      node.expanded = false;
      node.collapsedIcon = "fa fa-folder";
      node.expandedIcon = "fa fa-folder-open";
      node.name = node.label;
      node.label = node.name + " (" + node.files + ")";
      for(let child of node.children){
        this.setupTreeNode(child);
      }
  }

  checkFileCount(nodeList): number {
    let fileCount = 0;
    for(let node of nodeList){
      fileCount += node.files;
      fileCount += this.checkFileCount(node.children);
    }
    return fileCount;
  } 

  loadFolderContent(): void {
    console.log("loadFolderContent");
     this.documentService.loadFolderContent(this.data.refObjectType, this.data.guid, this.data.selectedNode.id, null, this.data.selectedNode.mapping).subscribe(
       (res) => {
        this.currentFiles = res;
        this.data.selectedNode.files = res.length
        this.data.selectedNode.label = this.data.selectedNode.name + " (" + res.length + ")";
        this.fileCount = this.checkFileCount(this.treeNode);
       }
     );
  }

  refreshUrls(event?:any): void {
    this.downloadAllUrl = this.documentService.getDownloadAllUrl(this.data.refObjectType, this.data.guid, this.data.selectedNode.id, this.data.selectedNode.mapping);
    this.uploaderUrl = this.documentService.getFolderUrl(this.data.refObjectType, this.data.guid, this.data.selectedNode.id, this.data.selectedNode.mapping);
  }

  onUpload(event: any): void {
    this.loadFolderContent();
  }

  togglePanel() {
    this.panelShown = !this.panelShown;
    if (!this.panelShown){
      this.loadTreeConfig()
    }
  }

  onSelectedNodeChange($event){
    console.log("selectedNodeChange", $event);
    this.data.selectedNode = $event;
    this.refreshUrls();
    this.loadFolderContent();
  }

  onDeleteFile(){
    this.loadFolderContent();
  }

}
