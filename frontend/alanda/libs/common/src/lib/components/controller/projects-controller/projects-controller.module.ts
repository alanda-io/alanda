import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AlandaProjectsControllerComponent } from './projects-controller.component';
import { TabViewModule } from 'primeng/tabview';
import { RouterModule } from '@angular/router';
import { PioModule } from '../../pio/pio.module';
import { HistoryGridModule } from '../../history/history-grid.module';

@NgModule({
  declarations: [AlandaProjectsControllerComponent],
  imports: [
    CommonModule,
    TabViewModule,
    RouterModule,
    PioModule,
    HistoryGridModule,
  ],
  exports: [AlandaProjectsControllerComponent],
})
export class ProjectsControllerModule {}
