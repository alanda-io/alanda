import {
  Component,
  Input,
  ViewChild,
  ElementRef,
  Inject,
  OnInit,
  Output,
} from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { AlandaComment } from '../../../api/models/comment';
import { AlandaCommentTag } from '../../../api/models/commentTag';
import { RxState } from '@rx-angular/state';
import { APP_CONFIG, AppSettings } from '../../../models/appSettings';
import { tagClass } from '../utils';
import { Subject } from 'rxjs';
import { filter, map, switchMapTo } from 'rxjs/operators';

interface AlandaCommentState {
  comment: AlandaComment;
  type: string;
  activeTagFilters: { [name: string]: boolean };
  tag: AlandaCommentTag
}

/**
 * Display component for a comment and a comment reply
 */
@Component({
  selector: 'alanda-comment',
  templateUrl: './comment.component.html',
  styleUrls: ['./comment.component.scss'],
})
export class AlandaCommentComponent extends RxState<AlandaCommentState>
  implements OnInit {
  toggleFilterClick$ = new Subject<AlandaCommentTag>();
  commentReplyFormSubmit$ = new Subject<Event>();

  commentReplyForm = this.fb.group({
    replyText: ['', Validators.required],
  });

  formStateAfterSubmit$ = this.commentReplyFormSubmit$.pipe(
    switchMapTo(this.commentReplyForm.statusChanges)
  );

  state$ = this.select();

  @Input()
  set comment(comment: AlandaComment) {
    this.set({ comment });
  }

  @Input()
  set type(type: string) {
    this.set({ type });
  }

  @Input()
  set activeTagFilters(activeTagFilters: { [name: string]: boolean }) {
    this.set({ activeTagFilters });
  }

  @Output()
  toggleFilterClick = this.toggleFilterClick$;
  @Output()
  submitReply = this.formStateAfterSubmit$.pipe(
    filter((v) => v === 'VALID'),
    map(() => {
      const comment = this.comment;
      return {
        text: this.commentReplyForm.get('replyText').value,
        taskId: comment.taskId,
        procInstId: comment.procInstId,
        replyTo: comment.guid,
      };
    }),
  );

  @ViewChild('replyContent') textArea: ElementRef;
  doReply = false;

  avatarBasePath: string;
  avatarExtension: string;
  defaultAvatarPath = 'assets/default-avatar.png';
  avatarPath = this.defaultAvatarPath;
  tagClass$ = this.select(
    filter(state => (state.activeTagFilters != null && state.tag != null)),
    map(state => tagClass(state.activeTagFilters, state.tag))
  );

  constructor(
    private readonly fb: FormBuilder,
    @Inject(APP_CONFIG) config: AppSettings,
  ) {
    super();
    this.avatarBasePath = config.AVATAR_BASE_PATH;
    this.avatarExtension = config.AVATAR_EXT;
    this.hold(
      this.formStateAfterSubmit$.pipe(filter((v) => v !== 'VALID')),
      () => this.commentReplyForm.markAsDirty(),
    );
  }

  ngOnInit() {
    this.avatarPath = `${this.avatarBasePath}/${this.get().comment.createUser}.${this.avatarExtension}`;
  }

  autofocus(): void {
    const area = this.textArea;
    setTimeout(() => {
      area.nativeElement.focus();
    });
  }

  fallbackImage(event): void {
    event.target.src = this.defaultAvatarPath;
  }
}
