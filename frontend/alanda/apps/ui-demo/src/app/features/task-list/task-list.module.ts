import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '../../shared/shared.module';
import { TaskListComponent } from './task-list.component';
import { TaskTableModule } from '@alanda/common';
import { PanelModule } from 'primeng/panel';
import { TemplateModule } from '@rx-angular/template';

@NgModule({
  declarations: [TaskListComponent],
  imports: [
    CommonModule,
    SharedModule,
    TaskTableModule,
    PanelModule,
    TemplateModule,
  ],
})
export class TaskListModule {}
