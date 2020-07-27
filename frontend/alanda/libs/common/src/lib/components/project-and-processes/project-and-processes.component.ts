import { Component, Input, OnInit, OnDestroy, Output, EventEmitter } from '@angular/core';
import { TreeNode } from 'primeng/api';
import { map, switchMap, toArray, finalize, exhaustMap, tap } from 'rxjs/operators';
import { Observable, from, Subject } from 'rxjs';
import { AlandaProject } from '../../api/models/project';
import { AlandaProjectApiService } from '../../api/projectApi.service';
import { XxxService, TreeNodeData, TreeNodeDataType, MappedAllowedProcesses } from './project-and-processes.service';
import { Router } from '@angular/router';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';
import { ProjectState } from '../../enums/projectState.enum';
import { PapActionConfig } from './pap-actions/pap-actions.component';
import { PapConfigDialogComponent } from './pap-config-dialog/pap-config-dialog.component';
import { PapRelateDialogComponent } from './pap-relate-dialog/pap-relate-dialog.component';

@Component({
  selector: 'alanda-project-and-processes',
  templateUrl: './project-and-processes.component.html',
  styleUrls: ['./project-and-processes.component.css'],
  providers: [DialogService]
})
export class AlandaProjectAndProcessesComponent implements OnInit, OnDestroy {

  @Input() project: AlandaProject;
  @Input() dateFormat = 'yyyy-MM-dd';
  @Input() filterOptions: any = {};
  @Output() changed: EventEmitter<void> = new EventEmitter();

  data: TreeNode[] = [];
  allowedProcesses: {[projectGuid: number]: MappedAllowedProcesses[]} = {};
  actionsConfig: {[nodeUuid: string]: PapActionConfig} = {};
  loading: boolean;
  subprocessSelect$: Subject<any> = new Subject();
  dynamicDialogRef: DynamicDialogRef;

  displayCancelProjectDialog: boolean;
  displayCancelProcessDialog: boolean;
  displayStartProcessDialog: boolean;
  displayRemoveProcessDialog: boolean;
  dialogData: {
    title?: string;
    content?: string;
    placeholder?: string;
    reason?: string;
    projectGuid?: number;
    processGuid?: number;
    refreshNode?: TreeNode;
  } = {};

  constructor(
    private readonly projectService: AlandaProjectApiService,
    private readonly xxxService: XxxService,
    private readonly router: Router,
    private readonly dialogService: DialogService
  ) {
    this.subprocessSelect$.pipe(
      tap(() => this.loading = true),
      exhaustMap(data => this.xxxService.startSubprocess(data).pipe(
        finalize(() => this.loading = false),
        tap(() => this.loadNode(data.parent))
      )),
    )
    .subscribe(() => this.changed.emit());
  }

  ngOnInit() {
  }

  loadProjectAndProcesses(collapsed?: boolean) {
    if (collapsed) {
      return;
    }
    this.loading = true;
    this.projectService.getProjectByGuid(this.project.guid, true).pipe(
      tap(project => project.pmcProjectType.configuration = '{"startSubprocessVariable":"acquicheck","startSubprocessMessageName":"acquicheck-requested","guiHiddenRoles":"akqui,baugu,transgu,baukoo,h3aacqui","subprocessProperties":[{"processDefinitionKey":"vacation-request","propertiesTemplate":"nsa/views/pmc.process.properties.h3aacqui.html","properties":[{"propertyName":"contract_type","typ":"dropdown","displayName":"Contract Type","defaultValue":"","values":[{"value":"normal","displayName":"Normal"},{"value":"frame_contract","displayName":"Frame Contract"}]}]},{"processDefinitionKey":"plan-and-perform-pb","properties":[{"propertyName":"skip_tx_radio","typ":"dropdown","displayName":"Skip TX/Radio inputs","defaultValue":"","values":[{"value":"yes","displayName":"Yes"},{"value":"no","displayName":"No"}]}]},{"processDefinitionKey":"create-ap","properties":[{"propertyName":"skip_tx_radio","typ":"dropdown","displayName":"Skip TX/Radio inputs","defaultValue":"","values":[{"value":"yes","displayName":"Yes"},{"value":"no","displayName":"No"}]}]},{"processDefinitionKey":"legal-check-contract","propertiesTemplate":"nsa/views/pmc.process.properties.h3aacqui.html","properties":[]},{"processDefinitionKey":"manage-site-owner","propertiesTemplate":"nsa/views/pmc.process.properties.h3aacqui.html","properties":[]},{"processDefinitionKey":"auth-check-a-plan-app","propertiesTemplate":"nsa/views/pmc.process.properties.h3aacqui.html","properties":[]},{"processDefinitionKey":"create-sar","propertiesTemplate":"nsa/views/pmc.process.properties.h3aacqui.html","properties":[]},{"processDefinitionKey":"create-bp","propertiesTemplate":"nsa/views/pmc.process.properties.bp.html","properties":[{"description":"Before starting the BP creation process please define who needs to provide information for BP creation. Note that everyone who provides input needs to approve the BP.","propertyName":"bpConfig_requiredApprovalFromAcquisition","typ":"dropdown","displayName":"Acquisition","defaultValue":"true","values":[{"value":"true","displayName":"Yes"},{"value":"false","displayName":"No"}]},{"propertyName":"bpConfig_requiredApprovalFromConstruction","typ":"dropdown","displayName":"Construction","defaultValue":"true","values":[{"value":"true","displayName":"Yes"},{"value":"false","displayName":"No"}]},{"propertyName":"bpConfig_requiredApprovalFromRadio","typ":"dropdown","displayName":"Radio","defaultValue":"true","values":[{"value":"true","displayName":"Yes"},{"value":"false","displayName":"No"}]},{"propertyName":"bpConfig_requiredApprovalFromTransmission","typ":"dropdown","displayName":"Transmission","defaultValue":"true","values":[{"value":"true","displayName":"Yes"},{"value":"false","displayName":"No"}]},{"propertyName":"bpConfig_requiredApprovalFromSharing","typ":"dropdown","displayName":"Sharing","defaultValue":"true","values":[{"value":"true","displayName":"Yes"},{"value":"false","displayName":"No"}]},{"description":"If the BANF for BP should be created and goods receipted automatically please select yes for Automatic BANF.","propertyName":"bpConfig_automaticBanf","typ":"dropdown","displayName":"Automatically create BANF","defaultValue":"false","values":[{"value":"true","displayName":"Yes"},{"value":"false","displayName":"No"}]}]}],"processDefsToHideAfterCompletion":["check-phase-partner","bp-assign-partner"],"processDefsToHide":["start-site-checks","tx-aggregation-planning-recon","tx-aggregation-delivery-recon","bbu-ne-assignment-check-recon"],"processDefsToHideButShowTasks":["acquisition-phase"],"obligatoryDueDate":true}'),
      map(project => this.xxxService.mapProjectsToTreeNode(project)),
      finalize(() => this.loading = false)
    ).subscribe(result => {
      this.loadActionConfig(result);
      this.data = result
    });
  }

  onNodeExpand(event) {
    const node = event.node;
    this.loadNode(node);
  }

  private getProjectWithProcessesAndTasks(project: AlandaProject): Observable<AlandaProject> {
    return this.projectService
      .getProcessesAndTasksForProject(project.guid).pipe(
        map(result => {
          const mapped = this.xxxService.getProcessesAndTasksForProject(result);
          project.processes = result.active;
          this.allowedProcesses[project.guid] = mapped.allowed;
          return project;
        })
      );
  }

  loadNode(node: TreeNode): void {
    const data: TreeNodeData = node.data;
    if (data.type === TreeNodeDataType.PROJECT) {
      this.loading = true;
      this.getProjectWithProcessesAndTasks(data.project)
      .pipe(
        switchMap(project => from(project.processes)),
        map(process => this.xxxService.mapProcessToTreeNode(process)),
        toArray(),
        map(processes => processes.sort((a, b) => (a.data.process.status > b.data.process.status) ? 1 : -1)),
        finalize(() => this.loading = false)
      )
      .subscribe(nodes => {
        node.children = nodes;
        node.children.push(this.xxxService.getStartProcessDropdownAsTreeNode());
        this.loadActionConfig(nodes);
        this.data = [...this.data];
      });
    }
  }

  getIconClass(type: TreeNodeDataType): string {
    switch (type) {
      case TreeNodeDataType.PROJECT:
        return 'fa fa-book';
      case TreeNodeDataType.TASK:
        return 'fa fa-user';
      case TreeNodeDataType.PROCESS:
        return 'fa fa-random';
      case TreeNodeDataType.ACTIVITY:
        return 'fa fa-clock';
      default:
        return '';
    }
  }

  createSubproject(data: TreeNodeData): void {
    this.router.navigate(['create/project', data.project.guid])
  }

  relateSubproject(data: TreeNodeData): void {
    this.filterOptions['project.projectId'] = `!${data.project.projectId}`;
    this.projectService.getChildTypes(data.project.projectTypeIdName).subscribe(types => {
      this.dynamicDialogRef = this.openRelateDialogModal('Select project(s) to add as subproject', {
        types: types.map(type => type.idName),
        filterOptions: this.filterOptions
      });

      this.dynamicDialogRef.onClose.subscribe((project: AlandaProject) => {
        if (project) {
          this.loading = true;
          this.projectService.updateProjectRelations(data.project.projectId, project.projectId, null, null, null)
          .pipe(
            finalize(() => this.loading = false)
          )
          .subscribe(res => {
            this.changed.emit();
            this.loadProjectAndProcesses();
          });
        }
      });
    });
  }

  moveMeTo(data: TreeNodeData): void {
    this.filterOptions['project.projectId'] = `!${data.project.projectId}`;
    this.projectService.getParentTypes(data.project.projectTypeIdName).subscribe(types => {
      this.dynamicDialogRef = this.openRelateDialogModal('Select new parent project(s)',{
        types: types.map(type => type.idName),
        filterOptions: this.filterOptions
      });

      this.dynamicDialogRef.onClose.subscribe((project: AlandaProject) => {
        if (project) {
          this.loading = true;
          this.projectService.updateProjectRelations(data.project.projectId, null, null, project.projectId, '*')
          .pipe(
            finalize(() => this.loading = false)
          )
          .subscribe(res => {
            this.changed.emit();
            this.loadProjectAndProcesses();
          });
        }
      });
    });
  }


  relateMeTo(data: TreeNodeData): void {
    this.filterOptions['project.projectId'] = `!${data.project.projectId}`;
    this.projectService.getParentTypes(data.project.projectTypeIdName).subscribe(types => {
      this.dynamicDialogRef = this.openRelateDialogModal('Select projects new parent project(s)', {
        types: types.map(type => type.idName),
        filterOptions: this.filterOptions
      });

      this.dynamicDialogRef.onClose.subscribe((project: AlandaProject) => {
        if (project) {
          this.loading = true;
          this.projectService.updateProjectRelations(data.project.projectId, null, null, project.projectId, null)
          .pipe(
            finalize(() => this.loading = false)
          )
          .subscribe(() => {
            this.changed.emit();
            this.loadProjectAndProcesses();
          });
        }
      });
    });
  }

  unrelateMe(data: TreeNodeData): void {
    this.dynamicDialogRef = this.openRelateDialogModal('Select parent project(s) to unrelate me from', {
      guid: data.project.guid,
      filterOptions: this.filterOptions
    });

    this.dynamicDialogRef.onClose.subscribe((project: AlandaProject) => {
      if (project) {
        this.loading = true;
        this.projectService
          .updateProjectRelations(
            data.project.projectId,
            null,
            null,
            null,
            project.projectId,
          ).pipe(
            finalize(() => this.loading = false)
          )
          .subscribe((res) => {
            this.changed.emit();
            this.loadProjectAndProcesses();
          });
      }
    });
  }

  openCancelProjectDialog(data: TreeNodeData) {
    this.displayCancelProjectDialog = true;
    this.dialogData.title = `Cancel ${data.project.projectId}`;
    this.dialogData.content = `Are you sure to cancel project ${data.project.projectId}? All progress will be lost !`;
    this.dialogData.placeholder = 'Reason for canceling the project';
    this.dialogData.projectGuid = data.project.guid;
  }

  openCancelProcessDialog(data: TreeNodeData, node: TreeNode) {
    this.displayCancelProcessDialog = true;
    this.dialogData.title = `Cancel Process`;
    this.dialogData.content = `Are you sure to cancel ${data.process.label}? All progress will be lost !`;
    this.dialogData.placeholder = 'Reason for canceling the process';
    this.dialogData.projectGuid = node.parent.data.project.guid;
    this.dialogData.processGuid = data.process.guid;
    this.dialogData.refreshNode = node.parent;
  }

  openStartProcessDialog(data: TreeNodeData, node: TreeNode) {
    this.displayStartProcessDialog = true;
    this.dialogData.content = `Start ${data.process.label}`;
    this.dialogData.projectGuid = node.parent.data.project.guid;
    this.dialogData.processGuid = data.process.guid;
    this.dialogData.refreshNode = node.parent;
  }

  openRemoveProcessDialog(data: TreeNodeData, node: TreeNode) {
    this.displayRemoveProcessDialog = true;
    this.dialogData.content = `Are you sure to remove ${data.process.label}? All progress will be lost !`;
    this.dialogData.placeholder = 'Reason for removing the process';
    this.dialogData.projectGuid = node.parent.data.project.guid;
    this.dialogData.processGuid = data.process.guid;
    this.dialogData.refreshNode = node.parent;
  }

  cancelProject(): void {
    this.loading = true;
    this.projectService.stopProject(this.dialogData.projectGuid, this.dialogData.reason).pipe(
      finalize(() => this.loading = false)
    ).subscribe(() => {
      this.changed.emit();
      this.loadProjectAndProcesses();
    });
  }

  cancelProcess() {
    this.loading = true;
    this.projectService.stopProjectProcess(this.dialogData.projectGuid, this.dialogData.processGuid, this.dialogData.reason).pipe(
      finalize(() => this.loading = false)
      ).subscribe(() => {
        this.changed.emit();
        this.loadNode(this.dialogData.refreshNode);
      });
  }

  removeSubprocess() {
    this.loading = true;
    this.projectService.removeProjectProcess(this.dialogData.projectGuid, this.dialogData.processGuid, this.dialogData.reason).pipe(
      finalize(() => this.loading = false)
      ).subscribe(() => {
        this.changed.emit();
        this.loadNode(this.dialogData.refreshNode);
      });
  }

  startSubprocess(): void {
    this.loading = true;
    this.projectService.startProjectProcess(this.dialogData.projectGuid, this.dialogData.processGuid)
    .pipe(finalize(() => this.loading = false))
    .subscribe(() => {
      this.changed.emit();
      this.loadNode(this.dialogData.refreshNode);
    });
  }

  configureProcess(data: TreeNodeData, project: AlandaProject): void {
    this.dynamicDialogRef = this.dialogService.open(PapConfigDialogComponent, {
      data: {
        configuration: JSON.parse(project.pmcProjectType.configuration),
        process: data.process,
        projectGuid: project.guid
      },
      header: `Process Configuration - ${data.process.processKey}`,
      width: '50%'
    });
  }

  private openRelateDialogModal(header: string, data: any): DynamicDialogRef {
    return this.dialogService.open(PapRelateDialogComponent, {
      data,
      header,
      width: '70%',
    });
  }

  private loadActionConfig(nodes: TreeNode[]): void {
    for (const node of nodes) {
      if (node.data.project) {
        this.actionsConfig[node.data.uuid] = {
          'RELATE-PROJECTS': {display: true},
          'CANCEL-PROJECT': {display: true},
          'RELATE-ME-TO': {display: true},
          'CREATE-SUBPROJECT': {display: true},
          'MOVE-ME-TO': {display: true},
          'RELATE-SUBPROJECT': {display: true},
          'UNRELATE-ME': {display: true},
        }
      }
      else if (node.data.process && node.data.type !== TreeNodeDataType.STARTPROCESS) {
        this.actionsConfig[node.data.uuid] = {
          'CONFIGURE-SUBPROCESS': {display: true}
        }
        if (node.data.process.status === ProjectState.NEW) {
          this.actionsConfig[node.data.uuid]['START-SUBPROCESS'] = {display: true};
          this.actionsConfig[node.data.uuid]['REMOVE-SUBPROCESS'] = {display: true};
        } else {
          this.actionsConfig[node.data.uuid]['CANCEL-PROCESS'] = {display: true};
        }
      }
    }
  }

  ngOnDestroy(): void {
    this.subprocessSelect$.unsubscribe();
  }
}
