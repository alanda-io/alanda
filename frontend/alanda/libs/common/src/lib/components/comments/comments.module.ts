import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { PanelModule } from 'primeng/panel';
import { ScrollPanelModule } from 'primeng/scrollpanel';
import { ButtonModule } from 'primeng/button';
import { TemplateModule } from '@rx-angular/template';
import { BadgeModule } from '../badge/badge.module';
import { AlandaCommentsComponent } from './comments/comments.component';
import { AlandaCommentComponent } from './comment/comment.component';

const DECLARATIONS = [AlandaCommentsComponent, AlandaCommentComponent];
@NgModule({
  declarations: DECLARATIONS,
  imports: [
    CommonModule,
    TemplateModule,
    ButtonModule,
    ReactiveFormsModule,
    BadgeModule,
    PanelModule,
    ScrollPanelModule,
  ],
  exports: DECLARATIONS,
})
export class CommentsModule {}
