import { AlandaComment } from '../../../api/models/comment';
import { Injectable } from '@angular/core';
import { RxState } from '@rx-angular/state';
import { AlandaCommentResponse } from '../../../api/models/commentResponse';
import { AlandaCommentApiService } from '../../../api/commentApi.service';
import { map, switchMap } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { DatePipe } from '@angular/common';
import { toLowerCase } from '../utils';
import { AlandaReplyPostBody } from '../../../api/models/replyPostBody';
import { AlandaCommentPostBody } from '../../../api/models/commenPostBody';
import { AlandaCommentTag } from '../../../api/models/commentTag';

export interface AlandaCommentAdapterState {
  comments: AlandaComment[];
}

/**
 * Provides a state of comments and handles filter and tag processing
 */
@Injectable()
export class AlandaCommentsAdapter extends RxState<AlandaCommentAdapterState> {
  connectFetchComments(processInstanceId$: Observable<string>): void {
    this.connect(
      'comments',
      processInstanceId$.pipe(
        switchMap((processInstanceId: string) =>
          this.commentApiService.getCommentsforPid(processInstanceId),
        ),
        map((response: AlandaCommentResponse) => response.comments),
      ),
      (s, comments: AlandaComment[]) => {
        return comments.map((comment: AlandaComment) => {
          return this.processComment(comment);
        });
      },
    );
  }

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
            comment.replies = [...comment.replies, newReply];
          }
          return comment;
        });
      },
    );
  }

  connectPostComment(
    commentPostBody$: Observable<AlandaCommentPostBody>,
  ): void {
    this.connect(
      'comments',
      commentPostBody$.pipe(
        switchMap((commentPostBody) =>
          this.commentApiService.postComment(commentPostBody),
        ),
      ),
      (oldState, newComment: AlandaComment) => {
        return [this.processComment(newComment), ...oldState.comments];
      },
    );
  }

  constructor(
    private readonly datePipe: DatePipe,
    private readonly commentApiService: AlandaCommentApiService,
  ) {
    super();
    this.set({ comments: [] });
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
    if (comment.taskName !== '' && comment.taskName != null) {
      comment.tagList.push({ name: comment.taskName, type: 'task' });
    }
    // Add process name to tag list
    if (comment.processName !== '' && comment.processName != null) {
      comment.tagList.push({ name: comment.processName, type: 'process' });
    }
    // Add process name to tag list
    if (comment.businessKey !== '' && comment.businessKey != null) {
      comment.tagList.push({ name: comment.businessKey, type: 'bo' });
    }
    // Extract hashtags out of subject
    if (comment.subject?.match(/#\w+/g)) {
      comment.subject.match(/#\w+/g).forEach((value: string) => {
        comment.tagList.push({ name: value.substr(1), type: 'user' });
      });
      comment.subject = comment.subject.replace(/#\w+/g, '');
    }
    // Extract hashtags out of text body
    if (comment.text?.match(/#\w+/g)) {
      comment.text.match(/#\w+/g).forEach((value: string) => {
        const tagName = value.substr(1);
        if (!this.containsTag(comment.tagList, tagName)) {
          comment.tagList.push({ name: tagName, type: 'user' });
        }
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

  private containsTag(tags: AlandaCommentTag[], tagName: string): boolean {
    for (const tag of tags) {
      if (tag.name === tagName) {
        return true;
      }
    }
    return false;
  }
}
