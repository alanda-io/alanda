import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {
  AlandaCreateProjectComponent,
  AlandaGroupManagementComponent,
  AlandaRoleManagementComponent,
  AlandaPermissionManagementComponent,
  AlandaFormsControllerComponent,
} from '@alanda/common';
import { HomeComponent } from './features/home/home.component';
import { PermissionsDemoComponent } from './components/permissions-demo/permissions-demo.component';
import { UserManagementContainerComponent } from './features/usermgmt/user-management-container/user-management-container.component';
import { ProjectMonitorComponent } from './features/project-monitor/project-monitor.component';
import { UserEnrichedProjectsControllerComponent } from './components/projects-controller/user-enriched-projects-controller.component';
import { ProjectsAndProcessesDemoComponent } from './components/projects-and-processes-demo/projects-and-processes-demo.component';
import { ProjectDetailsComponent } from './components/project-details/project-details.component';
import { TaskListComponent } from './features/task-list/task-list.component';

const routes: Routes = [
  { path: '', component: HomeComponent, data: { title: 'Home' } },
  {
    path: 'admin/users',
    component: UserManagementContainerComponent,
    data: { title: 'Admin User' },
  },
  {
    path: 'admin/groups',
    component: AlandaGroupManagementComponent,
    data: { title: 'Admin Groups' },
  },
  {
    path: 'admin/roles',
    component: AlandaRoleManagementComponent,
    data: { title: 'Admin Roles' },
  },
  {
    path: 'admin/permissions',
    component: AlandaPermissionManagementComponent,
    data: { title: 'Admin Permissions' },
  },
  {
    path: 'admin/permissions-demo',
    component: PermissionsDemoComponent,
    data: { title: 'Admin Permissions Demo' },
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
    path: 'projectdetails/:projectId',
    component: UserEnrichedProjectsControllerComponent,
    children: [
      {
        path: 'vacation',
        loadChildren: () =>
          import('./features/vacation/vacation.module').then(
            (m) => m.VacationModule,
          ),
      },
      {
        path: '**',
        component: ProjectDetailsComponent,
      },
    ],
  },
  {
    path: 'projectdetails/:projectId',
    component: ProjectsAndProcessesDemoComponent,
  },
  { path: '**', redirectTo: '' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' })],
  exports: [RouterModule],
})
export class AppRoutingModule {}
