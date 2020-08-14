import { Component, OnInit, ViewChild } from '@angular/core';
import { LazyLoadEvent, MenuItem, MessageService } from 'primeng/api';
import { Table } from 'primeng/table';
import { ServerOptions } from '../../models/serverOptions';
import { AlandaMonitorAPIService } from '../../services/monitorApi.service';
import { AlandaUser } from '../../api/models/user';
import { AlandaTaskApiService } from '../../api/taskApi.service';
import { AlandaUserApiService } from '../../api/userApi.service';
import { Router } from '@angular/router';

@Component({
  selector: 'alanda-task-table',
  templateUrl: './task-table.component.html',
  styleUrls: ['./task-table.component.scss'],
})
export class AlandaTaskTableComponent implements OnInit {
  tasksData: any = {};
  validLayouts: any[] = [];
  selectedLayout: any = {};
  loading = true;
  groupTasks = false;
  currentUser: AlandaUser;
  serverOptions: ServerOptions;
  menuItems: MenuItem[];
  showDelegateDialog = false;
  candidateUsers: any[] = [];
  delegatedTaskData: any;

  @ViewChild('tt') turboTable: Table;

  constructor(
    private readonly taskService: AlandaTaskApiService,
    private readonly monitorApiService: AlandaMonitorAPIService,
    private readonly userService: AlandaUserApiService,
    public messageService: MessageService,
    private readonly router: Router,
  ) {
    this.serverOptions = {
      pageNumber: 1,
      pageSize: 15,
      filterOptions: { hideSnoozedTasks: 1, mytasks: 1 },
      sortOptions: {},
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
        command: (onclick) => this.turboTable.reset(),
      },
    ];
  }

  ngOnInit(): void {
    this.loading = true;
    this.userService.getCurrentUser().subscribe(
      (user) => {
        this.currentUser = user;
        this.validLayouts = this.monitorApiService.getTaskListLayouts(user);
        this.selectedLayout = this.validLayouts.filter(
          (layout) => layout.name === 'default',
        )[0];
        this.validLayouts.sort((a, b) =>
          a.displayName.localeCompare(b.displayName),
        );
      },
      (error) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Get Current User',
          detail: error.message,
        });
        this.loading = false;
      },
    );
  }

  loadTasks(serverOptions: ServerOptions): void {
    this.loading = true;
    this.taskService.loadTasks(serverOptions).subscribe(
      (res) => {
        this.tasksData = res;
        for (const task of this.tasksData.results) {
          task.claimLabel = 'Claim';
          if (this.currentUser.guid === +task.task.assignee_id) {
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
    this.serverOptions = this.getNewServerOptions();
    if (event.sortField) {
      const sortOptions = {};
      const dir = event.sortOrder === 1 ? 'asc' : 'desc';
      sortOptions[event.sortField] = { dir, prio: 0 };
      this.serverOptions.sortOptions = sortOptions;
    }

    Object.keys(event.filters).forEach((key) => {
      this.serverOptions.filterOptions[key] = event.filters[key].value;
    });

    this.serverOptions.pageNumber =
      event.first / this.serverOptions.pageSize + 1;
    this.loadTasks(this.serverOptions);
  }

  onChangeLayout(): void {
    this.serverOptions.pageNumber = 1;
    const key = 'project.additionalInfo.rootparent.projectTypeIdName';
    this.serverOptions.filterOptions = { hideSnoozedTasks: 1 };
    if (!this.groupTasks) {
      this.serverOptions.filterOptions.mytasks = 1;
    }
    if (this.selectedLayout.filterOptions) {
      delete this.serverOptions.filterOptions[key];
      this.serverOptions.filterOptions[key] = this.selectedLayout.filterOptions[
        key
      ];
    } else {
      delete this.serverOptions.filterOptions[key];
    }
    this.loadTasks(this.serverOptions);
  }

  toggleGroupTasks(v: boolean): void {
    this.groupTasks = v;
    delete this.serverOptions.filterOptions.mytasks;
    if (!this.groupTasks) {
      this.serverOptions.filterOptions.mytasks = 1;
    }
    this.loadTasks(this.serverOptions);
  }

  getCondition(obj, condition): any {
    if (condition === undefined) return '';
    const props = Object.keys(obj).reduce((acc, next) => `${acc} , ${next}`);
    const evalCon = new Function(
      ` return function ({${props}})  { return ${condition}} `,
    );
    return evalCon()(obj);
  }

  claimAction(task): void {
    this.loading = true;
    if (this.currentUser.guid === +task.task.assignee_id) {
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
        .assign(task.task.task_id, this.currentUser.guid)
        .subscribe(
          (res) => {
            this.loading = false;
            task.task.assignee_id = String(this.currentUser.guid);
            task.task.assignee =
              this.currentUser.firstName + ' ' + this.currentUser.surname;
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

  getNewServerOptions(): ServerOptions {
    const serverOptions: ServerOptions = {
      pageNumber: 1,
      pageSize: 15,
      filterOptions: {
        hideSnoozedTasks: 1,
      },
      sortOptions: {},
    };
    if (this.selectedLayout.filterOptions) {
      for (const key of Object.keys(this.selectedLayout.filterOptions)) {
        serverOptions.filterOptions[key] = this.selectedLayout.filterOptions[
          key
        ];
      }
    }
    if (!this.groupTasks) {
      serverOptions.filterOptions.mytasks = 1;
    }
    return serverOptions;
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
              selectedUser.guid === String(this.currentUser.guid)
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
