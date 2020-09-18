import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AlandaSimpleSelectComponent } from './simple-select.component';
import { CardModule } from 'primeng/card';
import { DropdownModule } from 'primeng/dropdown';
import { MessageModule } from 'primeng/message';
import { ReactiveFormsModule } from '@angular/forms';

@NgModule({
  declarations: [AlandaSimpleSelectComponent],
  imports: [
    CommonModule,
    CardModule,
    DropdownModule,
    MessageModule,
    ReactiveFormsModule,
  ],
  exports: [AlandaSimpleSelectComponent],
})
export class SimpleSelectModule {}
