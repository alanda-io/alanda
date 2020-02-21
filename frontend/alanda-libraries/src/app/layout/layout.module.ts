import { NgModule } from '@angular/core';
import { HeaderComponent } from './header/header.component';
import { MenubarModule } from 'primeng/menubar';
import { ToastModule } from 'primeng/toast';

@NgModule({
  declarations: [HeaderComponent],
  imports: [
    MenubarModule,
    ToastModule
  ],
  exports: [
    HeaderComponent
  ],
  providers: [],
})
export class LayoutModule {
}
