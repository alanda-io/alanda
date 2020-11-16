import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AlandaGroupManagementComponent } from './group-management/group-management.component';
import { AlandaPermissionManagementComponent } from './permission-management/permission-management.component';
import { AlandaRoleManagementComponent } from './role-management/role-management.component';
import { AlandaUserManagementComponent } from './user-management/user-management.component';
import { PanelModule } from 'primeng/panel';
import { TableModule } from 'primeng/table';
import { InputTextModule } from 'primeng/inputtext';
import { TabViewModule } from 'primeng/tabview';
import { ButtonModule } from 'primeng/button';
import { ScrollPanelModule } from 'primeng/scrollpanel';
import { PickListModule } from 'primeng/picklist';
import { ReactiveFormsModule } from '@angular/forms';
import { InputSwitchModule } from 'primeng/inputswitch';

@NgModule({
  declarations: [
    AlandaGroupManagementComponent,
    AlandaPermissionManagementComponent,
    AlandaRoleManagementComponent,
    AlandaUserManagementComponent,
  ],
  imports: [
    CommonModule,
    PanelModule,
    TableModule,
    InputTextModule,
    TabViewModule,
    ButtonModule,
    ScrollPanelModule,
    PickListModule,
    ReactiveFormsModule,
    InputSwitchModule,
  ],
  exports: [
    AlandaGroupManagementComponent,
    AlandaPermissionManagementComponent,
    AlandaRoleManagementComponent,
    AlandaUserManagementComponent,
  ],
})
export class AdminModule {}
