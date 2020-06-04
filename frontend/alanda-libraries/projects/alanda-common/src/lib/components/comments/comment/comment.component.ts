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
  @Input() tagFilters;
  @ViewChild('replyContent') textArea: ElementRef;
  filterEnabled: boolean;
  doReply: boolean;
  loadingInProgress: boolean;

  constructor(private readonly pmcCommentService: AlandaCommentApiService) {}

  tagClass(tag: AlandaCommentTag): string {
    /* if(!this.filterEnabled || this.tagFilters.indexOf(tag.name) !== -1){
      //TODO: remove? improve
      if(tag.name === '#escalation'){
        return 'ui-button-danger';
      }
      if(tag.name.startsWith('#')){
        return 'ui-button-warning';
      }
      return 'ui-button-success';
    }
    return 'ui-button-info'; */
    if (this.tagFilters.includes((tag.name))) {
      return 'ui-button-success';
    }
    return 'ui-button-info';
  }

  autogrow() {
    const textArea = document.getElementById('replyTextarea');
    if (this.comment.replyText && this.comment.replyText.length === 0) {
      textArea.style.height = textArea.style.minHeight;
    } else {
      textArea.style.height = textArea.scrollHeight + 'px';
    }
  }

  autofocus() {
    const area = this.textArea;
    setTimeout(function() { area.nativeElement.focus() });
  }

  onSubmitReply(form: NgForm) {
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

  /* toggleFilter(name: string) {
    let filterIndex = this.tagFilters.indexOf(name);
    if(filterIndex !== -1){
      this.tagFilters.splice(filterIndex,1);
      if(this.tagFilters.length === 0){
        this.filterEnabled = false;
      }
    }
    else {
      this.tagFilters.push(name);
      this.filterEnabled = true;
     }
  } */

  refresh() {
    this.pmcCommentService.getCommentsforPid(this.comment.procInstId).subscribe(res => {
      this.comment = res.comments.filter(comment => comment.guid === this.comment.guid)[0];
    });
  }
}
