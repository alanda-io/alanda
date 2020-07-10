import { Component, Input } from '@angular/core';
import { AlandaCommentsAdapter } from './comments.adapter';
import { AlandaCommentsPresenter } from './comments.presenter';
import { AlandaProject } from '../../../api/models/project';
import { AlandaTask } from '../../../api/models/task';
import { RxState } from '@rx-angular/state';
import { combineLatest } from 'rxjs';
import { map, withLatestFrom } from 'rxjs/operators';

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
  providers: [AlandaCommentsAdapter, AlandaCommentsPresenter],
})
export class AlandaCommentsComponent extends RxState<AlandaCommentsState>{
  @Input()
  set task(task: AlandaTask) {
    this.set({ task });
  }

  @Input()
  set project(project: AlandaProject) {
    this.set({ project });
  }

  processInstanceId$ = combineLatest([
    this.select('project'),
    this.select('task')
  ]).pipe(
    map(([project, task]) => {
      return task ? task.process_instance_id : project.projectId
    })
  )

  commentPostBody$ = this.cp.commentText$.pipe(
    withLatestFrom(this.processInstanceId$),
    map(([commentText, processInstanceId]) => {
      const taskId = this.get().task ? this.get().task.task_id : null;
      return {
        subject: ' ', // Bad API
        text: commentText,
        taskId,
        procInstId: processInstanceId
      }
    })
  )

  constructor(
    private readonly ca: AlandaCommentsAdapter,
    readonly cp: AlandaCommentsPresenter,
  ) {
    super();
    this.cp.connect('comments', ca.select('comments'));
    this.ca.connectFetchComments(this.processInstanceId$);
    this.ca.connectPostComment(this.commentPostBody$);
    this.ca.connectPostReply(this.cp.replyPostBody$);
  }
}
