import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';
import { AppComponent } from './app.component';

/**
 * Add here your alanda-common components to test them independently from
 * the client project
 */

import { AppRoutingModule } from './app-routing.module';
import { LayoutModule } from './layout/layout.module';
import { CoreModule } from './core/core.module';
import { ViewsModule } from './views/views.module';
import { AlandaCommonModule, AppSettings, APP_CONFIG, AlandaProjectPropertiesService,
         AlandaProjectDetailsService } from 'projects/alanda-common/src/public-api';
import { ALANDA_CONFIG } from './app.settings';
import { ProjectPropertiesService } from './core/services/projectproperties.service';
import { ProjectDetailsService } from './core/services/projectdetails.service';
import { ProjectDetailsComponent } from './components/project-details/project-details.component';
import { ProjectPropertiesComponent } from './components/project-properties/project-properties.component';
import { SharedModule } from './shared/shared.module';
import { VacationModule } from './features/vacation/vacation.module';
import { CardModule } from 'primeng/card';
import { FieldsetModule } from 'primeng/fieldset';
import { ButtonModule } from 'primeng/button';
import { ReactiveFormsModule } from '@angular/forms';

const CURRENT_CONFIG: AppSettings = ALANDA_CONFIG;

@NgModule({
  declarations: [
    AppComponent,
    ProjectDetailsComponent,
    ProjectPropertiesComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    LayoutModule,
    CoreModule,
    ViewsModule,
    SharedModule,
    ReactiveFormsModule,
    CardModule,
    FieldsetModule,
    ButtonModule,
    AlandaCommonModule.forRoot(CURRENT_CONFIG)
  ],
  providers: [
    {provide: APP_CONFIG, useValue: CURRENT_CONFIG},
    {provide: AlandaProjectPropertiesService, useClass: ProjectPropertiesService },
  ],
  bootstrap: [AppComponent]
})
export class AppModule {

  constructor() {
  }

  ngDoBootstrap() {

  }
}


