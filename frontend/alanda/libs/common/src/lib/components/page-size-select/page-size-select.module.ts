import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AlandaPageSizeSelectComponent } from './page-size-select.component';
import { DropdownModule } from 'primeng/dropdown';

@NgModule({
  imports: [CommonModule, DropdownModule],
  declarations: [AlandaPageSizeSelectComponent],
  exports: [AlandaPageSizeSelectComponent],
})
export class PageSizeSelectModule {}
