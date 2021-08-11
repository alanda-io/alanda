import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AlandaProjectAndProcessesComponent } from './project-and-processes.component';
import { PapActionsComponent } from './pap-actions/pap-actions.component';
import { PapConfigDialogComponent } from './pap-config-dialog/pap-config-dialog.component';
import { PapReasonDialogComponent } from './pap-reason-dialog/pap-reason-dialog.component';
import { PapRelateDialogComponent } from './pap-relate-dialog/pap-relate-dialog.component';
import { PanelModule } from 'primeng/panel';
import { TreeTableModule } from 'primeng/treetable';
import { DropdownModule } from 'primeng/dropdown';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { FormsModule } from '@angular/forms';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { RouterModule } from '@angular/router';
import { PapActionSymbolComponent } from './pap-actions/pap-action-symbol/pap-action-symbol.component';
import { PapSubprocessPropertyInputComponent } from './pap-config-dialog/pap-subprocess-property-input/pap-subprocess-property-input.component';
import { MenuModule } from 'primeng/menu';
import { TooltipModule } from 'primeng/tooltip';
import { BadgeModule } from '../badge/badge.module';
import { DirectivesModule } from '../../directives/directives.module';
import { ButtonModule } from 'primeng/button';
import { TableModule } from 'primeng/table';
import { InputTextModule } from 'primeng/inputtext';
import { RippleModule } from 'primeng/ripple';
import { TruncatePipeModule } from '../../pipes/truncate.pipe';

@NgModule({
  declarations: [
    AlandaProjectAndProcessesComponent,
    PapActionsComponent,
    PapConfigDialogComponent,
    PapReasonDialogComponent,
    PapRelateDialogComponent,
    PapActionSymbolComponent,
    PapSubprocessPropertyInputComponent,
  ],
  imports: [
    CommonModule,
    PanelModule,
    TreeTableModule,
    DropdownModule,
    AutoCompleteModule,
    FormsModule,
    InputTextareaModule,
    RouterModule,
    MenuModule,
    TooltipModule,
    BadgeModule,
    DirectivesModule,
    ButtonModule,
    TableModule,
    InputTextModule,
    RippleModule,
    TruncatePipeModule,
  ],
  exports: [
    AlandaProjectAndProcessesComponent,
    PapActionsComponent,
    PapConfigDialogComponent,
    PapReasonDialogComponent,
    PapRelateDialogComponent,
    PapActionSymbolComponent,
    PapSubprocessPropertyInputComponent,
  ],
})
export class ProjectAndProcessesModule {}
