import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AppComponent } from './app.component';
import { TaskListModule } from './features/task-list/task-list.module';
import { ProjectMonitorModule } from './features/project-monitor/project-monitor.module';
import { AppRoutingModule } from './app-routing.module';
import { AppSettings, APP_CONFIG, AlandaCommonModule } from '@alanda/common';
import { ALANDA_CONFIG } from './app.settings';
import { MessageService } from 'primeng/api';
import { PermissionsDemoComponent } from './components/permissions-demo/permissions-demo.component';
import { RxState } from '@rx-angular/state';
import { ToastModule } from 'primeng/toast';
import { ProjectsAndProcessesDemoComponent } from './components/projects-and-processes-demo/projects-and-processes-demo.component';
import { HomeModule } from './features/home/home.module';
import { ProjectsControllerModule } from './components/projects-controller/projects-controller.module';
import { FormsControllerModule } from './components/forms-controller/forms-controller.module';

const CURRENT_CONFIG: AppSettings = ALANDA_CONFIG;

@NgModule({
  declarations: [
    AppComponent,
    PermissionsDemoComponent,
    ProjectsAndProcessesDemoComponent,
  ],
  imports: [
    CommonModule,
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    AlandaCommonModule.forRoot(CURRENT_CONFIG),
    HomeModule,
    ToastModule,
    TaskListModule,
    ProjectMonitorModule,
    ProjectsControllerModule,
    FormsControllerModule,
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
