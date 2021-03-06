import { Component, OnInit, Input } from '@angular/core';
import { AlandaProject } from '../../api/models/project';
import { AlandaGroup } from '../../api/models/group';
import { AlandaRole } from '../../api/models/role';
import { FormGroup, FormControl, AbstractControl } from '@angular/forms';
import { SelectItemGroup, SelectItem } from 'primeng/api';
import { AlandaPropertyApiService } from '../../api/propertyApi.service';
import { AlandaRoleApiService } from '../../api/roleApi.service';
import { mergeMap, concatMap } from 'rxjs/operators';
import { AlandaUser } from '../../api/models/user';
import { AlandaUserApiService } from '../../api/userApi.service';
import { AlandaGroupApiService } from '../../api/groupApi.service';
import { PERMISSION_PLACEHOLDER, Authorizations } from '../../permissions';

const SELECTOR = 'alanda-role-select';
@Component({
  selector: SELECTOR,
  templateUrl: './role-select.component.html',
  styleUrls: ['./role-select.component.scss'],
})
export class AlandaSelectRoleComponent implements OnInit {
  @Input() project: AlandaProject;
  @Input() type: string; // 'user' or 'group', determines if suggestions should be of type group or user
  @Input() displayName: string;
  @Input() roleName: string;
  @Input() onlyInherited = false; // if type === user and if set to true, only include users who have the role assigned through a group
  @Input() inherited = false; // this will replace onlyinherited above when it's done and its gona be much better zapperlott
  @Input() groupFilter?: string[]; // if type === user, additional filtering for groups is possible
  @Input() grouping = false; // if type === user, group users by their groups in dropdown
  // TODO: @Input() parent?: SelectRoleComponent;
  @Input() formName: string;
  @Input() user: AlandaUser;
  @Input() permissionString: string;

  @Input()
  set rootFormGroup(rootFormGroup: FormGroup) {
    if (rootFormGroup) {
      rootFormGroup.addControl(
        `${SELECTOR}-${this.roleName}`,
        this.roleSelectFormGroup,
      );
    }
  }

  groups: AlandaGroup[];
  items: string[] = [];
  options: { label: string; value: number }[] = [];
  optionsGrouped: SelectItemGroup[] = [];
  role: AlandaRole;
  roleSelectFormGroup = new FormGroup({
    selected: new FormControl(''),
  });

  constructor(
    private readonly userService: AlandaUserApiService,
    private readonly propService: AlandaPropertyApiService,
    private readonly groupService: AlandaGroupApiService,
    private readonly roleService: AlandaRoleApiService,
  ) {}

  ngOnInit() {
    if (!Authorizations.hasPermission(this.user, this.getPermissionString())) {
      this.roleSelectFormGroup.disable();
    }
    this.loadDropdown();
  }

  loadDropdown() {
    if (this.type === 'user') {
      if (!this.onlyInherited) {
        if (this.grouping) {
          console.warn(
            'grouping = true is not allowed when onlyInherited is set to true',
          );
        } else {
          this.options = [];
          this.roleService
            .getRoleByName(this.roleName)
            .pipe(
              mergeMap((role) => {
                this.role = role;
                return this.userService.getUsersForRole(role.guid);
              }),
            )
            .subscribe((users) => this.addUsersToOptions(users));
        }
      } else {
        this.addUsersToOptions();
      }
    } else if (this.type === 'group') {
      if (this.grouping) {
        console.warn('grouping input has no effect when type is set to group');
      }
      if (this.groupFilter) {
        console.warn(
          'groupFilter input has no effect when type is set to group',
        );
      }
      if (this.onlyInherited) {
        console.warn(
          'onlyInherited input has no effect when type is set to group',
        );
      }
      this.optionsGrouped = [];
      this.groupService.getGroupsForRole(this.roleName).subscribe((groups) => {
        this.options = groups.map((group) => {
          return { label: group.longName, value: group.guid };
        });
        this.loadProperty();
      });
    } else {
      console.warn('wrong type input for role-select');
    }
  }

  private addUsersToOptions(u?: AlandaUser[]) {
    this.groupService.getGroupsForRole(this.roleName).subscribe((groups) => {
      this.groups = groups;
      this.groups.forEach((group) => {
        this.userService.getUsersByGroupId(group.guid).subscribe((users) => {
          if (u) {
            users.push(...u);
          }
          if (this.grouping) {
            const mappedUsers: SelectItem[] = [];
            mappedUsers.push(
              ...users.map((user) => {
                return { label: user.displayName, value: user.guid };
              }),
            );
            this.optionsGrouped.push({
              label: group.longName,
              items: mappedUsers,
            });
            this.optionsGrouped = [...this.optionsGrouped];
          } else {
            users.forEach((user) => {
              if (
                this.options.filter((entry) => entry.label === user.displayName)
                  .length === 0
              ) {
                this.options.push({
                  label: user.displayName,
                  value: user.guid,
                });
                this.options = [...this.options];
              }
            });
          }
          this.loadProperty();
        });
      });
    });
  }

  private loadProperty() {
    if (this.type === 'user') {
      this.propService
        .get(null, 'user', this.project.guid, 'role_' + this.roleName)
        .pipe(concatMap((res) => this.userService.getUser(Number(res.value))))
        .subscribe((user) => {
          if (this.grouping) {
            this.optionsGrouped.forEach((group) => {
              const filteredGroup = group.items.filter(
                (item) => item.value === user.guid,
              );
              if (filteredGroup.length > 0) {
                const item = filteredGroup[0];
                this.roleSelectFormGroup.get('selected').setValue(item.value);
              }
            });
          } else {
            this.roleSelectFormGroup
              .get('selected')
              .setValue({ label: user.displayName, value: user.guid });
          }
        });
    } else if (this.type === 'group') {
      this.propService
        .get(null, 'group', this.project.guid, 'role_grp_' + this.roleName)
        .pipe(
          concatMap((res) =>
            this.groupService.getGroupByGuid(Number(res.value)),
          ),
        )
        .subscribe((group) => {
          if (group) {
            this.roleSelectFormGroup
              .get('selected')
              .setValue({ label: group.longName, value: group.guid });
          }
        });
    }
  }

  onChange() {
    if (this.type === 'user') {
      const selected = this.grouping
        ? this.roleSelectFormGroup.get('selected')
        : this.roleSelectFormGroup.get('selected').value;
      this.propService
        .setString(
          null,
          'user',
          this.project.guid,
          'role_' + this.roleName,
          selected.value,
        )
        .subscribe();
    } else if (this.type === 'group') {
      const selected = this.roleSelectFormGroup.get('selected').value;
      this.propService
        .setString(
          null,
          'group',
          this.project.guid,
          'role_grp_' + this.roleName,
          selected.value,
        )
        .subscribe();
    }
  }

  get selected(): AbstractControl {
    return this.roleSelectFormGroup.get('selected');
  }

  getPermissionString(): string {
    if (this.permissionString) {
      return this.permissionString;
    }
    return `prop:${this.project.authBase.replace(
      PERMISSION_PLACEHOLDER,
      'write',
    )}:role_${this.roleName}`;
  }
}
