import { Component, OnInit, ViewChild, Output } from '@angular/core';
import { AlandaGroup } from '../../../api/models/group';
import { AlandaPermission } from '../../../api/models/permission';
import { AlandaRole } from '../../../api/models/role';
import { AlandaUser } from '../../../api/models/user';
import { Table } from 'primeng/table';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { AlandaRoleApiService } from '../../../api/roleApi.service';
import { AlandaPermissionApiService } from '../../../api/permissionApi.service';
import { MessageService, LazyLoadEvent } from 'primeng/api';
import { ServerOptions } from '../../../models/serverOptions';
import { mergeMap } from 'rxjs/operators';
import { AlandaUserApiService } from '../../../api/userApi.service';
import { AlandaGroupApiService } from '../../../api/groupApi.service';
import { Subject } from 'rxjs';

@Component({
  selector: 'alanda-user-management',
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.scss'],
})
export class AlandaUserManagementComponent implements OnInit {
  users: AlandaUser[] = [];
  selectedUser: AlandaUser;
  loading = true;
  totalRecords: number;
  availableGroups: AlandaGroup[] = [];
  assignedGroups: AlandaGroup[] = [];
  availablePermissions: AlandaPermission[] = [];
  grantedPermissions: AlandaPermission[] = [];
  availableRoles: AlandaRole[] = [];
  assignedRoles: AlandaRole[] = [];

  userColumns = [
    { field: 'loginName', header: 'Login' },
    { field: 'firstName', header: 'First Name' },
    { field: 'surname', header: 'Surname' },
  ];

  @ViewChild('table') turboTable: Table;
  userForm: FormGroup;

  constructor(
    private readonly userService: AlandaUserApiService,
    private readonly groupService: AlandaGroupApiService,
    private readonly roleService: AlandaRoleApiService,
    private readonly permissionService: AlandaPermissionApiService,
    private readonly messageService: MessageService,
    private readonly fb: FormBuilder,
  ) {}

  ngOnInit() {
    this.initUserForm();
  }

  private initUserForm() {
    this.userForm = this.fb.group({
      loginName: ['', Validators.required],
      firstName: ['', Validators.required],
      surname: ['', Validators.required],
      email: ['', Validators.required],
      mobile: [''],
      locked: [false],
    });
  }

  onFormSubmit() {
    if (!this.userForm.valid) {
      this.userForm.markAsDirty();
      return;
    }
    if (this.selectedUser) {
      this.selectedUser.loginName = this.login;
      this.selectedUser.firstName = this.firstName;
      this.selectedUser.surname = this.lastName;
      this.selectedUser.email = this.email;
      this.selectedUser.mobile = this.mobile;
      this.selectedUser.locked = this.locked;
      this.updateUser(this.selectedUser);
    } else {
      this.createUser({
        loginName: this.login,
        firstName: this.firstName,
        surname: this.lastName,
        email: this.email,
        mobile: this.mobile,
        locked: this.locked,
        sso: true,
        groups: [],
        roles: [],
      });
    }
  }

  get login(): string {
    return this.userForm.get('loginName').value;
  }

  get firstName(): string {
    return this.userForm.get('firstName').value;
  }

  get lastName(): string {
    return this.userForm.get('surname').value;
  }

  get email(): string {
    return this.userForm.get('email').value;
  }

  get mobile(): string {
    return this.userForm.get('mobile').value;
  }

  get locked(): boolean {
    return this.userForm.get('locked').value;
  }

  private fillUserForm(user: AlandaUser) {
    this.userForm.patchValue(user);
  }

  onLoadUsers(event: LazyLoadEvent) {
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
    for (const filter in event.filters) {
      serverOptions.filterOptions[filter] = event.filters[filter].value;
    }
    this.userService.getUsers(serverOptions).subscribe(
      (result) => {
        this.loading = false;
        this.users = result.results;
        this.totalRecords = result.total;
      },
      (error) => {
        this.loading = false;
        this.messageService.add({
          severity: 'error',
          summary: 'Get Users',
          detail: error.message,
        });
      },
    );
  }

  onUserSelected(event: any) {
    this.selectedUser = event.data;
    this.fillUserForm(this.selectedUser);
    this.loadRoles();
    this.loadGroups();
    this.loadPermissions();
  }

  onUserUnselected() {
    this.selectedUser = null;
    this.initUserForm();
  }

  private createUser(user: AlandaUser) {
    this.userService.save(user).subscribe(
      (res) => {
        this.messageService.add({
          severity: 'success',
          summary: 'Create New User',
          detail: 'User has been created',
        });
        this.turboTable.reset();
      },
      (error) =>
        this.messageService.add({
          severity: 'error',
          summary: 'Create User',
          detail: error.message,
        }),
    );
  }

  private updateUser(user: AlandaUser) {
    const stringGroups: string[] = new Array<string>();
    this.assignedGroups.forEach((item) => {
      stringGroups.push(item.groupName);
    });
    user.groups = stringGroups;
    this.userService.updateUser(user).subscribe(
      (res) => {
        this.messageService.add({
          severity: 'success',
          summary: 'Update User',
          detail: 'User has been updated',
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

  private loadGroups() {
    const serverOptions: ServerOptions = {
      pageNumber: 1,
      pageSize: 999999,
      filterOptions: {},
      sortOptions: {},
    };
    this.userService
      .getGroupsForUser(this.selectedUser.loginName)
      .pipe(
        mergeMap((groups: AlandaGroup[]) => {
          this.assignedGroups = groups;
          return this.groupService.getGroups(serverOptions);
        }),
      )
      .subscribe(
        (result) => {
          this.availableGroups = result.results.filter((all) => {
            return (
              this.assignedGroups.filter((assigned) => {
                return assigned.guid === all.guid;
              }).length === 0
            );
          });
        },
        (error) =>
          this.messageService.add({
            severity: 'error',
            summary: 'Get Groups For User',
            detail: error.message,
          }),
      );
  }

  private loadRoles() {
    this.userService
      .getEffectiveRolesForUser(this.selectedUser.guid)
      .pipe(
        mergeMap((roles) => {
          this.assignedRoles = roles;
          return this.roleService.getRoles();
        }),
      )
      .subscribe(
        (result) => {
          this.availableRoles = result.filter((all) => {
            return (
              this.assignedRoles.filter((assigned) => {
                return assigned.guid === all.guid;
              }).length === 0
            );
          });
        },
        (error) =>
          this.messageService.add({
            severity: 'error',
            summary: 'Load Roles',
            detail: error.message,
          }),
      );
  }

  private loadPermissions(): void {
    this.userService
      .getEffectivePermissionsForUser(this.selectedUser.guid)
      .pipe(
        mergeMap((permissions: AlandaPermission[]) => {
          this.grantedPermissions = permissions;
          return this.permissionService.getPermissions();
        }),
      )
      .subscribe(
        (result) => {
          this.availablePermissions = result.filter((all) => {
            return (
              this.grantedPermissions.filter((assigned) => {
                return assigned.guid === all.guid;
              }).length === 0
            );
          });
        },
        (error) =>
          this.messageService.add({
            severity: 'error',
            summary: 'Load Permissions',
            detail: error.message,
          }),
      );
  }

  @Output()
  runAsUserClick = new Subject<string>();

  runAsUser() {
    this.runAsUserClick.next(this.selectedUser.loginName);
    // this.userService.runAsUser(this.selectedUser.loginName).subscribe(
    //   (user) =>
    //     this.messageService.add({
    //       severity: 'success',
    //       summary: 'Run As User',
    //       detail: `Run as ${user.loginName}`,
    //     }),
    //   (error) =>
    //     this.messageService.add({
    //       severity: 'error',
    //       summary: 'Run As User',
    //       detail: error.message,
    //     }),
    // );
  }

  onUpdateGroups() {
    const stringGroups: string[] = new Array<string>();
    this.assignedGroups.forEach((item) => {
      stringGroups.push(item.groupName);
    });
    this.selectedUser.groups = stringGroups;

    this.userService.updateUser(this.selectedUser).subscribe(
      (result) =>
        this.messageService.add({
          severity: 'success',
          summary: 'Update Groups',
          detail: 'Groups have been updated',
        }),
      (error) =>
        this.messageService.add({
          severity: 'error',
          summary: 'Update Groups',
          detail: error.message,
        }),
    );
  }

  onUpdateRoles() {
    this.userService
      .updateRolesForUser(this.selectedUser.guid, this.assignedRoles)
      .subscribe(
        (result) =>
          this.messageService.add({
            severity: 'success',
            summary: 'Update Roles',
            detail: 'Roles have been updated',
          }),
        (error) =>
          this.messageService.add({
            severity: 'error',
            summary: 'Update Roles',
            detail: error.message,
          }),
      );
  }

  onUpdatePermissions() {
    this.userService
      .updatePermissionsForUser(this.selectedUser.guid, this.grantedPermissions)
      .subscribe(
        (result) =>
          this.messageService.add({
            severity: 'success',
            summary: 'Update Permissions',
            detail: 'Permissions have been updated',
          }),
        (error) =>
          this.messageService.add({
            severity: 'error',
            summary: 'Update Permissions',
            detail: error.message,
          }),
      );
  }

  moveRole(event) {
    event.items.forEach((item) => {
      if (item.source) {
        this.assignedRoles.push(item);
        this.availableRoles.splice(this.availableRoles.indexOf(item), 1);
      }
    });
  }

  movePermission(event) {
    event.items.forEach((item) => {
      if (item.source) {
        this.grantedPermissions.push(item);
        this.availablePermissions.splice(
          this.availablePermissions.indexOf(item),
          1,
        );
      }
    });
  }

  onTabChange(event) {
    if (event.index === 1) {
      this.loadGroups();
    }
    if (event.index === 2) {
      this.loadRoles();
    }
    if (event.index === 3) {
      this.loadPermissions();
    }
  }
}
