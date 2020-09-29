import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AlandaProjectMonitorComponent } from './project-monitor.component';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'projects',
        component: AlandaProjectMonitorComponent,
        data: { title: 'Projects' },
      },
      { path: '**', redirectTo: '/' },
    ]),
  ],
  exports: [RouterModule],
})
export class ProjectMonitorRoutingModule {}
