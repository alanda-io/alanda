import { Component, Inject, Input } from '@angular/core';
import { AlandaProject } from '../../../api/models/project';
import { AlandaTask } from '../../../api/models/task';
import { AlandaUser } from '../../../api/models/user';
import { APP_CONFIG, AppSettings } from '../../../models/appSettings';
import { AlandaTaskApiService } from '../../../api/taskApi.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'alanda-page-header-bar',
  templateUrl: './page-header-bar.component.html',
  styleUrls: ['./page-header-bar.component.scss'],
})
export class PageHeaderBarComponent {
  @Input() project: AlandaProject;
  @Input() task: AlandaTask;

  candidateUsers: AlandaUser[];
  dateFormat: string;
  showDelegateDialog: boolean;

  constructor(
    private readonly taskService: AlandaTaskApiService,
    private readonly messageService: MessageService,
    @Inject(APP_CONFIG) config: AppSettings,
  ) {}

  openDelegationForm(): void {
    this.taskService
      .getCandidates(this.task.task_id)
      .subscribe((candidates) => {
        this.candidateUsers = candidates;
        this.showDelegateDialog = true;
      });
  }

  delegateTask(selectedUser: AlandaUser): void {
    if (selectedUser) {
      this.taskService.assign(this.task.task_id, selectedUser.guid).subscribe(
        () => {
          this.task.assignee_id = '' + selectedUser.guid;
          this.task.assignee = selectedUser.displayName;
          this.showDelegateDialog = false;
        },
        (error) =>
          this.messageService.add({
            severity: 'error',
            summary: 'Delegate Task',
            detail: error.message,
          }),
      );
    }
  }
}
