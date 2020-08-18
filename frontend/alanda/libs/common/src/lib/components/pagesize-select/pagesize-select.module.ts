import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AlandaPagesizeSelectComponent } from './pagesize-select.component';
import { DropdownModule } from 'primeng/dropdown';

@NgModule({
  declarations: [AlandaPagesizeSelectComponent],
  imports: [CommonModule, DropdownModule],
  exports: [AlandaPagesizeSelectComponent],
})
export class PagesizeSelectModule {}
