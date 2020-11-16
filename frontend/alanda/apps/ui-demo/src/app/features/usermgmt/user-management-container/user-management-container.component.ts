import { Component } from '@angular/core';
import { Subject } from 'rxjs';
import { RxState } from '@rx-angular/state';
import { UserStoreImpl } from '../../../store/user';
import { map } from 'rxjs/operators';

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

  constructor(private userStore: UserStoreImpl) {
    super();
    this.hold(
      this.runAsUserClick$.pipe(
        map((name) =>
          this.userStore.dispatch(this.userStore.createRunAsUserAction(name)),
        ),
      ),
    );
  }
}
