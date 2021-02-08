import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AppComponent } from './app.component';
import { TaskListModule } from './features/task-list/task-list.module';
import { ProjectMonitorModule } from './features/project-monitor/project-monitor.module';

/**
 * Add here your alanda-common components to test them independently from
 * the client project
 */

import { AppRoutingModule } from './app-routing.module';
import { CoreModule } from './core/core.module';
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
import { RxState } from '@rx-angular/state';
import { DropdownModule } from 'primeng/dropdown';
import { UserEnrichedProjectsControllerComponent } from './components/projects-controller/user-enriched-projects-controller.component';
import { TabViewModule } from 'primeng/tabview';
import { ToastModule } from 'primeng/toast';
import { ProjectsAndProcessesDemoComponent } from './components/projects-and-processes-demo/projects-and-processes-demo.component';
import { HomeModule } from './features/home/home.module';
import { UserEnrichedFormsControllerComponent } from './components/forms-controller/user-enriched-forms-controller.component';
import { ProgressSpinnerModule } from 'primeng/progressspinner';

const CURRENT_CONFIG: AppSettings = ALANDA_CONFIG;

@NgModule({
  declarations: [
    AppComponent,
    ProjectDetailsComponent,
    PermissionsDemoComponent,
    UserManagementContainerComponent,
    UserEnrichedProjectsControllerComponent,
    UserEnrichedFormsControllerComponent,
    ProjectsAndProcessesDemoComponent,
  ],
  imports: [
    CommonModule,
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    CoreModule,
    HomeModule,
    SharedModule,
    ReactiveFormsModule,
    CardModule,
    FieldsetModule,
    ButtonModule,
    ToastModule,
    PermissionModule,
    CommentsModule,
    AlandaCommonModule.forRoot(CURRENT_CONFIG),
    TemplateModule,
    DropdownModule,
    TabViewModule,
    TaskListModule,
    ProjectMonitorModule,
    ProgressSpinnerModule,
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
