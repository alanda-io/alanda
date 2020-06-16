import { Component, OnInit, Input } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AlandaCommentTag } from '../../api/models/commentTag';
import { RxState } from '@rx-angular/state';
import { AlandaCommentState, AlandaCommentsService } from '../../services/comments.service';
import { Subject } from 'rxjs';
import { AlandaTask } from '../../api/models/task';
import { map } from 'rxjs/operators';

@Component({
  selector: 'alanda-comments',
  templateUrl: './comments.component.html',
  styleUrls: ['./comments.component.scss'],
  providers: [AlandaCommentsService]
})
export class AlandaCommentsComponent extends RxState<AlandaCommentState> implements OnInit {
  @Input() task: AlandaTask;
  @Input() pid: string;
  procInstId: string;
  taskId: string;

  commentForm = this.fb.group({
    comment: ['', Validators.required]
  });

  commentFilterForm = this.fb.group({
    searchText: ['']
  });

  comments$ = this.select('comments');

  tags$ = this.commentsService.select('tagObjectMap').pipe(
    map((obj: {[tagName: string]: AlandaCommentTag}) => Object.values(obj))
  );

  hasActiveFilters$ = this.commentsService.select('activeTagFilters').pipe(
    map(filters => {
      return Object.values(filters).forEach(value => {
        return value;
      });
    })
  );

  commentFormSubmit$ = new Subject();
  commentText$ = this.commentFormSubmit$.pipe(
    map((commentForm: FormGroup) => {
      if (commentForm.invalid) {
        commentForm.markAsDirty();
        return;
      }
      const commentText = commentForm.value.comment.trim();
      commentForm.reset();
      return commentText;
    })
  );

  constructor(
    private readonly fb: FormBuilder,
    private readonly commentsService: AlandaCommentsService) {
    super();
    this.connect('comments', this.commentsService.filteredComments$);
    this.commentsService.connect('commentText', this.commentText$);
    this.commentsService.connect('searchText', this.commentFilterForm.get('searchText').valueChanges.pipe(
      map(text => text.trim().toLowerCase())
    ));
  }

  ngOnInit(): void {
    if (this.task !== undefined) {
      this.procInstId = this.task.process_instance_id;
      this.taskId = this.task.task_id;
    } else {
      this.taskId = null;
      this.procInstId = this.pid;
    }
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
