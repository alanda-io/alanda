import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AlandaSelectMilestoneComponent } from './milestone-select.component';
import { ReactiveFormsModule } from '@angular/forms';
import { CalendarModule } from 'primeng/calendar';

@NgModule({
  declarations: [AlandaSelectMilestoneComponent],
  imports: [CommonModule, ReactiveFormsModule, CalendarModule],
  exports: [AlandaSelectMilestoneComponent],
})
export class MilestoneSelectModule {}
