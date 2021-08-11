import { Component, OnInit, ViewChild } from '@angular/core';
import { AlandaPermission } from '../../../api/models/permission';
import { Table } from 'primeng/table';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { AlandaPermissionApiService } from '../../../api/permissionApi.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'alanda-permission-management',
  templateUrl: './permission-management.component.html',
  styleUrls: ['./permission-management.component.scss'],
})
export class AlandaPermissionManagementComponent implements OnInit {
  permissions: AlandaPermission[];
  selectedPermission: AlandaPermission;
  loading: boolean;
  totalRecords: number;

  permissionColumns: any[] = [
    { field: 'guid', header: 'Guid' },
    { field: 'key', header: 'Key' },
  ];

  permissionForm: FormGroup;

  @ViewChild('table') turboTable: Table;

  constructor(
    private readonly permissionService: AlandaPermissionApiService,
    private readonly fb: FormBuilder,
    private readonly messageService: MessageService,
  ) {}

  ngOnInit() {
    this.initPermissionForm();
    this.loadPermissions();
  }

  private initPermissionForm() {
    this.permissionForm = this.fb.group({
      key: ['', Validators.required],
    });
  }

  loadPermissions() {
    this.permissionService.getPermissions().subscribe((res) => {
      this.permissions = res;
      this.totalRecords = res.length;
    });
  }

  onFormSubmit() {
    if (!this.permissionForm.valid) {
      this.permissionForm.markAsDirty();
      return;
    }
    if (this.selectedPermission) {
      this.selectedPermission.key = this.key;
      this.updatePermission(this.selectedPermission);
    } else {
      this.createPermission({
        key: this.key,
      });
    }
  }

  onPermissionSelected(event) {
    this.selectedPermission = event.data;
    this.fillPermissionForm(this.selectedPermission);
  }

  onPermissionUnselected() {
    this.selectedPermission = null;
    this.initPermissionForm();
  }

  private fillPermissionForm(permission: AlandaPermission) {
    this.permissionForm.patchValue(permission);
  }

  get key(): string {
    return this.permissionForm.get('key').value;
  }

  private createPermission(permission: AlandaPermission) {
    this.permissionService.save(permission).subscribe(
      (res) => {
        this.messageService.add({
          severity: 'success',
          summary: 'Create permission',
          detail: 'Permission has been created',
        });
        this.permissions.push(res);
        this.totalRecords = this.permissions.length;
        this.permissionForm.reset();
      },
      (error) =>
        this.messageService.add({
          severity: 'error',
          summary: 'Create permission',
          detail: error.message,
        }),
    );
  }

  private updatePermission(permission: AlandaPermission) {
    this.permissionService.update(permission).subscribe(
      (res) => {
        this.messageService.add({
          severity: 'success',
          summary: 'Update permission',
          detail: 'Permission has been updated',
        });
      },
      (error) =>
        this.messageService.add({
          severity: 'error',
          summary: 'Update permission',
          detail: error.message,
        }),
    );
  }
}
