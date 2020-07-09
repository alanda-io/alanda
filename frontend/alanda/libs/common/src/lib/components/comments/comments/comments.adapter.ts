import { AlandaComment } from '../../../api/models/comment';
import { Injectable } from '@angular/core';
import { RxState } from '@rx-angular/state';
import { AlandaTask } from '../../../api/models/task';
import { AlandaCommentResponse } from '../../../api/models/commentResponse';
import { AlandaCommentApiService } from '../../../api/commentApi.service';
import { map, switchMap, withLatestFrom } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { AlandaTaskFormService } from '../../../form/alanda-task-form.service';
import { DatePipe } from '@angular/common';
import { toLowerCase } from '../utils';
import { AlandaReplyPostBody } from '../../../api/models/replyPostBody';

export interface AlandaCommentAdapterState {
  comments: AlandaComment[];
}

/**
 * Provides a state of comments and handles filter and tag processing
 */
@Injectable()
export class AlandaCommentsAdapter extends RxState<AlandaCommentAdapterState> {
  task$ = this.taskFormService.select('task');

  commentResponse$ = this.task$.pipe(
    switchMap((task: AlandaTask) =>
      this.commentApiService.getCommentsforPid(task.process_instance_id),
    ),
    map((result: AlandaCommentResponse) => result.comments),
  );

  connectPostReply(replyPostBody$: Observable<AlandaReplyPostBody>): void {
    this.connect(
      'comments',
      replyPostBody$.pipe(
        switchMap((replyPostBody) =>
          this.commentApiService.postComment(replyPostBody),
        ),
      ),
      (oldState, newReply: AlandaComment) => {
        return oldState.comments.map((comment) => {
          if (comment.guid === newReply.replyTo) {
            comment.replies = [newReply, ...comment.replies];
          }
          return comment;
        });
      },
    );
  }

  connectPostComment(commentText$: Observable<string>): void {
    this.connect(
      'comments',
      commentText$.pipe(
        withLatestFrom(this.task$),
        switchMap(([commentText, task]) =>
          this.commentApiService.postComment({
            subject: ' ', // Bad API
            text: commentText,
            taskId: task.task_id,
            procInstId: task.process_instance_id,
          }),
        ),
      ),
      (oldState, newComment: AlandaComment) => {
        return [this.processComment(newComment), ...oldState.comments];
      },
    );
  }

  constructor(
    private readonly datePipe: DatePipe,
    private readonly taskFormService: AlandaTaskFormService,
    private readonly commentApiService: AlandaCommentApiService,
  ) {
    super();
    this.set({ comments: [] });
    this.connect('comments', this.commentResponse$, (s, comments) => {
      return comments.map((comment: AlandaComment) => {
        return this.processComment(comment);
      });
    });
  }

  /**
   * Process an AlandaComment by commentFulltext and tagList and returns a new AlandaComment
   * @return AlandaComment
   */
  processComment(oldComment: AlandaComment): AlandaComment {
    const comment: AlandaComment = Object.assign({}, oldComment);

    comment.createDate = new Date(comment.createDate);
    comment.textDate = this.datePipe.transform(
      comment.createDate,
      'dd.LL.yyyy HH:mm',
    );

    let commentFulltext = this.extendFulltextComment(comment.text);
    commentFulltext = this.extendFulltextComment(
      comment.authorName,
      commentFulltext,
    );
    commentFulltext = this.extendFulltextComment(
      comment.textDate,
      commentFulltext,
    );

    comment.tagList = [];
    // Add task name to tag list
    if (comment.taskName !== '' && comment.taskName !== null) {
      comment.tagList.push({ name: comment.taskName, type: 'task' });
    }
    // Extract hashtags out of subject
    if (comment.subject?.includes('#')) {
      comment.subject.match(/#\w+/g).forEach((value: string) => {
        comment.tagList.push({ name: value.substr(1), type: 'user' });
      });
      comment.subject = comment.subject.replace(/#\w+/g, '');
    }
    // Extract hashtags out of text body
    if (comment.text?.includes('#')) {
      comment.text.match(/#\w+/g).forEach((value: string) => {
        comment.tagList.push({ name: value.substr(1), type: 'user' });
      });
      comment.text = comment.text.replace(/#\w+/g, '').trim();
    }

    comment.tagList.forEach((tag) => {
      commentFulltext += ` ${tag.name}`;
    });

    comment.replies = comment.replies.map((reply: AlandaComment) => {
      reply.createDate = new Date(reply.createDate);
      reply.textDate = this.datePipe.transform(
        reply.createDate,
        'dd.LL.yyyy HH:mm',
      );

      commentFulltext = this.extendFulltextComment(reply.text, commentFulltext);
      commentFulltext = this.extendFulltextComment(
        reply.authorName,
        commentFulltext,
      );
      commentFulltext = this.extendFulltextComment(
        reply.textDate,
        commentFulltext,
      );
      return reply;
    });

    comment.fulltext = commentFulltext;
    return comment;
  }

  extendFulltextComment(text: string, fulltext: string = ''): string {
    if (text !== null && text.length > 0) {
      fulltext += ` ${toLowerCase(text)}`;
    }
    return fulltext.trim();
  }
}
