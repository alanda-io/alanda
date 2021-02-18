import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProjectMonitorComponent } from './project-monitor.component';
import { PanelModule } from 'primeng/panel';
import { ProjectTableModule } from '@alanda/common';

@NgModule({
  declarations: [ProjectMonitorComponent],
  imports: [CommonModule, ProjectTableModule, PanelModule],
})
export class ProjectMonitorModule {}
