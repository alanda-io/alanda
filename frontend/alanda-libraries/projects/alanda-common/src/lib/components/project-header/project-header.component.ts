import { Component, OnInit, AfterViewInit, ViewChild, Input, ComponentFactoryResolver,
         ChangeDetectorRef, OnChanges, SimpleChanges } from '@angular/core';
import { ProjectPropertiesDirective } from '../controller/directives/project.properties.directive';
import { AlandaProject } from '../../api/models/project';
import { AlandaTask } from '../../api/models/task';
import { AlandaUser } from '../../api/models/user';
import { FormGroup, FormBuilder } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { AlandaProjectApiService } from '../../api/projectApi.service';
import { AlandaFormsRegisterService } from '../../services/formsRegister.service';
import { ProjectState } from '../../enums/projectState.enum';
import { convertUTCDate } from '../../utils/helper-functions';
import { AlandaTaskApiService } from '../../api/taskApi.service';
import { map, switchMap, debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { Observable, of, iif, race, merge } from 'rxjs';
import { AlandaProjectPropertiesService } from '../../services/project-properties.service';

@Component({
    selector: 'alanda-project-header',
    templateUrl: './project-header.component.html',
    styleUrls: [],
  })
  export class AlandaProjectHeaderComponent implements OnInit, AfterViewInit {

    @ViewChild(ProjectPropertiesDirective) propertiesHost: ProjectPropertiesDirective;
    @Input() project: AlandaProject;
    @Input() task: AlandaTask;

    taskDueDate: Date;
    loading: boolean;
    snoozedTask: boolean;
    candidateUsers: AlandaUser[];
    showDelegateDialog: boolean;
    allowedTagList: string[];
    priorities = [{label: '0 - Emergency', value: 0}, {label: '1 - Urgent', value: 1}, {label: '2 - Normal', value: 2}];
    projectHeaderForm: FormGroup;

    constructor(private componentFactoryResolver: ComponentFactoryResolver, private propertiesService: AlandaProjectPropertiesService,
                private taskService: AlandaTaskApiService, private cdRef: ChangeDetectorRef, private messageService: MessageService,
                private fb: FormBuilder, private projectService: AlandaProjectApiService,
                private formsRegisterService: AlandaFormsRegisterService) {}

    ngOnInit() {
      this.initFormGroup();
      this.projectHeaderForm.valueChanges.pipe(
        debounceTime(1200),
        distinctUntilChanged((a, b) => JSON.stringify(a) === JSON.stringify(b)),
        switchMap(changes => this.updateProject(changes)),
      ).subscribe(project => {
        this.messageService.add({
          severity: 'success',
          summary: 'Update Project',
          detail: 'Project has been updated'
        });
        if (project.version) {
          this.project.version = project.version;
        }
      });
    }

    private updateProject(changes: any): Observable<AlandaProject> {
      console.log(changes);
      if (changes.taskDueDate && changes.taskDueDate.toString() !== this.taskDueDate.toString()) {
        const taskDueDate = convertUTCDate(changes.taskDueDate).toISOString().substring(0, 10);
        this.taskService.updateDueDateOfTask(this.task.task_id, taskDueDate).subscribe(res => {
          this.messageService.add(
            {severity: 'success', summary: 'Update task due date', detail: 'Due date of task has been updated'}
          );
          this.taskDueDate = changes.taskDueDate;
          return of();
        },
        error => {
          this.messageService.add({severity: 'error', summary: 'Update Due Date Of Task', detail: error.message});
          return of();
        });
      } else {
        return of(changes).pipe(
          map(change => {
            return {
            ...this.project,
            priority: change.priority,
            tag: change.tag,
            dueDate: convertUTCDate(new Date(change.dueDate)),
            title: change.title,
            details: change.details,
            };
          }),
          switchMap(project => this.projectService.updateProject(project))
        );
      }
    }

    ngAfterViewInit() {
      this.loadProjectPropertiesComponent();
      this.cdRef.detectChanges();
    }

    private loadProjectPropertiesComponent() {
      if (this.propertiesService.getPropsForType(this.project.projectTypeIdName) === undefined) {
          return;
      }
      const componentFactory = this.componentFactoryResolver
          .resolveComponentFactory(this.propertiesService.getPropsForType(this.project.projectTypeIdName));
      const viewContainerRef = this.propertiesHost.viewContainerRef;
      viewContainerRef.clear();
      const componentRef = viewContainerRef.createComponent(componentFactory);
      (<any>componentRef.instance).project = this.project;
    }

    private initFormGroup() {
      this.projectHeaderForm = this.fb.group({
        tag: null,
        priority: null,
        dueDate: null,
        title: null,
        details: null,
        taskDueDate: null,
      });

      this.allowedTagList = this.project.pmcProjectType.allowedTagList;
        this.projectHeaderForm.patchValue(this.project, {emitEvent: false});
        if (this.project.status.valueOf() === ProjectState.CANCELED.valueOf()) {
          this.projectHeaderForm.disable({emitEvent: false});
        } else {
          this.projectHeaderForm.enable({emitEvent: false});
        }
        if (this.task) {
          this.projectHeaderForm.patchValue({taskDueDate: new Date(this.task.due)}, {emitEvent: false});
          this.taskDueDate = new Date(this.task.due);
        }
      this.formsRegisterService.registerForm(this.projectHeaderForm, 'projectHeaderForm');
    }

    searchTag(event: Event) {
      this.allowedTagList = [...this.allowedTagList];
    }

    openDelegationForm(): void {
      this.taskService.getCandidates(this.task.task_id).subscribe(
          candidates => {
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
          error => this.messageService.add({severity: 'error', summary: 'Delegate Task', detail: error.message}));
      }
    }

  }
