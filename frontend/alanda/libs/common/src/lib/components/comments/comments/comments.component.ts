import { Component, Input } from '@angular/core';
import { AlandaCommentsAdapter } from './comments.adapter';
import { AlandaCommentsPresenter } from './comments.presenter';
import { AlandaProject } from '../../../api/models/project';
import { AlandaTask } from '../../../api/models/task';
import { RxState } from '@rx-angular/state';
import { combineLatest, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { AlandaCommentPostBody } from '../../../api/models/commenPostBody';

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
export class AlandaCommentsComponent extends RxState<AlandaCommentsState> {
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
    this.select('task'),
  ]).pipe(
    map(([project, task]) => {
      if (task) {
        return task.process_instance_id;
      }

      return project.processes.find(process =>
        process.relation === 'MAIN'
      ).processInstanceId;
    }),
  );

  commentPostBody$: Observable<AlandaCommentPostBody> = combineLatest([
    this.cp.commentText$,
    this.processInstanceId$,
    this.select('task'),
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
    private readonly ca: AlandaCommentsAdapter,
    readonly cp: AlandaCommentsPresenter,
  ) {
    super();
    this.set({ task: null });
    this.cp.connect('comments', ca.select('comments'));
    this.ca.connectFetchComments(this.processInstanceId$);
    this.ca.connectPostComment(this.commentPostBody$);
    this.ca.connectPostReply(this.cp.replyPostBody$);
  }
}