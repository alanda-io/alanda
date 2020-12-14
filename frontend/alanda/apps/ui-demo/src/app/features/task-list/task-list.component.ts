import { Component } from '@angular/core';
import { RxState } from '@rx-angular/state';
import { AlandaTableLayout, Authorizations, TableType } from '@alanda/common';
import { map } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { findIndex, isEmpty } from 'lodash';
import { UserStoreImpl } from '../../store/user';
import { ContextEntry } from '../../context/contextEntry.interface';
import { TASK_LAYOUTS } from './constants';

interface TaskListState {
  contextEntry: ContextEntry;
  defaultLayout: number;
  groupTasks: boolean;
  layouts: AlandaTableLayout[];
}

@Component({
  selector: 'alanda-task-list',
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.scss'],
  providers: [RxState],
})
export class TaskListComponent {
  state$ = this.state.select();
  contextDataAvailableEvent$ = new Subject<ContextEntry[]>();
  groupTasksChangedEvent$ = new Subject<boolean>();
  layoutChangedEvent$ = new Subject<AlandaTableLayout>();
  user$ = this.userStore.currentUser$;
  taskLayouts: AlandaTableLayout[] = TASK_LAYOUTS;
  // Context Variables Keys
  layoutKey = 'task-list-layout';
  groupTasksKey = 'task-list-group-tasks';

  /**
   * Will be triggered when there is new context data available.
   *
   * @param entries ContextEntry[] the entries that I'm listening to
   * @returns data { defaultLayout: number, groupTasks: boolean }
   */
  handleContextDataAvailable$ = this.contextDataAvailableEvent$.pipe(
    map((entries) => {
      // tslint:disable-next-line: prefer-const
      let { defaultLayout, groupTasks, layouts } = this.state.get();

      const layoutContextEntry = entries.find((entry) =>
        entry.key.includes(this.layoutKey),
      );
      const groupTasksContextEntry = entries.find((entry) =>
        entry.key.includes(this.groupTasksKey),
      );
      if (layoutContextEntry && !isEmpty(layouts)) {
        defaultLayout = findIndex(layouts, {
          name: layoutContextEntry.value,
        });
      }
      if (groupTasksContextEntry) {
        groupTasks = groupTasksContextEntry.value;
      }
      return { defaultLayout, groupTasks };
    }),
  );

  constructor(
    private userStore: UserStoreImpl,
    private state: RxState<TaskListState>,
  ) {
    this.state.connect(this.handleContextDataAvailable$);
    this.state.connect(
      'contextEntry',
      this.layoutChangedEvent$.pipe(
        map((layout) => ({ key: this.layoutKey, value: layout.name })),
      ),
    );
    this.state.connect(
      'contextEntry',
      this.groupTasksChangedEvent$.pipe(
        map((groupTasks) => ({ key: this.groupTasksKey, value: groupTasks })),
      ),
    );

    this.state.connect(
      'layouts',
      this.user$.pipe(
        map((user) => {
          let filteredLayouts: AlandaTableLayout[] = this.taskLayouts.filter(
            (layout) =>
              Authorizations.hasPermissionForTableLayout(
                layout,
                user,
                TableType.TASK,
              ),
          );
          if (filteredLayouts.length === 0) {
            filteredLayouts = this.taskLayouts.filter(
              (layout) => layout.name === 'default',
            );
          }
          return filteredLayouts;
        }),
      ),
    );
  }
}
