import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AlandaDateSelectComponent } from './date-select.component';
import { CalendarModule } from 'primeng/calendar';
import { ReactiveFormsModule } from '@angular/forms';
import { LabelModule } from '../label/label.module';

@NgModule({
  declarations: [AlandaDateSelectComponent],
  imports: [CommonModule, CalendarModule, ReactiveFormsModule, LabelModule],
  exports: [AlandaDateSelectComponent],
})
export class DateSelectModule {}
