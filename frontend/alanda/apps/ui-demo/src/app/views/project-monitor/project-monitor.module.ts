import { NgModule } from '@angular/core';
import { AlandaProjectMonitorComponent } from './project-monitor.component';
import { PanelModule } from 'primeng/panel';
import { ProjectTableModule } from '@alanda/common';

@NgModule({
  declarations: [AlandaProjectMonitorComponent],
  imports: [ProjectTableModule, PanelModule],
})
export class ProjectMonitorModule {}
