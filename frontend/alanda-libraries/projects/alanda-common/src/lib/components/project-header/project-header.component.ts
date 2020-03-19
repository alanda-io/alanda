import { Component, OnInit, AfterViewInit, ViewChild, Input, ComponentFactoryResolver, ChangeDetectorRef } from '@angular/core';
import { ProjectPropertiesDirective } from '../controller/directives/project.properties.directive';
import { AlandaProject } from '../../api/models/project';
import { AlandaTask } from '../../api/models/task';
import { AlandaUser } from '../../api/models/user';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { AlandaProjectApiService } from '../../api/projectApi.service';
import { AlandaFormsRegisterService } from '../../services/formsRegister.service';
import { AlandaProjectPropertiesService } from '../../services/projectProperties.service';
import { ProjectState } from '../../enums/projectState.enum';
import { convertUTCDate } from '../../utils/helper-functions';
import { AlandaTaskApiService } from '../../api/taskApi.service';

@Component({
    selector: 'alanda-project-header',
    templateUrl: './project-header.component.html',
    styleUrls: [],
  })
  export class AlandaProjectHeaderComponent implements OnInit, AfterViewInit {

    @ViewChild(ProjectPropertiesDirective) propertiesHost: ProjectPropertiesDirective;
    @Input() project: AlandaProject;
    @Input() task: AlandaTask;

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
        this.allowedTagList = this.project.pmcProjectType.allowedTagList;
        this.initFormGroup();
    }

    ngAfterViewInit() {
        this.loadProjectPropertiesComponent();
        this.cdRef.detectChanges();
    }

    private loadProjectPropertiesComponent() {
        if(this.propertiesService.getPropsForType(this.project.projectTypeIdName) === undefined) {
            return;
        }
        const componentFactory = this.componentFactoryResolver
            .resolveComponentFactory(this.propertiesService.getPropsForType(this.project.projectTypeIdName));
        const viewContainerRef = this.propertiesHost.viewContainerRef;
        viewContainerRef.clear();
        const componentRef = viewContainerRef.createComponent(componentFactory);
        (<any>componentRef.instance).project = this.project;
    }

    private initFormGroup(){
        this.projectHeaderForm = this.fb.group({
            tag: [this.project.tag, Validators.required],
            prio: [this.priorities, Validators.required],
            projectDueDate: [new Date(this.project.dueDate), Validators.required],
            projectTitle: [this.project.title, Validators.required],
            projectDetails: [this.project.comment, Validators.required],
        });

          if(this.task){
            this.projectHeaderForm.addControl('taskDueDate', this.fb.control(new Date(this.task.due), Validators.required));
          }
          if(this.project.status.valueOf() === ProjectState.CANCELED.valueOf()){
            this.projectHeaderForm.disable();
          }
          this.formsRegisterService.registerForm(this.projectHeaderForm, "projectHeaderForm");
    }

    updateProject() {
        this.project.dueDate = convertUTCDate(this.projectHeaderForm.get('projectDueDate').value).toISOString().substring(0,10);
        this.projectService.updateProject(this.project).subscribe(project => {
            if(project.version){
                this.project.version = project.version;
            }
        },error => this.messageService.add({severity:'error', summary:'Update Project', detail: error.message}));
    }

    searchTag(event: Event) {
      this.allowedTagList = [...this.allowedTagList];
    }

    public updateDueDateOfTask() {
        const taskDueDate = convertUTCDate(this.projectHeaderForm.get('taskDueDate').value).toISOString().substring(0,10);
        this.taskService.updateDueDateOfTask(this.task.task_id, taskDueDate).subscribe(
          res => this.messageService.add({severity:'success', summary:'Update Due Date Of Task', detail:'Due date of task has successfully been updated'}),
          error => {this.messageService.add({severity:'error', summary:'Update Due Date Of Task', detail: error.message})})
    }

    openDelegationForm(): void {
        this.taskService.getCandidates(this.task.task_id).subscribe(
            candidates => {
            this.candidateUsers = candidates;
            this.showDelegateDialog = true;
        });
    }

    delegateTask(selectedUser: AlandaUser): void {
    if(selectedUser){
        this.taskService.assign(this.task.task_id,selectedUser.guid).subscribe(
        () => {
            this.task.assignee_id = ""+selectedUser.guid;
            this.task.assignee = selectedUser.displayName;
            this.showDelegateDialog = false;
        },
        error => this.messageService.add({severity:'error', summary:'Delegate Task', detail: error.message}));
    }
    }

  }
