import { NgModule } from '@angular/core';
import { HomeComponent } from './home/home.component';
import { ProjectMonitorModule } from './project-monitor/project-monitor.module';
import { TaskListModule } from './task-list/task-list.module';

@NgModule({
  declarations: [HomeComponent],
  imports: [ProjectMonitorModule, TaskListModule],
  providers: [],
})
export class ViewsModule {}
