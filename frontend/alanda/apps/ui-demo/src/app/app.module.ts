import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';
import { AppComponent } from './app.component';

/**
 * Add here your alanda-common components to test them independently from
 * the client project
 */

import { AppRoutingModule } from './app-routing.module';
import { CoreModule } from './core/core.module';
import { ViewsModule } from './views/views.module';
import {
  AppSettings,
  APP_CONFIG,
  AlandaCommonModule,
  PermissionModule,
  CommentsModule,
} from '@alanda/common';
import { ALANDA_CONFIG } from './app.settings';
import { ProjectDetailsComponent } from './components/project-details/project-details.component';
import { SharedModule } from './shared/shared.module';
import { CardModule } from 'primeng/card';
import { FieldsetModule } from 'primeng/fieldset';
import { ButtonModule } from 'primeng/button';
import { ReactiveFormsModule } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { PermissionsDemoComponent } from './components/permissions-demo/permissions-demo.component';
import { TemplateModule } from '@rx-angular/template';
import { UserManagementContainerComponent } from './features/usermgmt/user-management-container/user-management-container.component';

const CURRENT_CONFIG: AppSettings = ALANDA_CONFIG;

@NgModule({
  declarations: [
    AppComponent,
    ProjectDetailsComponent,
    PermissionsDemoComponent,
    UserManagementContainerComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    CoreModule,
    ViewsModule,
    SharedModule,
    ReactiveFormsModule,
    CardModule,
    FieldsetModule,
    ButtonModule,
    PermissionModule,
    CommentsModule,
    AlandaCommonModule.forRoot(CURRENT_CONFIG),
    TemplateModule,
  ],
  providers: [
    { provide: APP_CONFIG, useValue: CURRENT_CONFIG },
    MessageService,
  ],
  bootstrap: [AppComponent],
})
export class AppModule {
  constructor() {}
}
