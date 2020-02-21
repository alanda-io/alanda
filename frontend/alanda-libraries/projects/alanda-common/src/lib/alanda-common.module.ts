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
import { ProjectMonitorComponent } from './components/project-monitor/project-monitor.component';
import { AttachmentsComponent } from './components/attachments/attachments.component';
import { MonitorValuesPipe } from './pipes/nested-object.pipe';
import { TasklistComponent } from './components/task-list/tasklist.component';
import { FilterPipe } from './pipes/filter.pipe';
import { TagFilterPipe } from './pipes/tag-filter.pipe';
import { CommentsComponent } from './components/comments/comments.component';
import { CommentComponent } from './components/comments/comment/comment.component';
import { ProjectHeaderComponent } from './components/project-header/project-header.component';
import { ProjectPropertiesDirective } from './components/controller/directives/project.properties.directive';
import { PioComponent } from './components/pio/pio.component';
import { DiagramComponent } from './components/pio/diagram/diagram.component';
import { ProcessActivitiesComponent } from './components/pio/process-activities/process-activities.component';
import { HistoryGridComponent } from './components/history/history-grid.component';
import { CreateProjectComponent } from './components/create-project/create-project.component';
import { ProjectAndProcessesComponent } from './components/project-and-processes/project-and-processes.component';
import { ProjectControlItemComponent } from './components/project-and-processes/projectControlItem/project-control-item.component';
import { AppSettings, APP_CONFIG } from './models/appSettings';
import { SelectComponent } from './components/task/form-variables/simple-select/simple-select.component';
import { SelectRoleComponent } from './components/task/form-variables/role-select/role-select.component';
import { DateSelectComponent } from './components/task/form-variables/date-select/date-select.component';
import { DropdownSelectComponent } from './components/task/form-variables/dropdown-select/dropdown-select.component';
import { AlandaTaskTemplateComponent } from './components/task/template/alanda-task-template.component';
import { AccordionModule } from 'primeng/accordion'
import { SelectMilestoneComponent } from './components/task/form-variables/milestone-select/milestone-select.component';
import { UserManagementComponent } from './components/admin/user-management/user-management.component';
import { GroupManagementComponent } from './components/admin/group-management/group-management.component';
import { RoleManagementComponent } from './components/admin/role-management/role-management.component';
import { PermissionManagementComponent } from './components/admin/permission-management/permission-management.component';
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
    FormsControllerDirective,
    PioComponent,
    DiagramComponent,
    ProcessActivitiesComponent,
    HistoryGridComponent,
    CreateProjectComponent,
    ProjectAndProcessesComponent,
    ProjectControlItemComponent,
    SelectComponent,
    SelectRoleComponent,
    DateSelectComponent,
    DropdownSelectComponent,
    SelectMilestoneComponent,
    AlandaTaskTemplateComponent,
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
    FormsControllerDirective,
    PioComponent,
    DiagramComponent,
    ProcessActivitiesComponent,
    HistoryGridComponent,
    CreateProjectComponent,
    ProjectAndProcessesComponent,
    ProjectControlItemComponent,
    SelectComponent,
    SelectRoleComponent,
    SelectMilestoneComponent,
    DateSelectComponent,
    DropdownSelectComponent,
    AlandaTaskTemplateComponent
   ],
  entryComponents: [
  ]
})
export class AlandaCommonModule {

  public static forRoot(config: AppSettings): ModuleWithProviders {
    return {
      ngModule: AlandaCommonModule,
      providers: [
        {
          provide: APP_CONFIG,
          useValue: config
        },
      ]
    };
  }

}
