import { Component, OnInit, OnDestroy, ViewEncapsulation } from "@angular/core";
import { Subject, Subscription, Observable } from "rxjs";
import { PmcRoleServiceNg } from "../../../api/pmcrole.service";
import { FormGroup, FormControl, Validators } from "@angular/forms";
import { PmcPermissionServiceNg } from "../../../api/pmcpermission.service";
import { PmcRole } from "../../../models/pmcRole";
import { PmcPermission } from "../../../models/pmcPermission";

@Component({
  selector: 'role-admin',
  templateUrl: './role.component.html',
  styleUrls: ['./role.component.css'],
  encapsulation: ViewEncapsulation.None,

})
export class RoleComponent implements OnInit, OnDestroy {

    roles: PmcRole[];
    selectedRole: PmcRole;
    private selectedRoleUpdated = new Subject<PmcRole>();
    private roleSubscription: Subscription;
    totalRoles: number;
    loading: boolean;
    roleColumns: any[];
    actionLabel: string;
    form: FormGroup;
    availablePermissions: PmcPermission[];
    grantedPermissions: PmcPermission[];
    creationLabel: string;

    constructor(private pmcRoleService: PmcRoleServiceNg, private pmcPermissionService: PmcPermissionServiceNg) {}

    ngOnDestroy() {
      this.roleSubscription.unsubscribe();
    }

    ngOnInit(): void {
      this.form = new FormGroup({
        id: new FormControl(null),
        name: new FormControl(null, {validators: [Validators.required]}),
      });
      this.form.disable();
      this.roleColumns = [
        {field: 'guid', header: 'Guid'},
        {field: 'name', header: 'Name'},
      ];

      this.roleSubscription = this.getSelectedRoleUpdateListener().subscribe(
        (role: PmcRole) => {
            console.log("onRoleChange", role);
            this.form.setValue({
              id: role.guid,
              name: role.name,
            });
          this.loadPermissions();
        }
      );

      this.actionLabel = "Edit Role";
      this.creationLabel = "Create new Role";
      this.loading = true;
      this.pmcRoleService.getRoles()
      .subscribe(result => {
        this.loading = false;
        this.roles = result;
        this.totalRoles = result.length;
      });

    }

    getSelectedRoleUpdateListener(): Observable<PmcRole>{
      return this.selectedRoleUpdated.asObservable();
    }

    onRowSelect(event) {
      this.actionLabel = "Edit Role";
      this.creationLabel = "Create new Role";
      this.form.disable();
      this.selectedRole = event.data;
      this.selectedRoleUpdated.next(event.data);
    }

    private loadPermissions():void {
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

    onCreateNewRole(){
      if(this.creationLabel === "Create new Role"){
        this.creationLabel = "Save Role";
        this.selectedRole = null;
        this.form.reset();
        this.form.enable();
        this.form.get('id').disable();
      } else {
        if(this.form.valid){
          let role = new PmcRole();
          this.pmcRoleService.save(role).subscribe(
            (res) => {
              this.form.reset();
              this.form.disable();
              this.pmcRoleService.getRoles()
              .subscribe(result => {
                this.loading = false;
                this.roles = result;
                this.totalRoles = result.length;
              });
            });
        }
        else{
          console.log("form is not valid");
        }
      }
    }

    toggleEdit() {
      if(this.form.disabled){
        this.form.enable();
        this.form.get('id').disable();
        this.actionLabel = "Cancel";
      } else {
        this.actionLabel = "Edit Role";
        this.form.disable();
        this.pmcRoleService.getRoleByGuid(this.selectedRole.guid)
        .subscribe(result => {
          this.selectedRole = result;
        });
      }
    }

    updatePermissions():void {
      this.selectedRole.permissions = [...this.grantedPermissions];
      this.pmcRoleService.update(this.selectedRole).subscribe();
    }

    onUpdateRole(){
      this.actionLabel = "Edit Role";
      this.form.disable();
      this.selectedRole.name = this.form.get('name').value
      this.pmcRoleService.update(this.selectedRole).subscribe(
        (res) => {
          this.selectedRole = res;
          this.selectedRoleUpdated.next(res);
        }); 
    }
}