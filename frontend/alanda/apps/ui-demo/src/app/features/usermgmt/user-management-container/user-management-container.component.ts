import { Component } from '@angular/core';
import { Subject } from 'rxjs';
import { UserAdapter } from '@alanda/common';
import { RxState } from '@rx-angular/state';

@Component({
  selector: 'alanda-user-management-container',
  template: `
    <alanda-user-management
      (runAsUserClick)="runAsUserClick$.next($event)"
    ></alanda-user-management>
  `,
})
export class UserManagementContainerComponent extends RxState<any> {
  runAsUserClick$ = new Subject<string>();

  constructor(private userAdapter: UserAdapter) {
    super();
    this.userAdapter.connectRunAs(this.runAsUserClick$);
  }
}
