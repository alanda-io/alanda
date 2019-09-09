import { Component, OnInit, ViewChild, Input } from '@angular/core';
import { Panel } from 'primeng/panel';
import { FormGroup, NgForm } from '@angular/forms';
import { PmcCommentServiceNg } from '../../services/rest/pmccomment.service';
import { PmcComment } from '../../models/PmcComment';
import { DatePipe } from '@angular/common';
import { CommentTag } from '../../models/commentTag.model';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'comments-component',
  templateUrl: './comments.component.html',
  styleUrls: [],
})
export class CommentsComponent implements OnInit {

  @Input() task: any;
  @Input() pid: string;
  @ViewChild('commentPanel') commentPanel: Panel;
  loadingInProgress: boolean = false;
  filterEnabled: boolean = false;
  comments: PmcComment[] = [];
  tags: CommentTag[] = [];
  commentCount: number;
  procInstId: string;
  taskId: string;
  tagHash = {};
  tagFilters: string[] = [];
  searchText: string;
  subject = ' ';
  content = '';

  constructor(private pmcCommentService: PmcCommentServiceNg, private messageService: MessageService,
              private datePipe: DatePipe) {}

  ngOnInit() {
    if(this.task){
      this.procInstId = this.task.process_instance_id;
      this.taskId = this.task.task_id;
    } else {
      this.taskId = null;
      this.procInstId = this.pid;
    }
    this.loadComments();
  }

  autogrow(){
    let  textArea = document.getElementById("textarea")       
    if(this.content.length == 0) {
      textArea.style.height = textArea.style.minHeight;
    } else {
      textArea.style.height = textArea.scrollHeight + 'px';
    }
  }

  onSubmitComment(form: NgForm) {
    this.loadingInProgress = true;
    this.pmcCommentService.postComment({
        subject: this.subject,
        text: this.content,
        taskId: this.taskId,
        procInstId: this.procInstId,
      }).subscribe(
        res => {
          form.reset();
          this.loadComments();
          this.loadingInProgress = false;
        });
  }

  loadComments() {
    this.loadingInProgress = true;
    this.pmcCommentService.getCommentsforPid(this.procInstId).subscribe(
      (res: any) => {
        this.loadingInProgress = false;
        this.comments = [];
        this.tagHash = {};
        this.tags = [];
        res.comments.forEach((comment) => {
          this.processComment(comment, true);
        });
        if(res.filterByRefObject){
          let tagName = '#' + res.refObjectIdName;
          if(!this.tagHash[tagName]){
            this.tagHash[tagName] = 1;
            this.tags.push({name: tagName, type: 'user', status: true});
          }
          this.filterEnabled = true;
          this.tagFilters.push(tagName);
        }
        if(this.comments.length > 0 && this.task){
          let procName = this.task.process_name;
          if(this.tagHash[procName]){
            this.filterEnabled = true;
            this.tagFilters.push(procName);
          }
        }
      },
      error => this.messageService.add({severity:'error', summary:'Get Comments', detail: error.message}));
  }

  processComment(comment: PmcComment, topLevel: boolean): void {
    comment.createDate = new Date(comment.createDate);
    comment.textDate = this.datePipe.transform(comment.createDate, 'dd.LL.yy HH:mm');
    if(!topLevel) {
      return;
    }
    let commentFulltext = `${comment.text.toLowerCase()} ${comment.authorName.toLowerCase()} ${comment.textDate}`;

    this.extractTags(comment);
    this.comments.push(comment);
    for(let tag of comment.tagList){
      commentFulltext += ` ${tag.name}`
      if(!this.tagHash[tag.name]){
        this.tagHash[tag.name] = 1;
        this.tags.push({name: tag.name, type: tag.type, status: true});
      }
      
      for(let reply of comment.replies){
        this.processComment(reply, false);
        commentFulltext += `${reply.text.toLowerCase()} ${reply.authorName.toLowerCase()} ${reply.textDate}`;
      }
      comment.fulltext = commentFulltext;
    }
  }

  togglePanel() {
    if(this.commentPanel.collapsed){
      this.loadComments();
    }
  }

  tagClass(tag: CommentTag): string{
    if(this.tagFilters.indexOf(tag.name) != -1) {
      return 'ui-button-success';
    }
    return 'ui-button-info';
  }

  extractTags(comment: PmcComment) {
    comment.tagList = [];
    if(comment.processName){
      comment.tagList.push(new CommentTag(comment.processName, 'process'))
    }
    if(comment.taskName){
      comment.tagList.push(new CommentTag(comment.taskName, 'task'))
    }
    /* if(comment.siteIdName){
      comment.tagList.push(new CommentTag(`Site ${comment.siteIdName}`, 'bo'))
    }
    if(comment.saIdName){
      comment.tagList.push(new CommentTag(`SearchArea ${comment.saIdName}`, 'bo'))
    } */
    if(comment.subject){
      if(comment.subject.includes('#')){
        comment.subject.match(/#\w+/g).forEach((value) => {
          comment.tagList.push(new CommentTag(value, 'user'));
        })
        comment.subject = comment.subject.replace(/#\w+/g, '');
      }
    }
  }

  toggleFilter(name: string) {
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
  }
  
  clearFilters() {
    this.tagFilters = [];
    this.filterEnabled = false;
  }
}