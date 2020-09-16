import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AlandaHistoryGridComponent } from './history-grid.component';
import { PanelModule } from 'primeng/panel';
import { TableModule } from 'primeng/table';
import { InputTextModule } from 'primeng/inputtext';

@NgModule({
  declarations: [AlandaHistoryGridComponent],
  imports: [CommonModule, PanelModule, TableModule, InputTextModule],
  exports: [AlandaHistoryGridComponent],
})
export class HistoryGridModule {}
