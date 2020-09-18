import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AlandaDateSelectComponent } from './date-select.component';
import { CalendarModule } from 'primeng/calendar';

@NgModule({
  declarations: [AlandaDateSelectComponent],
  imports: [CommonModule, CalendarModule],
  exports: [AlandaDateSelectComponent],
})
export class DateSelectModule {}
