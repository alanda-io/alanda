import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './views/home/home.component';
import {
  AlandaCreateProjectComponent,
  AlandaProjectMonitorComponent,
  AlandaTasklistComponent,
  AlandaGroupManagementComponent,
  AlandaRoleManagementComponent,
  AlandaPermissionManagementComponent,
  AlandaUserManagementComponent,
  AlandaProjectsControllerComponent,
} from '@alanda/common';
import { AlandaFormsControllerComponent } from '@alanda/common';
import { PermissionsDemoComponent } from './components/permissions-demo/permissions-demo.component';
import { UserMnagementContainerComponent } from './features/usermgmt/user-mnagement-container/user-mnagement-container.component';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'admin/users', component: UserMnagementContainerComponent },
  { path: 'admin/groups', component: AlandaGroupManagementComponent },
  { path: 'admin/roles', component: AlandaRoleManagementComponent },
  { path: 'admin/permissions', component: AlandaPermissionManagementComponent },
  { path: 'admin/permissions-demo', component: PermissionsDemoComponent },
  { path: 'create/project', component: AlandaCreateProjectComponent },
  {
    path: 'create/project/:projectGuid',
    component: AlandaCreateProjectComponent,
  },
  { path: 'monitor/projects', component: AlandaProjectMonitorComponent },
  { path: 'tasks/list', component: AlandaTasklistComponent },
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
    component: AlandaProjectsControllerComponent,
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
  // { path: 'projectdetails/:projectId', component:  AlandaProjectsControllerComponent},
  { path: '**', redirectTo: '' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' })],
  exports: [RouterModule],
})
export class AppRoutingModule {}
