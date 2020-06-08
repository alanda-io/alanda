import { Component, OnInit, Input } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { MessageService } from 'primeng/api';
import { AlandaComment } from '../../api/models/comment';
import { AlandaCommentTag } from '../../api/models/commentTag';
import { AlandaCommentApiService } from '../../api/commentApi.service';

@Component({
  selector: 'alanda-comments',
  templateUrl: './comments.component.html',
  styleUrls: ['./comments.component.scss']
})
export class AlandaCommentsComponent implements OnInit {
  @Input() task: any;
  @Input() pid: string;
  loadingInProgress = false;
  filterEnabled = false;
  comments: AlandaComment[] = [];
  tags: AlandaCommentTag[] = [];
  procInstId: string;
  taskId: string;
  tagHash = {};
  tagFilters: string[] = [];

  commentForm = this.fb.group({
    comment: ['', Validators.required]
  });

  commentFilterForm = this.fb.group({
    searchText: ['']
  });

  constructor (private readonly commentService: AlandaCommentApiService,
    private readonly messageService: MessageService,
    private readonly datePipe: DatePipe,
    private readonly fb: FormBuilder) {}

  ngOnInit (): void {
    if (this.task) {
      this.procInstId = this.task.process_instance_id;
      this.taskId = this.task.task_id;
    } else {
      this.taskId = null;
      this.procInstId = this.pid;
    }
    this.loadComments();
  }

  onSubmitComment (): void {
    if (!this.commentForm.valid) {
      this.commentForm.markAsDirty();
      return;
    }

    this.loadingInProgress = true;
    this.commentService.postComment({
      subject: ' ',
      text: this.comment,
      taskId: this.taskId,
      procInstId: this.procInstId,
    }).subscribe(
      res => {
        this.commentForm.reset();
        this.loadComments();
        this.loadingInProgress = false;
      });
  }

  loadComments (): void {
    this.loadingInProgress = true;
    this.commentService.getCommentsforPid(this.procInstId).subscribe(
      (res: any) => {
        this.loadingInProgress = false;
        this.comments = [];
        this.tagHash = {};
        this.tags = [];
        res.comments.forEach((comment) => {
          this.processComment(comment, true);
        });
        if (res.filterByRefObject) {
          const tagName = '#' + res.refObjectIdName;
          if (!this.tagHash[tagName]) {
            this.tagHash[tagName] = 1;
            this.tags.push({ name: tagName, type: 'user', status: true });
          }
          this.filterEnabled = true;
          this.tagFilters.push(tagName);
        }
        if (this.comments.length > 0 && this.task) {
          const procName = this.task.process_name;
          if (this.tagHash[procName]) {
            this.filterEnabled = true;
            this.tagFilters.push(procName);
          }
        }
      },
      error => this.messageService.add({ severity: 'error', summary: 'Get Comments', detail: error.message }));
  }

  processComment (comment: AlandaComment, topLevel: boolean): void {
    comment.createDate = new Date(comment.createDate);
    comment.textDate = this.datePipe.transform(comment.createDate, 'dd.LL.yy HH:mm');
    if (!topLevel) {
      return;
    }
    let commentFulltext = `${comment.text.toLowerCase()} ${comment.authorName.toLowerCase()} ${comment.textDate}`;

    this.extractTags(comment);
    this.comments.push(comment);
    for (const tag of comment.tagList) {
      commentFulltext += ` ${tag.name}`;
      if (!this.tagHash[tag.name]) {
        this.tagHash[tag.name] = 1;
        this.tags.push({ name: tag.name, type: tag.type, status: true });
      }

      for (const reply of comment.replies) {
        this.processComment(reply, false);
        commentFulltext += `${reply.text.toLowerCase()} ${reply.authorName.toLowerCase()} ${reply.textDate}`;
      }
      comment.fulltext = commentFulltext;
    }
  }

  tagClass (tag: AlandaCommentTag): string {
    if (this.tagFilters.includes(tag.name)) {
      return 'ui-button-success';
    }
    return 'ui-button-info';
  }

  extractTags (comment: AlandaComment): void {
    comment.tagList = [];
    if (comment.taskName !== '') {
      comment.tagList.push({ name: comment.taskName, type: 'task' });
    }
    if (comment.subject.includes('#')) {
      comment.subject.match(/#\w+/g).forEach((value) => {
        comment.tagList.push({ name: value, type: 'user' });
      });
      comment.subject = comment.subject.replace(/#\w+/g, '');
    }
  }

  toggleFilter (name: string): void {
    const filterIndex = this.tagFilters.indexOf(name);
    if (filterIndex !== -1) {
      this.tagFilters.splice(filterIndex, 1);
      if (this.tagFilters.length === 0) {
        this.filterEnabled = false;
      }
    } else {
      this.tagFilters.push(name);
      this.filterEnabled = true;
    }
  }

  clearFilters (): void {
    this.tagFilters = [];
    this.filterEnabled = false;
  }

  get searchText (): string {
    return this.commentFilterForm.get('searchText').value;
  }

  get comment (): string {
    return this.commentForm.get('comment').value;
  }

  get filteredCommentsBySearchAndTags (): Array<AlandaComment> {
    let filteredComments: AlandaComment[] = this.comments;

    if (this.searchText.trim().length > 0) {
      filteredComments = this.comments.filter((comment: AlandaComment) => {
        return comment.fulltext.trim().toLowerCase().includes(this.searchText.trim().toLowerCase());
      });
    }

    if (this.filterEnabled) {
      return filteredComments.filter((comment) => {
        return comment.tagList.findIndex((tag) => {
          return this.tagFilters.includes(tag.name);
        }) > -1;
      });
    }
    return filteredComments;
  }
}
