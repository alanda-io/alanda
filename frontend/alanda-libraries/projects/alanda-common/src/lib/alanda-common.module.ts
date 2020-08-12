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
import { BasicAuthInterceptor } from './interceptors/basic-auth.interceptor';
import { ErrorInterceptor } from './interceptors/error.interceptor';
import { TaskServiceNg } from './api/task.service';
import { PmcUserServiceNg } from './api/pmcuser.service';
import { PmcGroupServiceNg } from './api/pmcgroup.service';
import { PmcRoleServiceNg } from './api/pmcrole.service';
import { DocumentServiceNg } from './api/document.service';
import { TreeModule } from 'primeng/tree';
import { ProjectServiceNg } from './api/project.service';
import { InputSwitchModule } from 'primeng/inputswitch';
import { SplitButtonModule } from 'primeng/splitbutton';
import { MenuModule } from 'primeng/menu';
import { TabMenuModule } from 'primeng/tabmenu';
import { TableAPIServiceNg } from './services/tableAPI.service';
import { OverlayPanelModule } from 'primeng/overlaypanel';
import { CheckboxModule } from 'primeng/checkbox';
import { ToggleButtonModule } from 'primeng/togglebutton';
import { ListboxModule } from 'primeng/listbox';
import { PmcPermissionServiceNg } from './api/pmcpermission.service';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { PmcCommentServiceNg } from './api/pmccomment.service';
import { ProjectPropertiesServiceNg } from './services/project-properties.service';
import { FieldsetModule } from 'primeng/fieldset';
import { CalendarModule } from 'primeng/calendar';
import { AuthorizationServiceNg } from './services/authorization.service';
import { DatePipe, CommonModule } from '@angular/common';
import { FormsServiceNg } from './services/forms.service';
import { FormsControllerDirective } from './components/controller/directives/forms-controller.directive';
import { ProjectDetailsServiceNg } from './services/project-details.service';
import { MessageService} from 'primeng/api';
import { MessageModule } from 'primeng/message';
import { FormsRegisterService } from './services/forms-register.service';
import { AttachmentsListComponent } from './components/attachments/attachments-list/attachments-list.component';
import { AttachmentsTreeComponent } from './components/attachments/attachments-tree/attachments-tree.component';
import { ProjectMonitorComponent } from './components/project-monitor/project-monitor.component';
import { AttachmentsComponent } from './components/attachments/attachments.component';
import { MonitorValuesPipe } from './pipes/monitor-values.pipe';
import { TasklistComponent } from './components/task-list/tasklist.component';
import { FilterPipe } from './pipes/filter.pipe';
import { TagFilterPipe } from './pipes/tag-filter.pipe';
import { CommentsComponent } from './components/comments/comments.component';
import { CommentComponent } from './components/comments/comment/comment.component';
import { ProjectHeaderComponent } from './components/project-header/project-header.component';
import { ProjectPropertiesDirective } from './components/controller/directives/project.properties.directive';
import { FormsControllerComponent } from './components/controller/forms-controller/forms-controller.component';
import { ProjectsControllerComponent } from './components/controller/projects-controller/projects-controller.component';
import { ProjectDetailsDirective } from './components/project-header/directives/project-details-controller.directive';
import { PioComponent } from './components/pio/pio.component';
import { DiagramComponent } from './components/pio/diagram/diagram.component';
import { ProcessActivitiesComponent } from './components/pio/process-activities/process-activities.component';
import { HistoryGridComponent } from './components/history/history-grid.component';
import { CreateProjectComponent } from './components/create-project/create-project.component';
import { AppSettings, APP_CONFIG } from './models/appSettings';
import { PropertyService } from './api/property.service';
import { SelectComponent } from './components/task/form-variables/simple-select/simple-select.component';
import { SelectRoleComponent } from './components/task/form-variables/role-select/role-select.component';
import { DateSelectComponent } from './components/task/form-variables/date-select/date-select.component';
import { DropdownSelectComponent } from './components/task/form-variables/dropdown-select/dropdown-select.component';
import { AlandaTaskTemplateComponent } from './components/task/template/alanda-task-template.component';
import { AccordionModule } from 'primeng/accordion';
import { SelectMilestoneComponent } from './components/task/form-variables/milestone-select/milestone-select.component';
import { UserManagementComponent } from './components/admin/user-management/user-management.component';
import { GroupManagementComponent } from './components/admin/group-management/group-management.component';
import { RoleManagementComponent } from './components/admin/role-management/role-management.component';
import { PermissionManagementComponent } from './components/admin/permission-management/permission-management.component';
import { HistoryServiceNg } from './api/history.service';
import { TreeTableModule } from 'primeng/treetable';
import { AlandaPagesizeSelectComponent } from './components/pagesize-select/pagesize-select.component';
import {AlandaProjectAndProcessesComponent} from './components/project-and-processes/project-and-processes.component';
import {PapActionsComponent} from './components/project-and-processes/pap-actions/pap-actions.component';
import {RelateDialogComponent} from './components/project-and-processes/pap-actions/relate-dialog/relate-dialog.component';
import {DynamicDialogModule} from 'primeng/dynamicdialog';
import {AlandaChecklistPanelComponent} from './components/checklist-panel/checklist-panel.component';
import {AlandaChecklistComponent} from './components/checklist-panel/checklist/checklist.component';
import {AlandaChecklistMenuComponent} from './components/checklist-menu/checklist-menu.component';
import { OrderListModule } from 'primeng/orderlist';

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
    AccordionModule,
    TreeTableModule,
    DynamicDialogModule,
    OrderListModule
  ],
  declarations: [
    UserManagementComponent,
    AttachmentsListComponent,
    AttachmentsTreeComponent,
    ProjectMonitorComponent,
    AttachmentsComponent,
    MonitorValuesPipe,
    TasklistComponent,
    FilterPipe,
    TagFilterPipe,
    GroupManagementComponent,
    RoleManagementComponent,
    PermissionManagementComponent,
    CommentsComponent,
    CommentComponent,
    ProjectHeaderComponent,
    ProjectPropertiesDirective,
    FormsControllerComponent,
    ProjectsControllerComponent,
    FormsControllerDirective,
    ProjectDetailsDirective,
    PioComponent,
    DiagramComponent,
    ProcessActivitiesComponent,
    HistoryGridComponent,
    CreateProjectComponent,
    SelectComponent,
    SelectRoleComponent,
    DateSelectComponent,
    DropdownSelectComponent,
    SelectMilestoneComponent,
    AlandaTaskTemplateComponent,
    AlandaProjectAndProcessesComponent,
    AlandaPagesizeSelectComponent,
    PapActionsComponent,
    RelateDialogComponent,
    AlandaChecklistPanelComponent,
    AlandaChecklistComponent,
    AlandaChecklistMenuComponent
  ],
  exports: [
    ProjectMonitorComponent,
    AttachmentsComponent,
    AttachmentsListComponent,
    AttachmentsTreeComponent,
    MonitorValuesPipe,
    TasklistComponent,
    FilterPipe,
    TagFilterPipe,
    UserManagementComponent,
    RoleManagementComponent,
    GroupManagementComponent,
    PermissionManagementComponent,
    CommentsComponent,
    CommentComponent,
    ProjectHeaderComponent,
    ProjectPropertiesDirective,
    FormsControllerComponent,
    ProjectsControllerComponent,
    FormsControllerDirective,
    ProjectDetailsDirective,
    PioComponent,
    DiagramComponent,
    ProcessActivitiesComponent,
    HistoryGridComponent,
    CreateProjectComponent,
    SelectComponent,
    SelectRoleComponent,
    SelectMilestoneComponent,
    DateSelectComponent,
    DropdownSelectComponent,
    AlandaTaskTemplateComponent,
    AlandaProjectAndProcessesComponent,
    PapActionsComponent,
    RelateDialogComponent,
    AlandaChecklistPanelComponent,
    AlandaChecklistComponent,
    AlandaChecklistMenuComponent
   ],
  entryComponents: [
    RelateDialogComponent
  ]
})
export class AlandaCommonModule {

  public static forRoot(config: AppSettings): ModuleWithProviders {
    return {
      ngModule: AlandaCommonModule,
      providers: [
        TaskServiceNg,
        PmcUserServiceNg,
        PmcGroupServiceNg,
        PmcRoleServiceNg,
        PmcPermissionServiceNg,
        ProjectServiceNg,
        DocumentServiceNg,
        BasicAuthInterceptor,
        ErrorInterceptor,
        TableAPIServiceNg,
        PmcCommentServiceNg,
        ProjectPropertiesServiceNg,
        AuthorizationServiceNg,
        DatePipe,
        FormsServiceNg,
        ProjectDetailsServiceNg,
        MessageService,
        PropertyService,
        FormsRegisterService,
        HistoryServiceNg,
        {
          provide: APP_CONFIG,
          useValue: config
        },
      ]
    }
  }

}
