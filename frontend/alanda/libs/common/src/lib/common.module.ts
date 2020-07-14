import { TableModule } from 'primeng/table';
import { DropdownModule } from 'primeng/dropdown';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { FileUploadModule } from 'primeng/fileupload';
import { DialogModule } from 'primeng/dialog';
import { PanelModule } from 'primeng/panel';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { LightboxModule } from 'primeng/lightbox';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { MultiSelectModule } from 'primeng/multiselect';
import { ToastModule } from 'primeng/toast';
import { InputTextModule } from 'primeng/inputtext';
import { TabViewModule } from 'primeng/tabview';
import { PickListModule } from 'primeng/picklist';
import { ScrollPanelModule } from 'primeng/scrollpanel';
import { TreeModule } from 'primeng/tree';
import { InputSwitchModule } from 'primeng/inputswitch';
import { SplitButtonModule } from 'primeng/splitbutton';
import { MenuModule } from 'primeng/menu';
import { TabMenuModule } from 'primeng/tabmenu';
import { OverlayPanelModule } from 'primeng/overlaypanel';
import { CheckboxModule } from 'primeng/checkbox';
import { ToggleButtonModule } from 'primeng/togglebutton';
import { ListboxModule } from 'primeng/listbox';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { FieldsetModule } from 'primeng/fieldset';
import { CalendarModule } from 'primeng/calendar';
import { MessageModule } from 'primeng/message';
import { AttachmentsListComponent } from './components/attachments/attachments-list/attachments-list.component';
import { AttachmentsTreeComponent } from './components/attachments/attachments-tree/attachments-tree.component';
import { AlandaProjectMonitorComponent } from './components/project-monitor/project-monitor.component';
import { MonitorValuesPipe } from './pipes/nested-object.pipe';
import { AlandaTasklistComponent } from './components/task-list/tasklist.component';
import { FilterPipe } from './pipes/filter.pipe';
import { TagFilterPipe } from './pipes/tag-filter.pipe';
import { AlandaProjectHeaderComponent } from './components/project-header/project-header.component';
import { ProjectPropertiesDirective } from './components/controller/directives/project.properties.directive';
import { AlandaPioComponent } from './components/pio/pio.component';
import { DiagramComponent } from './components/pio/diagram/diagram.component';
import { ProcessActivitiesComponent } from './components/pio/process-activities/process-activities.component';
import { AlandaHistoryGridComponent } from './components/history/history-grid.component';
import { AlandaCreateProjectComponent } from './components/create-project/create-project.component';
import { AlandaProjectAndProcessesComponent } from './components/project-and-processes/project-and-processes.component';
import { AppSettings, APP_CONFIG } from './models/appSettings';
import { AlandaSimpleSelectComponent } from './components/task/form-variables/simple-select/simple-select.component';
import { AlandaSelectRoleComponent } from './components/task/form-variables/role-select/role-select.component';
import { AlandaDateSelectComponent } from './components/task/form-variables/date-select/date-select.component';
import { AlandaDropdownSelectComponent } from './components/task/form-variables/dropdown-select/dropdown-select.component';
import { AccordionModule } from 'primeng/accordion';
import { AlandaSelectMilestoneComponent } from './components/task/form-variables/milestone-select/milestone-select.component';
import { AlandaUserManagementComponent } from './components/admin/user-management/user-management.component';
import { AlandaGroupManagementComponent } from './components/admin/group-management/group-management.component';
import { AlandaRoleManagementComponent } from './components/admin/role-management/role-management.component';
import { AlandaPermissionManagementComponent } from './components/admin/permission-management/permission-management.component';
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
import { AlandaMonitorAPIService } from './services/monitorApi.service';
import { AlandaAttachmentsComponent } from './components/attachments/attachments.component';
import { AlandaProjectsControllerComponent } from './components/controller/projects-controller/projects-controller.component';
import { AlandaProjectPropertiesService } from './services/project-properties.service';
import { TreeTableModule } from 'primeng/treetable';
import { NgModule, ModuleWithProviders } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MessagesModule } from 'primeng/messages';
import { Error500Interceptor } from './interceptors/error500.interceptor';
import { AlandaFormsControllerComponent } from './form/forms-controller/forms-controller.component';
import { AlandaVarSelectComponent } from './form/components/var-select/var-select.component';
import { AlandaPropSelectComponent } from './form/components/prop-select/prop-select.component';
import { DynamicDialogModule } from 'primeng/dynamicdialog';
import { PapActionsComponent } from './components/project-and-processes/pap-actions/pap-actions.component';
import { RelateDialogComponent } from './components/project-and-processes/pap-actions/relate-dialog/relate-dialog.component';
import { PermissionModule } from './permissions/permission.module';
import { AlandaVarRoleUserSelectComponent } from './form/components/var-role-user-select/var-role-user-select.component';
import { AlandaVarDisplayComponent } from './form/components/var-display/var-display.component';
import { AlandaPropCheckboxComponent } from './form/components/prop-checkbox/prop-checkbox.component';
import { CommentsModule } from './components/comments/comments.module';
import { BadgeModule } from './components/badge/badge.module';

@NgModule({
  imports: [
    CommonModule,
    HttpClientModule,
    FormsModule,
    TableModule,
    DropdownModule,
    AutoCompleteModule,
    FileUploadModule,
    DialogModule,
    PanelModule,
    CardModule,
    LightboxModule,
    ButtonModule,
    ProgressSpinnerModule,
    MultiSelectModule,
    InputTextModule,
    ToastModule,
    TabViewModule,
    PickListModule,
    ScrollPanelModule,
    TreeModule,
    InputSwitchModule,
    SplitButtonModule,
    MenuModule,
    TabMenuModule,
    OverlayPanelModule,
    CheckboxModule,
    ReactiveFormsModule,
    ToggleButtonModule,
    ListboxModule,
    InputTextareaModule,
    FieldsetModule,
    CalendarModule,
    MessagesModule,
    AccordionModule,
    TreeTableModule,
    MessageModule,
    DynamicDialogModule,
    BadgeModule,
  ],
  declarations: [
    AlandaUserManagementComponent,
    AttachmentsListComponent,
    AttachmentsTreeComponent,
    AlandaProjectMonitorComponent,
    AlandaAttachmentsComponent,
    MonitorValuesPipe,
    AlandaTasklistComponent,
    FilterPipe,
    TagFilterPipe,
    AlandaGroupManagementComponent,
    AlandaRoleManagementComponent,
    AlandaPermissionManagementComponent,
    AlandaProjectHeaderComponent,
    ProjectPropertiesDirective,
    AlandaFormsControllerComponent,
    AlandaPioComponent,
    DiagramComponent,
    ProcessActivitiesComponent,
    AlandaHistoryGridComponent,
    AlandaCreateProjectComponent,
    AlandaSimpleSelectComponent,
    AlandaSelectRoleComponent,
    AlandaDateSelectComponent,
    AlandaDropdownSelectComponent,
    AlandaSelectMilestoneComponent,
    AlandaProjectsControllerComponent,
    AlandaProjectAndProcessesComponent,
    PapActionsComponent,
    RelateDialogComponent,
    AlandaVarSelectComponent,
    AlandaPropSelectComponent,
    AlandaPropCheckboxComponent,
    AlandaProjectAndProcessesComponent,
    AlandaVarRoleUserSelectComponent,
    AlandaVarDisplayComponent,
  ],
  exports: [
    PermissionModule,
    BadgeModule,
    CommentsModule,
    AlandaProjectMonitorComponent,
    AlandaAttachmentsComponent,
    AttachmentsListComponent,
    AttachmentsTreeComponent,
    MonitorValuesPipe,
    AlandaTasklistComponent,
    FilterPipe,
    TagFilterPipe,
    AlandaUserManagementComponent,
    AlandaRoleManagementComponent,
    AlandaGroupManagementComponent,
    AlandaPermissionManagementComponent,
    AlandaProjectHeaderComponent,
    ProjectPropertiesDirective,
    AlandaPioComponent,
    DiagramComponent,
    ProcessActivitiesComponent,
    AlandaHistoryGridComponent,
    AlandaCreateProjectComponent,
    AlandaSimpleSelectComponent,
    AlandaSelectRoleComponent,
    AlandaSelectMilestoneComponent,
    AlandaDateSelectComponent,
    AlandaDropdownSelectComponent,
    AlandaProjectsControllerComponent,
    AlandaProjectAndProcessesComponent,
    PapActionsComponent,
    RelateDialogComponent,
    AlandaVarSelectComponent,
    AlandaPropSelectComponent,
    AlandaPropCheckboxComponent,
    AlandaVarRoleUserSelectComponent,
    AlandaVarDisplayComponent,
  ],
  entryComponents: [
    RelateDialogComponent,
    AlandaVarSelectComponent,
    AlandaPropSelectComponent,
  ],
})
export class AlandaCommonModule {
  public static forRoot(config: AppSettings): ModuleWithProviders {
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
        AlandaPermissionApiService,
        AlandaProcessApiService,
        AlandaProjectApiService,
        AlandaPropertyApiService,
        AlandaRoleApiService,
        AlandaExceptionHandlingService,
        AlandaMonitorAPIService,
        AlandaProjectPropertiesService,
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
