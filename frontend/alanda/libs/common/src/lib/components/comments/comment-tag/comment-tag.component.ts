import { Component, Input, OnInit, Output } from '@angular/core';
import { AlandaCommentTag } from '../../../api/models/commentTag';
import { distinctUntilSomeChanged, RxState } from '@rx-angular/state';
import { map } from 'rxjs/operators';
import { Subject } from 'rxjs';

interface CommentTagComponentState {
  tag: AlandaCommentTag;
  active: boolean;
}

@Component({
  selector: 'alanda-comment-tag',
  templateUrl: './comment-tag.component.html',
  styleUrls: ['./comment-tag.component.scss'],
})
export class AlandaCommentTagComponent extends RxState<
  CommentTagComponentState
> {
  toggleFilterClick$ = new Subject<AlandaCommentTag>();

  tag$ = this.select(
    distinctUntilSomeChanged(['active', 'tag']),
    map((oldState) => ({
      tag: oldState.tag,
      class: oldState.active ? 'ui-button-success' : 'ui-button-info',
    })),
  );

  @Input()
  set tag(tag: AlandaCommentTag) {
    this.set({ tag });
  }

  @Input()
  set isActive(active: boolean) {
    this.set({ active });
  }
  @Output()
  toggleFilterClick = this.toggleFilterClick$;
}
