import { Component, Inject, Input, Output } from '@angular/core';

import { AbstractControl, FormBuilder, Validators } from '@angular/forms';
import { catchError, switchMap, tap } from 'rxjs/operators';
import { combineLatest, EMPTY, of, Subject } from 'rxjs';
import { RxState } from '@rx-angular/state';
import { MessageService } from 'primeng/api';
import { EventEmitter } from '@angular/core';
import { AlandaTask } from '../../api/models/task';
import { AlandaCommentApiService } from '../../api/commentApi.service';
import { AppSettings, APP_CONFIG } from '../../models/appSettings';

interface CommentDialogProperties {
  dialogHeader: string;
  commentLabel: string;
  commentButtonLabel: string;
  commentTextAreaPlaceholder: string;
  commentSubject: string;
  commentButtonClass: string;
  commentButtonIcon: string;
}

interface CommentState {
  processInstanceId: string;
  task: AlandaTask;
  showCommentDialog: boolean;
  properties: CommentDialogProperties;
  comment: string;
}

const initState = {
  showCommentDialog: false,
  properties: {
    dialogHeader: 'Please enter a comment!',
    commentLabel: 'Comment',
    commentButtonLabel: 'Submit',
    commentTextAreaPlaceholder: 'Write your thoughts here',
    commentSubject: '',
    commentButtonClass: 'p-button-text',
    commentButtonIcon: 'pi pi-check',
  },
};
@Component({
  selector: 'alanda-comment-dialog',
  templateUrl: './comment-dialog.component.html',
  styleUrls: ['./comment-dialog.component.css'],
})
export class AlandaCommentDialogComponent {
  state$ = this.state.select();
  saveComment$ = new Subject<string>();
  onVisibleChange$ = new Subject<boolean>();
  commentForm = this.fb.group({
    comment: [null, Validators.required],
  });

  @Input() set dialogProperties(properties: Partial<CommentDialogProperties>) {
    this.state.set({
      properties: { ...this.state.get('properties'), ...properties },
    });
  }
  @Input() set task(task: AlandaTask) {
    this.state.set({ task });
  }

  @Input() set isVisible(showCommentDialog: boolean) {
    this.state.set({ showCommentDialog });
  }
  get isVisible() {
    return this.state.get('showCommentDialog');
  }
  @Output() isVisibleChange = new EventEmitter<boolean>();

  @Output() commentSaved = new EventEmitter<boolean>();

  visibleChange$ = this.state.select('showCommentDialog').pipe(
    tap((val) => {
      this.isVisibleChange.emit(val);
    }),
  );

  sendCommentSaved$ = this.saveComment$.pipe(
    tap(() => this.commentSaved.emit(true)),
  );

  handleSaveComment$ = combineLatest([
    this.saveComment$,
    this.state.select('task'),
  ]).pipe(
    switchMap(([comment, task]) =>
      this.commentService.postComment({
        subject: this.state.get('properties').commentSubject,
        text: comment,
        taskId: task.task_id,
        procInstId: task.process_instance_id,
      }),
    ),
    catchError((err) => {
      console.log(err);
      return EMPTY;
    }),
    switchMap(() => {
      return of(false);
    }),
  );

  constructor(
    private state: RxState<CommentState>,
    private commentService: AlandaCommentApiService,
    private readonly fb: FormBuilder,
    private messageService: MessageService,
    @Inject(APP_CONFIG) config: AppSettings,
  ) {
    this.state.set(initState);
    this.state.connect('showCommentDialog', this.handleSaveComment$);
    this.state.hold(this.sendCommentSaved$);
    this.state.hold(this.visibleChange$);
  }

  get comment(): AbstractControl {
    return this.commentForm.get('comment');
  }
}
