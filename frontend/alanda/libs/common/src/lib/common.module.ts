import { MonitorValuesPipeModule } from './pipes/nested-object.pipe';
import { TaskTableModule } from './components/task-table/task-table.module';
import { TagFilterPipeModule } from './pipes/tag-filter.pipe';
import { ProjectHeaderModule } from './components/project-header/project-header.module';
import { AppSettings, APP_CONFIG } from './models/appSettings';
import { AlandaUserApiService } from './api/userApi.service';
import { AlandaTaskApiService } from './api/taskApi.service';
import { AlandaCommentApiService } from './api/commentApi.service';
import { AlandaDocumentApiService } from './api/documentApi.service';
import { AlandaGroupApiService } from './api/groupApi.service';
import { AlandaHistoryApiService } from './api/historyApi.service';
import { AlandaMilestoneApiService } from './api/milestoneApi.service';
import { AlandaPermissionApiService } from './api/permissionApi.service';
import { AlandaProcessApiService } from './api/processApi.service';
import { AlandaProjectApiService } from './api/projectApi.service';
import { AlandaPropertyApiService } from './api/propertyApi.service';
import { AlandaRoleApiService } from './api/roleApi.service';
import { AlandaExceptionHandlingService } from './services/exceptionHandling.service';
import { AlandaProjectPropertiesService } from './services/project-properties.service';
import { NgModule, ModuleWithProviders } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Error500Interceptor } from './interceptors/error500.interceptor';
import { AlandaVarSelectComponent } from './form/components/var-select/var-select.component';
import { AlandaPropSelectComponent } from './form/components/prop-select/prop-select.component';
import { PermissionModule } from './permissions/permission.module';
import { CommentsModule } from './components/comments/comments.module';
import { BadgeModule } from './components/badge/badge.module';
import { DirectivesModule } from './directives/directives.module';
import { AlandaProcessConfigModalService } from './services/process-config-modal.service';
import { PapRelateDialogComponent } from './components/project-and-processes/pap-relate-dialog/pap-relate-dialog.component';
import { PapConfigDialogComponent } from './components/project-and-processes/pap-config-dialog/pap-config-dialog.component';
import { PhaseTabModule } from './components/phase-tab/phase-tab.module';
import { AlandaProjectAndProcessesService } from './components/project-and-processes/project-and-processes.service';
import { AlandaProcessMessageApiService } from './api/processMessageApi.service';
import { HeaderModule } from './components/header/header.module';
import { AlandaTitleService } from './services/title.service';
import { PapReasonDialogComponent } from './components/project-and-processes/pap-reason-dialog/pap-reason-dialog.component';
import { ProjectTableModule } from './components/project-table/project-table.module';
import { FilterPipeModule } from './pipes/filter.pipe';
import { PageSizeSelectModule } from './components/page-size-select/page-size-select.module';
import { AttachmentsModule } from './components/attachments/attachments.module';
import { PioModule } from './components/pio/pio.module';
import { HistoryGridModule } from './components/history/history-grid.module';
import { CreateProjectModule } from './components/create-project/create-project.module';
import { AdminModule } from './components/admin/admin.module';
import { ProjectAndProcessesModule } from './components/project-and-processes/project-and-processes.module';
import { DateSelectModule } from './components/date-select/date-select.module';
import { DropdownSelectModule } from './components/dropdown-select/dropdown-select.module';
import { RoleSelectModule } from './components/role-select/role-select.module';
import { SimpleSelectModule } from './components/simple-select/simple-select.module';
import { FormModule } from './form/form.module';
import { ProjectsControllerModule } from './components/controller/projects-controller/projects-controller.module';
import { MilestoneSelectModule } from './components/milestone-select/milestone-select.module';
import { SnoozeModule } from './components/snooze/snooze.module';
import { LabelModule } from './components/label/label.module';
import { CommentDialogModule } from './components/comment-dialog/comment-dialog.module';

@NgModule({
  imports: [
    CommonModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    BadgeModule,
    PhaseTabModule,
    PermissionModule,
    ProjectTableModule,
    MonitorValuesPipeModule,
    TaskTableModule,
    PageSizeSelectModule,
    FilterPipeModule,
    AttachmentsModule,
    CommentsModule,
    ProjectHeaderModule,
    DirectivesModule,
    PioModule,
    HistoryGridModule,
    CreateProjectModule,
    AdminModule,
    TagFilterPipeModule,
    ProjectAndProcessesModule,
    DateSelectModule,
    DropdownSelectModule,
    RoleSelectModule,
    SimpleSelectModule,
    FormModule,
    ProjectsControllerModule,
    MilestoneSelectModule,
    SnoozeModule,
    LabelModule,
    CommentDialogModule,
  ],
  exports: [
    BadgeModule,
    PhaseTabModule,
    PermissionModule,
    ProjectTableModule,
    MonitorValuesPipeModule,
    TaskTableModule,
    PageSizeSelectModule,
    FilterPipeModule,
    AttachmentsModule,
    CommentsModule,
    HeaderModule,
    ProjectHeaderModule,
    DirectivesModule,
    PioModule,
    HistoryGridModule,
    CreateProjectModule,
    AdminModule,
    TagFilterPipeModule,
    ProjectAndProcessesModule,
    DateSelectModule,
    DropdownSelectModule,
    RoleSelectModule,
    SimpleSelectModule,
    FormModule,
    ProjectsControllerModule,
    MilestoneSelectModule,
    SnoozeModule,
    LabelModule,
    CommentDialogModule
  ],
  entryComponents: [
    PapRelateDialogComponent,
    PapReasonDialogComponent,
    AlandaVarSelectComponent,
    AlandaPropSelectComponent,
    PapConfigDialogComponent,
  ],
})
export class AlandaCommonModule {
  public static forRoot(
    config: AppSettings,
  ): ModuleWithProviders<AlandaCommonModule> {
    return {
      ngModule: AlandaCommonModule,
      providers: [
        AlandaUserApiService,
        AlandaTaskApiService,
        AlandaCommentApiService,
        AlandaDocumentApiService,
        AlandaGroupApiService,
        AlandaHistoryApiService,
        AlandaMilestoneApiService,
        AlandaProcessMessageApiService,
        AlandaPermissionApiService,
        AlandaProcessApiService,
        AlandaProjectApiService,
        AlandaPropertyApiService,
        AlandaRoleApiService,
        AlandaExceptionHandlingService,
        AlandaProjectPropertiesService,
        AlandaProcessConfigModalService,
        AlandaProjectAndProcessesService,
        AlandaTitleService,
        Error500Interceptor,
        {
          provide: APP_CONFIG,
          useValue: config,
        },
        DatePipe,
      ],
    };
  }
}
