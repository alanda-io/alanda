
import { Component, OnInit, Input } from "@angular/core";
import { MessageService, SelectItemGroup } from "primeng/api";
import { FormGroup, FormControl } from "@angular/forms";
import { PmcUserServiceNg } from "../../../services/rest/pmcuser.service";
import { Project } from "../../../models/project.model";
import { PmcGroupServiceNg } from "../../../services/rest/pmcgroup.service";
import { PmcGroup } from "../../../models/PmcGroup";
import { PmcUser } from "../../../models/PmcUser";
import { PropertyService } from "../../../services/rest/property.service";

@Component({
    selector: 'select-role',
    templateUrl: './role-select.component.html',
    styleUrls: [],
  })
export class SelectRoleComponent implements OnInit {

    @Input() project: Project;
    @Input() type: string; //'user' or 'group'
    @Input() displayName: string;
    @Input() roleName: string;
    @Input() onlyInhertited?: boolean = true; // if type == user and if set to false, also include users who have the group assigned directly
    @Input() groupFilter?: string[]; // if type == user, additional filtering for groups is possible
    @Input() grouping?: boolean = false; // if type == user, group users by their groups in dropdown
    //TODO: @Input() parent?: SelectRoleComponent; 

    private groups: PmcGroup[];
    
    items: string[] = [];
    options: {label: string, value: PmcUser | PmcGroup}[] = [];
    optionsGrouped: SelectItemGroup[] = [];

    formGroup: FormGroup;

    constructor(private userService: PmcUserServiceNg, private messageService: MessageService, private propService: PropertyService,
                private groupService: PmcGroupServiceNg){}

    ngOnInit(){  
      if(this.type === 'user') {
        this.groupService.getGroupsForRole(this.roleName).subscribe(groups => {
          this.groups = groups;
          this.groups.forEach(group => {
            let tempGroup = [];
            this.userService.getUsersByGroupId(group.guid).subscribe(users => {
              if(this.grouping) {
                tempGroup.push(...users.map(user => {return {label: user.displayName, value: user}}));
                this.optionsGrouped.push({label: group.longName, items: tempGroup});
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
      } else if(this.type === 'group') {
        if(this.grouping) {
          console.warn('grouping input has no effect when type is set to group');
        }
        if(this.groupFilter) {
          console.warn('groupFilter input has no effect when type is set to group');
        }
        if(this.onlyInhertited) {
          console.warn('onlyInhertited input has no effect when type is set to group');
        }
        this.groupService.getGroupsForRole(this.roleName).subscribe(groups => {
          this.options = groups.map(group => {
            return {label: group.longName, value: group};
          });
          
        })

      } else console.warn('wrong type input for role-select');


      this.initFormGroup();
    }

    private initFormGroup() {
      if(this.type == 'user') {
        this.propService.get(null, 'user', this.project.guid,  'role_' + this.roleName).subscribe();
      } else if(this.type == 'group') {
        this.propService.get(null, 'group', this.project.guid,  'role_grp_' + this.roleName).subscribe();
      }
      
      this.formGroup = new FormGroup({
        selected: new FormControl(null),
      });
    }

    save() {
      if(this.type == 'user') {
        let selected = this.grouping ? this.formGroup.get('selected') : this.formGroup.get('selected').value;
        this.propService.setString(null, 'user', this.project.guid, 'role_' + this.roleName, selected.value.guid).subscribe();
      } else if(this.type == 'group') {
        let selected = this.formGroup.get('selected').value;
        this.propService.setString(null, 'group', this.project.guid, 'role_grp_' + this.roleName, selected.value.guid).subscribe();
      }
    }

    
  }