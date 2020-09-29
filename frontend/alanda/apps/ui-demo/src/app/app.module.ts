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
import { CardModule } from 'primeng/card';
import { FieldsetModule } from 'primeng/fieldset';
import { ButtonModule } from 'primeng/button';
import { ReactiveFormsModule } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { PermissionsDemoComponent } from './components/permissions-demo/permissions-demo.component';
import { TemplateModule } from '@rx-angular/template';
import { RxState } from '@rx-angular/state';
import { DropdownModule } from 'primeng/dropdown';
import { UserEnrichedProjectsControllerComponent } from './components/projects-controller/user-enriched-projects-controller.component';
import { TabViewModule } from 'primeng/tabview';
import { UserManagementModule } from './features/user-management/user-management.module';

const CURRENT_CONFIG: AppSettings = ALANDA_CONFIG;

@NgModule({
  declarations: [
    AppComponent,
    ProjectDetailsComponent,
    PermissionsDemoComponent,
    UserEnrichedProjectsControllerComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    CoreModule,
    ViewsModule,
    ReactiveFormsModule,
    CardModule,
    FieldsetModule,
    ButtonModule,
    PermissionModule,
    CommentsModule,
    AlandaCommonModule.forRoot(CURRENT_CONFIG),
    TemplateModule,
    DropdownModule,
    TabViewModule,
    UserManagementModule,
  ],
  providers: [
    { provide: APP_CONFIG, useValue: CURRENT_CONFIG },
    MessageService,
    RxState,
  ],
  bootstrap: [AppComponent],
})
export class AppModule {
  constructor() {}
}
