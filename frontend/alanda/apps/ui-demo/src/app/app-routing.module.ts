import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AlandaCreateProjectComponent } from '@alanda/common';
import { HomeComponent } from './features/home/home.component';
import { ProjectMonitorComponent } from './features/project-monitor/project-monitor.component';
import { ProjectsControllerComponent } from './components/projects-controller/projects-controller.component';
import { TaskListComponent } from './features/task-list/task-list.component';
import { UserEnrichedFormsControllerComponent } from './components/forms-controller/user-enriched-forms-controller.component';

const routes: Routes = [
  { path: '', component: HomeComponent, data: { title: 'Home' } },
  {
    path: 'admin',
    loadChildren: () =>
      import('./features/admin/admin.module').then((m) => m.AdminModule),
  },
  {
    path: 'create/project',
    component: AlandaCreateProjectComponent,
    data: { title: 'Create Project' },
  },
  {
    path: 'create/project/:projectGuid',
    component: AlandaCreateProjectComponent,
  },
  {
    path: 'monitor/projects',
    component: ProjectMonitorComponent,
    data: { title: 'Projects' },
  },
  {
    path: 'tasks/list',
    component: TaskListComponent,
    data: { title: 'Tasks' },
  },
  {
    path: 'forms',
    component: UserEnrichedFormsControllerComponent,
    children: [
      {
        path: 'vacation',
        loadChildren: () =>
          import('./features/vacation/vacation.module').then(
            (m) => m.VacationModule,
          ),
      },
    ],
  },
  {
    path: 'projectdetails/:projectId',
    component: ProjectsControllerComponent,
    children: [
      {
        path: 'vacation',
        loadChildren: () =>
          import('./features/vacation/vacation.module').then(
            (m) => m.VacationModule,
          ),
      },
    ],
  },
  { path: '**', redirectTo: '' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' })],
  exports: [RouterModule],
})
export class AppRoutingModule {}
