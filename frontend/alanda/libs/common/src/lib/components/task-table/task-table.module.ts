import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { DropdownModule } from 'primeng/dropdown';
import { InputTextModule } from 'primeng/inputtext';
import { MenuModule } from 'primeng/menu';
import { SplitButtonModule } from 'primeng/splitbutton';
import { TableModule } from 'primeng/table';
import { PanelModule } from 'primeng/panel';
import { AlandaTaskTableComponent } from './task-table.component';
import { DialogModule } from 'primeng/dialog';
import { FormsModule } from '@angular/forms';
import { MonitorValuesPipeModule } from '../../pipes/nested-object.pipe';

@NgModule({
  declarations: [AlandaTaskTableComponent],
  imports: [
    CommonModule,
    PanelModule,
    ButtonModule,
    DropdownModule,
    TableModule,
    SplitButtonModule,
    MenuModule,
    InputTextModule,
    DialogModule,
    FormsModule,
    MonitorValuesPipeModule,
  ],
  exports: [AlandaTaskTableComponent],
})
export class TaskTableModule {}
