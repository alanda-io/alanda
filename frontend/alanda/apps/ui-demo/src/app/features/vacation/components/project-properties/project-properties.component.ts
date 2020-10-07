import { Component } from '@angular/core';
import {
  AlandaProject,
  AlandaUser,
  AlandaUserApiService,
} from '@alanda/common';
import { FormGroup } from '@angular/forms';
import { Subject } from 'rxjs';
import { RxState } from '@rx-angular/state';
import { debounceTime, map, switchMap } from 'rxjs/operators';
import { SelectItem } from 'primeng/api';

interface PropState {
  options: SelectItem[];
}
@Component({
  templateUrl: './project-properties.component.html',
  providers: [RxState],
})
export class ProjectPropertiesComponent {
  project: AlandaProject;
  user: AlandaUser;
  rootForm: FormGroup;

  state$ = this.state.select();
  autoCompEvent$ = new Subject<string>();

  autoComp$ = this.autoCompEvent$.pipe(
    debounceTime(300),
    switchMap((searchTerm) => {
      return this.userService.searchUsers(searchTerm, 'vacation-approver');
    }),
    map((users) => {
      let ret: SelectItem[] = new Array();
      users.forEach((user) => {
        ret.push({ label: user.displayName, value: user.guid });
      });
      return ret;
    }),
  );

  constructor(
    private state: RxState<PropState>,
    private userService: AlandaUserApiService,
  ) {
    // this.state.set({ options: [] });
    this.state.connect('options', this.autoComp$);
    this.autoCompEvent$.next('');
  }
}
