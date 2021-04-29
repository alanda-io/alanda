import {
  Component,
  OnInit,
  AfterViewInit,
  ViewChild,
  Input,
  ComponentFactoryResolver,
  ChangeDetectorRef,
  Inject,
} from '@angular/core';
import { ProjectPropertiesDirective } from '../../directives/project.properties.directive';
import { AlandaProject } from '../../api/models/project';
import { AlandaTask } from '../../api/models/task';
import { AlandaUser } from '../../api/models/user';
import { FormGroup, FormBuilder } from '@angular/forms';
import { MessageService, SelectItem } from 'primeng/api';
import { AlandaProjectApiService } from '../../api/projectApi.service';
import { ProjectState } from '../../enums/projectState.enum';
import { convertUTCDate } from '../../utils/helper-functions';
import { AlandaTaskApiService } from '../../api/taskApi.service';
import {
  map,
  switchMap,
  debounceTime,
  distinctUntilChanged,
} from 'rxjs/operators';
import { Observable, of } from 'rxjs';
import { AlandaProjectPropertiesService } from '../../services/project-properties.service';
import { AlandaPropertyApiService } from '../../api/propertyApi.service';
import { APP_CONFIG, AppSettings } from '../../models/appSettings';
import { Authorizations } from '../../permissions';
import { LocaleSettings } from 'primeng/calendar';

@Component({
  selector: 'alanda-project-header',
  templateUrl: './project-header.component.html',
  styleUrls: ['./project-header.component.scss'],
})
export class AlandaProjectHeaderComponent implements OnInit, AfterViewInit {
  @ViewChild(ProjectPropertiesDirective)
  propertiesHost: ProjectPropertiesDirective;

  @Input() project: AlandaProject;
  @Input() task: AlandaTask;
  @Input() user: AlandaUser;
  @Input() rootFormGroup: FormGroup;
  @Input() activePhaseIndex: number;
  @Input() phase: string;

  dateFormat: string;
  locale: LocaleSettings;
  taskDueDate: Date;
  loading: boolean;
  snoozedTask: boolean;
  candidateUsers: AlandaUser[];
  showDelegateDialog: boolean;
  allowedTagList: string[];
  priorities: SelectItem[];

  projectHeaderForm = this.fb.group({
    tag: null,
    priority: null,
    dueDate: null,
    title: null,
    comment: null,
    taskDueDate: null,
  });

  constructor(
    private readonly componentFactoryResolver: ComponentFactoryResolver,
    private readonly propertiesService: AlandaProjectPropertiesService,
    private readonly taskService: AlandaTaskApiService,
    private readonly cdRef: ChangeDetectorRef,
    private readonly messageService: MessageService,
    private readonly fb: FormBuilder,
    private readonly projectService: AlandaProjectApiService,
    private readonly propertyService: AlandaPropertyApiService,
    @Inject(APP_CONFIG) config: AppSettings,
  ) {
    this.dateFormat = config.DATE_FORMAT;
    this.locale = config.LOCALE_PRIME;
    this.priorities = config.PRIORITIES;
  }

  ngOnInit(): void {
    if (this.rootFormGroup != null) {
      this.rootFormGroup.addControl(
        'alanda-project-header',
        this.projectHeaderForm,
      );
    }
    if (this.project) {
      this.initFormGroup();
    }
    this.projectHeaderForm.valueChanges
      .pipe(
        debounceTime(1200),
        distinctUntilChanged((a, b) => JSON.stringify(a) === JSON.stringify(b)),
        switchMap((changes) => this.updateProject(changes)),
      )
      .subscribe((project) => {
        this.messageService.add({
          severity: 'success',
          summary: 'Update Project',
          detail: 'Project has been updated',
        });
        if (project.version) {
          this.project.version = project.version;
        }
      });
    // if (this.project) {
    //   this.propertyService
    //     .getPropertiesMap(this.project.guid)
    //     .subscribe((ret) => {
    //       const props: Map<string, any> = ret;
    //     });
    // }
  }

  private updateProject(changes: any): Observable<AlandaProject> {
    console.log(changes);
    if (
      changes.taskDueDate &&
      changes.taskDueDate.toString() !== this.taskDueDate?.toString()
    ) {
      const taskDueDate = convertUTCDate(changes.taskDueDate)
        .toISOString()
        .substring(0, 10);
      this.taskService
        .updateDueDateOfTask(this.task.task_id, taskDueDate)
        .subscribe(
          (res) => {
            this.messageService.add({
              severity: 'success',
              summary: 'Update task due date',
              detail: 'Due date of task has been updated',
            });
            this.taskDueDate = changes.taskDueDate;
            return of();
          },
          (error) => {
            this.messageService.add({
              key: 'center',
              severity: 'error',
              summary: 'Update Due Date Of Task',
              detail: error.message,
            });
            return of();
          },
        );
    } else {
      return of(changes).pipe(
        map((change) => {
          return {
            ...this.project,
            priority: change.priority,
            tag: change.tag,
            dueDate: change.dueDate
              ? convertUTCDate(new Date(change.dueDate))
              : null,
            title: change.title,
            comment: change.comment,
          };
        }),
        switchMap((project) => this.projectService.updateProject(project)),
      );
    }
  }

  ngAfterViewInit(): void {
    if (this.project) {
      this.loadProjectPropertiesComponent();
    }
    this.cdRef.detectChanges();
  }

  private loadProjectPropertiesComponent(): void {
    if (
      this.propertiesService.getPropsForType(this.project.projectTypeIdName) ===
      undefined
    ) {
      return;
    }
    const componentFactory = this.componentFactoryResolver.resolveComponentFactory(
      this.propertiesService.getPropsForType(this.project.projectTypeIdName),
    );
    const viewContainerRef = this.propertiesHost.viewContainerRef;
    viewContainerRef.clear();
    const componentRef = viewContainerRef.createComponent(componentFactory);
    (componentRef.instance as any).project = this.project;
    (componentRef.instance as any).user = this.user;
    (componentRef.instance as any).rootForm = this.rootFormGroup;
    (componentRef.instance as any).phase = this.phase;
    (componentRef.instance as any).projectChanged?.subscribe((project) => {
      this.project = project;
      this.initFormGroup();
    });
  }

  private initFormGroup(): void {
    const authBase = `project:${this.project.authBase}`;
    this.allowedTagList = this.project.pmcProjectType.allowedTagList;
    if (!Authorizations.hasPermission(this.user, `${authBase}:tag`, 'write')) {
      this.projectHeaderForm.get('tag').disable({ emitEvent: false });
    }
    this.projectHeaderForm.patchValue(this.project, { emitEvent: false });
    if (this.project.status.valueOf() === ProjectState.CANCELED.valueOf()) {
      this.projectHeaderForm.disable({ emitEvent: false });
    } else {
      this.projectHeaderForm.enable({ emitEvent: false });
      if (
        !Authorizations.hasPermission(
          this.user,
          `${authBase}:priority`,
          'write',
        )
      ) {
        this.projectHeaderForm.get('priority').disable({ emitEvent: false });
      }
      if (
        !Authorizations.hasPermission(this.user, `${authBase}:dueDate`, 'write')
      ) {
        this.projectHeaderForm.get('dueDate').disable({ emitEvent: false });
      }
      if (
        !Authorizations.hasPermission(this.user, `${authBase}:title`, 'write')
      ) {
        this.projectHeaderForm.get('title').disable({ emitEvent: false });
      }
      if (
        !Authorizations.hasPermission(this.user, `${authBase}:comment`, 'write')
      ) {
        this.projectHeaderForm.get('comment').disable({ emitEvent: false });
      }
    }
    if (this.task) {
      this.projectHeaderForm.patchValue(
        { taskDueDate: this.task.due ? new Date(this.task.due) : null },
        { emitEvent: false },
      );
      this.taskDueDate = this.task.due ? new Date(this.task.due) : null;
    }
  }

  searchTag(event: Event): void {
    this.allowedTagList = [...this.allowedTagList];
  }

  openDelegationForm(): void {
    this.taskService
      .getCandidates(this.task.task_id)
      .subscribe((candidates) => {
        this.candidateUsers = candidates;
        this.showDelegateDialog = true;
      });
  }

  delegateTask(selectedUser: AlandaUser): void {
    if (selectedUser) {
      this.taskService.assign(this.task.task_id, selectedUser.guid).subscribe(
        () => {
          this.task.assignee_id = '' + selectedUser.guid;
          this.task.assignee = selectedUser.displayName;
          this.showDelegateDialog = false;
        },
        (error) =>
          this.messageService.add({
            severity: 'error',
            summary: 'Delegate Task',
            detail: error.message,
          }),
      );
    }
  }
}
