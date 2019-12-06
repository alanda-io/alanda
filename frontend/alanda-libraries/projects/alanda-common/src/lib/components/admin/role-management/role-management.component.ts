import { Component, OnInit, ViewEncapsulation, ViewChild } from "@angular/core";
import { PmcRoleServiceNg } from "../../../api/pmcrole.service";
import { FormGroup, Validators, FormBuilder } from "@angular/forms";
import { PmcPermissionServiceNg } from "../../../api/pmcpermission.service";
import { PmcRole } from "../../../models/pmcRole";
import { PmcPermission } from "../../../models/pmcPermission";
import { PmcUser } from "../../../models/pmcUser";
import { Table } from "primeng/table";
import { MessageService, LazyLoadEvent } from "primeng/api";
import { PmcUserServiceNg } from "../../../api/pmcuser.service";

@Component({
  selector: 'alanda-role-management',
  templateUrl: './role-management.component.html',
  styleUrls: ['./role-management.component.css'],
  encapsulation: ViewEncapsulation.None,

})
export class RoleManagementComponent implements OnInit {

    roles: PmcRole[];
    selectedRole: PmcRole;
    usersWithRole: PmcUser[] = [];
    loading: boolean;
    
    roleForm: FormGroup;
    availablePermissions: PmcPermission[] = [];
    grantedPermissions: PmcPermission[] = [];

    roleColumns = [
      {field: 'guid', header: 'Guid'},
      {field: 'name', header: 'Name'},
    ];

    @ViewChild('table') turboTable: Table;

    constructor(private pmcRoleService: PmcRoleServiceNg,
                private pmcPermissionService: PmcPermissionServiceNg,
                private messageService: MessageService,
                private pmcUserService: PmcUserServiceNg,
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
      this.pmcRoleService.getRoles().subscribe(res => {
        this.roles = res;
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

    private updateRole(pmcRole: PmcRole) {
      this.pmcRoleService.update(pmcRole).subscribe(
        res => {
          this.messageService.add({severity:'success', summary:'Update Role', detail: 'Role has been updated'})
          this.turboTable.reset();
        },
        error => this.messageService.add({severity:'error', summary:'Update Role', detail: error.message}));
    }

    private createRole(pmcRole: PmcRole) {
      this.pmcRoleService.save(pmcRole).subscribe(
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

    private fillRoleForm(role: PmcRole) {
      this.roleForm.patchValue(role);
    }

    private loadPermissions() {
      this.grantedPermissions = [...this.selectedRole.permissions];
      this.pmcPermissionService.getPermissions()
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
      this.pmcRoleService.update(this.selectedRole).subscribe(
        res => {
          this.messageService.add({severity:'success', summary:'Update permissions', detail: 'Permissions have been updated'})
          this.turboTable.reset();
        },
        error => this.messageService.add({severity:'error', summary:'Update permissions', detail: error.message})
      );
    }
}