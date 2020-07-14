import { Component, Input, ViewChild, ElementRef, Inject, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { AlandaComment } from '../../../api/models/comment';
import { AlandaCommentTag } from '../../../api/models/commentTag';
import { AlandaCommentApiService } from '../../../api/commentApi.service';
import { RxState } from '@rx-angular/state';
import {
  AlandaCommentsService,
  AlandaCommentState,
} from '../../../services/comments.service';
import { APP_CONFIG, AppSettings } from '../../../models/appSettings';

/**
 * Display component for a comment and a comment reply
 */
@Component({
  selector: 'alanda-comment',
  templateUrl: './comment.component.html',
  styleUrls: ['./comment.component.scss'],
})
export class AlandaCommentComponent extends RxState<AlandaCommentState> implements OnInit {
  @Input() comment: AlandaComment;
  @Input() type: string;
  @ViewChild('replyContent') textArea: ElementRef;
  doReply = false;
  loadingInProgress: boolean;

  commentReplyForm = this.fb.group({
    replyText: ['', Validators.required],
  });

  avatarBasePath: string;
  avatarExtension: string;
  defaultAvatarPath = 'assets/default-avatar.png';
  avatarPath = this.defaultAvatarPath;

  constructor(
    private readonly commentApiService: AlandaCommentApiService,
    private readonly commentsService: AlandaCommentsService,
    private readonly fb: FormBuilder,
    @Inject(APP_CONFIG) config: AppSettings,
  ) {
    super();
    this.avatarBasePath = config.AVATAR_BASE_PATH;
    this.avatarExtension = config.AVATAR_EXT;
  }

  ngOnInit() {
    this.avatarPath = `${this.avatarBasePath}/${this.comment.createUser}.${this.avatarExtension}`
  }

  tagClass(tag: AlandaCommentTag): string {
    return this.commentsService.tagClass(tag);
  }

  toggleTagFilter(tag: AlandaCommentTag): void {
    this.commentsService.toggleTagFilter(tag);
  }

  /**
   * Autofocus the textarea if it is visible
   */
  autofocus(): void {
    const area = this.textArea;
    setTimeout(function () {
      area.nativeElement.focus();
    });
  }

  onSubmitReply(): void {
    if (!this.commentReplyForm.valid) {
      this.commentReplyForm.markAsDirty();
      return;
    }

    this.loadingInProgress = true;
    this.commentApiService
      .postComment({
        text: this.commentReplyForm.get('replyText').value,
        taskId: this.comment.taskId,
        procInstId: this.comment.procInstId,
        replyTo: this.comment.guid,
      })
      .subscribe((res) => {
        this.refreshComment();
        this.commentReplyForm.reset();
        this.loadingInProgress = false;
      });
  }

  refreshComment(): void {
    this.commentApiService
      .getCommentsforPid(this.comment.procInstId)
      .subscribe((res) => {
        this.comment = res.comments.filter(
          (comment) => comment.guid === this.comment.guid,
        )[0];
        this.comment = this.commentsService.processComment(this.comment);
      });
  }

  fallbackImage(event): void {
    event.target.src = this.defaultAvatarPath;
  }
}
