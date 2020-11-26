import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AlandaSelectMilestoneComponent } from './milestone-select.component';
import { ReactiveFormsModule } from '@angular/forms';
import { CalendarModule } from 'primeng/calendar';
import { LabelModule } from '../label/label.module';
import { InputTextModule } from 'primeng/inputtext';
import { DialogModule } from 'primeng/dialog';
import { InputTextareaModule } from 'primeng/inputtextarea';

@NgModule({
  declarations: [AlandaSelectMilestoneComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    CalendarModule,
    LabelModule,
    InputTextModule,
    DialogModule,
    InputTextareaModule,
  ],
  exports: [AlandaSelectMilestoneComponent],
})
export class MilestoneSelectModule {}
