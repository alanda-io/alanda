import { Component, OnInit, ViewEncapsulation, ViewChild } from "@angular/core";
import { PmcGroupServiceNg } from "../../../api/pmcgroup.service";
import { mergeMap } from "rxjs/operators";
import { PmcRoleServiceNg } from "../../../api/pmcrole.service";
import { FormGroup,Validators, FormBuilder } from "@angular/forms";
import { PmcPermissionServiceNg } from "../../../api/pmcpermission.service";
import { PmcGroup } from "../../../models/pmcGroup";
import { PmcPermission } from "../../../models/pmcPermission";
import { ServerOptions } from "../../../models/serverOptions";
import { PmcUser } from "../../../models/pmcUser";
import { MessageService, LazyLoadEvent } from "primeng/api";
import { PmcUserServiceNg } from "../../../api/pmcuser.service";
import { Table } from "primeng/table";
import { PmcRole } from "../../../models/pmcRole";

@Component({
  selector: 'alanda-group-management',
  templateUrl: './group-management.component.html',
  styleUrls: ['./group-management.component.css'],
  encapsulation: ViewEncapsulation.None,

})
export class GroupManagementComponent implements OnInit {

  groups: PmcGroup[] = [];
  selectedGroup: PmcGroup;
  usersInSelectedGroup: PmcUser[] = [];

  availablePermissions: PmcPermission[] = [];
  grantedPermissions: PmcPermission[] = [];
  availableRoles: PmcRole[] = [];
  assignedRoles: PmcRole[] = [];

  loading = true;
  groupColumns = [
    {field: 'guid', header: 'Guid'},
    {field: 'groupName', header: 'Group Name'},
    {field: 'longName', header: 'Long Name'},
    {field: 'groupSource', header: 'Group Source'}
  ];
  
  @ViewChild('table') turboTable: Table;

  groupForm: FormGroup;

  constructor(private pmcGroupService: PmcGroupServiceNg,
              private pmcRoleService: PmcRoleServiceNg,
              private pmcPermissionService: PmcPermissionServiceNg,
              private fb: FormBuilder,
              private messageService: MessageService, 
              private pmcUserService: PmcUserServiceNg) {}

  ngOnInit()  {
    this.initGroupForm();
  }

  private initGroupForm() {
    this.groupForm = this.fb.group({
      groupName: ['', Validators.required],
      longName: ['', Validators.required],
      groupSource: [{value: 'alanda', disabled: true}],
    });
  }

  onFormSubmit() {
    if(!this.groupForm.valid) {
      this.groupForm.markAsDirty();
      return;
    }
    if(this.selectedGroup) {
      this.selectedGroup.groupName = this.groupName;
      this.selectedGroup.longName = this.longName;

      this.updateGroup(this.selectedGroup);
    } else {
      this.createGroup({
        longName: this.longName,
        groupName: this.groupName,
        active: true,
        groupSource: 'alanda'
      });
    }
  }

  private updateGroup(group: PmcGroup) {
    this.pmcGroupService.update(group).subscribe(
      res => {
        this.messageService.add({severity:'success', summary:'Update User', detail:'Group has been updated'})
      },
      error => this.messageService.add({severity:'error', summary:'Update User', detail: error.message}));
  }

  private createGroup(group: PmcGroup) {
    this.pmcGroupService.save(group).subscribe(
      res => {
        this.messageService.add({severity:'success', summary:'Create New Group', detail: 'Group has been created'})
        this.turboTable.reset();
      },
      error => this.messageService.add({severity:'error', summary:'Create Group', detail: error.message}));
  }

  get groupName(): string {
    return this.groupForm.get('groupName').value;
  }

  get longName(): string {
    return this.groupForm.get('longName').value;
  }

  private fillGroupForm(group: PmcGroup) {
    this.groupForm.patchValue(group);
  }

  onLoadGroups(event: LazyLoadEvent) {
    this.loading = true;
    let serverOptions: ServerOptions = {
      pageNumber: (event.first/event.rows) + 1 ,
      pageSize: event.rows,
      filterOptions: {},
      sortOptions: {}
    }

    if (event.sortField) {
      let sortOptions = {}
      const dir = event.sortOrder == 1 ? "asc" : "desc";
      sortOptions[event.sortField] = {dir: dir, prio: 0}
      serverOptions.sortOptions = sortOptions;
    }

    for (let f in event.filters) {
      serverOptions.filterOptions[f] = event.filters[f].value;
      if(event.filters[f].value){}
    }
    this.pmcGroupService.getGroups(serverOptions)
    .subscribe(result => {
      this.loading = false;
      this.groups = result.results;
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
      this.pmcUserService.getUsersByGroupId(this.selectedGroup.guid).subscribe(res => {
        this.usersInSelectedGroup = res;
      });

    }

    private loadRoles() {
      this.assignedRoles = [...this.selectedGroup.roles];
      this.pmcRoleService.getRoles()
        .subscribe(result => {
          this.availableRoles = result.filter(all => {
            return this.assignedRoles.filter(assigned => {
              return assigned.guid === all.guid;
            }).length == 0;
          });
        });
    }

    private loadPermissions() {
      this.pmcGroupService.getEffectivePermissionsForGroup(this.selectedGroup.guid)
      .pipe(
        mergeMap((permissions: PmcPermission[]) => {
          this.grantedPermissions = permissions;
          return this.pmcPermissionService.getPermissions();
        })
      )
      .subscribe(result => {
        this.availablePermissions = result.filter(all => {
          return this.grantedPermissions.filter(assigned => {
            return assigned.guid === all.guid;
          }).length == 0;
        });
      });
    }

    updateRoles() {
      this.pmcGroupService.updateRolesForGroup(this.selectedGroup.guid, this.assignedRoles).subscribe(
        res => {
          this.messageService.add({severity:'success', summary:'Update roles', detail: 'Roles have been updated'})
          this.turboTable.reset();
        },
        error => this.messageService.add({severity:'error', summary:'Update roles', detail: error.message}));
    }

    updatePermissions() {
      this.pmcGroupService.updatePermissionsForGroup(this.selectedGroup.guid, this.grantedPermissions).subscribe(
        res => {
          this.messageService.add({severity:'success', summary:'Update permissions', detail: 'Permissions have been updated'})
          this.turboTable.reset();
        },
        error => this.messageService.add({severity:'error', summary:'Update permissions', detail: error.message}));
    }

}