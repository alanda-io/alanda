import { NgModule, ModuleWithProviders } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { DropdownModule } from 'primeng/dropdown';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { FileUploadModule } from 'primeng/fileupload';
import { DialogModule } from 'primeng/dialog';
import { PanelModule } from 'primeng/panel';
import { ButtonModule} from 'primeng/button';
import { CardModule } from 'primeng/card';
import { LightboxModule } from 'primeng/lightbox';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { MultiSelectModule } from 'primeng/multiselect';
import { ToastModule } from 'primeng/toast';
import { InputTextModule } from 'primeng/inputtext';
import { TabViewModule } from 'primeng/tabview';
import { PickListModule } from 'primeng/picklist';
import { ScrollPanelModule } from 'primeng/scrollpanel';
import { HttpClientModule } from '@angular/common/http';
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
import { CommonModule } from '@angular/common';
import { FormsControllerDirective } from './components/controller/directives/forms-controller.directive';
import { MessageModule } from 'primeng/message';
import { AttachmentsListComponent } from './components/attachments/attachments-list/attachments-list.component';
import { AttachmentsTreeComponent } from './components/attachments/attachments-tree/attachments-tree.component';
import { AlandaProjectMonitorComponent } from './components/project-monitor/project-monitor.component';
import { MonitorValuesPipe } from './pipes/nested-object.pipe';
import { AlandaTasklistComponent } from './components/task-list/tasklist.component';
import { FilterPipe } from './pipes/filter.pipe';
import { TagFilterPipe } from './pipes/tag-filter.pipe';
import { AlandaCommentsComponent } from './components/comments/comments.component';
import { AlandaCommentComponent } from './components/comments/comment/comment.component';
import { AlandaProjectHeaderComponent } from './components/project-header/project-header.component';
import { ProjectPropertiesDirective } from './components/controller/directives/project.properties.directive';
import { AlandaPioComponent } from './components/pio/pio.component';
import { DiagramComponent } from './components/pio/diagram/diagram.component';
import { ProcessActivitiesComponent } from './components/pio/process-activities/process-activities.component';
import { AlandaHistoryGridComponent } from './components/history/history-grid.component';
import { AlandaCreateProjectComponent } from './components/create-project/create-project.component';
import { AlandaProjectAndProcessesComponent } from './components/project-and-processes/project-and-processes.component';
import { ProjectControlItemComponent } from './components/project-and-processes/projectControlItem/project-control-item.component';
import { AppSettings, APP_CONFIG } from './models/appSettings';
import { AlandaSelectComponent } from './components/task/form-variables/simple-select/simple-select.component';
import { AlandaSelectRoleComponent } from './components/task/form-variables/role-select/role-select.component';
import { AlandaDateSelectComponent } from './components/task/form-variables/date-select/date-select.component';
import { AlandaDropdownSelectComponent } from './components/task/form-variables/dropdown-select/dropdown-select.component';
import { AlandaTaskComponent } from './components/task/alanda-task.component';
import { AccordionModule } from 'primeng/accordion'
import { AlandaSelectMilestoneComponent } from './components/task/form-variables/milestone-select/milestone-select.component';
import { AlandaUserManagementComponent } from './components/admin/user-management/user-management.component';
import { AlandaGroupManagementComponent } from './components/admin/group-management/group-management.component';
import { AlandaRoleManagementComponent } from './components/admin/role-management/role-management.component';
import { AlandaPermissionManagementComponent } from './components/admin/permission-management/permission-management.component';
import { AlandaUserService } from './api/alandaUser.service';
import { AlandaTaskService } from './api/alandaTask.service';
import { AlandaCommentService } from './api/alandaComment.service';
import { AlandaDocumentService } from './api/alandaDocument.service';
import { AlandaGroupService } from './api/alandaGroup.service';
import { AlandaHistoryService } from './api/alandaHistory.service';
import { AlandaMilestoneService } from './api/alandaMilestone.service';
import { AlandaPermissionService } from './api/alandaPermission.service';
import { AlandaProcessService } from './api/alandaProcess.service';
import { AlandaProjectService } from './api/alandaProject.service';
import { AlandaPropertyService } from './api/alandaProperty.service';
import { AlandaRoleService } from './api/alandaRole.service';
import { AlandaAuthorizationService } from './services/alandaAuthorization.service';
import { AlandaExceptionHandlingService } from './services/alandaExceptionHandling.service';
import { AlandaMonitorAPIService } from './services/alandaMonitorApi.service';
import { AlandaProjectPropertiesService } from './services/alandaProjectProperties.service';
import { AlandaAttachmentsComponent } from './components/attachments/attachments.component';
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
    MessageModule,
    AccordionModule
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
    AlandaCommentsComponent,
    AlandaCommentComponent,
    AlandaProjectHeaderComponent,
    ProjectPropertiesDirective,
    FormsControllerDirective,
    AlandaPioComponent,
    DiagramComponent,
    ProcessActivitiesComponent,
    AlandaHistoryGridComponent,
    AlandaCreateProjectComponent,
    AlandaProjectAndProcessesComponent,
    ProjectControlItemComponent,
    AlandaSelectComponent,
    AlandaSelectRoleComponent,
    AlandaDateSelectComponent,
    AlandaDropdownSelectComponent,
    AlandaSelectMilestoneComponent,
    AlandaTaskComponent,
  ],
  exports: [
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
    AlandaCommentsComponent,
    AlandaCommentComponent,
    AlandaProjectHeaderComponent,
    ProjectPropertiesDirective,
    FormsControllerDirective,
    AlandaPioComponent,
    DiagramComponent,
    ProcessActivitiesComponent,
    AlandaHistoryGridComponent,
    AlandaCreateProjectComponent,
    AlandaProjectAndProcessesComponent,
    ProjectControlItemComponent,
    AlandaSelectComponent,
    AlandaSelectRoleComponent,
    AlandaSelectMilestoneComponent,
    AlandaDateSelectComponent,
    AlandaDropdownSelectComponent,
    AlandaTaskComponent
   ],
  entryComponents: []
})
export class AlandaCommonModule {

  public static forRoot(config: AppSettings): ModuleWithProviders {
    return {
      ngModule: AlandaCommonModule,
      providers: [
        AlandaUserService,
        AlandaTaskService,
        AlandaCommentService,
        AlandaDocumentService,
        AlandaGroupService,
        AlandaHistoryService,
        AlandaMilestoneService,
        AlandaPermissionService,
        AlandaProcessService,
        AlandaProjectService,
        AlandaPropertyService,
        AlandaRoleService,
        AlandaAuthorizationService,
        AlandaExceptionHandlingService,
        AlandaMonitorAPIService,
        AlandaProjectPropertiesService,
        {
          provide: APP_CONFIG,
          useValue: config
        },
      ]
    };
  }

}
