
import { Component, OnInit, Input } from "@angular/core";
import { MessageService, SelectItemGroup, SelectItem } from "primeng/api";
import { FormGroup, FormControl, Validators } from "@angular/forms";
import { PmcUserServiceNg } from "../../../services/rest/pmcuser.service";
import { Project } from "../../../models/project.model";
import { PmcGroupServiceNg } from "../../../services/rest/pmcgroup.service";
import { PmcGroup } from "../../../models/PmcGroup";
import { PmcUser } from "../../../models/PmcUser";
import { PropertyService } from "../../../services/rest/property.service";
import { PmcRole } from "../../../models/PmcRole";
import { PmcRoleServiceNg } from "../../../services/rest/pmcrole.service";
import { FormsRegisterService } from "../../../services/forms-register.service";

@Component({
    selector: 'select-role',
    templateUrl: './role-select.component.html',
    styleUrls: [],
  })
export class SelectRoleComponent implements OnInit {

    @Input() project: Project;
    @Input() type: string; //'user' or 'group', determines if suggestions should be of type group or user
    @Input() displayName: string;
    @Input() roleName: string;
    @Input() onlyInherited?: boolean = true; // if type == user and if set to false, also include users who have the group assigned directly
    @Input() groupFilter?: string[]; // if type == user, additional filtering for groups is possible
    @Input() grouping?: boolean = false; // if type == user, group users by their groups in dropdown
    //TODO: @Input() parent?: SelectRoleComponent;
    @Input() formName: string;

    groups: PmcGroup[];
    items: string[] = [];
    options: {label: string, value: PmcUser | PmcGroup}[] = [];
    optionsGrouped: SelectItemGroup[] = [];
    role: PmcRole;
    roleSelectFormGroup: FormGroup;

    constructor(private userService: PmcUserServiceNg, private messageService: MessageService, private propService: PropertyService,
                private groupService: PmcGroupServiceNg, private roleService: PmcRoleServiceNg, private formsRegisterService: FormsRegisterService){}

    ngOnInit(){  
      this.initFormGroup();
    }

    load() {
      if(this.type === 'user') {
        if(!this.onlyInherited) {
          if(this.grouping) {
            console.warn('grouping = true is not allowed when onlyInherited is set to true');
          } else {
            this.roleService.getRoleByName(this.roleName).subscribe(role => {
              this.role = role
              this.userService.getUsersForRole(role.guid).subscribe(users => this.addUsersToOptions(users));
            });
          }
        } else {
          this.addUsersToOptions();
        }
      } else if(this.type === 'group') {
        if(this.grouping) {
          console.warn('grouping input has no effect when type is set to group');
        }
        if(this.groupFilter) {
          console.warn('groupFilter input has no effect when type is set to group');
        }
        if(this.onlyInherited) {
          console.warn('onlyInherited input has no effect when type is set to group');
        }
        this.groupService.getGroupsForRole(this.roleName).subscribe(groups => {
          this.options = groups.map(group => {
            return {label: group.longName, value: group};
          });
        })
      } else console.warn('wrong type input for role-select');
    }

    private addUsersToOptions(u?: PmcUser[]) {
      this.groupService.getGroupsForRole(this.roleName).subscribe(groups => {
        this.groups = groups;
        this.options = [];
        this.optionsGrouped = [];
        this.groups.forEach(group => {
          this.userService.getUsersByGroupId(group.guid).subscribe(users => {
            if(u) {
              users.push(...u);
            }
            if(this.grouping) {
              let mappedUsers: SelectItem[] = [];
              mappedUsers.push(...users.map(user => {return {label: user.displayName, value: user}}));
              this.optionsGrouped.push({label: group.longName, items: mappedUsers});
              this.optionsGrouped = [...this.optionsGrouped];
            } else {
              users.forEach(user => {
                if(this.options.filter(entry => entry.label == user.displayName).length == 0) {
                  this.options.push({label: user.displayName, value: user});
                  this.options = [...this.options];
                }
              });   
            }
          });
        });
      })
    }

    private initFormGroup() {
      if(this.type == 'user') {
        this.propService.get(null, 'user', this.project.guid,  'role_' + this.roleName).subscribe();
      } else if(this.type == 'group') {
        this.propService.get(null, 'group', this.project.guid,  'role_grp_' + this.roleName).subscribe();
      }
      
      this.roleSelectFormGroup = new FormGroup({
        selected: new FormControl(null, Validators.required),
      });

      this.formsRegisterService.registerForm(this.roleSelectFormGroup, this.formName);
    }

    save() {
      if(this.type == 'user') {
        let selected = this.grouping ? this.roleSelectFormGroup.get('selected') : this.roleSelectFormGroup.get('selected').value;
        this.propService.setString(null, 'user', this.project.guid, 'role_' + this.roleName, selected.value.guid).subscribe();
      } else if(this.type == 'group') {
        let selected = this.roleSelectFormGroup.get('selected').value;
        this.propService.setString(null, 'group', this.project.guid, 'role_grp_' + this.roleName, selected.value.guid).subscribe();
      }
    }

    
  }