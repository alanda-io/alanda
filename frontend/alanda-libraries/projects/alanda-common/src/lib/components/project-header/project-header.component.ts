import {
  Component,
  OnInit,
  AfterViewInit,
  ViewChild,
  Input,
  ComponentFactoryResolver,
  ChangeDetectorRef,
} from '@angular/core';
import { ProjectPropertiesDirective } from '../controller/directives/project.properties.directive';
import { AlandaProject } from '../../api/models/project';
import { AlandaTask } from '../../api/models/task';
import { AlandaUser } from '../../api/models/user';
import { FormGroup, FormBuilder } from '@angular/forms';
import { MessageService } from 'primeng/api';
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
import { AlandaPhaseTabItem } from '../phase-tab/phase-tab.component';

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
  @Input()
  set rootFormGroup(rootFormGroup: FormGroup) {
    if (rootFormGroup) {
      rootFormGroup.addControl('alanda-project-header', this.projectHeaderForm);
    }
  }

  taskDueDate: Date;
  loading: boolean;
  snoozedTask: boolean;
  candidateUsers: AlandaUser[];
  showDelegateDialog: boolean;
  allowedTagList: string[];
  priorities = [
    { label: '0 - Emergency', value: 0 },
    { label: '1 - Urgent', value: 1 },
    { label: '2 - Normal', value: 2 },
  ];

  projectHeaderForm = this.fb.group({
    tag: null,
    priority: null,
    dueDate: null,
    title: null,
    details: null,
    taskDueDate: null,
  });

  items: AlandaPhaseTabItem[] = [
    { header: 'Overview', state: null },
    { header: 'Sharing', state: 'not set' },
    { header: 'Acquisition', state: 'active' },
    { header: 'Civil Works', state: 'active' },
    { header: 'Integration', state: 'active' }
  ];

  constructor(
    private readonly componentFactoryResolver: ComponentFactoryResolver,
    private readonly propertiesService: AlandaProjectPropertiesService,
    private readonly taskService: AlandaTaskApiService,
    private readonly cdRef: ChangeDetectorRef,
    private readonly messageService: MessageService,
    private readonly fb: FormBuilder,
    private readonly projectService: AlandaProjectApiService,
    private readonly propertyService: AlandaPropertyApiService
  ) {}

  ngOnInit(): void {
    this.initFormGroup();
    this.projectHeaderForm.valueChanges
      .pipe(
        debounceTime(1200),
        distinctUntilChanged((a, b) => JSON.stringify(a) === JSON.stringify(b)),
        switchMap((changes) => this.updateProject(changes))
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
    this.propertyService
      .getPropertiesMap(this.project.guid)
      .subscribe((ret) => {
        const props: Map<string, any> = ret;
        console.log('props', props);
      });
  }

  private updateProject(changes: any): Observable<AlandaProject> {
    console.log(changes);
    if (
      changes.taskDueDate &&
      changes.taskDueDate.toString() !== this.taskDueDate.toString()
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
              severity: 'error',
              summary: 'Update Due Date Of Task',
              detail: error.message,
            });
            return of();
          }
        );
    } else {
      return of(changes).pipe(
        map((change) => {
          return {
            ...this.project,
            priority: change.priority,
            tag: change.tag,
            dueDate: convertUTCDate(new Date(change.dueDate)),
            title: change.title,
            details: change.details,
          };
        }),
        switchMap((project) => this.projectService.updateProject(project))
      );
    }
  }

  ngAfterViewInit(): void {
    this.loadProjectPropertiesComponent();
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
      this.propertiesService.getPropsForType(this.project.projectTypeIdName)
    );
    const viewContainerRef = this.propertiesHost.viewContainerRef;
    viewContainerRef.clear();
    const componentRef = viewContainerRef.createComponent(componentFactory);
    (<any>componentRef.instance).project = this.project;
  }

  private initFormGroup(): void {
    this.allowedTagList = this.project.pmcProjectType.allowedTagList;
    this.projectHeaderForm.patchValue(this.project, { emitEvent: false });
    if (this.project.status.valueOf() === ProjectState.CANCELED.valueOf()) {
      this.projectHeaderForm.disable({ emitEvent: false });
    } else {
      this.projectHeaderForm.enable({ emitEvent: false });
    }
    if (this.task) {
      this.projectHeaderForm.patchValue(
        { taskDueDate: new Date(this.task.due) },
        { emitEvent: false }
      );
      this.taskDueDate = new Date(this.task.due);
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
          })
      );
    }
  }
}
