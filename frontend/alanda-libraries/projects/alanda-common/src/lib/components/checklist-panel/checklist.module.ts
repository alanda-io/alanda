import { NgModule } from '@angular/core';
import { ToastModule } from 'primeng/toast';
import { CommonModule } from '@angular/common';
import { AlandaChecklistPanelComponent } from './checklist-panel.component';
import { PanelModule } from 'primeng/panel';
import { CardModule } from 'primeng/card';
import { AlandaChecklistComponent } from './checklist/checklist.component';
import { CheckboxModule } from 'primeng/checkbox';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ProgressBarModule } from 'primeng/progressbar';

@NgModule({
  imports: [ToastModule, CommonModule, PanelModule, CardModule, CheckboxModule, FormsModule, ProgressBarModule, ReactiveFormsModule],
  declarations: [AlandaChecklistPanelComponent, AlandaChecklistComponent],
  exports: [AlandaChecklistPanelComponent, AlandaChecklistComponent],
})
export class ChecklistModule {}
