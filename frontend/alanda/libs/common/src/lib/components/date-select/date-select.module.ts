import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AlandaDateSelectComponent } from './date-select.component';
import { CalendarModule } from 'primeng/calendar';
import { ReactiveFormsModule } from '@angular/forms';

@NgModule({
  declarations: [AlandaDateSelectComponent],
  imports: [CommonModule, CalendarModule, ReactiveFormsModule],
  exports: [AlandaDateSelectComponent],
})
export class DateSelectModule {}
