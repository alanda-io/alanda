import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AlandaTaskListComponent } from './task-list.component';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'list',
        component: AlandaTaskListComponent,
        data: { title: 'Tasks' },
      },
      { path: '**', redirectTo: '/' },
    ]),
  ],
  exports: [RouterModule],
})
export class TaskListRoutingModule {}
