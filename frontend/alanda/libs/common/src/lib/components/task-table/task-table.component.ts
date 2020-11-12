import {
  Component,
  Inject,
  Input,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import { LazyLoadEvent, MenuItem, MessageService } from 'primeng/api';
import { Table } from 'primeng/table';
import { ServerOptions } from '../../models/serverOptions';
import { AlandaUser } from '../../api/models/user';
import { AlandaTaskApiService } from '../../api/taskApi.service';
import { AlandaTableLayout } from '../../api/models/tableLayout';
import { AlandaListResult } from '../../api/models/listResult';
import { AlandaTask } from '../../api/models/task';
import { RxState } from '@rx-angular/state';
import { isObservable, merge, Observable, of, Subject } from 'rxjs';
import {
  catchError,
  delay,
  filter,
  map,
  startWith,
  switchMap,
  tap,
} from 'rxjs/operators';
import { APP_CONFIG, AppSettings } from '../../models/appSettings';
import { AlandaProject } from '../../api/models/project';
import { exportAsCsv, formatDateISO } from '../../utils/helper-functions';
import { AlandaTableColumnDefinition } from '../../api/models/tableColumnDefinition';

const DEFAULT_LAYOUT_INIT = 0;
const EXPORT_FILE_NAME = 'download';
const CLAIM_TEXT = 'Claim';
const UNCLAIM_TEXT = 'Unclaim';

interface AlandaTaskTableState {
  loading: boolean;
  selectedProject: AlandaProject;
  showProjectDetailsModal: boolean;
  user: AlandaUser;
  serverOptions: ServerOptions;
  tasksData: AlandaListResult<AlandaTask>;
}

const initState = {
  tasksData: {
    total: 0,
    results: [],
  },
};

@Component({
  selector: 'alanda-task-table',
  templateUrl: './task-table.component.html',
  styleUrls: ['./task-table.component.scss'],
  providers: [RxState],
})
export class AlandaTaskTableComponent implements OnInit {
  state$ = this.state.select();
  private _defaultLayout = DEFAULT_LAYOUT_INIT;
  closeProjectDetailsModalEvent$ = new Subject<AlandaProject>();
  needReloadEvent$ = new Subject();
  setupProjectDetailsModalEvent$ = new Subject<AlandaProject>();
  tableLazyLoadEvent$ = new Subject<LazyLoadEvent>();
  @Input() set defaultLayout(defaultLayout: number) {
    this._defaultLayout = defaultLayout;
    if (this.layouts) {
      this.selectedLayout = this.layouts[this._defaultLayout];
    }
  }
  @Input() layouts: AlandaTableLayout[];
  @Input() dateFormat: string;
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
  @Input() routerBasePath = '/forms';
  @Output() layoutChanged = new Subject<AlandaTableLayout>();
  @Output() toggleGroupTasksChanged = new Subject<boolean>();

  selectedLayout: AlandaTableLayout;
  menuItems: MenuItem[];
  showDelegateDialog = false;
  candidateUsers: any[] = [];
  delegatedTaskData: any;
  dateFormatPrime: string;
  hiddenColumns = {};
  filteredColumns: AlandaTableColumnDefinition[] = [];

  @ViewChild('tt') turboTable: Table;

  /**
   * Loads tasks from server when the server options changes or
   * when a need reload event is triggered
   *
   * @returns data { tasksData: AlandaListResult<AlandaTask>, working: boolean }
   */
  loadTaskFromServer$ = merge(
    this.tableLazyLoadEvent$,
    this.needReloadEvent$.pipe(
      delay(1000),
      map(() => this.turboTable),
    ),
  ).pipe(
    map((event) => this.buildServerOptions(event)),
    switchMap((serverOptions) =>
      this.taskService.loadTasks(serverOptions).pipe(
        map((tasks) => this.mapClaimLabels(tasks)),
        map((tasksData) => ({ serverOptions, tasksData, loading: false })),
        startWith({ loading: true }),
        catchError((err) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Load Tasks',
            detail: err.message,
          });
          return of({ serverOptions, loading: false });
        }),
      ),
    ),
  );

  constructor(
    private readonly taskService: AlandaTaskApiService,
    public messageService: MessageService,
    private state: RxState<AlandaTaskTableState>,
    @Inject(APP_CONFIG) config: AppSettings,
  ) {
    this.state.set(initState);
    if (!this.dateFormat) {
      this.dateFormat = config.DATE_FORMAT;
    }
    this.dateFormatPrime = config.DATE_FORMAT_PRIME;

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
    this.state.hold(this.tableLazyLoadEvent$);
  }

  ngOnInit(): void {
    if (!this.selectedLayout) {
      this.selectedLayout = this.layouts[this._defaultLayout];
      this.filteredColumns = this.layouts[this._defaultLayout].columnDefs;
      this.menuItems = this.updateMenu(this.filteredColumns);
    }
    this.layouts.sort((a, b) => a.displayName.localeCompare(b.displayName));
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

    if (this.selectedLayout.filterOptions) {
      for (const [key, value] of Object.entries(
        this.selectedLayout.filterOptions,
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

  mapClaimLabels(
    tasks: AlandaListResult<AlandaTask>,
  ): AlandaListResult<AlandaTask> {
    const user = this.state.get()?.user;
    const newList = tasks?.results?.map((task) => {
      task.claimLabel =
        user?.guid === +task.task?.assignee_id ? UNCLAIM_TEXT : CLAIM_TEXT;
      return task;
    });

    tasks.results = newList;
    return tasks;
  }

  onChangeLayout(): void {
    this.needReloadEvent$.next();
    this.layoutChanged.next(this.selectedLayout);
    this.filteredColumns = this.selectedLayout.columnDefs;
    this.menuItems = this.updateMenu(this.filteredColumns);
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

  // TODO Kill and Refactor
  claimAction(task): void {
    this.state.set({ loading: true });
    if (this.state.get().user.guid === +task.task.assignee_id) {
      this.taskService.unclaim(task.task.task_id).subscribe(
        () => {
          if (this.groupTasks) {
            task.task.assignee_id = 0;
            task.task.assignee = '';
            task.claimLabel = 'Claim';
          } else {
            const tasksData = this.state.get()?.tasksData;
            tasksData.results.splice(tasksData.results.indexOf(task), 1);
            this.state.set({ tasksData });
          }
          this.messageService.add({
            severity: 'success',
            summary: 'Unclaim Task',
            detail: 'Task has been unclaimed',
          });
          this.needReloadEvent$.next();
        },
        (error) => {
          this.state.set({ loading: false });
          this.messageService.add({
            severity: 'error',
            summary: 'Unclaim Task',
            detail: error.message,
          });
        },
      );
    } else {
      this.taskService
        .assign(task.task.task_id, this.state.get().user.guid)
        .subscribe(
          (res) => {
            task.task.assignee_id = String(this.state.get().user.guid);
            task.task.assignee =
              this.state.get().user.firstName +
              ' ' +
              this.state.get().user.surname;
            task.claimLabel = 'Unclaim';
            this.messageService.add({
              severity: 'success',
              summary: 'Claim Task',
              detail: 'Task has been claimed',
            });
            this.needReloadEvent$.next();
          },
          (error) => {
            this.state.set({ loading: false });
            this.messageService.add({
              severity: 'error',
              summary: 'Claim Task',
              detail: error.message,
            });
          },
        );
    }
  }

  openDelegationForm(data): void {
    this.delegatedTaskData = data;
    this.taskService.getCandidates(data.task.task_id).subscribe((res) => {
      this.candidateUsers = res;
      this.showDelegateDialog = true;
    });
  }

  // TODO Kill and Refactor
  delegateTask(selectedUser): void {
    if (selectedUser) {
      this.state.set({ loading: true });
      this.taskService
        .assign(this.delegatedTaskData.task.task_id, selectedUser.guid)
        .subscribe(
          (res) => {
            if (
              this.groupTasks ||
              selectedUser.guid === String(this.state.get().user.guid)
            ) {
              this.delegatedTaskData.task.assignee_id = +selectedUser.guid;
              this.delegatedTaskData.task.assignee = selectedUser.displayName;
            } else {
              const tasksData = this.state.get()?.tasksData;
              tasksData.results.splice(
                tasksData.results.indexOf(this.delegatedTaskData),
                1,
              );
              this.state.set({ tasksData });
            }
            this.hideDelegateDialog();
            this.needReloadEvent$.next();
          },
          (err) => {
            this.state.set({ loading: false });
            this.hideDelegateDialog();
          },
        );
    }
  }

  hideDelegateDialog(): void {
    this.delegatedTaskData = {};
    this.showDelegateDialog = false;
  }

  encodeURIAndReplace(v): string {
    return encodeURIComponent(v).replace(/%/g, '~');
  }

  getTaskPath(formKey: string, taskId: string): string {
    return `${this.routerBasePath}/${formKey}/${taskId}`;
  }

  openTask(formKey: string, taskId: string): void {
    window.open(this.getTaskPath(formKey, taskId), '_blank');
  }

  onDateSelect(value, field): void {
    this.turboTable.filter(formatDateISO(value), field, 'contains');
  }

  // TODO Kill and Refactor
  exportAllData() {
    const { serverOptions, tasksData } = this.state.get();
    serverOptions.pageNumber = 1;
    serverOptions.pageSize = tasksData.total;
    this.taskService.loadTasks(serverOptions).subscribe((res) => {
      const data = [...res.results];
      exportAsCsv(data, this.selectedLayout.columnDefs, EXPORT_FILE_NAME);
    });
  }

  updateMenu(columnDefs: AlandaTableColumnDefinition[]): MenuItem[] {
    this.hiddenColumns = {};
    const columnMenuItems: MenuItem[] = [];
    columnDefs.forEach((column) => {
      columnMenuItems.push({
        label: column.displayName,
        icon: 'pi pi-eye',
        command: () => this.toggleColumn(column.name),
      });
    });

    return [
      {
        label: 'Primary Actions',
        items: [
          {
            label: 'Download CSV visible page',
            icon: 'pi pi-fw pi-download',
            command: () => this.turboTable.exportCSV(),
          },
          {
            label: 'Download CSV all pages',
            icon: 'pi pi-fw pi-download',
            command: () => this.exportAllData(),
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

  toggleColumn(name: string): void {
    if (this.hiddenColumns.hasOwnProperty(name)) {
      const index = this.hiddenColumns[name];
      delete this.hiddenColumns[name];
      this.menuItems[1].items[index].icon = 'pi pi-eye';
    } else {
      this.selectedLayout.columnDefs.some((column, index) => {
        if (column.name === name) {
          this.hiddenColumns[column.name] = index;
          this.menuItems[1].items[index].icon = 'pi pi-eye-slash';
          return true;
        }
      });
    }
    this.filteredColumns = this.selectedLayout.columnDefs.filter(
      (column, index) => {
        return !this.hiddenColumns.hasOwnProperty(column.name);
      },
    );
  }
}
