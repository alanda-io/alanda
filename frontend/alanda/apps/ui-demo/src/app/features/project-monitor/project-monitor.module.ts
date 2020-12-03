import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '../../shared/shared.module';
import { ProjectMonitorComponent } from './project-monitor.component';
import { PanelModule } from 'primeng/panel';
import { ProjectTableModule } from '@alanda/common';

@NgModule({
  declarations: [ProjectMonitorComponent],
  imports: [CommonModule, SharedModule, ProjectTableModule, PanelModule],
})
export class ProjectMonitorModule {}
