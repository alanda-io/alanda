import { Component, OnInit, OnDestroy, ViewEncapsulation } from "@angular/core";
import { LazyLoadEvent } from "primeng/components/common/api";
import { PmcGroupServiceNg } from "../../../core/api/pmcgroup.service";
import { mergeMap } from "rxjs/operators";
import { Subject, Subscription, Observable } from "rxjs";
import { PmcRoleServiceNg } from "../../../core/api/pmcrole.service";
import { FormGroup, FormControl, Validators } from "@angular/forms";
import { PmcPermissionServiceNg } from "../../../core/api/pmcpermission.service";
import { PmcGroup } from "../../../models/pmcGroup";
import { PmcPermission } from "../../../models/pmcPermission";
import { ServerOptions } from "../../../models/serverOptions";

@Component({
  selector: 'group-admin',
  templateUrl: './group.component.html',
  styleUrls: ['./group.component.css'],
  encapsulation: ViewEncapsulation.None,

})
export class GroupComponent implements OnInit, OnDestroy {

    groups: PmcGroup[];
    selectedGroup: PmcGroup;
    private selectedGroupUpdated = new Subject<PmcGroup>();
    private groupSubscription: Subscription;
    totalGroups: number;
    loading: boolean;
    groupColumns: any[];
    actionLabel: string;
    form: FormGroup;
    availablePermissions: PmcPermission[];
    grantedPermissions: PmcPermission[];
    creationLabel: string;

    availableRoles: any[];
    assignedRoles: any[];

    constructor(private pmcGroupService: PmcGroupServiceNg,
                private pmcRoleService: PmcRoleServiceNg,
                private pmcPermissionService: PmcPermissionServiceNg) {}

    ngOnDestroy() {
      this.groupSubscription.unsubscribe();
    }

    ngOnInit(): void {
      this.form = new FormGroup({
        id: new FormControl(null),
        groupName: new FormControl(null, {validators: [Validators.required]}),
        longGroupName: new FormControl(null, {validators: [Validators.required]}),
        groupSource: new FormControl(null, {validators: [Validators.required]}),
      });
      this.form.disable();
      this.groupColumns = [
        {field: 'guid', header: 'Guid'},
        {field: 'groupName', header: 'Group Name'},
        {field: 'longName', header: 'Long Name'},
        {field: 'groupSource', header: 'Group Source'}
      ];

      this.groupSubscription = this.getSelectedGroupUpdateListener().subscribe(
        (group: PmcGroup) => {
            this.form.setValue({
              id: group.guid,
              groupName: group.groupName,
              longGroupName: group.longName,
              groupSource: group.groupSource,
            });
          this.loadPermissions(group.guid);
          this.loadRoles();
        }
      );

      this.actionLabel = "Edit Group";
      this.creationLabel = "Create new Group";
      this.loading = true;
    }

    getSelectedGroupUpdateListener(): Observable<PmcGroup>{
      return this.selectedGroupUpdated.asObservable();
    }

    loadGroupsLazy(event: LazyLoadEvent) {
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
        serverOptions.filterOptions[f] = event.filters[f].value;
        if(event.filters[f].value){}
      }
      this.pmcGroupService.getGroups(serverOptions)
      .subscribe(result => {
        this.loading = false;
        this.groups = result.results;
        this.totalGroups = result.total;
      });
    }

    onRowSelect(event) {
      this.actionLabel = "Edit Group";
      this.creationLabel = "Create new Group";
      this.form.disable();
      this.pmcGroupService.getGroupByGuid(event.data.guid)
      .subscribe(result => {
        this.selectedGroup = result;
        this.selectedGroupUpdated.next(result);
      });
    }

    private loadRoles():void {
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

    private loadPermissions(guid: number): void{
      this.pmcGroupService.getEffectivePermissionsForGroup(guid)
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

    onCreateNewGroup(){
      if(this.creationLabel === "Create new Group"){
        this.creationLabel = "Save Group";
        this.selectedGroup = null;
        this.form.reset();
        this.form.enable();
        this.form.get('id').disable();
      } else {
        if(this.form.valid){
          let group = new PmcGroup();
          group.groupName = this.form.get('groupName').value;
          group.longName = this.form.get('longGroupName').value;
          group.groupSource = this.form.get('groupSource').value;
          group.active = true;
          this.pmcGroupService.save(group).subscribe(
            res => {
              this.form.reset();
              this.form.disable();
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
        this.actionLabel = "Edit Group";
        this.form.disable();
        this.pmcGroupService.getGroupByGuid(this.selectedGroup.guid)
        .subscribe(result => {
          this.selectedGroup = result;
        });
      }
    }

    updateRoles():void {
      this.pmcGroupService.updateRolesForGroup(this.selectedGroup.guid, this.assignedRoles).subscribe();
    }

    updatePermissions():void {
      this.pmcGroupService.updatePermissionsForGroup(this.selectedGroup.guid, this.grantedPermissions).subscribe();
    }

    onUpdateGroup(){
      this.actionLabel = "Edit Group";
      this.form.disable();
      this.selectedGroup.groupName = this.form.get('groupName').value
      this.selectedGroup.longName = this.form.get('longGroupName').value
      this.selectedGroup.groupSource = this.form.get('groupSource').value
      this.pmcGroupService.update(this.selectedGroup).subscribe(
        res => {
          this.selectedGroup = res;
          this.selectedGroupUpdated.next(res);
        }); 
    }
}