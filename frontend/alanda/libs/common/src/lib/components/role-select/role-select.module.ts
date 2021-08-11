import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AlandaSelectRoleComponent } from './role-select.component';
import { DropdownModule } from 'primeng/dropdown';
import { MessageModule } from 'primeng/message';
import { ReactiveFormsModule } from '@angular/forms';
import { LabelModule } from '../label/label.module';

@NgModule({
  declarations: [AlandaSelectRoleComponent],
  imports: [
    CommonModule,
    DropdownModule,
    MessageModule,
    ReactiveFormsModule,
    LabelModule,
  ],
  exports: [AlandaSelectRoleComponent],
})
export class RoleSelectModule {}
