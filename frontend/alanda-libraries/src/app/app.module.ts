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
import { VacationModule } from './vacation/vacation.module';
import { VacationProjectPropertiesService } from './vacation/services/vacation-projectproperties.service';
import { VacationProjectDetailsService } from './vacation/services/vacation-projectdetails.service';
import { VacationFormsService } from './vacation/services/vacation-forms.service';
import { FormsServiceNg } from 'projects/alanda-common/src/lib/services/forms.service';
import { ProjectPropertiesServiceNg } from 'projects/alanda-common/src/lib/services/project-properties.service';
import { AppSettings, AlandaCommonModule, APP_CONFIG, ProjectDetailsServiceNg } from 'projects/alanda-common/src/public-api';

const CURRENT_CONFIG: AppSettings = ALANDA_CONFIG;
@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    AlandaCommonModule.forRoot(CURRENT_CONFIG),
    MenubarModule,
    CalendarModule,
    ToastModule,
    VacationModule
  ],
  providers: [
    {provide: APP_CONFIG, useValue: CURRENT_CONFIG},
    {provide: ProjectPropertiesServiceNg, useClass: VacationProjectPropertiesService},
    {provide: ProjectDetailsServiceNg, useClass: VacationProjectDetailsService},
    {provide: FormsServiceNg, useClass: VacationFormsService},
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
