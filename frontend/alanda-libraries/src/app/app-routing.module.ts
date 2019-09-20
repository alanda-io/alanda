import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { UserComponent } from 'projects/alanda-common/src/lib/components/admin/user-management/user.component';
import { CreateProjectComponent } from 'projects/alanda-common/src/lib/components/create-project/create-project.component';
import { ProjectMonitorComponent } from 'projects/alanda-common/src/lib/components/project-monitor/project-monitor.component';
import { TasklistComponent } from 'projects/alanda-common/src/lib/components/task-monitor/tasklist.component';
import { AttachmentsComponent } from 'projects/alanda-common/src/lib/components/attachments/attachments.component';
import { GroupComponent } from 'projects/alanda-common/src/lib/components/admin/group-management/group.component';
import { RoleComponent } from 'projects/alanda-common/src/lib/components/admin/role-management/role.component';
import { PermissionComponent } from 'projects/alanda-common/src/lib/components/admin/permission-management/permission.component';
import { CommentsComponent } from 'projects/alanda-common/src/lib/components/comments/comments.component';
import { FormsControllerComponent } from 'projects/alanda-common/src/lib/components/controller/forms-controller/forms-controller.component';
import { ProjectsControllerComponent } from 'projects/alanda-common/src/lib/components/controller/projects-controller/projects-controller.component';

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
  { path: 'forms/:formKey/:taskId', component: FormsControllerComponent },
  { path: 'projectdetails/:projectId', component: ProjectsControllerComponent },
  { path: '**', redirectTo: ''}
]

@NgModule({
  imports: [ RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload'})],
  exports: [ RouterModule ]
})
export class AppRoutingModule { }
