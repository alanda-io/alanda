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
import { AlandaCommonModule, AppSettings, APP_CONFIG, AlandaProjectPropertiesService,
         AlandaProjectDetailsService } from 'projects/alanda-common/src/public-api';
import { ALANDA_CONFIG } from './app.settings';
import { ProjectPropertiesService } from './core/services/projectproperties.service';
import { ProjectDetailsService } from './core/services/projectdetails.service';
import { ProjectDetailsComponent } from './components/project-details/project-details.component';
import { ProjectPropertiesComponent } from './components/project-properties/project-properties.component';
import { SharedModule } from './shared/shared.module';
import { VacationModule } from './features/vacation/vacation.module';
import { CommonModule } from '@angular/common';
import { CardModule } from 'primeng/card';
import { FieldsetModule } from 'primeng/fieldset';
import { ButtonModule } from 'primeng/button';
import { PrepareVacationRequestComponent } from './features/vacation/forms/prepare-vacation-request.component';
import { CheckVacationRequestComponent } from './features/vacation/forms/check-vacation-request.component';
import { ModifyVacationRequestComponent } from './features/vacation/forms/modify-vacation-request.component';
import { DefaultTaskComponent } from './features/vacation/forms/default-task-template.component';

const CURRENT_CONFIG: AppSettings = ALANDA_CONFIG;

@NgModule({
  declarations: [
    AppComponent,
    ProjectDetailsComponent,
    ProjectPropertiesComponent,



    PrepareVacationRequestComponent,
    CheckVacationRequestComponent,
    ModifyVacationRequestComponent,
    DefaultTaskComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    LayoutModule,
    CoreModule,
    ViewsModule,
    //SharedModule,

    //VacationModule,
    CardModule,
    FieldsetModule,
    ButtonModule,

    AlandaCommonModule.forRoot(CURRENT_CONFIG)
  ],
  providers: [
    {provide: APP_CONFIG, useValue: CURRENT_CONFIG},
    {provide: AlandaProjectPropertiesService, useClass: ProjectPropertiesService },
    {provide: AlandaProjectDetailsService, useClass: ProjectDetailsService },
  ],
  entryComponents: [
    ProjectDetailsComponent,
    ProjectPropertiesComponent,


    PrepareVacationRequestComponent,
    CheckVacationRequestComponent,
    ModifyVacationRequestComponent,
    DefaultTaskComponent,
  ],
  bootstrap: [AppComponent]
})
export class AppModule {

  constructor() {
  }

  ngDoBootstrap() {

  }
}


