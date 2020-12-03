import { Component, ViewEncapsulation } from '@angular/core';
import { AlandaTableLayout, Authorizations, TableType } from '@alanda/common';
import { map } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { findIndex, isEmpty } from 'lodash';
import { RxState } from '@rx-angular/state';
import { UserStoreImpl } from '../../store/user';
import { ContextEntry } from '../../context/contextEntry.interface';
import { PROJECT_LAYOUTS } from './constants';

interface ProjectMonitorState {
  contextEntry: ContextEntry;
  defaultLayout: number;
  layouts: AlandaTableLayout[];
}

@Component({
  selector: 'alanda-project-monitor',
  templateUrl: './project-monitor.component.html',
  styleUrls: ['./project-monitor.component.scss'],
  providers: [RxState],
})
export class ProjectMonitorComponent {
  state$ = this.state.select();
  user$ = this.userStore.currentUser$;
  contextDataAvailableEvent$ = new Subject<ContextEntry[]>();
  layoutChangedEvent$ = new Subject<AlandaTableLayout>();
  projectLayouts: AlandaTableLayout[] = PROJECT_LAYOUTS;
  layoutKey = 'project-list-layout';

  /**
   * Will be triggered when there is new context data available.
   *
   * @param entries ContextEntry[] the entries that I'm listening to
   * @returns data { defaultLayout: number, groupTasks: boolean }
   */
  handleContextDataAvailable$ = this.contextDataAvailableEvent$.pipe(
    map((entries) => {
      // tslint:disable-next-line: prefer-const
      let { defaultLayout, layouts } = this.state.get();

      const layoutContextEntry = entries.find((entry) =>
        entry.key.includes(this.layoutKey),
      );
      if (layoutContextEntry && !isEmpty(layouts)) {
        defaultLayout = findIndex(layouts, {
          name: layoutContextEntry.value,
        });
      }
      return { defaultLayout };
    }),
  );

  constructor(
    private userStore: UserStoreImpl,
    private state: RxState<ProjectMonitorState>,
  ) {
    this.state.connect(this.handleContextDataAvailable$);
    this.state.connect(
      'contextEntry',
      this.layoutChangedEvent$.pipe(
        map((layout) => ({ key: this.layoutKey, value: layout.name })),
      ),
    );

    this.state.connect(
      'layouts',
      this.user$.pipe(
        map((user) => {
          if (
            Authorizations.hasPermission(
              user,
              'monitor:read:neubau-umbau-details',
            )
          ) {
            const columnDef = this.projectLayouts.find(
              (layout) => layout.name === 'umbau',
            ).columnDefs;
            if (columnDef.findIndex((def) => def.name === 'X') === -1) {
              this.projectLayouts
                .find((layout) => layout.name === 'umbau')
                .columnDefs.push({
                  displayName: 'X',
                  name: 'X',
                });
            }
          }

          return this.projectLayouts;
        }),
      ),
    );
  }
}
