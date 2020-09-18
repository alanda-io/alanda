import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AlandaDropdownSelectComponent } from './dropdown-select.component';
import { DropdownModule } from 'primeng/dropdown';
import { ReactiveFormsModule } from '@angular/forms';

@NgModule({
  declarations: [AlandaDropdownSelectComponent],
  imports: [CommonModule, DropdownModule, ReactiveFormsModule],
  exports: [AlandaDropdownSelectComponent],
})
export class DropdownSelectModule {}
