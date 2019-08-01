import { Component, OnInit, OnDestroy, ViewEncapsulation } from "@angular/core";
import { Subject, Subscription, Observable } from "rxjs";
import { FormGroup, FormControl, Validators } from "@angular/forms";
import { PmcPermission } from "../../../models/PmcPermission";
import { PmcPermissionServiceNg } from "../../../services/rest/pmcpermission.service";

@Component({
  selector: 'permission-admin',
  templateUrl: './permission.component.html',
  styleUrls: ['./permission.component.css'],
  encapsulation: ViewEncapsulation.None,

})
export class PermissionComponent implements OnInit, OnDestroy {

    permissions: PmcPermission[];
    selectedPermission: PmcPermission;
    private selectedPermissionUpdated = new Subject<PmcPermission>();
    private permissionSubscription: Subscription;
    totalPermissions: number;
    loading: boolean;
    permissionColumns: any[];
    actionLabel: string;
    form: FormGroup;
    creationLabel: string;

    constructor(private pmcPermissionService: PmcPermissionServiceNg) {
    }

    ngOnDestroy() {
      this.permissionSubscription.unsubscribe();
    }

    ngOnInit(): void {
      this.form = new FormGroup({
        id: new FormControl(null),
        key: new FormControl(null, {validators: [Validators.required]}),
      });
      this.form.disable();
      this.permissionColumns = [
        {field: 'guid', header: 'Guid'},
        {field: 'key', header: 'Key'},
      ];

      this.permissionSubscription = this.getselectedPermissionUpdateListener().subscribe(
        (permission: PmcPermission) => {
            console.log("onPermissionChange", permission);
            this.form.setValue({
              id: permission.guid,
              key: permission.key,
            });
        }
      );

      this.actionLabel = "Edit Permission";
      this.creationLabel = "Create new Permission";
      this.loading = true;
      this.pmcPermissionService.getPermissions()
      .subscribe(result => {
        this.loading = false;
        this.permissions = result;
        this.totalPermissions = result.length;
      });
    }

    getselectedPermissionUpdateListener(): Observable<PmcPermission>{
      return this.selectedPermissionUpdated.asObservable();
    }

    onRowSelect(event) {
      this.actionLabel = "Edit Permission";
      this.creationLabel = "Create new Permission";
      this.form.disable();
      this.selectedPermission = event.data;
      this.selectedPermissionUpdated.next(event.data);
    }

    onCreateNewPermission(){
      if(this.creationLabel === "Create new Permission"){
        this.creationLabel = "Save Permission";
        this.selectedPermission = null;
        this.form.reset();
        this.form.enable();
        this.form.get('id').disable();
      } else {
        if(this.form.valid){
          let permission = new PmcPermission();
          permission.key = this.form.get('key').value;
          this.pmcPermissionService.save(permission).subscribe(
            (res) => {
              this.form.reset();
              this.form.disable();
              this.pmcPermissionService.getPermissions()
              .subscribe(result => {
                this.loading = false;
                this.permissions = result;
                this.totalPermissions = result.length;
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
        this.actionLabel = "Edit Permission";
        this.form.disable();
        this.pmcPermissionService.getPermissionByGuid(this.selectedPermission.guid)
        .subscribe(result => {
          this.selectedPermission = result;
        });
      }
    }

    onUpdatePermission(){
      this.actionLabel = "Edit Permission";
      this.form.disable();
      this.selectedPermission.key = this.form.get('key').value
      this.pmcPermissionService.update(this.selectedPermission).subscribe(
        (res) => {
          this.selectedPermission = res;
          this.selectedPermissionUpdated.next(res);
        }); 
    }
}