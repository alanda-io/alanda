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
import { Router } from '@angular/router';
import { AlandaTableLayout } from '../../api/models/tableLayout';
import { AlandaListResult } from '../../api/models/listResult';
import { AlandaTask } from '../../api/models/task';
import { RxState } from '@rx-angular/state';
import { isObservable, Observable, Subject } from 'rxjs';
import { map } from 'rxjs/operators';
import { APP_CONFIG, AppSettings } from '../../models/appSettings';
import { AlandaProject } from '../../api/models/project';

const defaultLayoutInit = 0;

interface AlandaTaskTableState {
  selectedProject: AlandaProject;
  showProjectDetailsModal: boolean;
  user: AlandaUser;
}

@Component({
  selector: 'alanda-task-table',
  templateUrl: './task-table.component.html',
  styleUrls: ['./task-table.component.scss'],
  providers: [RxState],
})
export class AlandaTaskTableComponent implements OnInit {
  state$ = this.state.select();
  private _defaultLayout = defaultLayoutInit;
  closeProjectDetailsModalEvent$ = new Subject<AlandaProject>();
  setupProjectDetailsModalEvent$ = new Subject<AlandaProject>();
  @Input() set defaultLayout(defaultLayout: number) {
    this._defaultLayout = defaultLayout;
    if (this.layouts) {
      this.selectedLayout = this.layouts[this._defaultLayout];
    }
  }
  @Input() layouts: AlandaTableLayout[];
  @Input() dateFormat: string;
  @Input() tableLayout = 'auto';
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
  @Output() layoutChanged = new Subject<AlandaTableLayout>();
  @Output() toggleGroupTasksChanged = new Subject<boolean>();

  tasksData: AlandaListResult<AlandaTask>;
  selectedLayout: AlandaTableLayout;
  loading = true;
  menuItems: MenuItem[];
  showDelegateDialog = false;
  candidateUsers: any[] = [];
  delegatedTaskData: any;

  @ViewChild('tt') turboTable: Table;

  constructor(
    private readonly taskService: AlandaTaskApiService,
    public messageService: MessageService,
    private readonly router: Router,
    private state: RxState<AlandaTaskTableState>,
    @Inject(APP_CONFIG) config: AppSettings,
  ) {
    if (!this.dateFormat) {
      this.dateFormat = config.DATE_FORMAT;
    }
    this.tasksData = {
      total: 0,
      results: [],
    };

    this.menuItems = [
      {
        label: 'Download CSV',
        icon: 'pi pi-fw pi-download',
        command: (onclick) => this.turboTable.exportCSV(),
      },
      {
        label: 'Reset all filters',
        icon: 'pi pi-fw pi-times',
        command: (onclick) => this.turboTable.clear(),
      },
    ];

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
      this.closeProjectDetailsModalEvent$.pipe(
        map((project) => {
          if (project) {
            this.loadTasksLazy(this.turboTable as LazyLoadEvent);
          }
          return false;
        }),
      ),
    );
  }

  ngOnInit(): void {
    if (!this.selectedLayout) {
      this.selectedLayout = this.layouts[this._defaultLayout];
    }
    this.layouts.sort((a, b) => a.displayName.localeCompare(b.displayName));
  }

  loadTasks(serverOptions: ServerOptions): void {
    this.loading = true;
    this.taskService.loadTasks(serverOptions).subscribe(
      (res) => {
        this.tasksData.total = res.total;
        this.tasksData.results = [...res.results];
        for (const task of this.tasksData.results) {
          task.claimLabel = 'Claim';
          if (this.state.get().user.guid === +task.task.assignee_id) {
            task.claimLabel = 'Unclaim';
          }
        }
        this.loading = false;
      },
      (error) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Load Tasks',
          detail: error.message,
        });
        this.loading = false;
      },
    );
  }

  loadTasksLazy(event: LazyLoadEvent): void {
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

    this.loadTasks(serverOptions);
  }

  onChangeLayout(): void {
    this.loadTasksLazy(this.turboTable as LazyLoadEvent);
    this.layoutChanged.next(this.selectedLayout);
  }

  toggleGroupTasks(value: boolean): void {
    this.groupTasks = value;
    this.loadTasksLazy(this.turboTable as LazyLoadEvent);
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

  claimAction(task): void {
    this.loading = true;
    if (this.state.get().user.guid === +task.task.assignee_id) {
      this.taskService.unclaim(task.task.task_id).subscribe(
        (res) => {
          this.loading = false;
          if (this.groupTasks) {
            task.task.assignee_id = 0;
            task.task.assignee = '';
            task.claimLabel = 'Claim';
          } else {
            this.tasksData.results.splice(
              this.tasksData.results.indexOf(task),
              1,
            );
          }
          this.messageService.add({
            severity: 'success',
            summary: 'Unclaim Task',
            detail: 'Task has been unclaimed',
          });
        },
        (error) => {
          this.loading = false;
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
            this.loading = false;
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
          },
          (error) => {
            this.loading = false;
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

  delegateTask(selectedUser): void {
    if (selectedUser) {
      this.loading = true;
      this.taskService
        .assign(this.delegatedTaskData.task.task_id, selectedUser.guid)
        .subscribe(
          (res) => {
            this.loading = false;
            if (
              this.groupTasks ||
              selectedUser.guid === String(this.state.get().user.guid)
            ) {
              this.delegatedTaskData.task.assignee_id = +selectedUser.guid;
              this.delegatedTaskData.task.assignee = selectedUser.displayName;
            } else {
              this.tasksData.results.splice(
                this.tasksData.results.indexOf(this.delegatedTaskData),
                1,
              );
            }
            this.hideDelegateDialog();
          },
          (err) => {
            this.loading = false;
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

  openTask(formKey: string, taskId: string): void {
    this.router.navigate(['/forms/' + formKey + '/' + taskId]);
  }
}
