import { NgModule } from '@angular/core';
import { AlandaProjectTableComponent } from './project-table.component';
import { TableModule } from 'primeng/table';
import { CommonModule } from '@angular/common';
import { MonitorValuesPipeModule } from '../../pipes/nested-object.pipe';
import { MenuModule } from 'primeng/menu';
import { DropdownModule } from 'primeng/dropdown';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { FormsModule } from '@angular/forms';
import { PageSizeSelectModule } from '../page-size-select/page-size-select.module';

@NgModule({
  imports: [
    TableModule,
    CommonModule,
    MonitorValuesPipeModule,
    MenuModule,
    DropdownModule,
    InputTextModule,
    ButtonModule,
    FormsModule,
    PageSizeSelectModule,
  ],
  declarations: [AlandaProjectTableComponent],
  exports: [AlandaProjectTableComponent],
})
export class ProjectTableModule {}
