import { Injectable } from '@angular/core';
import { RxState } from '@rx-angular/state';

import { filter, map, share, startWith, switchMap, switchMapTo, tap } from 'rxjs/operators';
import { combineLatest, Observable, Subject } from 'rxjs';
import { AlandaComment, AlandaCommentTag } from '@alanda/common';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import {
  tagClass,
  toggleTagFilter,
  toLowerCase,
  trackByCommentId,
  trackByTagId,
} from '../utils';
import { AlandaReplyPostBody } from '../../../api/models/replyPostBody';

interface AlandaCommentPresenterState {
  comments: AlandaComment[];
  tagObjectMap: { [tagName: string]: AlandaCommentTag };
  activeTagFilters: { [tagName: string]: boolean };
  searchText: string;
  commentText: string;
  tag: AlandaCommentTag
}

/**
 * Provides a state of comments and handles filter and tag processing
 */
@Injectable()
export class AlandaCommentsPresenter extends RxState<
  AlandaCommentPresenterState
> {
  commentForm = this.fb.group({
    comment: ['', Validators.required],
  });

  commentFilterForm = this.fb.group({
    searchText: [''],
  });

  submitReply$ = new Subject<AlandaReplyPostBody>();

  commentFormSubmit$ = new Subject<Event>();
  formStateAfterSubmit$ = this.commentFormSubmit$.pipe(
    switchMapTo(this.commentForm.statusChanges)
  );

  commentText$ = this.formStateAfterSubmit$.pipe(
    filter((v) => v === 'VALID'),
    map(() => this.commentForm.get('comment').value)
  );

  searchText$ = this.commentFilterForm.get('searchText').valueChanges;

  tagObjectMap$ = this.select('comments').pipe(
    map((comments: AlandaComment[]) => {
      const tagMap = {};
      comments.forEach((comment: AlandaComment) => {
        comment.tagList.forEach((tag: AlandaCommentTag) => {
          tagMap[tag.name] = { name: tag.name, type: tag.type };
        });
      });
      return tagMap;
    }),
  );

  activeTagFilters$ = this.select('tagObjectMap').pipe(
    map((tags) => {
      const activeFilters = {};
      Object.keys(tags).forEach((key: string) => {
        activeFilters[key] = false;
      });
      return activeFilters;
    }),
  );

  filteredComments$ = combineLatest([
    this.select('comments'),
    this.select('searchText'),
    this.select('activeTagFilters'),
  ]).pipe(
    map(
      ([comments, searchText, activeTagFilters]: [
        AlandaComment[],
        string,
        { string: boolean },
      ]) => {
        const filteredComments = this.filterCommentsBySearchText(
          comments,
          searchText,
        );
        return this.filterCommentsByTags(filteredComments, activeTagFilters);
      },
    ),
  );

  trackByCommentId = trackByCommentId;

  trackByTagId = trackByTagId;

  constructor(private readonly fb: FormBuilder) {
    super();
    this.set({searchText: '' });
    this.connect('activeTagFilters', this.activeTagFilters$);
    this.connect('tagObjectMap', this.tagObjectMap$);
    this.connect('searchText', this.searchText$.pipe(map(toLowerCase)));
    this.connect('commentText', this.commentText$);
  }

  tags$ = this.select('tagObjectMap').pipe(
    map((obj: { [tagName: string]: AlandaCommentTag }) => Object.values(obj)),
  );

  hasActiveFilters$ = this.select('activeTagFilters').pipe(
    map((filters) => {
      return (
        Object.values(filters).findIndex((value: boolean) => {
          return value;
        }) > -1
      );
    }),
  );

  /**
   * Provides style class for tag if active or not
   */
  tagClass$ = this.select(
    filter(state => (state.activeTagFilters != null && state.tag != null)),
    map(state => tagClass(state.activeTagFilters, state.tag))
  );

  /**
   * Toggles given tag state in active filter map
   */
  toggleTagFilter = (tag: AlandaCommentTag) =>
    this.set('activeTagFilters', (oldState) =>
      toggleTagFilter(oldState.activeTagFilters, tag),
    );

  /**
   * Fulltext search for comment
   */
  filterCommentsBySearchText(
    comments: AlandaComment[],
    searchText: string,
  ): AlandaComment[] {
    if (searchText.length > 0) {
      return comments.filter((comment: AlandaComment) => {
        return toLowerCase(comment.fulltext).includes(searchText);
      });
    }
    return comments;
  }

  /**
   * Filter comments by active tag filters
   */
  filterCommentsByTags(
    comments: AlandaComment[],
    activeTagFilters: { string: boolean },
  ): AlandaComment[] {
    const filteredComments = comments.filter((comment: AlandaComment) => {
      return (
        comment.tagList.findIndex((tag: AlandaCommentTag) => {
          return activeTagFilters[tag.name];
        }) > -1
      );
    });
    return filteredComments.length > 0 ? filteredComments : comments;
  }

  /**
   * Removes all active filters
   */
  clearAllTagFilters(): void {
    this.set({ activeTagFilters: {} });
  }
}
