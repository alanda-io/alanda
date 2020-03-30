import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule, Inject } from '@angular/core';
import { AppComponent } from './app.component';

/**
 * Add here your alanda-common components to test them independently from
 * the client project
 */

import { AppRoutingModule } from './app-routing.module';
import { LayoutModule } from './layout/layout.module';
import { CoreModule } from './core/core.module';
import { ViewsModule } from './views/views.module';
import { AlandaCommonModule, AppSettings, APP_CONFIG, AlandaProjectPropertiesService, AlandaProjectDetailsService } from 'projects/alanda-common/src/public_api';
import { ALANDA_CONFIG } from './app.settings';
import { ProjectPropertiesService } from './core/services/projectproperties.service';
import { ProjectDetailsService } from './core/services/projectdetails.service';
import { VacationProjectDetailsComponent } from './components/vacation-project-details/vacation-project-details.component';
import { VacationProjectPropertiesComponent } from './components/vacation-project-properties/vacation-project-properties.component';
import { FieldsetModule } from 'primeng/fieldset';
import { CardModule } from 'primeng/card';

const CURRENT_CONFIG: AppSettings = ALANDA_CONFIG;

@NgModule({
  declarations: [
    AppComponent,
    VacationProjectDetailsComponent,
    VacationProjectPropertiesComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    LayoutModule,
    CoreModule,
    ViewsModule,
    AlandaCommonModule.forRoot(CURRENT_CONFIG),
    FieldsetModule,
    CardModule,
  ],
  providers: [
    {provide: APP_CONFIG, useValue: CURRENT_CONFIG},
    {provide: AlandaProjectPropertiesService, useClass: ProjectPropertiesService },
    {provide: AlandaProjectDetailsService, useClass: ProjectDetailsService },
  ],
  entryComponents: [
    VacationProjectDetailsComponent,
    VacationProjectPropertiesComponent
  ],
  bootstrap: [AppComponent]
})
export class AppModule {

  constructor() {
  }

  ngDoBootstrap() {

  }
}


