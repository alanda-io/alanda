import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { UserComponent, CreateProjectComponent, ProjectMonitorComponent, TasklistComponent,AttachmentsComponent,
         GroupComponent, RoleComponent, PermissionComponent, CommentsComponent, FormsControllerComponent, ProjectsControllerComponent } from 'projects/alanda-common/src/public_api';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'admin/users', component: UserComponent },
  { path: 'create/project', component: CreateProjectComponent },
  { path: 'monitor/projects', component: ProjectMonitorComponent },
  { path: 'tasks/list', component: TasklistComponent },
  { path: 'attachment', component: AttachmentsComponent },
  { path: 'admin/groups', component: GroupComponent },
  { path: 'admin/roles', component: RoleComponent },
  { path: 'admin/permissions', component: PermissionComponent },
  { path: 'comments', component: CommentsComponent },
  { path: 'forms/:formKey/:taskId', component: FormsControllerComponent},
  { path: 'projectdetails/:projectId', component: ProjectsControllerComponent},
  { path: '**', redirectTo: ''}
]

@NgModule({
  imports: [ RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload'})],
  exports: [ RouterModule ]
})
export class AppRoutingModule { }
