import { Component, Input } from '@angular/core';
import {
  AlandaProject,
  AlandaUser,
  AlandaUserApiService,
} from '@alanda/common';
import { Subject } from 'rxjs';
import { RxState } from '@rx-angular/state';
import { map } from 'rxjs/operators';
import { SelectItem } from 'primeng/api';

interface PropState {
  options: SelectItem[];
}
@Component({
  selector: 'alanda-vacation-project-properties',
  templateUrl: './project-properties.component.html',
  providers: [RxState],
})
export class ProjectPropertiesComponent {
  @Input() project: AlandaProject;
  @Input() user: AlandaUser;
  state$ = this.state.select();
  autoCompEvent$ = new Subject<string>();

  autoComp$ = this.userService.searchUsers('', 'vacation-approver').pipe(
    map((users) => {
      const ret: SelectItem[] = new Array();
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
    this.state.connect('options', this.autoComp$);
    this.autoCompEvent$.next('');
  }
}
