import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserComponent, ProjectMonitorComponent, TasklistComponent, AttachmentsComponent } from 'projects/alanda-common/src/public_api';
import { HomeComponent } from './components/home/home.component';
import { GroupComponent } from 'projects/alanda-common/src/lib/components/admin/group/group.component';
import { RoleComponent } from 'projects/alanda-common/src/lib/components/admin/role/role.component';
import { PermissionComponent } from 'projects/alanda-common/src/lib/components/admin/permission/permission.component';
import { CommentsComponent } from 'projects/alanda-common/src/lib/components/comments/comments.component';
import { FormsControllerComponent } from 'projects/alanda-common/src/lib/components/forms/tasks/forms-controller/forms-controller.component';
import { ProjectsControllerComponent } from 'projects/alanda-common/src/lib/components/forms/projects/projects-controller/projects-controller.component';
import {CreateProjectComponent} from 'projects/alanda-common/src/lib/components/create-project/create-project.component';

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
