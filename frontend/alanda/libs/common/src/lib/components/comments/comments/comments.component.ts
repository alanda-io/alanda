import { Component, Input } from '@angular/core';
import { AlandaCommentsAdapter } from './comments.adapter';
import { AlandaCommentsPresenter } from './comments.presenter';
import { AlandaProject } from '../../../api/models/project';
import { AlandaTask } from '../../../api/models/task';
import { RxState } from '@rx-angular/state';
import { combineLatest, Observable } from 'rxjs';
import { filter, map, startWith } from 'rxjs/operators';
import { AlandaCommentPostBody } from '../../../api/models/commenPostBody';
import { FormBuilder, FormGroup } from '@angular/forms';

interface AlandaCommentsState {
  task: AlandaTask;
  project: AlandaProject;
}

/**
 * Wrapper component for comments that provides access to filters and creating new comments.
 */
@Component({
  selector: 'alanda-comments',
  templateUrl: './comments.component.html',
  styleUrls: ['./comments.component.scss'],
  providers: [AlandaCommentsAdapter, AlandaCommentsPresenter, RxState],
})
export class AlandaCommentsComponent {
  @Input()
  set task(task: AlandaTask) {
    this.state.set({ task });
  }

  @Input()
  set project(project: AlandaProject) {
    this.state.set({ project });
  }

  @Input()
  set rootFormGroup(rootFormGroup: FormGroup) {
    if (rootFormGroup) {
      rootFormGroup.addControl(
        'alanda-task-has-comment',
        this.taskHasCommentForm,
      );
    }
  }

  taskHasCommentForm = this.fb.group({
    hasComment: [null],
  });

  processInstanceId$ = combineLatest([
    this.state.select('project').pipe(startWith(null)),
    this.state.select('task').pipe(startWith(null)),
  ]).pipe(
    filter(([project, task]) => project !== null || task !== null),
    map(([project, task]) => {
      if (task) {
        return task.process_instance_id;
      }

      if (project) {
        return project.processes.find((process) => process.relation === 'MAIN')
          .processInstanceId;
      }

      return null;
    }),
  );

  commentPostBody$: Observable<AlandaCommentPostBody> = combineLatest([
    this.cp.commentText$,
    this.processInstanceId$,
    this.state.select('task'),
  ]).pipe(
    map(([commentText, processInstanceId, task]) => {
      return {
        subject: ' ', // Bad API
        text: commentText,
        taskId: task ? task.task_id : null,
        procInstId: processInstanceId,
      };
    }),
  );

  constructor(
    private state: RxState<AlandaCommentsState>,
    private readonly ca: AlandaCommentsAdapter,
    readonly cp: AlandaCommentsPresenter,
    private readonly fb: FormBuilder,
  ) {
    this.cp.connect(
      'comments',
      this.ca.select('comments'),
      (oldState, comments) => {
        if (this.state.get().task) {
          this.taskHasCommentForm.setValue({
            hasComment:
              comments.findIndex(
                (_comment) => _comment.taskId === this.state.get().task.task_id,
              ) !== -1
                ? true
                : null,
          });
        }

        return comments;
      },
    );
    this.ca.connectFetchComments(this.processInstanceId$);
    this.ca.connectPostComment(this.commentPostBody$);
    this.ca.connectPostReply(this.cp.replyPostBody$);
  }
}
