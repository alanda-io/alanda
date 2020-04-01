import { Component, OnInit, Input } from '@angular/core';
import { FormGroup, FormControl } from '@angular/forms';
import { AlandaPropertyApiService } from '../../../../api/propertyApi.service';
import { ServerOptions } from '../../../../models/serverOptions';
import { AlandaUserApiService } from '../../../../api/userApi.service';

@Component({
    selector: 'alanda-dropdown-select',
    templateUrl: './dropdown-select.component.html',
    styleUrls: [],
  })
export class AlandaDropdownSelectComponent implements OnInit {

    @Input() project: any;
    @Input() displayName: string;
    @Input() key: string;
    @Input() groupId?: number;

    users: any[];
    userForm: FormGroup;

    constructor(private userService: AlandaUserApiService, private propertyService: AlandaPropertyApiService) {}

    ngOnInit(){
      let serverOptions: ServerOptions = {
        pageNumber: 1,
        pageSize: 999999,
        filterOptions: {},
        sortOptions: {}
      }
      if(!this.groupId){
        this.userService.getUsers(serverOptions).subscribe(res => {this.users = res.results;this.load();});
      } else {
        this.userService.getUsersByGroupId(this.groupId).subscribe(res => {this.users = res; this.load();});
      }
      this.initFormGroup();
    }

    initFormGroup() {
      this.userForm = new FormGroup({
        user: new FormControl(null),
      });
    }

    save() {
      this.propertyService.setString(null, null, this.project.guid, this.key, this.userForm.get('user').value.displayName).subscribe();
    }

    load() {
      this.propertyService.get(null, null, this.project.guid, this.key).subscribe(
        res => {
          if(res.value) {
            let user = this.users.filter(u => u.displayName == res.value)[0];
            this.userForm.get('user').setValue(user);
          }
        }
      );
    }


  }
