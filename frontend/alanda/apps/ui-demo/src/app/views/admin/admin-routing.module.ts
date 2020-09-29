import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { UserManagementContainerComponent } from './components/user-management/user-management-container.component';
import {
  AlandaGroupManagementComponent,
  AlandaPermissionManagementComponent,
  AlandaRoleManagementComponent,
} from '@alanda/common';
import { PermissionsDemoComponent } from './components/permissions-demo/permissions-demo.component';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'users',
        component: UserManagementContainerComponent,
        data: { title: 'Admin User' },
      },
      {
        path: 'groups',
        component: AlandaGroupManagementComponent,
        data: { title: 'Admin Groups' },
      },
      {
        path: 'roles',
        component: AlandaRoleManagementComponent,
        data: { title: 'Admin Roles' },
      },
      {
        path: 'permissions',
        component: AlandaPermissionManagementComponent,
        data: { title: 'Admin Permissions' },
      },
      {
        path: 'permissions-demo',
        component: PermissionsDemoComponent,
        data: { title: 'Admin Permissions Demo' },
      },
      { path: '**', redirectTo: '/' },
    ]),
  ],
  exports: [RouterModule],
})
export class AdminRoutingModule {}
