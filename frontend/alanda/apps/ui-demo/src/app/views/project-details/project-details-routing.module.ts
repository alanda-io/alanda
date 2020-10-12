import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { UserEnrichedProjectsControllerComponent } from './components/projects-controller/user-enriched-projects-controller.component';
import { ProjectDetailsComponent } from './project-details.component';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: ':projectId',
        component: UserEnrichedProjectsControllerComponent,
        children: [
          {
            path: '',
            component: ProjectDetailsComponent,
          },
        ],
      },
    ]),
  ],
  exports: [RouterModule],
})
export class ProjectDetailsRoutingModule {}
