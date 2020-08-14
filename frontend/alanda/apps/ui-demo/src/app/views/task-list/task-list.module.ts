import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AlandaTaskListComponent } from './task-list.component';
import { TaskTableModule } from '@alanda/common';
import { PanelModule } from 'primeng/panel';

@NgModule({
  declarations: [AlandaTaskListComponent],
  imports: [CommonModule, TaskTableModule, PanelModule],
})
export class TaskListModule {}
