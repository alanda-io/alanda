import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PageHeaderComponent } from './page-header.component';
import { TabViewModule } from 'primeng/tabview';
import { RouterModule } from '@angular/router';
import { PioModule } from '../pio/pio.module';
import { HistoryGridModule } from '../history/history-grid.module';
import { TooltipModule } from 'primeng/tooltip';
import { MenuModule } from 'primeng/menu';
import { PanelModule } from 'primeng/panel';
import { FilterPipeModule } from '../../pipes/filter.pipe';
import { DialogModule } from 'primeng/dialog';
import { DropdownModule } from 'primeng/dropdown';
import { ButtonModule } from 'primeng/button';
import { PageHeaderBarComponent } from './page-header-bar/page-header-bar.component';

@NgModule({
  declarations: [PageHeaderComponent, PageHeaderBarComponent],
  imports: [
    CommonModule,
    TabViewModule,
    RouterModule,
    PioModule,
    HistoryGridModule,
    TooltipModule,
    MenuModule,
    PanelModule,
    FilterPipeModule,
    DialogModule,
    DropdownModule,
    ButtonModule,
  ],
  exports: [PageHeaderComponent, PageHeaderBarComponent],
})
export class PageHeaderModule {}
