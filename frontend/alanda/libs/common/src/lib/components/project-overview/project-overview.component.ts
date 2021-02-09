import {
  AfterViewInit,
  Component,
  ComponentFactoryResolver,
  ContentChild,
  ElementRef,
  Inject,
  Input,
  OnInit,
  ViewChild,
} from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ProjectPropertiesDirective } from '../../directives/project.properties.directive';
import {
  catchError,
  debounceTime,
  distinctUntilChanged,
  switchMap,
  tap,
} from 'rxjs/operators';
import { MessageService, SelectItem } from 'primeng/api';
import { RxState } from '@rx-angular/state';
import { convertUTCDate } from '../../utils/helper-functions';
import { AlandaProject } from '../../api/models/project';
import { AlandaProjectApiService } from '../../api/projectApi.service';
import { EMPTY, Subject } from 'rxjs';
import { AlandaTaskApiService } from '../../api/taskApi.service';
import { AlandaTask } from '../../api/models/task';
import { AlandaProjectPropertiesService } from '../../services/project-properties.service';
import { AlandaUser } from '../../api/models/user';
import { APP_CONFIG, AppSettings } from '../../models/appSettings';
import { LocaleSettings } from 'primeng/calendar';
import { Authorizations } from '../../permissions';
import { ProjectState } from '../../enums/projectState.enum';

@Component({
  selector: 'alanda-project-overview',
  templateUrl: './project-overview.component.html',
  styleUrls: ['./project-overview.component.scss'],
})
export class ProjectOverviewComponent implements OnInit, AfterViewInit {
  @ViewChild(ProjectPropertiesDirective)
  propertiesHost: ProjectPropertiesDirective;
  @Input() set formGroup(formGroup: FormGroup) {
    if (formGroup) {
      this.projectOverviewForm = formGroup;
    }
  }
  @Input() project: AlandaProject;
  @Input() task: AlandaTask;
  @Input() user: AlandaUser;
  @Input() rootFormGroup: FormGroup;
  @ContentChild('content') content: ElementRef;

  locale: LocaleSettings;
  projectOverviewForm: FormGroup = this.fb.group({
    tag: null,
    priority: null,
    dueDate: null,
    title: null,
    comment: null,
    taskDueDate: null,
  });
  taskDueDate: Date;
  allowedTagList: string[];
  priorities: SelectItem[];

  updateProjectEvent$ = new Subject<any>();
  updateDueDateOfTaskEvent$ = new Subject<string>();

  projectOverviewChanged$ = this.projectOverviewForm.valueChanges.pipe(
    debounceTime(1200),
    distinctUntilChanged((a, b) => JSON.stringify(a) === JSON.stringify(b)),
    tap((changes) => {
      if (
        changes.taskDueDate &&
        changes.taskDueDate.toString() !== this.taskDueDate.toString()
      ) {
        const taskDueDate = convertUTCDate(changes.taskDueDate)
          .toISOString()
          .substring(0, 10);
        this.updateDueDateOfTaskEvent$.next(taskDueDate);
      } else {
        this.updateProjectEvent$.next({
          ...this.project,
          priority: changes.priority,
          tag: changes.tag,
          dueDate: changes.dueDate
            ? convertUTCDate(new Date(changes.dueDate))
            : null,
          title: changes.title,
          comment: changes.comment,
        });
      }
    }),
  );

  updateProject$ = this.updateProjectEvent$.pipe(
    switchMap((project) => this.projectService.updateProject(project)),
    catchError((error) => {
      this.messageService.add({
        severity: 'error',
        summary: 'Update Project Failed',
        detail: error,
      });
      return EMPTY;
    }),
    tap((updatedProject: AlandaProject) => {
      this.messageService.add({
        severity: 'success',
        summary: 'Update Project',
        detail: 'Project has been updated',
      });

      if (updatedProject.version) {
        this.project.version = updatedProject.version;
      }
    }),
  );

  updateDueDateOfTask$ = this.updateDueDateOfTaskEvent$.pipe(
    switchMap((taskDueDate) =>
      this.taskService.updateDueDateOfTask(this.task.task_id, taskDueDate),
    ),
  );

  constructor(
    private readonly fb: FormBuilder,
    private readonly messageService: MessageService,
    private readonly state: RxState<any>,
    private readonly projectService: AlandaProjectApiService,
    private readonly taskService: AlandaTaskApiService,
    private readonly propertiesService: AlandaProjectPropertiesService,
    private readonly componentFactoryResolver: ComponentFactoryResolver,
    @Inject(APP_CONFIG) config: AppSettings,
  ) {
    this.priorities = config.PRIORITIES;
    this.locale = config.LOCALE_PRIME;

    this.state.hold(this.projectOverviewChanged$);
    this.state.hold(this.updateProject$);
    this.state.hold(this.updateDueDateOfTask$);
  }

  ngOnInit(): void {
    if (this.rootFormGroup != null) {
      this.rootFormGroup.addControl(
        'alanda-project-header',
        this.projectOverviewForm,
      );
    }
    if (this.project) {
      this.initFormGroup();
    }
  }

  ngAfterViewInit(): void {
    if (this.project) {
      this.loadProjectPropertiesComponent();
    }
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
    (componentRef.instance as any).projectChanged?.subscribe((project) => {
      this.project = project;
      this.initFormGroup();
    });
  }

  private initFormGroup(): void {
    const authBase = `project:${this.project.authBase}`;
    this.allowedTagList = this.project.pmcProjectType.allowedTagList;
    if (!Authorizations.hasPermission(this.user, `${authBase}:tag`, 'write')) {
      this.projectOverviewForm.get('tag').disable({ emitEvent: false });
    }
    this.projectOverviewForm.patchValue(this.project, { emitEvent: false });
    if (this.project.status.valueOf() === ProjectState.CANCELED.valueOf()) {
      this.projectOverviewForm.disable({ emitEvent: false });
    } else {
      this.projectOverviewForm.enable({ emitEvent: false });
      if (
        !Authorizations.hasPermission(
          this.user,
          `${authBase}:priority`,
          'write',
        )
      ) {
        this.projectOverviewForm.get('priority').disable({ emitEvent: false });
      }
      if (
        !Authorizations.hasPermission(this.user, `${authBase}:dueDate`, 'write')
      ) {
        this.projectOverviewForm.get('dueDate').disable({ emitEvent: false });
      }
      if (
        !Authorizations.hasPermission(this.user, `${authBase}:title`, 'write')
      ) {
        this.projectOverviewForm.get('title').disable({ emitEvent: false });
      }
      if (
        !Authorizations.hasPermission(this.user, `${authBase}:comment`, 'write')
      ) {
        this.projectOverviewForm.get('comment').disable({ emitEvent: false });
      }
    }
    if (this.task) {
      this.projectOverviewForm.patchValue(
        { taskDueDate: this.task.due ? new Date(this.task.due) : null },
        { emitEvent: false },
      );
      this.taskDueDate = this.task.due ? new Date(this.task.due) : null;
    }
  }

  searchTag(): void {
    this.allowedTagList = [...this.allowedTagList];
  }
}
