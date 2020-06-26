import { NgModule } from '@angular/core';
import { HeaderComponent } from './header/header.component';
import { MenubarModule } from 'primeng/menubar';
import { ToastModule } from 'primeng/toast';
import { CommonModule } from '@angular/common';

@NgModule({
  declarations: [HeaderComponent],
  imports: [
    MenubarModule,
    ToastModule,
    CommonModule
  ],
  exports: [
    HeaderComponent
  ],
  providers: [],
})
export class LayoutModule {
}
