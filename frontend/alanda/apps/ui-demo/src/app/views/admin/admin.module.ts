import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminRoutingModule } from './admin-routing.module';
import { PermissionsDemoComponent } from './components/permissions-demo/permissions-demo.component';
import { ReactiveFormsModule } from '@angular/forms';
import { AlandaCommonModule } from '@alanda/common';
import { CalendarModule } from 'primeng/calendar';
import { InputTextModule } from 'primeng/inputtext';
import { UserManagementContainerComponent } from './components/user-management/user-management-container.component';

@NgModule({
  declarations: [PermissionsDemoComponent, UserManagementContainerComponent],
  imports: [
    CommonModule,
    AdminRoutingModule,
    ReactiveFormsModule,
    AlandaCommonModule,
    InputTextModule,
    CalendarModule,
  ],
  exports: [PermissionsDemoComponent, UserManagementContainerComponent],
})
export class AdminModule {}
