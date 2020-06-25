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
import {
  AppSettings,
  APP_CONFIG,
  AlandaCommonModule,
  AlandaProjectPropertiesService,
  PermissionModule
} from '@alanda-libraries/common';
import { ALANDA_CONFIG } from './app.settings';
import { ProjectPropertiesService } from './core/services/projectproperties.service';
import { ProjectDetailsComponent } from './components/project-details/project-details.component';
import { ProjectPropertiesComponent } from './components/project-properties/project-properties.component';
import { SharedModule } from './shared/shared.module';
import { CardModule } from 'primeng/card';
import { FieldsetModule } from 'primeng/fieldset';
import { ButtonModule } from 'primeng/button';
import { ReactiveFormsModule } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { PermissionsDemoComponent } from './components/permissions-demo/permissions-demo.component';

const CURRENT_CONFIG: AppSettings = ALANDA_CONFIG;

@NgModule({
  declarations: [
    AppComponent,
    ProjectDetailsComponent,
    ProjectPropertiesComponent,
    PermissionsDemoComponent,
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
    PermissionModule,
    AlandaCommonModule.forRoot(CURRENT_CONFIG),
  ],
  providers: [
    { provide: APP_CONFIG, useValue: CURRENT_CONFIG },
    {
      provide: AlandaProjectPropertiesService,
      useClass: ProjectPropertiesService,
    },
    MessageService,
  ],
  bootstrap: [AppComponent],
})
export class AppModule {
  constructor() {}

  ngDoBootstrap() {}
}
