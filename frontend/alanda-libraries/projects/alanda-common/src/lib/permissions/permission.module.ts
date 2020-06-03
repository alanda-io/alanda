import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {PermissionsDirective} from './permissions.directive';

@NgModule({
  imports: [
    CommonModule
  ],
  declarations: [PermissionsDirective],
  exports: [PermissionsDirective]
})
// forRoot for UserService
export class PermissionModule { }
