import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './views/home/home.component';
import { AlandaCreateProjectComponent, AlandaProjectMonitorComponent, AlandaTasklistComponent,
         AlandaGroupManagementComponent, AlandaRoleManagementComponent,
         AlandaPermissionManagementComponent,
         AlandaUserManagementComponent,
         AlandaProjectsControllerComponent,
         } from 'projects/alanda-common/src/public_api';
import { PrepareVacationRequestComponent } from './features/vacation/forms/prepare-vacation-request.component';
import { CheckVacationRequestComponent } from './features/vacation/forms/check-vacation-request.component';
import { ModifyVacationRequestComponent } from './features/vacation/forms/modify-vacation-request.component';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'admin/users', component: AlandaUserManagementComponent },
  { path: 'admin/groups', component: AlandaGroupManagementComponent },
  { path: 'admin/roles', component: AlandaRoleManagementComponent },
  { path: 'admin/permissions', component: AlandaPermissionManagementComponent },
  { path: 'create/project', component: AlandaCreateProjectComponent },
  { path: 'monitor/projects', component: AlandaProjectMonitorComponent },
  { path: 'tasks/list', component: AlandaTasklistComponent },
  //{ path: 'forms/vacation', loadChildren: './features/vacation/vacation.module#VacationModule'},
  { path: 'forms/vacation', children: [
    { path: 'prepare-vacation-request/:taskId', component: PrepareVacationRequestComponent},
    { path: 'check-vacation-request/:taskId', component: CheckVacationRequestComponent},
    { path: 'modify-vacation-request/:taskId', component: ModifyVacationRequestComponent},
  ]},
  { path: 'projectdetails/:projectId', component:  AlandaProjectsControllerComponent},
  { path: '**', redirectTo: ''}
];

@NgModule({
  imports: [ RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload'})],
  exports: [ RouterModule ]
})
export class AppRoutingModule { }
