import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AlandaCommentDialogComponent } from './comment-dialog.component';
import { ReactiveFormsModule } from '@angular/forms';
import { CalendarModule } from 'primeng/calendar';
import { InputTextModule } from 'primeng/inputtext';
import { DialogModule } from 'primeng/dialog';
import { LabelModule } from '../label/label.module';
import { InputTextareaModule } from 'primeng/inputtextarea';

@NgModule({
  declarations: [AlandaCommentDialogComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    CalendarModule,
    LabelModule,
    InputTextModule,
    DialogModule,
    InputTextareaModule,
  ],
  exports: [AlandaCommentDialogComponent],
})
export class CommentDialogModule {}