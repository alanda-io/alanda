import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TaskListComponent } from './task-list.component';
import { TaskTableModule } from '@alanda/common';
import { PanelModule } from 'primeng/panel';
import { TemplateModule } from '@rx-angular/template';

@NgModule({
  declarations: [TaskListComponent],
  imports: [CommonModule, TaskTableModule, PanelModule, TemplateModule],
})
export class TaskListModule {}
