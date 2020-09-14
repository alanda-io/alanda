import {
  Component,
  Input,
  OnInit,
  OnDestroy,
  Output,
  EventEmitter,
} from '@angular/core';
import { TreeNode } from 'primeng/api';
import {
  map,
  switchMap,
  toArray,
  finalize,
  exhaustMap,
  tap,
  debounceTime,
} from 'rxjs/operators';
import { Observable, from, Subject } from 'rxjs';
import { AlandaProject } from '../../api/models/project';
import { AlandaProjectApiService } from '../../api/projectApi.service';
import {
  AlandaProjectAndProcessesService,
  TreeNodeData,
  TreeNodeDataType,
  MappedAllowedProcesses,
} from './project-and-processes.service';
import { Router } from '@angular/router';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';
import { ProjectState } from '../../enums/projectState.enum';
import { PapConfigDialogComponent } from './pap-config-dialog/pap-config-dialog.component';
import { PapRelateDialogComponent } from './pap-relate-dialog/pap-relate-dialog.component';
import { AlandaProjectType } from '../../api/models/projectType';
import { AlandaProcess } from '../../api/models/process';
import { PapReasonDialogComponent } from './pap-reason-dialog/pap-reason-dialog.component';
import { ProcessRelation } from '../../enums/processRelation.enum';

@Component({
  selector: 'alanda-project-and-processes',
  templateUrl: './project-and-processes.component.html',
  styleUrls: ['./project-and-processes.component.css'],
  providers: [DialogService, AlandaProjectAndProcessesService],
})
export class AlandaProjectAndProcessesComponent implements OnInit, OnDestroy {
  @Input() project: AlandaProject;
  @Input() dateFormat = 'yyyy-MM-dd';
  @Input() filterOptions: any = {};
  @Output() changed: EventEmitter<void> = new EventEmitter();
  data: TreeNode[] = [];
  loading: boolean;
  readOnly: boolean;
  subprocessSelect$: Subject<any> = new Subject();
  updateDetails$: Subject<{
    project: AlandaProject;
    process: AlandaProcess;
  }> = new Subject();
  refObjectSelect$: Subject<{
    project: AlandaProject;
    process: AlandaProcess;
  }> = new Subject();

  allowedProcesses: { [projectGuid: number]: MappedAllowedProcesses[] } = {};
  dynamicDialogRef: DynamicDialogRef;
  refObjects: string[] = [];

  constructor(
    private readonly projectService: AlandaProjectApiService,
    private readonly papService: AlandaProjectAndProcessesService,
    private readonly router: Router,
    private readonly dialogService: DialogService,
  ) {
    this.subprocessSelect$
      .pipe(
        tap(() => (this.loading = true)),
        exhaustMap((data) =>
          this.startSubprocess(data).pipe(
            finalize(() => (this.loading = false)),
            tap(() => this.loadNode(data.parent)),
          ),
        ),
      )
      .subscribe(() => this.changed.emit());

    this.updateDetails$
      .pipe(
        debounceTime(400),
        tap(() => (this.loading = true)),
        switchMap((v) =>
          this.justSaveSubprocess(v.project, v.process).pipe(
            finalize(() => (this.loading = false)),
          ),
        ),
      )
      .subscribe();

    this.refObjectSelect$
      .pipe(
        tap(() => (this.loading = true)),
        switchMap((v) =>
          this.justSaveSubprocess(v.project, v.process).pipe(
            finalize(() => (this.loading = false)),
          ),
        ),
      )
      .subscribe();
  }

  ngOnInit() {}

  loadProjectAndProcesses(collapsed?: boolean) {
    if (collapsed) {
      return;
    }
    this.loading = true;
    this.projectService
      .getProjectByGuid(this.project.guid, true)
      .pipe(
        map((project) => this.papService.mapProjectsToTreeNode(project)),
        finalize(() => (this.loading = false)),
      )
      .subscribe((result) => {
        this.data = result;
      });
  }

  onNodeExpand(event) {
    const node = event.node;
    this.loadNode(node);
  }

  autocompleteSites(searchTerm: any, projectType: AlandaProjectType) {
    if (searchTerm.query.trim() === '') {
      this.refObjects = [];
      return;
    }
    if (projectType) {
      this.projectService
        .autocompleteRefObjects(searchTerm.query, projectType.objectType)
        .subscribe((res) => {
          this.refObjects = res.map((item) => item.idName);
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
    this.router.navigate(['create/project', data.project.guid]);
  }

  relateSubproject(data: TreeNodeData): void {
    this.filterOptions['project.projectId'] = `!${data.project.projectId}`;
    this.projectService
      .getChildTypes(data.project.projectTypeIdName)
      .subscribe((types) => {
        this.dynamicDialogRef = this.openRelateDialogModal(
          'Select project(s) to add as subproject',
          {
            types: types.map((type) => type.idName),
            filterOptions: this.filterOptions,
          },
        );
        this.dynamicDialogRef.onClose.subscribe((project: AlandaProject) => {
          if (project) {
            this.loading = true;
            this.projectService
              .updateProjectRelations(
                data.project.projectId,
                project.projectId,
                null,
                null,
                null,
              )
              .pipe(finalize(() => (this.loading = false)))
              .subscribe((res) => {
                this.changed.emit();
                this.loadProjectAndProcesses();
              });
          }
        });
      });
  }

  moveMeTo(data: TreeNodeData): void {
    this.filterOptions['project.projectId'] = `!${data.project.projectId}`;
    this.projectService
      .getParentTypes(data.project.projectTypeIdName)
      .subscribe((types) => {
        this.dynamicDialogRef = this.openRelateDialogModal(
          'Select new parent project(s)',
          {
            types: types.map((type) => type.idName),
            filterOptions: this.filterOptions,
          },
        );

        this.dynamicDialogRef.onClose.subscribe((project: AlandaProject) => {
          if (project) {
            this.loading = true;
            this.projectService
              .updateProjectRelations(
                data.project.projectId,
                null,
                null,
                project.projectId,
                '*',
              )
              .pipe(finalize(() => (this.loading = false)))
              .subscribe((res) => {
                this.changed.emit();
                this.loadProjectAndProcesses();
              });
          }
        });
      });
  }

  relateMeTo(data: TreeNodeData): void {
    this.filterOptions['project.projectId'] = `!${data.project.projectId}`;
    this.projectService
      .getParentTypes(data.project.projectTypeIdName)
      .subscribe((types) => {
        this.dynamicDialogRef = this.openRelateDialogModal(
          'Select projects new parent project(s)',
          {
            types: types.map((type) => type.idName),
            filterOptions: this.filterOptions,
          },
        );

        this.dynamicDialogRef.onClose.subscribe((project: AlandaProject) => {
          if (project) {
            this.loading = true;
            this.projectService
              .updateProjectRelations(
                data.project.projectId,
                null,
                null,
                project.projectId,
                null,
              )
              .pipe(finalize(() => (this.loading = false)))
              .subscribe(() => {
                this.changed.emit();
                this.loadProjectAndProcesses();
              });
          }
        });
      });
  }

  unrelateMe(data: TreeNodeData): void {
    this.dynamicDialogRef = this.openRelateDialogModal(
      'Select parent project(s) to unrelate me from',
      {
        guid: data.project.guid,
        filterOptions: this.filterOptions,
      },
    );

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
          )
          .pipe(finalize(() => (this.loading = false)))
          .subscribe((res) => {
            this.changed.emit();
            this.loadProjectAndProcesses();
          });
      }
    });
  }

  openCancelProjectDialog(data: TreeNodeData) {
    this.dynamicDialogRef = this.openReasonDialogModal(
      `Cancel ${data.project.projectId}`,
      {
        placeholder: 'Reason for canceling the project',
        content: `Are you sure to cancel project ${data.project.projectId}? All progress will be lost !`,
      },
    );

    this.dynamicDialogRef.onClose.subscribe((reason: string) => {
      if (reason !== null) {
        this.loading = true;
        this.projectService
          .stopProject(data.project.guid, reason)
          .pipe(finalize(() => (this.loading = false)))
          .subscribe(() => {
            this.changed.emit();
            this.loadProjectAndProcesses();
          });
      }
    });
  }

  openCancelProcessDialog(data: TreeNodeData, node: TreeNode) {
    this.dynamicDialogRef = this.openReasonDialogModal('Cancel Process', {
      placeholder: 'Reason for canceling the process',
      content: `Are you sure to cancel ${data.process.label}? All progress will be lost !`,
    });
    this.dynamicDialogRef.onClose.subscribe((reason: string) => {
      if (reason !== null) {
        this.loading = true;
        this.projectService
          .stopProjectProcess(
            data.relatedProject.guid,
            data.process.guid,
            reason,
          )
          .pipe(finalize(() => (this.loading = false)))
          .subscribe(() => {
            this.changed.emit();
            this.loadNode(node.parent);
          });
      }
    });
  }

  openStartProcessDialog(data: TreeNodeData, node: TreeNode) {
    this.dynamicDialogRef = this.openReasonDialogModal(
      `Start ${data.process.label}`,
      {
        content: `Are you sure to start ${data.process.label}?`,
      },
    );
    this.dynamicDialogRef.onClose.subscribe((reason: string) => {
      if (reason !== null) {
        this.loading = true;
        this.projectService
          .startProjectProcess(data.relatedProject.guid, data.process.guid)
          .pipe(finalize(() => (this.loading = false)))
          .subscribe(() => {
            this.changed.emit();
            this.loadNode(node.parent);
          });
      }
    });
  }

  openRemoveProcessDialog(data: TreeNodeData, node: TreeNode) {
    this.dynamicDialogRef = this.openReasonDialogModal('Remove Process', {
      placeholder: 'Reason for removing the process',
      content: `Are you sure to remove ${data.process.label}? All progress will be lost !`,
    });
    this.dynamicDialogRef.onClose.subscribe((reason: string) => {
      if (reason !== null) {
        this.loading = true;
        this.projectService
          .removeProjectProcess(
            data.relatedProject.guid,
            data.process.guid,
            reason,
          )
          .pipe(finalize(() => (this.loading = false)))
          .subscribe(() => {
            this.changed.emit();
            this.loadNode(node.parent);
          });
      }
    });
  }

  configureProcess(data: TreeNodeData, project: AlandaProject): void {
    this.dynamicDialogRef = this.dialogService.open(PapConfigDialogComponent, {
      data: {
        configuration: JSON.parse(project.pmcProjectType.configuration),
        process: data.process,
        projectGuid: project.guid,
      },
      header: `Process Configuration - ${data.process.processKey}`,
      width: '50%',
    });
  }

  private startSubprocess(value: {
    data: TreeNodeData;
    parent: TreeNode;
  }): Observable<AlandaProcess> {
    value.data.process.status = ProjectState.NEW;
    value.data.process.relation = ProcessRelation.CHILD;
    value.data.process.workDetails = '';
    value.data.process.businessObject =
      value.parent.data.project.refObjectIdName;
    return this.projectService.saveProjectProcess(
      value.parent.data.project.guid,
      value.data.process,
    );
  }

  private justSaveSubprocess(
    project: AlandaProject,
    process: AlandaProcess,
  ): Observable<AlandaProcess> {
    return this.projectService.saveProjectProcess(project.guid, process);
  }

  private loadNode(node: TreeNode): void {
    const data: TreeNodeData = node.data;
    if (data.type === TreeNodeDataType.PROJECT) {
      this.loading = true;
      this.getProjectWithProcessesAndTasks(data.project)
        .pipe(
          switchMap((project) => from(project.processes)),
          map((process) =>
            this.papService.mapProcessToTreeNode(process, data.project),
          ),
          toArray(),
          map((processes) =>
            processes.sort((a, b) =>
              a.data.process.status > b.data.process.status ? 1 : -1,
            ),
          ),
          finalize(() => (this.loading = false)),
        )
        .subscribe((nodes) => {
          node.children = nodes;
          node.children.push(
            this.papService.getStartProcessDropdownAsTreeNode(data.project),
          );
          this.data = [...this.data];
        });
    }
  }

  private openRelateDialogModal(header: string, data: any): DynamicDialogRef {
    return this.dialogService.open(PapRelateDialogComponent, {
      data,
      header,
      width: '70%',
    });
  }

  private openReasonDialogModal(header: string, data?: any): DynamicDialogRef {
    return this.dialogService.open(PapReasonDialogComponent, {
      data,
      header,
      width: '40%',
    });
  }

  private getProjectWithProcessesAndTasks(
    project: AlandaProject,
  ): Observable<AlandaProject> {
    return this.projectService
      .getProcessesAndTasksForProject(project.guid)
      .pipe(
        map((result) => {
          const mapped = this.papService.getProcessesAndTasksForProject(result);
          project.processes = result.active;
          this.allowedProcesses[project.guid] = mapped.allowed;
          return project;
        }),
      );
  }

  ngOnDestroy(): void {
    this.subprocessSelect$.unsubscribe();
    this.updateDetails$.unsubscribe();
    this.refObjectSelect$.unsubscribe();
  }
}
