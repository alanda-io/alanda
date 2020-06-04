import { Component, Input, ViewChild, ElementRef } from '@angular/core';
import { NgForm } from '@angular/forms';
import { AlandaComment } from '../../../api/models/comment';
import { AlandaCommentTag } from '../../../api/models/commentTag';
import { AlandaCommentApiService } from '../../../api/commentApi.service';

@Component({
  selector: 'alanda-comment',
  templateUrl: './comment.component.html',
  styleUrls: ['./comment.component.scss']
})
export class AlandaCommentComponent {
  @Input() comment: AlandaComment;
  @Input() type: string;
  @Input() tagFilters: Array<string>;
  @ViewChild('replyContent') textArea: ElementRef;
  doReply: boolean;
  loadingInProgress: boolean;

  constructor (private readonly pmcCommentService: AlandaCommentApiService) {}

  tagClass (tag: AlandaCommentTag): string {
    if (this.tagFilters.includes(tag.name)) {
      return 'ui-button-success';
    }
    return 'ui-button-info';
  }

  autofocus (): void {
    const area = this.textArea;
    setTimeout(function () { area.nativeElement.focus() });
  }

  onSubmitReply (form: NgForm): void {
    this.loadingInProgress = true;
    this.pmcCommentService.postComment({
      text: this.comment.replyText,
      taskId: this.comment.taskId,
      procInstId: this.comment.procInstId,
      replyTo: this.comment.guid
    }).subscribe(
      res => {
        this.refresh();
        form.reset();
        this.loadingInProgress = false;
      }
    );
  }

  refresh (): void {
    this.pmcCommentService.getCommentsforPid(this.comment.procInstId).subscribe(res => {
      this.comment = res.comments.filter(comment => comment.guid === this.comment.guid)[0];
    });
  }
}
