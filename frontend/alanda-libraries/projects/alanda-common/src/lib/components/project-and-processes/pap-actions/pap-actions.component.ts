import { Component, OnInit, Input, OnDestroy, EventEmitter, Output } from '@angular/core';
import { DialogService, DynamicDialogRef } from 'primeng/dynamicdialog';
import { MenuItem } from 'primeng/api';
import { TreeNodeData } from '../project-and-processes.service';
import { RelateDialogComponent } from './relate-dialog/relate-dialog.component';
import { AlandaProjectApiService } from '../../../api/projectApi.service';
import { AlandaProject } from '../../../api/models/project';
import { Router } from '@angular/router';

@Component({
  selector: 'alanda-pap-actions',
  templateUrl: './pap-actions.component.html',
  styleUrls: ['./pap-actions.component.css'],
  providers: [DialogService]
})
export class PapActionsComponent implements OnInit, OnDestroy {
  @Input() data: TreeNodeData;

  optionItems: MenuItem[];

  ref: DynamicDialogRef;

  displayCancelDialog: boolean;

  cancelType: string;

  @Output() changeEvent: EventEmitter<void> = new EventEmitter<void>();

  constructor (private readonly dialogService: DialogService, private readonly projectService: AlandaProjectApiService, private readonly router: Router) {}

  ngOnInit () {
    this.optionItems = [
      {
        label: 'Create subproject',
        icon: 'fa fa-angle-right',
        command: (onclick) => this.router.navigate(['create/project', this.data.id])
      },
      {
        label: 'Relate subproject',
        icon: 'fa fa-angle-right',
        command: (onclick) => this.relateSubproject()
      },
      {
        label: 'Relate me to',
        icon: 'fa fa-angle-right',
        command: (onclick) => this.relateMeTo()
      },
      {
        label: 'Unrelate me',
        icon: 'fa fa-angle-right',
        command: (onclick) => this.unrelateMe()
      },
      {
        label: 'Move me to',
        icon: 'fa fa-angle-right',
        command: (onclick) => this.moveMeTo()
      }
    ];
  }

  onCancelClick (reason: string) {
    if (this.cancelType === 'project') {
      this.projectService.stopProject(this.data.id, reason).subscribe(res => {
        this.changeEvent.emit();
      });
    } else {
      this.projectService.stopProjectProcess(this.data.value.projectGuid, this.data.id, reason).subscribe(res => {
        this.changeEvent.emit();
      });
    }
  }

  private openDynamicDialogModal (header: string, data: any): DynamicDialogRef {
    return this.dialogService.open(RelateDialogComponent, {
      data,
      header,
      width: '70%'
    });
  }

  private relateMeTo () {
    this.projectService.getParentTypes(this.data.value.projectTypeIdName).subscribe(types => {
      this.ref = this.openDynamicDialogModal('Select projects new parent project(s)', { types: types.map(type => type.idName) });
      this.ref.onClose.subscribe((project: AlandaProject) => {
        if (project) {
          this.projectService.updateProjectRelations(this.data.value.projectId, null, null, project.projectId, null).subscribe(res => {
            this.changeEvent.emit();
          });
        }
      });
    });
  }

  private unrelateMe () {
    this.ref = this.openDynamicDialogModal('Select parent project(s) to unrelate me from', { projectId: this.data.value.projectId });
    this.ref.onClose.subscribe((project: AlandaProject) => {
      if (project) {
        this.projectService.updateProjectRelations(this.data.value.projectId, null, null, null, project.projectId).subscribe(res => {
          this.changeEvent.emit();
        });
      }
    });
  }

  private relateSubproject () {
    this.projectService.getChildTypes(this.data.value.projectTypeIdName).subscribe(types => {
      this.ref = this.openDynamicDialogModal('Select project(s) to add as subproject', { types: types.map(type => type.idName) });
      this.ref.onClose.subscribe((project: AlandaProject) => {
        if (project) {
          this.projectService.updateProjectRelations(this.data.value.projectId, project.projectId, null, null, null).subscribe(res => {
            this.changeEvent.emit();
          });
        }
      });
    });
  }

  private moveMeTo () {
    this.projectService.getParentTypes(this.data.value.projectTypeIdName).subscribe(types => {
      this.ref = this.openDynamicDialogModal('Select new parent project(s)', { types: types.map(type => type.idName) });
      this.ref.onClose.subscribe((project: AlandaProject) => {
        if (project) {
          this.projectService.updateProjectRelations(this.data.value.projectId, null, null, project.projectId, '*').subscribe(res => {
            this.changeEvent.emit();
          });
        }
      });
    });
  }

  ngOnDestroy () {
    if (this.ref) {
      this.ref.close();
    }
  }
}


