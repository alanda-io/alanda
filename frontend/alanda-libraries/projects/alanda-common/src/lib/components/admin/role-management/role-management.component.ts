import { Component, ViewEncapsulation, OnInit, ViewChild } from '@angular/core';
import { AlandaPermission } from '../../../api/models/permission';
import { AlandaRole } from '../../../api/models/role';
import { AlandaUser } from '../../../api/models/user';
import { Table } from 'primeng/table';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { AlandaRoleApiService } from '../../../api/roleApi.service';
import { AlandaPermissionApiService } from '../../../api/permissionApi.service';
import { MessageService, LazyLoadEvent } from 'primeng/api';
import { AlandaUserApiService } from '../../../api/userApi.service';

@Component({
  selector: 'alanda-role-management',
  templateUrl: './role-management.component.html',
  styleUrls: ['./role-management.component.css'],
  encapsulation: ViewEncapsulation.None,

})
export class AlandaRoleManagementComponent implements OnInit {

    roles: AlandaRole[];
    selectedRole: AlandaRole;
    usersWithRole: AlandaUser[] = [];
    loading: boolean;
    totalRecords: number;

    roleForm: FormGroup;
    availablePermissions: AlandaPermission[] = [];
    grantedPermissions: AlandaPermission[] = [];

    roleColumns = [
      {field: 'guid', header: 'Guid'},
      {field: 'name', header: 'Name'},
    ];

    @ViewChild('table') turboTable: Table;

    constructor(private roleService: AlandaRoleApiService,
                private permissionService: AlandaPermissionApiService,
                private messageService: MessageService,
                private pmcUserService: AlandaUserApiService,
                private fb: FormBuilder) {}

    ngOnInit() {
      this.initRoleForm();
    }

    private initRoleForm() {
      this.roleForm = this.fb.group({
        name: ['', Validators.required]
      });
    }

    onLoadRoles(event: LazyLoadEvent) {
      this.roleService.getRoles().subscribe(res => {
        this.roles = res;
        this.totalRecords = this.roles.length;
      });
    }

    onFormSubmit() {
      if(!this.roleForm.valid) {
        this.roleForm.markAsDirty();
        return;
      }
      if(this.selectedRole) {
        this.selectedRole.name = this.roleName;

        this.updateRole(this.selectedRole);
      } else {
        this.createRole({
          idName: this.roleName
        });
      }
    }

    get roleName(): string {
     return this.roleForm.get('name').value;
    }

    private loadUsers() {
      this.usersWithRole = [];
      this.pmcUserService.getUsersForRole(this.selectedRole.guid).subscribe(users => {
        this.usersWithRole.push(...users);
      });
    }

    private updateRole(pmcRole: AlandaRole) {
      this.roleService.update(pmcRole).subscribe(
        res => {
          this.messageService.add({severity:'success', summary:'Update Role', detail: 'Role has been updated'})
          this.turboTable.reset();
        },
        error => this.messageService.add({severity:'error', summary:'Update Role', detail: error.message}));
    }

    private createRole(pmcRole: AlandaRole) {
      this.roleService.save(pmcRole).subscribe(
        res => {
          this.messageService.add({severity:'success', summary:'Create Role', detail: 'Role has been created'})
          this.turboTable.reset();
      },
      error => this.messageService.add({severity:'error', summary:'Create Role', detail: error.message}));
    }

    onRoleSelect(event) {
      this.selectedRole = event.data;
      this.fillRoleForm(this.selectedRole);
      this.loadPermissions();
      this.loadUsers();

    }

    onRoleUnselect() {
      this.selectedRole = null;
      this.initRoleForm();
    }

    private fillRoleForm(role: AlandaRole) {
      this.roleForm.patchValue(role);
    }

    private loadPermissions() {
      this.grantedPermissions = [...this.selectedRole.permissions];
      this.permissionService.getPermissions()
        .subscribe(result => {
          this.availablePermissions = result.filter(all => {
            return this.grantedPermissions.filter(assigned => {
              return assigned.guid === all.guid;
            }).length == 0;
          });
        });
    }

    updatePermissions() {
      this.selectedRole.permissions = [...this.grantedPermissions];
      this.roleService.update(this.selectedRole).subscribe(
        res => {
          this.messageService.add({severity:'success', summary:'Update permissions', detail: 'Permissions have been updated'})
          this.turboTable.reset();
        },
        error => this.messageService.add({severity:'error', summary:'Update permissions', detail: error.message})
      );
    }
}
