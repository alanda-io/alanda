import { Component, Inject, Input, Output, ViewChild } from '@angular/core';
import { LazyLoadEvent, MenuItem, MessageService } from 'primeng/api';
import { Table } from 'primeng/table';
import { ServerOptions } from '../../models/serverOptions';
import { AlandaUser } from '../../api/models/user';
import { AlandaTaskApiService } from '../../api/taskApi.service';
import { AlandaTableLayout } from '../../api/models/tableLayout';
import { AlandaListResult } from '../../api/models/listResult';
import { AlandaTaskListData } from '../../api/models/task';
import { RxState } from '@rx-angular/state';
import {
  combineLatest,
  EMPTY,
  isObservable,
  merge,
  Observable,
  of,
  Subject,
} from 'rxjs';
import {
  catchError,
  delay,
  exhaustMap,
  filter,
  map,
  skip,
  startWith,
  switchMap,
  tap,
  withLatestFrom,
} from 'rxjs/operators';
import { APP_CONFIG, AppSettings } from '../../models/appSettings';
import { AlandaProject } from '../../api/models/project';
import { exportAsCsv } from '../../utils/helper-functions';
import { AlandaTableColumnDefinition } from '../../api/models/tableColumnDefinition';
import { Router } from '@angular/router';
import { ExportType } from '../../enums/exportType.enum';

const EXPORT_FILE_NAME = 'download';
const CLAIM_TEXT = 'Claim';
const UNCLAIM_TEXT = 'Unclaim';

interface AlandaTaskTableState {
  loading: boolean;
  selectedProject: AlandaProject;
  showProjectDetailsModal: boolean;
  user: AlandaUser;
  serverOptions: ServerOptions;
  tasksData: AlandaListResult<AlandaTaskListData>;
  defaultLayout: number;
  layouts: AlandaTableLayout[];
  selectedLayout: AlandaTableLayout;
  filteredColumns: AlandaTableColumnDefinition[];
  menuItems: MenuItem[];
  candidateUsers: AlandaUser[];
  delegatedTaskData: AlandaTaskListData;
}

const initState = {
  tasksData: {
    total: 0,
    results: [],
  },
  layouts: [],
  menuItem: [],
};
const DEFAULT_BUTTON_MENU_ICON = 'pi pi-bars';
const LOADING_ICON = 'pi pi-spin pi-spinner';
@Component({
  selector: 'alanda-task-table',
  templateUrl: './task-table.component.html',
  styleUrls: ['./task-table.component.scss'],
  providers: [RxState],
})
export class AlandaTaskTableComponent {
  state$ = this.state.select();
  menuButtonIcon = DEFAULT_BUTTON_MENU_ICON;
  closeProjectDetailsModalEvent$ = new Subject<AlandaProject>();
  lazyLoadEvent$ = new Subject();
  needReloadEvent$ = new Subject();
  delegateTaskEvent$ = new Subject<AlandaTaskListData>();
  delegateTaskToUserEvent$ = new Subject<AlandaUser>();
  setupProjectDetailsModalEvent$ = new Subject<AlandaProject>();
  menuBarVisible = false;

  @Input() set defaultLayout(defaultLayout: number) {
    // setting "undefined" on an already undefined field does not trigger
    // state Observables to fire, so we set to a default value to trigger
    // loading
    if (defaultLayout === undefined) {
      defaultLayout = 0;
    }
    this.state.set({ defaultLayout });
  }
  @Input() set layouts(layouts: AlandaTableLayout[]) {
    if (layouts != null && layouts.length > 1) {
      layouts = layouts.sort((a, b) =>
        a.displayName.localeCompare(b.displayName),
      );
    }
    this.state.set({ layouts });
  }
  @Input() tableStyle: object;
  @Input() autoLayout = false;
  @Input() resizableColumns = true;
  @Input() responsive = true;
  @Input() groupTasks = false;
  @Input()
  set user(user: Observable<AlandaUser> | AlandaUser) {
    if (isObservable(user)) {
      this.state.connect('user', user);
    } else {
      this.state.set({ user });
    }
  }
  @Input() target = '_self';
  @Input() targetDblClick = '_blank';
  @Input() routerBasePath = '/forms';
  @Output() layoutChanged = new Subject<AlandaTableLayout>();
  @Output() toggleGroupTasksChanged = new Subject<boolean>();

  dateFormat: string;
  showDelegateDialog = false;
  hiddenColumns = {};
  layoutChange$ = new Subject<any>();
  onMenuItemColumnClick$ = new Subject<string>();
  onExportCsvClick$ = new Subject<ExportType>();
  claimTask$ = new Subject<AlandaTaskListData>();
  @ViewChild('tt') turboTable: Table;

  /**
   * Loads tasks from server when the server options changes or
   * when a need reload event is triggered
   *
   * @returns data { tasksData: AlandaListResult<AlandaTask>, working: boolean }
   */
  loadTaskFromServer$ = merge(
    this.lazyLoadEvent$,
    this.needReloadEvent$.pipe(map(() => this.turboTable)),
  ).pipe(
    map((event) => this.buildServerOptions(event)),
    switchMap((serverOptions) =>
      this.taskService.loadTasks(serverOptions).pipe(
        withLatestFrom(this.state.select('user')),
        map(([response, user]) => {
          response.results = response.results.map((task) => {
            task.claimLabel =
              user?.guid === +task.task?.assignee_id
                ? UNCLAIM_TEXT
                : CLAIM_TEXT;
            return task;
          });
          return response;
        }),
        map((tasksData) => ({ serverOptions, tasksData, loading: false })),
        startWith({ loading: true }),
        catchError((err) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Load Tasks',
            detail: err.message,
          });
          const tasksData = this.state.get().tasksData;
          return of({ serverOptions, tasksData, loading: false });
        }),
      ),
    ),
  );

  filteredColumnsChanged$ = this.state
    .select('selectedLayout')
    .pipe(map((selectedLayout) => selectedLayout.columnDefs));

  layoutChanged$ = this.state.select('selectedLayout').pipe(
    skip(1),
    tap((layout) => {
      this.needReloadEvent$.next();
      this.layoutChanged.next(layout);
    }),
  );

  updateMenuColumnOptions$ = this.state
    .select('selectedLayout')
    .pipe(map((selectedLayout) => this.updateMenu(selectedLayout.columnDefs)));

  selectedLayoutChanged$ = this.layoutChange$.pipe(map((event) => event.value));

  defaultLayoutChange$ = combineLatest([
    this.state.select('layouts'),
    this.state.select('defaultLayout'),
  ]).pipe(
    filter(([layouts, defaultLayout]) => layouts.length > 0),
    map(
      ([layouts, defaultLayout]) =>
        layouts[defaultLayout != null ? defaultLayout : 0],
    ),
  );

  toggleColumn$ = this.onMenuItemColumnClick$.pipe(
    map((name) => {
      const selectedLayout = this.state.get('selectedLayout');
      const menuItems = this.state.get('menuItems');
      if (this.hiddenColumns.hasOwnProperty(name)) {
        const index = this.hiddenColumns[name];
        delete this.hiddenColumns[name];
        menuItems[1].items[index].icon = 'pi pi-eye';
      } else {
        selectedLayout.columnDefs.some((column, index) => {
          if (column.name === name) {
            this.hiddenColumns[column.name] = index;
            menuItems[1].items[index].icon = 'pi pi-eye-slash';
            return true;
          }
        });
      }
      return {
        menuItems,
        filteredColumns: this.state
          .get('selectedLayout')
          .columnDefs.filter((column, index) => {
            return !this.hiddenColumns.hasOwnProperty(column.name);
          }),
      };
    }),
  );

  exportAllCsv$ = this.onExportCsvClick$.pipe(
    filter((exportType) => exportType === ExportType.ALL_DATA),
    exhaustMap(() => {
      const { serverOptions, tasksData } = this.state.get();
      serverOptions.pageNumber = 1;
      serverOptions.pageSize = tasksData.total;
      this.menuButtonIcon = LOADING_ICON;
      return this.taskService.loadTasks(serverOptions);
    }),
    catchError((err) => {
      console.log(err);
      return EMPTY;
    }),
    tap((result) => {
      const data = [...result.results];
      exportAsCsv(data, this.state.get('filteredColumns'), EXPORT_FILE_NAME);
      this.menuButtonIcon = DEFAULT_BUTTON_MENU_ICON;
    }),
  );

  exportCurrentPageData$ = this.onExportCsvClick$.pipe(
    filter((exportType) => exportType === ExportType.VISIBLE_DATA),
    tap(() =>
      exportAsCsv(
        this.state.get('tasksData').results,
        this.state.get('filteredColumns'),
        EXPORT_FILE_NAME,
      ),
    ),
  );

  claimTaskSideEffect$ = this.claimTask$.pipe(
    withLatestFrom(this.state.select('user')),
    tap(() => this.state.set({ loading: true })),
    switchMap(([taskData, user]) => {
      if (user.guid === +taskData.task.assignee_id) {
        return this.taskService.unclaim(taskData.task.task_id).pipe(
          catchError((err, caught) => {
            this.state.set({ loading: false });
            this.messageService.add({
              severity: 'error',
              summary: 'Unclaim Task',
              detail: err.message,
            });
            console.error(err);
            return EMPTY;
          }),
          tap(() => {
            this.messageService.add({
              severity: 'success',
              summary: 'Unclaim Task',
              detail: 'Task has been unclaimed',
            });
          }),
        );
      } else {
        return this.taskService.assign(taskData.task.task_id, user.guid).pipe(
          catchError((err) => {
            this.state.set({ loading: false });
            this.messageService.add({
              severity: 'error',
              summary: 'Claim Task',
              detail: err.message,
            });
            console.error(err);
            return EMPTY;
          }),
          tap(() => {
            this.messageService.add({
              severity: 'success',
              summary: 'Claim Task',
              detail: 'Task has been claimed',
            });
          }),
        );
      }
    }),
    delay(1000), // Wait for elastic search to update before updating table data
    tap(() => {
      this.state.set({ loading: false });
      this.needReloadEvent$.next();
    }),
  );

  delegateTask$ = this.delegateTaskEvent$.pipe(
    switchMap((data: AlandaTaskListData) => {
      return this.taskService.getCandidates(data.task.task_id).pipe(
        catchError((err, caught) => {
          console.error(err);
          return EMPTY;
        }),
        tap(() => (this.showDelegateDialog = true)),
        map((response) => {
          return {
            candidateUsers: response,
            delegatedTaskData: data,
          };
        }),
      );
    }),
  );

  delegateTaskToUser$ = this.delegateTaskToUserEvent$.pipe(
    withLatestFrom(
      this.state.select('delegatedTaskData'),
      this.state.select('user'),
    ),
    tap(() => this.state.set({ loading: true })),
    switchMap(([selectedUser, delegatedTaskData, user]) => {
      return this.taskService
        .assign(delegatedTaskData.task.task_id, selectedUser.guid)
        .pipe(
          catchError((err, caught) => {
            console.error(err);
            this.state.set({ loading: false });
            return EMPTY;
          }),
          delay(1000),
          tap(() => {
            this.hideDelegateDialog();
            this.state.set({ loading: false });
            this.needReloadEvent$.next();
          }),
        );
    }),
  );

  constructor(
    private readonly taskService: AlandaTaskApiService,
    public messageService: MessageService,
    private state: RxState<AlandaTaskTableState>,
    @Inject(APP_CONFIG) config: AppSettings,
    private router: Router,
  ) {
    this.state.set(initState);
    this.dateFormat = config.DATE_FORMAT;
    this.state.connect(
      'selectedLayout',
      merge(this.defaultLayoutChange$, this.selectedLayoutChanged$),
    );
    this.state.connect('filteredColumns', this.filteredColumnsChanged$);
    this.state.connect('menuItems', this.updateMenuColumnOptions$);
    this.state.hold(merge(this.exportAllCsv$, this.exportCurrentPageData$));
    this.state.connect(this.toggleColumn$);
    this.state.hold(this.layoutChanged$);
    this.state.hold(this.updateMenuColumnOptions$);
    this.state.hold(this.claimTaskSideEffect$);
    this.state.connect(
      this.setupProjectDetailsModalEvent$.pipe(
        map((selectedProject) => ({
          selectedProject,
          showProjectDetailsModal: true,
        })),
      ),
    );
    this.state.connect(
      'showProjectDetailsModal',
      this.closeProjectDetailsModalEvent$.pipe(map(() => false)),
    );
    this.state.connect(this.loadTaskFromServer$);
    this.state.hold(
      this.closeProjectDetailsModalEvent$.pipe(
        filter((project) => !!project),
        delay(1000),
        tap(() => this.needReloadEvent$.next()),
      ),
    );

    this.state.hold(this.needReloadEvent$);
    this.state.hold(this.lazyLoadEvent$);
    this.state.connect(this.delegateTask$);
    this.state.hold(this.delegateTaskToUser$);
  }

  buildServerOptions(event: LazyLoadEvent): ServerOptions {
    const serverOptions: ServerOptions = {
      pageNumber: event.first / event.rows + 1,
      pageSize: event.rows,
      filterOptions: {
        hideSnoozedTasks: 1,
      },
      sortOptions: {},
    };

    if (!this.groupTasks) {
      serverOptions.filterOptions.mytasks = 1;
    } else {
      delete serverOptions.filterOptions.mytasks;
    }

    if (this.state.get('selectedLayout').filterOptions) {
      for (const [key, value] of Object.entries(
        this.state.get('selectedLayout').filterOptions,
      )) {
        serverOptions.filterOptions[key] = value;
      }
    }

    Object.keys(event.filters).forEach((key) => {
      serverOptions.filterOptions[key] = event.filters[key].value;
    });

    if (event.sortField) {
      const dir = event.sortOrder === 1 ? 'asc' : 'desc';
      serverOptions.sortOptions[event.sortField] = { dir, prio: 0 };
    }

    return serverOptions;
  }

  toggleGroupTasks(value: boolean): void {
    this.groupTasks = value;
    this.needReloadEvent$.next();
    this.toggleGroupTasksChanged.next(value);
  }

  getCondition(obj, condition): any {
    if (condition === undefined) {
      return '';
    }
    const props = Object.keys(obj).reduce((acc, next) => `${acc} , ${next}`);
    const evalCon = new Function(
      ` return function ({${props}})  { return ${condition}} `,
    );
    return evalCon()(obj);
  }

  hideDelegateDialog(): void {
    this.state.set({
      delegatedTaskData: null,
    });
    this.showDelegateDialog = false;
  }

  encodeURIAndReplace(v): string {
    return encodeURIComponent(v).replace(/%/g, '~');
  }

  getTaskPath(formKey: string, taskId: string): string {
    return `${this.routerBasePath}/${formKey}/${taskId}`;
  }

  openTask(formKey: string, taskId: string): void {
    const baseUrl = window.location.href?.replace(this.router.url, '');
    window.open(
      baseUrl.concat(this.getTaskPath(formKey, taskId)),
      this.targetDblClick,
    );
  }

  updateMenu(columnDefs: AlandaTableColumnDefinition[]): MenuItem[] {
    this.hiddenColumns = {};
    const columnMenuItems: MenuItem[] = [];
    columnDefs.forEach((column) => {
      columnMenuItems.push({
        label: column.displayName,
        icon: 'pi pi-eye',
        command: () => this.onMenuItemColumnClick$.next(column.name),
      });
    });
    return [
      {
        label: 'Primary Actions',
        items: [
          {
            label: 'Download CSV visible page',
            icon: 'pi pi-fw pi-download',
            command: () => this.onExportCsvClick$.next(ExportType.VISIBLE_DATA),
          },
          {
            label: 'Download CSV all pages',
            icon: 'pi pi-fw pi-download',
            command: () => this.onExportCsvClick$.next(ExportType.ALL_DATA),
          },
          {
            label: 'Reset all filters',
            icon: 'pi pi-fw pi-times',
            command: () => this.turboTable.clear(),
          },
        ],
      },
      {
        label: 'Column display',
        items: columnMenuItems,
      },
    ];
  }
}
