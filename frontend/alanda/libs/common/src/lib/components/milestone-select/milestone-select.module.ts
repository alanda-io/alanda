import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AlandaSelectMilestoneComponent } from './milestone-select.component';
import { ReactiveFormsModule } from '@angular/forms';
import { CalendarModule } from 'primeng/calendar';
import { LabelModule } from '../label/label.module';

@NgModule({
  declarations: [AlandaSelectMilestoneComponent],
  imports: [CommonModule, ReactiveFormsModule, CalendarModule, LabelModule],
  exports: [AlandaSelectMilestoneComponent],
})
export class MilestoneSelectModule {}
