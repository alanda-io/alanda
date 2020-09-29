import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserManagementContainerComponent } from './components/user-management-container.component';
import { AlandaCommonModule } from '@alanda/common';

@NgModule({
  declarations: [UserManagementContainerComponent],
  imports: [CommonModule, AlandaCommonModule],
  exports: [UserManagementContainerComponent],
})
export class UserManagementModule {}
