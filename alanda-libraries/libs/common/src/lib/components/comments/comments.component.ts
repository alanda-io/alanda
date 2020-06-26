import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AlandaCommentTag } from '../../api/models/commentTag';
import { RxState } from '@rx-angular/state';
import { AlandaCommentState, AlandaCommentsService } from '../../services/comments.service';
import { Subject } from 'rxjs';
import { map } from 'rxjs/operators';

/**
 * Wrapper component for comments that provides access to filters and creating new comments.
 */
@Component({
  selector: 'alanda-comments',
  templateUrl: './comments.component.html',
  styleUrls: ['./comments.component.scss'],
  providers: [AlandaCommentsService]
})
export class AlandaCommentsComponent extends RxState<AlandaCommentState> {
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
      return Object.values(filters).findIndex((value: boolean) => {
        return value;
      }) > -1;
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