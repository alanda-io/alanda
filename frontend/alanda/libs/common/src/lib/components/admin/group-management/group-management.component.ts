import { Component, OnInit, ViewChild } from '@angular/core';
import { AlandaGroup } from '../../../api/models/group';
import { AlandaPermission } from '../../../api/models/permission';
import { AlandaRole } from '../../../api/models/role';
import { AlandaUser } from '../../../api/models/user';
import { Table } from 'primeng/table';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { AlandaGroupApiService } from '../../../api/groupApi.service';
import { AlandaRoleApiService } from '../../../api/roleApi.service';
import { AlandaPermissionApiService } from '../../../api/permissionApi.service';
import { MessageService, LazyLoadEvent } from 'primeng/api';
import { ServerOptions } from '../../../models/serverOptions';
import { mergeMap } from 'rxjs/operators';
import { AlandaUserApiService } from '../../../api/userApi.service';

@Component({
  selector: 'alanda-group-management',
  templateUrl: './group-management.component.html',
  styleUrls: ['./group-management.component.scss'],
})
export class AlandaGroupManagementComponent implements OnInit {
  groups: AlandaGroup[] = [];
  selectedGroup: AlandaGroup;
  usersInSelectedGroup: AlandaUser[] = [];

  availablePermissions: AlandaPermission[] = [];
  grantedPermissions: AlandaPermission[] = [];
  availableRoles: AlandaRole[] = [];
  assignedRoles: AlandaRole[] = [];
  totalRecords: number;
  loading = true;
  groupColumns: any[] = [
    { field: 'guid', header: 'Guid' },
    { field: 'groupName', header: 'Group Name' },
    { field: 'longName', header: 'Long Name' },
    { field: 'groupSource', header: 'Group Source' },
  ];

  @ViewChild('table') turboTable: Table;

  groupForm: FormGroup;

  constructor(
    private readonly groupService: AlandaGroupApiService,
    private readonly roleService: AlandaRoleApiService,
    private readonly permissionService: AlandaPermissionApiService,
    private readonly fb: FormBuilder,
    private readonly messageService: MessageService,
    private readonly userService: AlandaUserApiService,
  ) {}

  ngOnInit() {
    this.initGroupForm();
  }

  private initGroupForm() {
    this.groupForm = this.fb.group({
      groupName: ['', Validators.required],
      longName: ['', Validators.required],
      groupSource: [{ value: 'alanda', disabled: true }],
    });
  }

  onFormSubmit() {
    if (!this.groupForm.valid) {
      this.groupForm.markAsDirty();
      return;
    }
    if (this.selectedGroup) {
      this.selectedGroup.groupName = this.groupName;
      this.selectedGroup.longName = this.longName;

      this.updateGroup(this.selectedGroup);
    } else {
      this.createGroup({
        longName: this.longName,
        groupName: this.groupName,
        active: true,
        groupSource: 'alanda',
      });
    }
  }

  private updateGroup(group: AlandaGroup) {
    this.groupService.update(group).subscribe(
      (res) => {
        this.messageService.add({
          severity: 'success',
          summary: 'Update User',
          detail: 'Group has been updated',
        });
      },
      (error) =>
        this.messageService.add({
          severity: 'error',
          summary: 'Update User',
          detail: error.message,
        }),
    );
  }

  private createGroup(group: AlandaGroup) {
    this.groupService.save(group).subscribe(
      (res) => {
        this.messageService.add({
          severity: 'success',
          summary: 'Create New Group',
          detail: 'Group has been created',
        });
        this.turboTable.reset();
      },
      (error) =>
        this.messageService.add({
          severity: 'error',
          summary: 'Create Group',
          detail: error.message,
        }),
    );
  }

  get groupName(): string {
    return this.groupForm.get('groupName').value;
  }

  get longName(): string {
    return this.groupForm.get('longName').value;
  }

  private fillGroupForm(group: AlandaGroup) {
    this.groupForm.patchValue(group);
  }

  onLoadGroups(event: LazyLoadEvent) {
    this.loading = true;
    const serverOptions: ServerOptions = {
      pageNumber: event.first / event.rows + 1,
      pageSize: event.rows,
      filterOptions: {},
      sortOptions: {},
    };

    if (event.sortField) {
      const sortOptions = {};
      const dir = event.sortOrder === 1 ? 'asc' : 'desc';
      sortOptions[event.sortField] = { dir, prio: 0 };
      serverOptions.sortOptions = sortOptions;
    }

    for (const f in event.filters) {
      if (event.filters[f].value) {
        serverOptions.filterOptions[f] = event.filters[f].value;
      }
    }
    this.groupService.getGroups(serverOptions).subscribe((result) => {
      this.loading = false;
      this.groups = result.results;
      this.totalRecords = this.groups.length;
    });
  }

  onGroupSelected(event) {
    this.selectedGroup = event.data;
    this.fillGroupForm(this.selectedGroup);
    this.loadRoles();
    this.loadUsers();
    this.loadPermissions();
  }

  onGroupUnselected() {
    this.selectedGroup = null;
    this.initGroupForm();
  }

  private loadUsers() {
    this.usersInSelectedGroup = [];
    this.userService
      .getUsersByGroupId(this.selectedGroup.guid)
      .subscribe((res) => {
        this.usersInSelectedGroup = res;
      });
  }

  private loadRoles() {
    this.assignedRoles = [...this.selectedGroup.roles];
    this.roleService.getRoles().subscribe((result) => {
      this.availableRoles = result.filter((all) => {
        return (
          this.assignedRoles.filter((assigned) => {
            return assigned.guid === all.guid;
          }).length === 0
        );
      });
    });
  }

  private loadPermissions() {
    this.groupService
      .getEffectivePermissionsForGroup(this.selectedGroup.guid)
      .pipe(
        mergeMap((permissions: AlandaPermission[]) => {
          this.grantedPermissions = permissions;
          return this.permissionService.getPermissions();
        }),
      )
      .subscribe((result) => {
        this.availablePermissions = result.filter((all) => {
          return (
            this.grantedPermissions.filter((assigned) => {
              return assigned.guid === all.guid;
            }).length === 0
          );
        });
      });
  }

  updateRoles() {
    this.groupService
      .updateRolesForGroup(this.selectedGroup.guid, this.assignedRoles)
      .subscribe(
        (res) => {
          this.messageService.add({
            severity: 'success',
            summary: 'Update roles',
            detail: 'Roles have been updated',
          });
          this.turboTable.reset();
        },
        (error) =>
          this.messageService.add({
            severity: 'error',
            summary: 'Update roles',
            detail: error.message,
          }),
      );
  }

  updatePermissions() {
    this.groupService
      .updatePermissionsForGroup(
        this.selectedGroup.guid,
        this.grantedPermissions,
      )
      .subscribe(
        (res) => {
          this.messageService.add({
            severity: 'success',
            summary: 'Update permissions',
            detail: 'Permissions have been updated',
          });
          this.turboTable.reset();
        },
        (error) =>
          this.messageService.add({
            severity: 'error',
            summary: 'Update permissions',
            detail: error.message,
          }),
      );
  }
}
