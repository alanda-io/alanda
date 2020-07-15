import { Injectable } from '@angular/core';
import { RxState, toDictionary, toggle, patch } from '@rx-angular/state';
import { filter, map } from 'rxjs/operators';
import { combineLatest, Observable, Subject } from 'rxjs';
import { FormBuilder, Validators } from '@angular/forms';
import {
  Dictionary,
  toLowerCase,
  trackByCommentId,
  trackByTagId,
} from '../utils';
import { AlandaReplyPostBody } from '../../../api/models/replyPostBody';
import { AlandaComment } from '../../../api/models/comment';
import { AlandaCommentTag } from '../../../api/models/commentTag';

interface AlandaCommentPresenterState {
  comments: AlandaComment[];
  tagObjectMap: { [tagName: string]: AlandaCommentTag };
  activeTagFilters: { [tagName: string]: boolean };
  searchText: string;
  tag: AlandaCommentTag;
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

  replyPostBody$ = new Subject<AlandaReplyPostBody>();

  commentFormSubmit$ = new Subject<Event>();

  commentText$: Observable<string> = this.commentFormSubmit$.pipe(
    filter(() => {
      return this.commentForm.valid;
    }),
    map(() => {
      const value = this.commentForm.get('comment').value;
      this.commentForm.reset();
      return value;
    }),
  );

  searchText$: Observable<string> = this.commentFilterForm.get('searchText')
    .valueChanges;

  tagObjectMap$: Observable<Dictionary<AlandaCommentTag>> = this.select(
    'comments',
  ).pipe(
    map((comments: AlandaComment[]) => {
      return comments.reduce((tagMap, comment: AlandaComment) => {
        return patch(tagMap, toDictionary(comment.tagList, 'name'));
      }, {} as Dictionary<AlandaCommentTag>);
    }),
  );

  activeTagFiltersFromTagObject$ = this.select('tagObjectMap').pipe(
    map((tags) => {
      const activeFilters = {};
      Object.keys(tags).forEach((key: string) => {
        activeFilters[key] = false;
      });
      return activeFilters;
    }),
  );

  activeTagFilters$ = this.select('activeTagFilters');

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
    this.set({ searchText: '' });
    this.connect('activeTagFilters', this.activeTagFiltersFromTagObject$);
    this.connect('tagObjectMap', this.tagObjectMap$);
    this.connect('searchText', this.searchText$.pipe(map(toLowerCase)));
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
   * Toggles given tag state in active filter map
   */
  toggleTagFilter = (tag: AlandaCommentTag) =>
    this.set('activeTagFilters', (oldState) =>
      toggle(oldState.activeTagFilters, tag.name),
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
