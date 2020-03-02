import { Component, OnInit, ViewEncapsulation, ViewChild } from "@angular/core";
import { PmcUserServiceNg } from "../../../api/pmcuser.service";
import { LazyLoadEvent, MessageService } from "primeng/components/common/api";
import { PmcGroupServiceNg } from "../../../api/pmcgroup.service";
import { mergeMap } from "rxjs/operators";
import { PmcRoleServiceNg } from "../../../api/pmcrole.service";
import { PmcPermissionServiceNg } from "../../../api/pmcpermission.service";
import { PmcUser } from "../../../models/pmcUser";
import { PmcGroup } from "../../../models/pmcGroup";
import { PmcPermission } from "../../../models/pmcPermission";
import { ServerOptions } from "../../../models/serverOptions";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { Table } from "primeng/table";
import { PmcRole } from "../../../models/pmcRole";

@Component({
  selector: 'alanda-user-management',
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.css'],
  encapsulation: ViewEncapsulation.None,
})
export class UserManagementComponent implements OnInit {

    users: PmcUser[] = [];
    totalRecords: number;
    selectedUser: PmcUser;
    loading = true;
    availableGroups: PmcGroup[] = [];
    assignedGroups: PmcGroup[] = [];
    availablePermissions: PmcPermission[] = [];
    grantedPermissions: PmcPermission[] = [];
    availableRoles: PmcRole[] = [];
    assignedRoles: PmcRole[] = [];

    userColumns = [
      {field: 'loginName', header: 'Login'},
      {field: 'firstName', header: 'First Name'},
      {field: 'surname', header: 'Surname'}
    ];

    @ViewChild('table') turboTable: Table;
    userForm: FormGroup;

    constructor(private pmcUserService: PmcUserServiceNg,
                private pmcGroupService: PmcGroupServiceNg,
                private pmcRoleService: PmcRoleServiceNg,
                private pmcPermissionService: PmcPermissionServiceNg,
                private messageService: MessageService,
                private fb: FormBuilder) {
    }

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
      if(!this.userForm.valid) {
        this.userForm.markAsDirty();
        return;
      }
      if(this.selectedUser) {
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
          roles: []
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

    private fillUserForm(user: PmcUser) {
      this.userForm.patchValue(user);
    }

    onLoadUsers(event: LazyLoadEvent) {
      this.loading = true;
      let serverOptions: ServerOptions = {
        pageNumber: (event.first/event.rows) + 1 ,
        pageSize: event.rows,
        filterOptions: {},
        sortOptions: {}
      }
      if (event.sortField){
        let sortOptions = {}
        const dir = event.sortOrder == 1 ? 'asc' : 'desc';
        sortOptions[event.sortField] = {dir: dir, prio: 0}
        serverOptions.sortOptions = sortOptions;
      }
      for (const filter in event.filters) {
        serverOptions.filterOptions[filter] = event.filters[filter].value
      }
      this.pmcUserService.getUsers(serverOptions)
      .subscribe(
        result => {
          this.loading = false;
          this.users = result.results;
          this.totalRecords = result.total;
        },
        error => {
          this.loading = false;
          this.messageService.add({severity:'error', summary:'Get Users', detail: error.message});
        });
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

    private createUser(user: PmcUser) {
      this.pmcUserService.save(user).subscribe(
        res => {
          this.messageService.add({severity:'success', summary:'Create New User', detail: 'User has been created'});
          this.turboTable.reset();
        },
        error => this.messageService.add({severity:'error', summary:'Create User', detail: error.message}));
    }

    private updateUser(user: PmcUser) {
      let stringGroups: string[] = new Array<string>();
      this.assignedGroups.forEach(item => {
        stringGroups.push(item.groupName);
      });
      user.groups = stringGroups;
        this.pmcUserService.updateUser(user).subscribe(
          res => {
            this.messageService.add({severity:'success', summary:'Update User', detail:'User has been updated'})
          },
          error => this.messageService.add({severity:'error', summary:'Update User', detail: error.message}));
    }


    private loadGroups() {
      let serverOptions: ServerOptions = {
        pageNumber: 1,
        pageSize: 999999,
        filterOptions: {},
        sortOptions: {}
      }
      this.pmcUserService.getGroupsForUser(this.selectedUser.loginName)
      .pipe(
        mergeMap((groups:PmcGroup[]) => {
          this.assignedGroups = groups;
          return this.pmcGroupService.getGroups(serverOptions);
        }),
      )
      .subscribe(
        result => {
          this.availableGroups = result.results.filter(all => {
            return this.assignedGroups.filter(assigned => {
              return assigned.guid === all.guid;
            }).length == 0;
          });
        },
        error => this.messageService.add({severity:'error', summary:'Get Groups For User', detail: error.message}));
    }

    private loadRoles() {
      this.pmcUserService.getEffectiveRolesForUser(this.selectedUser.guid)
      .pipe(
        mergeMap(roles => {
          this.assignedRoles = roles;
          return this.pmcRoleService.getRoles();
        })
      )
      .subscribe(
        result => {
          this.availableRoles = result.filter(all => {
            return this.assignedRoles.filter(assigned => {
              return assigned.guid === all.guid;
            }).length == 0;
          });
        },
        error => this.messageService.add({severity:'error', summary:'Load Roles', detail: error.message}));
    }

    private loadPermissions(): void{
      this.pmcUserService.getEffectivePermissionsForUser(this.selectedUser.guid)
      .pipe(
        mergeMap((permissions: PmcPermission[]) => {
          this.grantedPermissions = permissions;
          return this.pmcPermissionService.getPermissions();
        })
      )
      .subscribe(
        result => {
          this.availablePermissions = result.filter(all => {
            return this.grantedPermissions.filter(assigned => {
              return assigned.guid === all.guid;
            }).length == 0;
          });
        },
        error => this.messageService.add({severity:'error', summary:'Load Permissions', detail: error.message}));
    }

    runAsUser() {
      this.pmcUserService.runAsUser(this.selectedUser.loginName).subscribe(
        user => this.messageService.add({severity:'success', summary:'Run As User', detail: `Run as ${user.loginName}`}),
        error => this.messageService.add({severity:'error', summary:'Run As User', detail: error.message})
      );
    }


    onUpdateGroups() {
      let stringGroups: string[] = new Array<string>();
      this.assignedGroups.forEach(item => {
        stringGroups.push(item.groupName);
      });
      this.selectedUser.groups = stringGroups;

      this.pmcUserService.updateUser(this.selectedUser).subscribe(
        result => this.messageService.add({severity:'success', summary:'Update Groups', detail: 'Groups have been updated'}),
        error => this.messageService.add({severity:'error', summary:'Update Groups', detail: error.message})
      );
    }

    onUpdateRoles() {
      this.pmcUserService.updateRolesForUser(this.selectedUser.guid, this.assignedRoles).subscribe(
        result => this.messageService.add({severity:'success', summary:'Update Roles', detail: 'Roles have been updated'}),
        error => this.messageService.add({severity:'error', summary:'Update Roles', detail: error.message})
      );
    }

    onUpdatePermissions() {
      this.pmcUserService.updatePermissionsForUser(this.selectedUser.guid, this.grantedPermissions).subscribe(
        result => this.messageService.add({severity:'success', summary:'Update Permissions', detail: 'Permissions have been updated'}),
        error => this.messageService.add({severity:'error', summary:'Update Permissions', detail: error.message})
      );
    }

    moveRole(event) {
      event.items.forEach(item => {
        if(item.source) {
          this.assignedRoles.push(item);
          this.availableRoles.splice(this.availableRoles.indexOf(item), 1);
        }
      })
    }

    movePermission(event) {
      event.items.forEach(item => {
        if(item.source) {
          this.grantedPermissions.push(item);
          this.availablePermissions.splice(this.availablePermissions.indexOf(item), 1);
        }
      })
    }

    onTabChange(event) {
      if(event.index === 1) {
        this.loadGroups();
      }
      if(event.index === 2) {
        this.loadRoles();
      }
      if(event.index === 3) {
        this.loadPermissions();
      }
    }
  }
