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
import { AppSettings, APP_CONFIG } from './models/AppSettings';
import { BasicAuthInterceptor } from './auth/basic-auth.interceptor';
import { ErrorInterceptor } from './auth/error.interceptor';
import { TaskServiceNg } from './services/rest/task.service';
import { UserComponent } from './components/admin/user/user.component';
import { PmcUserServiceNg } from './services/rest/pmcuser.service';
import { PmcGroupServiceNg } from './services/rest/pmcgroup.service';
import { PmcRoleServiceNg } from './services/rest/pmcrole.service';
import { ProjectMonitorComponent } from './components/project-monitor/project-monitor.component';
import { DocumentServiceNg } from './services/rest/document.service';
import { AttachmentsListComponent } from './components/attachments/attachments-list/attachments-list.component';
import { AttachmentsTreeComponent } from './components/attachments/attachments-tree/attachments-tree.component';
import { TreeModule } from 'primeng/tree';
import { AttachmentsComponent } from './components/attachments/attachments.component';
import { MonitorValuesPipe } from './components/shared/monitorValues.pipe';
import { TasklistComponent } from './components/tasklist/tasklist.component';
import { ProjectServiceNg } from './services/rest/project.service';
import { InputSwitchModule } from 'primeng/inputswitch';
import { FilterPipe } from './components/shared/filter.pipe';
import { SplitButtonModule } from 'primeng/splitbutton';
import { MenuModule } from 'primeng/menu';
import { TabMenuModule } from 'primeng/tabmenu';
import { TableAPIServiceNg } from './services/tableAPI.service';
import { OverlayPanelModule } from 'primeng/overlaypanel';
import { CheckboxModule } from 'primeng/checkbox';
import { ToggleButtonModule } from 'primeng/togglebutton';
import { ListboxModule } from 'primeng/listbox';
import { GroupComponent } from './components/admin/group/group.component';
import { RoleComponent } from './components/admin/role/role.component';
import { PermissionComponent } from './components/admin/permission/permission.component';
import { PmcPermissionServiceNg } from './services/rest/pmcpermission.service';
import { CommentsComponent } from './components/comments/comments.component';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { PmcCommentServiceNg } from './services/rest/pmccomment.service';
import { TagFilterPipe } from './components/shared/tagFilter.pipe';
import { ProjectHeaderComponent } from './components/project-header/project-header.component';
import { ProjectPropertiesDirective } from './components/project-header/project.properties.directive';
import { ProjectPropertiesServiceNg } from './services/projectproperties.service';
import { FieldsetModule } from 'primeng/fieldset';
import { CalendarModule } from 'primeng/calendar';
import { AuthorizationServiceNg } from './services/authorization.service';
import { DatePipe, CommonModule } from '@angular/common';
import { FormsServiceNg } from './services/forms.service';
import { FormsControllerComponent } from './components/forms/tasks/forms-controller.component';
import { ProjectsControllerComponent } from './components/forms/projects/projects-controller/projects-controller.component';
import { FormsControllerDirective } from './components/forms/directives/forms-controller.directive';
import { ProjectDetailsServiceNg } from './services/projectdetails.service';
import { ProjectDetailsDirective } from './components/forms/directives/project-details-controller.directive';
import { MessageService } from 'primeng/api';
import { ProcessActivitiesComponent } from './components/pio/process-activities/process-activities.component';
import { HistoryGridComponent } from './components/history/history-grid.component';
import { CreateProjectComponent } from './components/create-project/create-project.component';
import { VacationProjectDetailsComponent } from './components/forms/projects/vacation/vacation-project-details.component';
import { PrepareVacationRequestComponent } from './components/forms/tasks/vacation/prepare-vacation-request.component';
import { PioComponent } from './components/pio/pio.component';
import { DiagramComponent } from './components/pio/diagram/diagram.component';
import { CheckVacationRequestComponent } from './components/forms/tasks/vacation/check-vacation-request.component';
import { ModifyVacationRequestComponent } from './components/forms/tasks/vacation/modify-vacation-request.component';
import { PerformHandoverActivitiesComponent } from './components/forms/tasks/vacation/perform-handover-activities.component';
import { InformSubstituteComponent } from './components/forms/tasks/vacation/inform-substitute.component';
import { ProjectAndProcessesComponent } from './components/projectAndProcesses/project-and-processes.component';
import { ProjectControlItemComponent } from './components/projectAndProcesses/projectControlItem/project-control-item.component';
import { SelectComponent } from './components/forms/variables/select.component';
import { ProjectPropertiesVacationComponent } from './components/project-header/project-properties-vacation/project.properties.vacation.component';
import { SelectRoleComponent } from './components/forms/variables/role-select.component';
import { DatepickerComponent } from './components/forms/variables/datepicker.component';
import { PropertyService } from './services/rest/property.service';
import { DropdownSelectComponent } from './components/forms/variables/dropdown-select.component';
import { CommentComponent } from './components/comments/comment/comment.component';
import { MessageModule } from 'primeng/message';
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
    DatepickerComponent,
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
    DatepickerComponent,
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
        {
          provide: APP_CONFIG,
          useValue: config
        },
      ]
    }
  }

}
