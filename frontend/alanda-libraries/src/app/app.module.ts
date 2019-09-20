import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule, Inject } from '@angular/core';

import { AppComponent } from './app.component';

/**
 * Add here your alanda-common components to test them independently from
 * the client project
 */

import { CalendarModule} from 'primeng/calendar';
import { MenubarModule } from 'primeng/menubar';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { ALANDA_CONFIG } from './app.settings';
import { AppRoutingModule } from './app-routing.module';
import { HomeComponent } from './components/home/home.component';
import { AppSettings, APP_CONFIG } from 'projects/alanda-common/src/lib/models/appSettings';
import { AlandaCommonModule } from 'projects/alanda-common/src/lib/alanda-common.module';

const CURRENT_CONFIG: AppSettings = ALANDA_CONFIG;
@NgModule({
  declarations: [
    AppComponent,
    HomeComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    AlandaCommonModule.forRoot(CURRENT_CONFIG),
    MenubarModule,
    CalendarModule,
    ToastModule,
  ],
  providers: [
    {
      provide: APP_CONFIG,
      useValue: CURRENT_CONFIG
    },
    MessageService,
  ],
  bootstrap: [AppComponent]
})
export class AppModule { 

  constructor(@Inject(APP_CONFIG) config: AppSettings) {
    console.log("Settings", config);
  }

  ngDoBootstrap() {

  }
}
