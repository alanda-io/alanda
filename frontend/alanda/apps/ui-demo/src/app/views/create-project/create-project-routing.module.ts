import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AlandaCreateProjectComponent } from '@alanda/common';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'project',
        component: AlandaCreateProjectComponent,
        data: { title: 'Create Project' },
      },
      {
        path: 'project/:projectGuid',
        component: AlandaCreateProjectComponent,
        data: { title: 'Create Project' },
      },
      { path: '**', redirectTo: '/' },
    ]),
  ],
  exports: [RouterModule],
})
export class CreateProjectRoutingModule {}
