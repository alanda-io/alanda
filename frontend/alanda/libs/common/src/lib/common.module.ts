import { CardModule } from 'primeng/card';
import { DropdownModule } from 'primeng/dropdown';
import { CalendarModule } from 'primeng/calendar';
import { CheckboxModule } from 'primeng/checkbox';
import { MonitorValuesPipeModule } from './pipes/nested-object.pipe';
import { TaskTableModule } from './components/task-table/task-table.module';
import { TagFilterPipeModule } from './pipes/tag-filter.pipe';
import { ProjectHeaderModule } from './components/project-header/project-header.module';
import { AppSettings, APP_CONFIG } from './models/appSettings';
import { AlandaSimpleSelectComponent } from './components/task/form-variables/simple-select/simple-select.component';
import { AlandaSelectRoleComponent } from './components/task/form-variables/role-select/role-select.component';
import { AlandaDateSelectComponent } from './components/task/form-variables/date-select/date-select.component';
import { AlandaDropdownSelectComponent } from './components/task/form-variables/dropdown-select/dropdown-select.component';
import { AlandaSelectMilestoneComponent } from './components/task/form-variables/milestone-select/milestone-select.component';
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
import { AlandaProjectsControllerComponent } from './components/controller/projects-controller/projects-controller.component';
import { AlandaProjectPropertiesService } from './services/project-properties.service';
import { NgModule, ModuleWithProviders } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Error500Interceptor } from './interceptors/error500.interceptor';
import { AlandaFormsControllerComponent } from './form/forms-controller/forms-controller.component';
import { AlandaVarSelectComponent } from './form/components/var-select/var-select.component';
import { AlandaPropSelectComponent } from './form/components/prop-select/prop-select.component';
import { PermissionModule } from './permissions/permission.module';
import { AlandaVarRoleUserSelectComponent } from './form/components/var-role-user-select/var-role-user-select.component';
import { AlandaVarDisplayComponent } from './form/components/var-display/var-display.component';
import { AlandaPropCheckboxComponent } from './form/components/prop-checkbox/prop-checkbox.component';
import { CommentsModule } from './components/comments/comments.module';
import { BadgeModule } from './components/badge/badge.module';
import { DirectivesModule } from './directives/directives.module';
import { AlandaProcessConfigModalService } from './services/process-config-modal.service';
import { PapRelateDialogComponent } from './components/project-and-processes/pap-relate-dialog/pap-relate-dialog.component';
import { PapConfigDialogComponent } from './components/project-and-processes/pap-config-dialog/pap-config-dialog.component';
import { PhaseTabModule } from './components/phase-tab/phase-tab.module';
import { AlandaVarCheckboxComponent } from './form/components/var-checkbox/var-checkbox.component';
import { AlandaProjectAndProcessesService } from './components/project-and-processes/project-and-processes.service';
import { AlandaProcessMessageApiService } from './api/processMessageApi.service';
import { AlandaVarDatepickerComponent } from './form/components/var-datepicker/var-datepicker.component';
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

@NgModule({
  imports: [
    CardModule,
    DropdownModule,
    CalendarModule,
    CheckboxModule,
    CommonModule,
    HttpClientModule,
    FormsModule,
    BadgeModule,
    ReactiveFormsModule,
    PhaseTabModule,
    PermissionModule,
    ProjectTableModule,
    MonitorValuesPipeModule,
    TaskTableModule,
    PageSizeSelectModule,
    FilterPipeModule,
    AttachmentsModule,
    ProjectHeaderModule,
    DirectivesModule,
    PioModule,
    HistoryGridModule,
    CreateProjectModule,
    AdminModule,
    TagFilterPipeModule,
    ProjectAndProcessesModule,
  ],
  declarations: [
    AlandaFormsControllerComponent,
    AlandaSimpleSelectComponent,
    AlandaSelectRoleComponent,
    AlandaDateSelectComponent,
    AlandaDropdownSelectComponent,
    AlandaSelectMilestoneComponent,
    AlandaProjectsControllerComponent,
    AlandaVarSelectComponent,
    AlandaPropSelectComponent,
    AlandaPropCheckboxComponent,
    AlandaVarRoleUserSelectComponent,
    AlandaVarDisplayComponent,
    AlandaVarCheckboxComponent,
    AlandaVarDatepickerComponent,
  ],
  exports: [
    PermissionModule,
    BadgeModule,
    CommentsModule,
    ProjectTableModule,
    MonitorValuesPipeModule,
    TaskTableModule,
    PageSizeSelectModule,
    FilterPipeModule,
    AlandaSimpleSelectComponent,
    AlandaSelectRoleComponent,
    AlandaSelectMilestoneComponent,
    AlandaDateSelectComponent,
    AlandaDropdownSelectComponent,
    AlandaProjectsControllerComponent,
    AlandaVarSelectComponent,
    AlandaPropSelectComponent,
    AlandaPropCheckboxComponent,
    AlandaVarRoleUserSelectComponent,
    AlandaVarDisplayComponent,
    PhaseTabModule,
    AlandaVarCheckboxComponent,
    AlandaVarDatepickerComponent,
    HeaderModule,
    AttachmentsModule,
    ProjectHeaderModule,
    DirectivesModule,
    PioModule,
    HistoryGridModule,
    CreateProjectModule,
    AdminModule,
    TagFilterPipeModule,
    ProjectAndProcessesModule,
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
