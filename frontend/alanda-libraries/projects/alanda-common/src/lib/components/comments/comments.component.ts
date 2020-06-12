import { Component, OnInit, Input } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { MessageService } from 'primeng/api';
import { AlandaCommentTag } from '../../api/models/commentTag';
import { AlandaCommentApiService } from '../../api/commentApi.service';
import { AlandaTaskFormService } from '../../form/alanda-task-form.service';
import {
  map
} from 'rxjs/operators';
import { RxState } from '@rx-angular/state';
import { AlandaCommentState, AlandaCommentsService, TagObject } from '../../services/comments.service';

@Component({
  selector: 'alanda-comments',
  templateUrl: './comments.component.html',
  styleUrls: ['./comments.component.scss'],
  providers: [AlandaCommentsService]
})
export class AlandaCommentsComponent extends RxState<AlandaCommentState> implements OnInit {
  @Input() task: any;
  @Input() pid: string;
  loadingInProgress = false;
  procInstId: string;
  taskId: string;

  commentForm = this.fb.group({
    comment: ['', Validators.required]
  });

  commentFilterForm = this.fb.group({
    searchText: ['']
  });

  comments$ = this.select('comments');
  tags$ = this.commentsService.select('tagObjectMap').pipe(map((obj: {[tagName: string]: TagObject}) => Object.values(obj)));

  hasActiveFilters$ = this.commentsService.select('activeTagFilters').pipe(
    map(filters => {
      return Object.values(filters).forEach(value => {
        return value;
      });
    })
  );

  constructor(private readonly commentApiService: AlandaCommentApiService,
    private readonly messageService: MessageService,
    private readonly datePipe: DatePipe,
    private readonly fb: FormBuilder,
    private readonly taskFormService: AlandaTaskFormService,
    private readonly commentsService: AlandaCommentsService) {
    super();
    this.connect('comments', this.commentsService.filteredComments$);
    this.commentsService.connect('searchText', this.commentFilterForm.get('searchText').valueChanges.pipe(
      map(text => text.trim().toLowerCase())
    ));
  }

  ngOnInit(): void {
    if (this.task) {
      this.procInstId = this.task.process_instance_id;
      this.taskId = this.task.task_id;
    } else {
      this.taskId = null;
      this.procInstId = this.pid;
    }
  }

  onSubmitComment(): void {
    if (!this.commentForm.valid) {
      this.commentForm.markAsDirty();
      return;
    }

    this.loadingInProgress = true;
    this.commentApiService.postComment({
      subject: ' ',
      text: this.commentForm.get('comment').value,
      taskId: this.taskId,
      procInstId: this.procInstId,
    }).subscribe(
      res => {
        this.commentForm.reset();
        this.loadingInProgress = false;
      });
  }

  tagClass(tag: AlandaCommentTag): string {
    return this.commentsService.tagClass(tag);
  }

  toggleTagFilter(tag: AlandaCommentTag): void {
    this.commentsService.toggleTagFilter(tag);
  }

  clearAllTagFilters(): void {
    this.commentsService.clearAllTagFilters();
  }
}
