import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './views/home/home.component';
import { AlandaUserManagementComponent, AlandaCreateProjectComponent, AlandaProjectMonitorComponent, AlandaTasklistComponent,
         AlandaGroupManagementComponent, AlandaRoleManagementComponent,
         AlandaPermissionManagementComponent } from 'projects/alanda-common/src/public_api';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'admin/users', component: AlandaUserManagementComponent },
  { path: 'admin/groups', component: AlandaGroupManagementComponent },
  { path: 'admin/roles', component: AlandaRoleManagementComponent },
  { path: 'admin/permissions', component: AlandaPermissionManagementComponent },
  { path: 'create/project', component: AlandaCreateProjectComponent },
  { path: 'monitor/projects', component: AlandaProjectMonitorComponent },
  { path: 'tasks/list', component: AlandaTasklistComponent },
  { path: 'forms', children: [
    { path: 'vacation', loadChildren: './features/vacation/vacation.module#VacationModule'},
  ]},
  { path: 'projectdetails/:projectId', component:  HomeComponent},
  { path: '**', redirectTo: ''}
];

@NgModule({
  imports: [ RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload'})],
  exports: [ RouterModule ]
})
export class AppRoutingModule { }
