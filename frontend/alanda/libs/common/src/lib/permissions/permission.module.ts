import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AlandaPermissionsDirective } from './permissions.directive';

@NgModule({
  imports: [CommonModule],
  declarations: [AlandaPermissionsDirective],
  exports: [AlandaPermissionsDirective],
})
// forRoot for UserService
export class PermissionModule {}
