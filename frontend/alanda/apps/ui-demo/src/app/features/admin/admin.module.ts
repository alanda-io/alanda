import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AlandaCommonModule } from '@alanda/common';
import { UserManagementContainerComponent } from './user-management-container/user-management-container.component';
import { AdminRoutingModule } from './admin-routing.module';

@NgModule({
  declarations: [UserManagementContainerComponent],
  imports: [CommonModule, AlandaCommonModule, AdminRoutingModule],
})
export class AdminModule {}
