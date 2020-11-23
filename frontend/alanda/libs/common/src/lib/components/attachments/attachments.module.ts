import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AlandaAttachmentsComponent } from './attachments.component';
import { AttachmentsTreeComponent } from './attachments-tree/attachments-tree.component';
import { AttachmentsListComponent } from './attachments-list/attachments-list.component';
import { TableModule } from 'primeng/table';
import { BadgeModule } from '../badge/badge.module';
import { PanelModule } from 'primeng/panel';
import { ButtonModule } from 'primeng/button';
import { FileUploadModule } from 'primeng/fileupload';
import { TreeModule } from 'primeng/tree';
import { LightboxModule } from 'primeng/lightbox';
import { RippleModule } from 'primeng/ripple';
import { DialogModule } from 'primeng/dialog';
import { InplaceModule } from 'primeng/inplace';
import { InputTextModule } from 'primeng/inputtext';
import { TooltipModule } from 'primeng/tooltip';

@NgModule({
  declarations: [
    AlandaAttachmentsComponent,
    AttachmentsTreeComponent,
    AttachmentsListComponent,
  ],
  imports: [
    CommonModule,
    TableModule,
    BadgeModule,
    PanelModule,
    ButtonModule,
    FileUploadModule,
    TreeModule,
    LightboxModule,
    RippleModule,
    DialogModule,
    InplaceModule,
    InputTextModule,
    TooltipModule,
  ],
  exports: [
    AlandaAttachmentsComponent,
    AttachmentsTreeComponent,
    AttachmentsListComponent,
  ],
})
export class AttachmentsModule {}
