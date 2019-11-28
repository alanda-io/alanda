import { Component, OnInit, ViewEncapsulation, ViewChild } from "@angular/core";
import { FormGroup, Validators, FormBuilder } from "@angular/forms";
import { PmcPermissionServiceNg } from "../../../api/pmcpermission.service";
import { PmcPermission } from "../../../models/pmcPermission";
import { Table } from "primeng/table";
import { MessageService, LazyLoadEvent } from "primeng/api";

@Component({
  selector: 'alanda-permission-management',
  templateUrl: './permission-management.component.html',
  styleUrls: ['./permission-management.component.css'],
  encapsulation: ViewEncapsulation.None,

})
export class PermissionManagementComponent implements OnInit {

    permissions: PmcPermission[];
    selectedPermission: PmcPermission;
    loading: boolean;
    permissionColumns = [
      {field: 'guid', header: 'Guid'},
      {field: 'key', header: 'Key'},
    ];
    permissionForm: FormGroup;

    @ViewChild('table') turboTable: Table;

    constructor(private pmcPermissionService: PmcPermissionServiceNg, private fb: FormBuilder, private messageService: MessageService) {}

    ngOnInit() {
      this.initPermissionForm();
    }

    private initPermissionForm() {
      this.permissionForm = this.fb.group({
        key: ['', Validators.required]
      });
    }

    onLoadPermissions(event: LazyLoadEvent) {
      this.pmcPermissionService.getPermissions().subscribe(res => {
          this.permissions = res;
        }
      );
    }

    onFormSubmit() {
      if(!this.permissionForm.valid) {
        this.permissionForm.markAsDirty();
        return;
      }
      if(this.selectedPermission) {
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

    private fillPermissionForm(permission: PmcPermission) {
      this.permissionForm.patchValue(permission);
    }

    get key(): string {
      return this.permissionForm.get('key').value;
    }

    private createPermission(permission: PmcPermission) {
      this.pmcPermissionService.save(permission).subscribe(
        res => {
          this.messageService.add({severity:'success', summary:'Create permission', detail: 'Permission has been created'})
          this.turboTable.reset();
        },
        error => this.messageService.add({severity:'error', summary:'Create permission', detail: error.message}));
    }

    private updatePermission(permission: PmcPermission){
      this.pmcPermissionService.update(permission).subscribe(
        res => {
          this.messageService.add({severity:'success', summary:'Update permission', detail: 'Permission has been updated'})
      },
      error => this.messageService.add({severity:'error', summary:'Update permission', detail: error.message}));
    }
}