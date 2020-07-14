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
import { Observable, Subject } from 'rxjs';
import { map, withLatestFrom } from 'rxjs/operators';
import { AlandaReplyPostBody } from '../../../api/models/replyPostBody';

interface AlandaCommentState {
  comment: AlandaComment;
  type: string;
  activeTagFilters: { [name: string]: boolean };
  tag: AlandaCommentTag;
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

  submitReply$: Observable<
    AlandaReplyPostBody
  > = this.commentReplyFormSubmit$.pipe(
    withLatestFrom(
      this.select('comment'),
      this.commentReplyForm.get('replyText').valueChanges,
    ),
    map(([_, comment, value]) => {
      this.commentReplyForm.reset();
      return {
        text: value,
        taskId: comment.taskId,
        procInstId: comment.procInstId,
        replyTo: comment.guid,
      };
    }),
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
  set activeTagFilters(
    activeTagFilters: Observable<{ [name: string]: boolean }>,
  ) {
    this.connect('activeTagFilters', activeTagFilters);
  }

  @Output()
  toggleFilterClick = this.toggleFilterClick$;
  @Output()
  submitReply = this.submitReply$;

  @ViewChild('replyContent') textArea: ElementRef;
  doReply = false;

  avatarBasePath: string;
  avatarExtension: string;
  defaultAvatarPath = 'assets/default-avatar.png';
  avatarPath = this.defaultAvatarPath;

  constructor(
    private readonly fb: FormBuilder,
    @Inject(APP_CONFIG) config: AppSettings,
  ) {
    super();
    this.avatarBasePath = config.AVATAR_BASE_PATH;
    this.avatarExtension = config.AVATAR_EXT;
  }

  ngOnInit() {
    this.avatarPath = `${this.avatarBasePath}/${
      this.get().comment.createUser
    }.${this.avatarExtension}`;
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
