import { AlandaComment } from '../api/models/comment';
import { Injectable } from '@angular/core';
import { RxState } from '@rx-angular/state';
import { AlandaTask } from '../api/models/task';
import { AlandaCommentResponse } from '../api/models/commentResponse';
import { AlandaCommentTag } from '../api/models/commentTag';
import { AlandaCommentApiService } from '../api/commentApi.service';
import { DatePipe } from '@angular/common';
import { AlandaTaskFormService } from '../form/alanda-task-form.service';
import { map, share, switchMap } from 'rxjs/operators';
import { combineLatest, merge } from 'rxjs';

export interface AlandaCommentState {
  comments: AlandaComment[];
  tagObjectMap: {[tagName: string]: AlandaCommentTag};
  activeTagFilters: {[tagName: string]: boolean};
  searchText: string;
  commentText: string;
  task: AlandaTask;
}

@Injectable({ providedIn: 'root' })
export class AlandaCommentsService extends RxState<AlandaCommentState> {
  task$ = this.taskFormService.select('task');

  commentResponse$ = this.task$.pipe(
    switchMap((task: AlandaTask) => this.commentApiService.getCommentsforPid(task.process_instance_id)),
    share()
  );

  commentPostResponse$ = this.select('commentText').pipe(
    switchMap((commentText: string) => this.commentApiService.postComment({
      subject: ' ',
      text: commentText,
      taskId: this.get().task.task_id,
      procInstId: this.get().task.process_instance_id,
    })),
    map((newComment: AlandaComment) => {
      this.set('comments', oldState => {
        const comments = oldState.comments;
        const comment: AlandaComment = Object.assign({}, newComment);
        comments.unshift(this.processComment(comment));
        return comments;
      });
      return newComment;
    }),
    switchMap((comment: AlandaComment) => this.commentApiService.getCommentsforPid(comment.procInstId))
  );

  comments$ = merge(this.commentResponse$, this.commentPostResponse$).pipe(
    map((commentResponse: AlandaCommentResponse) => {
      return commentResponse.comments.map((comment: AlandaComment) => {
        return this.processComment(comment);
      });
    })
  );

  tagObjectMap$ = this.commentResponse$.pipe(
    map((commentResponse: AlandaCommentResponse) => {
      const tagMap = {};
      commentResponse.comments.forEach((comment: AlandaComment) => {
        comment.tagList.forEach((tag: AlandaCommentTag) => {
          tagMap[tag.name] = { name: tag.name, type: tag.type };
        });
      });
      return tagMap;
    })
  );

  activeTagFilters$ = this.select('tagObjectMap').pipe(
    map(tags => {
      const activeFilters = {};
      Object.keys(tags).forEach((key: string) => {
        activeFilters[key] = false;
      });
      return activeFilters;
    })
  );

  filteredComments$ = combineLatest(
    [this.select('comments'),
      this.select('searchText'),
      this.select('activeTagFilters')]
  ).pipe(
    map(([comments, searchText, activeTagFilters]: [AlandaComment[], string, {string: boolean}]) => {
      const filteredComments = this.filterCommentsBySearchText(comments, searchText);
      return this.filterCommentsByTags(filteredComments, activeTagFilters);
    }),
  );

  constructor(
    private readonly commentApiService: AlandaCommentApiService,
    private readonly datePipe: DatePipe,
    private readonly taskFormService: AlandaTaskFormService) {
    super();
    this.set({ comments: [], activeTagFilters: {}, searchText: '' });
    this.connect('task', this.task$);
    this.connect('activeTagFilters', this.activeTagFilters$);
    this.connect('comments', this.comments$);
    this.connect('tagObjectMap', this.tagObjectMap$);
  }

  processComment(comment: AlandaComment): AlandaComment {
    comment.createDate = new Date(comment.createDate);
    comment.textDate = this.datePipe.transform(comment.createDate, 'dd.LL.yy HH:mm');

    let commentFulltext = this.extendFulltextComment(comment.text);
    commentFulltext = this.extendFulltextComment(comment.authorName, commentFulltext);
    commentFulltext = this.extendFulltextComment(comment.textDate, commentFulltext);

    comment.tagList = [];
    if (comment.taskName !== '') {
      comment.tagList.push({ name: comment.taskName, type: 'task' });
    }
    if (comment.subject.includes('#')) {
      comment.subject.match(/#\w+/g).forEach((value: string) => {
        comment.tagList.push({ name: value, type: 'user' });
      });
      comment.subject = comment.subject.replace(/#\w+/g, '');
    }

    comment.tagList.forEach(tag => {
      commentFulltext += ` ${tag.name}`;
    });

    comment.replies = comment.replies.map((reply: AlandaComment) => {
      reply.createDate = new Date(reply.createDate);
      reply.textDate = this.datePipe.transform(reply.createDate, 'dd.LL.yy HH:mm');

      commentFulltext = this.extendFulltextComment(reply.text, commentFulltext);
      commentFulltext = this.extendFulltextComment(reply.authorName, commentFulltext);
      commentFulltext = this.extendFulltextComment(reply.textDate, commentFulltext);
      return reply;
    });

    comment.fulltext = commentFulltext;
    return comment;
  }

  extendFulltextComment(text: string, fulltext: string = ''): string {
    if (text !== null && text.length > 0) {
      fulltext += ` ${text.toLowerCase()}`;
    }
    return fulltext.trim();
  }

  filterCommentsBySearchText(comments: AlandaComment[], searchText: string): Array<AlandaComment> {
    if (searchText.length > 0) {
      return comments.filter((comment: AlandaComment) => {
        return comment.fulltext.trim().toLowerCase().includes(searchText);
      });
    }
    return comments;
  }

  filterCommentsByTags(comments: AlandaComment[], activeTagFilters: {string: boolean }): Array<AlandaComment> {
    const filteredComments = comments.filter((comment: AlandaComment) => {
      return comment.tagList.findIndex((tag: AlandaCommentTag) => {
        return activeTagFilters[tag.name];
      }) > -1;
    });
    return filteredComments.length > 0 ? filteredComments : comments;
  }

  tagClass(tag: AlandaCommentTag): string {
    if (this.get().activeTagFilters[tag.name]) {
      return 'ui-button-success';
    }
    return 'ui-button-info';
  }

  toggleTagFilter(tag: AlandaCommentTag): void {
    this.set('activeTagFilters', oldState => {
      return { [tag.name]: !oldState.activeTagFilters[tag.name] };
    });
  }

  clearAllTagFilters(): void {
    this.set({ activeTagFilters: {} });
  }
}
