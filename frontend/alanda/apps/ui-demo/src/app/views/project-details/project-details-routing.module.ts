import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { UserEnrichedProjectsControllerComponent } from './components/projects-controller/user-enriched-projects-controller.component';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: ':projectId',
        component: UserEnrichedProjectsControllerComponent,
        children: [
          {
            path: 'vacation',
            loadChildren: () =>
              import('../../features/vacation/vacation.module').then(
                (m) => m.VacationModule,
              ),
          },
        ],
      },
      { path: '**', redirectTo: '/' },
    ]),
  ],
  exports: [RouterModule],
})
export class ProjectDetailsRoutingModule {}
