import { NgModule } from '@angular/core';
import { AlandaHeaderComponent } from './header.component';
import { MenubarModule } from 'primeng/menubar';
import { ToastModule } from 'primeng/toast';
import { TemplateModule } from '@rx-angular/template';
import { CommonModule } from '@angular/common';

@NgModule({
  imports: [MenubarModule, ToastModule, TemplateModule, CommonModule],
  declarations: [AlandaHeaderComponent],
  exports: [AlandaHeaderComponent],
})
export class HeaderModule {}
