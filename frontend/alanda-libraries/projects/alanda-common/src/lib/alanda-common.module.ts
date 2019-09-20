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
import { BasicAuthInterceptor } from './core/interceptors/basic-auth.interceptor';
import { ErrorInterceptor } from './core/interceptors/error.interceptor';
import { TaskServiceNg } from './core/api/task.service';
import { PmcUserServiceNg } from './core/api/pmcuser.service';
import { PmcGroupServiceNg } from './core/api/pmcgroup.service';
import { PmcRoleServiceNg } from './core/api/pmcrole.service';
import { DocumentServiceNg } from './core/api/document.service';
import { TreeModule } from 'primeng/tree';
import { ProjectServiceNg } from './core/api/project.service';
import { InputSwitchModule } from 'primeng/inputswitch';
import { SplitButtonModule } from 'primeng/splitbutton';
import { MenuModule } from 'primeng/menu';
import { TabMenuModule } from 'primeng/tabmenu';
import { TableAPIServiceNg } from './core/services/tableAPI.service';
import { OverlayPanelModule } from 'primeng/overlaypanel';
import { CheckboxModule } from 'primeng/checkbox';
import { ToggleButtonModule } from 'primeng/togglebutton';
import { ListboxModule } from 'primeng/listbox';
import { PmcPermissionServiceNg } from './core/api/pmcpermission.service';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { PmcCommentServiceNg } from './core/api/pmccomment.service';
import { ProjectPropertiesServiceNg } from './core/services/project-properties.service';
import { FieldsetModule } from 'primeng/fieldset';
import { CalendarModule } from 'primeng/calendar';
import { AuthorizationServiceNg } from './core/services/authorization.service';
import { DatePipe, CommonModule } from '@angular/common';
import { FormsServiceNg } from './core/services/forms.service';
import { FormsControllerDirective } from './components/controller/directives/forms-controller.directive';
import { ProjectDetailsServiceNg } from './core/services/project-details.service';
import { MessageService } from 'primeng/api';
import { VacationProjectDetailsComponent } from './VACATION/vacation-project-details.component';
import { MessageModule } from 'primeng/message';
import { FormsRegisterService } from './core/services/forms-register.service';
import { UserComponent } from './components/admin/user-management/user.component';
import { AttachmentsListComponent } from './components/attachments/attachments-list/attachments-list.component';
import { AttachmentsTreeComponent } from './components/attachments/attachments-tree/attachments-tree.component';
import { ProjectMonitorComponent } from './components/project-monitor/project-monitor.component';
import { AttachmentsComponent } from './components/attachments/attachments.component';
import { MonitorValuesPipe } from './pipes/monitor-values.pipe';
import { TasklistComponent } from './components/task-monitor/tasklist.component';
import { FilterPipe } from './pipes/filter.pipe';
import { TagFilterPipe } from './pipes/tag-filter.pipe';
import { GroupComponent } from './components/admin/group-management/group.component';
import { RoleComponent } from './components/admin/role-management/role.component';
import { PermissionComponent } from './components/admin/permission-management/permission.component';
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
import { PrepareVacationRequestComponent } from './VACATION/vacation/prepare-vacation-request.component';
import { CheckVacationRequestComponent } from './VACATION/vacation/check-vacation-request.component';
import { ModifyVacationRequestComponent } from './VACATION/vacation/modify-vacation-request.component';
import { PerformHandoverActivitiesComponent } from './VACATION/vacation/perform-handover-activities.component';
import { InformSubstituteComponent } from './VACATION/vacation/inform-substitute.component';
import { ProjectAndProcessesComponent } from './components/project-and-processes/project-and-processes.component';
import { ProjectControlItemComponent } from './components/project-and-processes/projectControlItem/project-control-item.component';
import { SelectComponent } from './core/tasks/form-variables/simple-select/simple-select.component';
import { ProjectPropertiesVacationComponent } from './components/project-header/project-properties-vacation/project.properties.vacation.component';
import { SelectRoleComponent } from './core/tasks/form-variables/role-select/role-select.component';
import { DateSelectComponent } from './core/tasks/form-variables/date-select/date-select.component';
import { DropdownSelectComponent } from './core/tasks/form-variables/dropdown-select/dropdown-select.component';
import { AppSettings, APP_CONFIG } from './models/appSettings';
import { PropertyService } from './core/api/property.service';

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
  ],
  declarations: [
    UserComponent,
    AttachmentsListComponent,
    AttachmentsTreeComponent,
    ProjectMonitorComponent,
    AttachmentsComponent,
    MonitorValuesPipe,
    TasklistComponent,
    FilterPipe,
    TagFilterPipe,
    GroupComponent,
    RoleComponent,
    PermissionComponent,
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
    VacationProjectDetailsComponent,
    PrepareVacationRequestComponent,
    CheckVacationRequestComponent,
    ModifyVacationRequestComponent,
    PerformHandoverActivitiesComponent,
    InformSubstituteComponent,
    ProjectAndProcessesComponent,
    ProjectControlItemComponent,
    SelectComponent,
    ProjectPropertiesVacationComponent,
    SelectRoleComponent,
    DateSelectComponent,
    DropdownSelectComponent,
  ],
  exports: [
    UserComponent,        
    ProjectMonitorComponent,
    AttachmentsComponent,
    AttachmentsListComponent,
    AttachmentsTreeComponent,
    MonitorValuesPipe,
    TasklistComponent,
    FilterPipe,
    TagFilterPipe,
    RoleComponent,
    GroupComponent,
    UserComponent,
    PermissionComponent,
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
    VacationProjectDetailsComponent,
    PrepareVacationRequestComponent,
    CheckVacationRequestComponent,
    ModifyVacationRequestComponent,
    PerformHandoverActivitiesComponent,
    InformSubstituteComponent,
    ProjectAndProcessesComponent,
    ProjectControlItemComponent,
    SelectComponent,
    ProjectPropertiesVacationComponent,
    SelectRoleComponent,
    DateSelectComponent,
    DropdownSelectComponent
   ],
  entryComponents: [
    VacationProjectDetailsComponent,
    PrepareVacationRequestComponent,
    ProjectHeaderComponent,
    ProjectsControllerComponent,
    VacationProjectDetailsComponent,
    CheckVacationRequestComponent,
    ModifyVacationRequestComponent,
    PerformHandoverActivitiesComponent,
    InformSubstituteComponent,
    ProjectPropertiesVacationComponent,
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
        {
          provide: APP_CONFIG,
          useValue: config
        },
      ]
    }
  }

}
