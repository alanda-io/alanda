import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';
import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { CoreModule } from './core/core.module';
import { AppSettings, APP_CONFIG, AlandaCommonModule } from '@alanda/common';
import { ALANDA_CONFIG } from './app.settings';
import { RxState } from '@rx-angular/state';
import { ToastModule } from 'primeng/toast';

const CURRENT_CONFIG: AppSettings = ALANDA_CONFIG;

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    CoreModule,
    ToastModule,
    AlandaCommonModule.forRoot(CURRENT_CONFIG),
  ],
  providers: [{ provide: APP_CONFIG, useValue: CURRENT_CONFIG }, RxState],
  bootstrap: [AppComponent],
})
export class AppModule {
  constructor() {}
}
