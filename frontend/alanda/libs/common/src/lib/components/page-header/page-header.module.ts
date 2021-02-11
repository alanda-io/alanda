import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PageHeaderComponent } from './page-header.component';
import { TabViewModule } from 'primeng/tabview';
import { RouterModule } from '@angular/router';
import { PioModule } from '../pio/pio.module';
import { HistoryGridModule } from '../history/history-grid.module';
import { TooltipModule } from 'primeng/tooltip';
import { MenuModule } from 'primeng/menu';

@NgModule({
  declarations: [PageHeaderComponent],
  imports: [
    CommonModule,
    TabViewModule,
    RouterModule,
    PioModule,
    HistoryGridModule,
    TooltipModule,
    MenuModule,
  ],
  exports: [PageHeaderComponent],
})
export class PageHeaderModule {}
