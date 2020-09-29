import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AlandaFormsControllerComponent } from '@alanda/common';

const routes: Routes = [
  {
    path: '',
    loadChildren: () =>
      import('./views/home/home.module').then((m) => m.HomeModule),
  },
  {
    path: 'admin',
    loadChildren: () =>
      import('./views/admin/admin.module').then((m) => m.AdminModule),
  },
  {
    path: 'create',
    loadChildren: () =>
      import('./views/create-project/create-project.module').then(
        (m) => m.CreateProjectModule,
      ),
  },
  {
    path: 'monitor',
    loadChildren: () =>
      import('./views/project-monitor/project-monitor.module').then(
        (m) => m.ProjectMonitorModule,
      ),
  },
  {
    path: 'tasks',
    loadChildren: () =>
      import('./views/task-list/task-list.module').then(
        (m) => m.TaskListModule,
      ),
  },
  {
    path: 'forms',
    children: [
      {
        path: 'vacation',
        component: AlandaFormsControllerComponent,
        loadChildren: () =>
          import('./features/vacation/vacation.module').then(
            (m) => m.VacationModule,
          ),
      },
    ],
  },
  {
    path: 'projectdetails',
    loadChildren: () =>
      import('./views/project-details/project-details.module').then(
        (m) => m.ProjectDetailsModule,
      ),
  },
  { path: '**', redirectTo: '' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' })],
  exports: [RouterModule],
})
export class AppRoutingModule {}
