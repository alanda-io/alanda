import { NgModule } from '@angular/core';
import { AlandaProjectMonitorComponent } from './project-monitor.component';
import { PanelModule } from 'primeng/panel';
import { ProjectTableModule } from '@alanda/common';
import { ProjectMonitorRoutingModule } from './project-monitor-routing.module';

@NgModule({
  declarations: [AlandaProjectMonitorComponent],
  imports: [ProjectTableModule, PanelModule, ProjectMonitorRoutingModule],
})
export class ProjectMonitorModule {}
