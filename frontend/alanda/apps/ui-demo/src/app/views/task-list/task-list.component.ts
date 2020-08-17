import { Component } from '@angular/core';
import {
  AlandaMonitorAPIService,
  AlandaTableLayout,
} from '@alanda/common';
import { UserAdapter } from '../../core/services/user.adapter';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';

@Component({
  selector: 'alanda-task-list',
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.scss'],
})
export class AlandaTaskListComponent {
  layouts$: Observable<AlandaTableLayout[]>
  user$ = this.userAdapter.currentUser$;

  constructor(
    private monitorApiService: AlandaMonitorAPIService,
    private userAdapter: UserAdapter
  ) {
    this.layouts$ = this.user$.pipe(
      map((user) => {
        return this.monitorApiService.getTaskListLayouts(user);
      })
    )
  }
}
