import { NgModule } from '@angular/core';
import { HomeModule } from './home/home.module';
import { ProjectMonitorModule } from './project-monitor/project-monitor.module';
import { TaskListModule } from './task-list/task-list.module';

@NgModule({
  imports: [HomeModule, ProjectMonitorModule, TaskListModule],
})
export class ViewsModule {}
