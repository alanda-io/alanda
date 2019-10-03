import { Component, OnInit, OnDestroy, ViewEncapsulation } from "@angular/core";
import { PmcUserServiceNg } from "../../../api/pmcuser.service";
import { LazyLoadEvent, MessageService } from "primeng/components/common/api";
import { PmcGroupServiceNg } from "../../../api/pmcgroup.service";
import { mergeMap } from "rxjs/operators";
import { Subject, Subscription, Observable } from "rxjs";
import { PmcRoleServiceNg } from "../../../api/pmcrole.service";
import { FormGroup, FormControl, Validators } from "@angular/forms";
import { PmcPermissionServiceNg } from "../../../api/pmcpermission.service";
import { PmcUser } from "../../../models/pmcUser";
import { PmcDepartment } from "../../../models/pmcDepartment";
import { PmcGroup } from "../../../models/pmcGroup";
import { PmcPermission } from "../../../models/pmcPermission";
import { ServerOptions } from "../../../models/serverOptions";

@Component({
  selector: 'user-admin',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css'],
  encapsulation: ViewEncapsulation.None,
})
export class UserComponent implements OnInit, OnDestroy {

    users: PmcUser[];
    selectedUser: PmcUser;
    private selectedUserUpdated = new Subject<PmcUser>();
    private userSubscription: Subscription;
    totalUsers: number;
    loading: boolean;
    userColumns: any[];
    departmentList: PmcDepartment[];
    userAction: string;
    userForm: FormGroup;
    availableGroups: PmcGroup[];
    assignedGroups: PmcGroup[];
    availablePermissions: PmcPermission[];
    grantedPermissions: PmcPermission[];
    userCreationLabel: string;

    availableRoles: any[];
    assignedRoles: any[];

    constructor(private pmcUserService: PmcUserServiceNg,
                private pmcGroupService: PmcGroupServiceNg,
                private pmcRoleService: PmcRoleServiceNg,
                private pmcPermissionService: PmcPermissionServiceNg,
                private messageService: MessageService) {
    }

    ngOnDestroy() {
      this.userSubscription.unsubscribe();
    }

    ngOnInit(): void {
      this.userForm = new FormGroup({
        id: new FormControl(null),
        login: new FormControl(null, {validators: [Validators.required]}),
        firstname: new FormControl(null, {validators: [Validators.required]}),
        surname: new FormControl(null, {validators: [Validators.required]}),
        email: new FormControl(null, {validators: [Validators.required]}),
        mobile: new FormControl(null),
        locked: new FormControl(null)
      });
      this.userForm.disable();
      this.userColumns = [
        {field: 'loginName', header: 'Login'},
        {field: 'firstName', header: 'First Name'},
        {field: 'surname', header: 'Surame'}
      ];

      this.userSubscription = this.getSelectedUserUpdateListener().subscribe(
        (user: PmcUser) => {
            this.userForm.setValue({
              id: user.guid,
              login: user.loginName,
              firstname: user.firstName,
              surname: user.surname,
              email: user.email,
              mobile: user.mobile,
              locked: user.locked
            });
          this.loadGroups(user.loginName);
          this.loadPermissions(user.guid);
          this.loadRoles(user.guid);
        }
      );

      this.userAction = "Edit User";
      this.userCreationLabel = "Create new User";
      this.loading = true;
    }

    getSelectedUserUpdateListener(): Observable<PmcUser>{
      return this.selectedUserUpdated.asObservable();
    }

    loadUsersLazy(event: LazyLoadEvent) {
      this.loading = true;

      let serverOptions: ServerOptions = {
        pageNumber: (event.first/event.rows) + 1 ,
        pageSize: event.rows,
        filterOptions: {},
        sortOptions: {}
      }

      if(event.sortField){
        let sortOptions = {}
        const dir = event.sortOrder == 1 ? "asc" : "desc";
        sortOptions[event.sortField] = {dir: dir, prio: 0}
        serverOptions.sortOptions = sortOptions;
      }

      for (let f in event.filters) {
        serverOptions.filterOptions[f] = event.filters[f].value
      }
      this.pmcUserService.getUsers(serverOptions)
      .subscribe(
        result => {
          this.loading = false;
          this.users = result.results;
          this.totalUsers = result.total;
        },
        error => {
          this.messageService.add({severity:'error', summary:'Get Users', detail: error.message});
          this.loading = false;
        });
    }

    onRowSelect(event) {
      this.userAction = "Edit User";
      this.userCreationLabel = "Create new User";
      this.userForm.disable();
      this.pmcUserService.getUserByLogin(event.data.loginName)
      .subscribe(
        result => {
          this.selectedUser = result;
          this.selectedUserUpdated.next(result);
        },
        error => this.messageService.add({severity:'error', summary:'Get User By Login', detail: error.message}));
    }

    private loadGroups(loginName: string):void {
      let serverOptions: ServerOptions = {
        pageNumber: 1,
        pageSize: 999999,
        filterOptions: {},
        sortOptions: {}
      }
      this.pmcUserService.getGroupsForUser(loginName)
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

    private loadRoles(userGuid: number):void {
      this.pmcUserService.getEffectiveRolesForUser(userGuid)
      .pipe(
        mergeMap((roles:any[]) => {
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

    private loadPermissions(userGuid: number): void{
      this.pmcUserService.getEffectivePermissionsForUser(userGuid)
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

    onCreateNewUser(){
      if(this.userCreationLabel === "Create new User"){
        this.userCreationLabel = "Save User";
        this.selectedUser = null;
        this.userForm.reset();
        this.userForm.enable();
        this.userForm.get('id').disable();
        this.userForm.get('locked').setValue(false);
      } else {
        if(this.userForm.valid){
          let user = new PmcUser();
          user.email = this.userForm.get('email').value;
          user.loginName = this.userForm.get('login').value;
          user.firstName = this.userForm.get('firstname').value;
          user.surname = this.userForm.get('surname').value;
          user.locked = this.userForm.get('locked').value;
          user.mobile = this.userForm.get('mobile').value;
          user.roles = [];
          user.sso = true;
          user.groups = [];
          this.pmcUserService.save(user).subscribe(
            res => {
              this.messageService.add({severity:'success', summary:'Create New User', detail: 'User has been created'}),
              this.userForm.reset();
              this.userForm.disable();
            },
            error => this.messageService.add({severity:'error', summary:'Create New User', detail: error.message}));
        }
      }
    }

    toggleEdit() {
      if(this.userForm.disabled){
        this.userForm.enable();
        this.userForm.get('id').disable();
        this.userForm.get('login').disable();
        this.userAction = "Cancel";
      } else {
        this.userAction = "Edit User";
        this.userForm.disable();
        this.pmcUserService.getUser(this.selectedUser.guid)
        .subscribe(result => {
          this.selectedUser = result;
        });
      }
    }

    updateGroups():void {
      let stringGroups: string[] = new Array<string>(); 
      this.assignedGroups.forEach(item => {
        stringGroups.push(item.groupName);
      });
      this.selectedUser.groups = stringGroups;
      console.log(this.selectedUser);

      this.pmcUserService.updateUser(this.selectedUser).subscribe(
        result => this.messageService.add({severity:'success', summary:'Update Groups', detail:`Successfully updated groups for user ${this.selectedUser.loginName}`}),
        error => this.messageService.add({severity:'error', summary:'Update Groups', detail: error.message})
      );
    }

    updateRoles():void {
      this.pmcUserService.updateRolesForUser(this.selectedUser.guid, this.assignedRoles).subscribe(
        result => this.messageService.add({severity:'success', summary:'Update Roles', detail:`Successfully updated roles for user ${this.selectedUser.loginName}`}),
        error => this.messageService.add({severity:'error', summary:'Update Roles', detail: error.message})
      );
    }

    updatePermissions():void {
      this.pmcUserService.updatePermissionsForUser(this.selectedUser.guid, this.grantedPermissions).subscribe(
        result => this.messageService.add({severity:'success', summary:'Update Permissions', detail:`Successfully updated permissions for user ${this.selectedUser.loginName}`}),
        error => this.messageService.add({severity:'error', summary:'Update Permissions', detail: error.message})
      );
    }

    onUpdateUser(){
      this.userAction = "Edit User";
      this.userForm.disable();
      /**
       * Temporary Solution to make user update work. 
       */
      let stringGroups: string[] = new Array<string>(); 
      this.assignedGroups.forEach(item => {
        stringGroups.push(item.groupName);
      });
      this.selectedUser.groups = stringGroups;
      //
      this.selectedUser.firstName = this.userForm.get('firstname').value
      this.selectedUser.surname = this.userForm.get('surname').value
      this.selectedUser.email = this.userForm.get('email').value
      this.selectedUser.locked = this.userForm.get('locked').value
      this.selectedUser.loginName = this.userForm.get('login').value
      this.selectedUser.mobile = this.userForm.get('mobile').value
      this.pmcUserService.updateUser(this.selectedUser).subscribe(
        (res) => {
          this.messageService.add({severity:'success', summary:'Update User', detail:'Successfully updated user'}),
          this.selectedUser = res;
          this.selectedUserUpdated.next(res);
        },
        error => this.messageService.add({severity:'error', summary:'Update User', detail: error.message})); 
    }
}