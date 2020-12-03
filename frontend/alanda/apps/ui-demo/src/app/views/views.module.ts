import { NgModule } from '@angular/core';
import { HomeModule } from './home/home.module';
import { TaskListModule } from './task-list/task-list.module';

@NgModule({
  imports: [HomeModule, TaskListModule],
})
export class ViewsModule {}
