import { Component, Input, ViewChild, ElementRef } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { AlandaComment } from '../../../api/models/comment';
import { AlandaCommentTag } from '../../../api/models/commentTag';
import { AlandaCommentApiService } from '../../../api/commentApi.service';
import { RxState } from '@rx-angular/state';
import { AlandaCommentsService, AlandaCommentState } from '../../../services/comments.service';

@Component({
  selector: 'alanda-comment',
  templateUrl: './comment.component.html',
  styleUrls: ['./comment.component.scss']
})
export class AlandaCommentComponent extends RxState<AlandaCommentState> {
  @Input() comment: AlandaComment;
  @Input() type: string;
  @ViewChild('replyContent') textArea: ElementRef;
  doReply = false;
  loadingInProgress: boolean;

  commentReplyForm = this.fb.group({
    replyText: ['', Validators.required]
  });

  constructor(
    private readonly commentApiService: AlandaCommentApiService,
    private readonly commentsService: AlandaCommentsService,
    private readonly fb: FormBuilder) {
    super();
  }

  tagClass(tag: AlandaCommentTag): string {
    return this.commentsService.tagClass(tag);
  }

  autofocus(): void {
    const area = this.textArea;
    setTimeout(function() { area.nativeElement.focus() });
  }

  onSubmitReply(): void {
    if (!this.commentReplyForm.valid) {
      this.commentReplyForm.markAsDirty();
      return;
    }

    this.loadingInProgress = true;
    this.commentApiService.postComment({
      text: this.commentReplyForm.get('replyText').value,
      taskId: this.comment.taskId,
      procInstId: this.comment.procInstId,
      replyTo: this.comment.guid
    }).subscribe(
      res => {
        this.refreshComment();
        this.commentReplyForm.reset();
        this.loadingInProgress = false;
      }
    );
  }

  refreshComment(): void {
    this.commentApiService.getCommentsforPid(this.comment.procInstId).subscribe(res => {
      this.comment = res.comments.filter(comment => comment.guid === this.comment.guid)[0];
      this.comment = this.commentsService.processComment(this.comment);
    });
  }
}
