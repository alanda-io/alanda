import { OnInit, Component, Input, ViewChild, ComponentFactoryResolver, AfterViewInit, ChangeDetectorRef } from "@angular/core";
import { ProjectPropertiesDirective } from "./project.properties.directive";
import { ProjectPropertiesServiceNg } from "../../services/projectproperties.service";
import { FormGroup, Validators, FormBuilder } from "@angular/forms";
import { ProjectServiceNg } from "../../services/rest/project.service";
import { TaskServiceNg } from "../../services/rest/task.service";
import { PmcUser } from "../../models/PmcUser";
import { PmcUserServiceNg } from "../../services/rest/pmcuser.service";
import { MessageService } from "primeng/api";
import { PmcTask } from "../../models/pmcTask";
import { Project } from "../../models/project.model";

@Component({
    selector: 'project-header-component',
    templateUrl: './project-header.component.html',
    styleUrls: [],
  })
  export class ProjectHeaderComponent implements OnInit, AfterViewInit{

    @ViewChild(ProjectPropertiesDirective) propertiesHost: ProjectPropertiesDirective;
    @Input() project: Project;
    @Input() task: PmcTask;
    @Input() baseFormGroup: FormGroup;

    snoozedTask: boolean;
    candidateUsers: PmcUser[];
    showDelegateDialog: boolean;
    allowedTagList: string[];
    currentUser: PmcUser;
    priorities = [{label: '0 - Emergency', value: 0}, {label: '1 - Urgent', value: 1}, {label: '2 - Normal', value: 2}];
    projectHeaderForm: FormGroup;

    constructor(private componentFactoryResolver: ComponentFactoryResolver, private propertiesService: ProjectPropertiesServiceNg,
                private taskService: TaskServiceNg, private cdRef:ChangeDetectorRef, private userService: PmcUserServiceNg, 
                private messageService: MessageService, private fb: FormBuilder, private projectService: ProjectServiceNg) {}

    ngOnInit() {
        this.userService.getCurrentUser().subscribe(
            user => this.currentUser = user,
           error => this.messageService.add({severity:'error', summary:'Get Current User', detail: error.message})
        );
        this.allowedTagList = this.project.pmcProjectType.allowedTagList;
        this.initFormGroup();
    }

    ngAfterViewInit() {
        this.loadProjectPropertiesComponent();
        this.cdRef.detectChanges();
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
          if(this.project.status.toLowerCase() === 'canceled'){
            this.projectHeaderForm.disable();
          }
        if(this.baseFormGroup) {
            this.baseFormGroup.addControl('projectHeaderForm', this.projectHeaderForm);
        }
    }

    updateProject() {
        this.project.dueDate = this.projectHeaderForm.get('projectDueDate').value.toISOString().substring(0,10);
        this.projectService.updateProject(this.project).subscribe(res => {
            if(res.data.version){
                this.project.version = res.data.version;
            }},
            error => this.messageService.add({severity:'error', summary:'Update Project', detail: error.message}));
    }

    searchTag(event: Event) {
      this.allowedTagList = [...this.allowedTagList];
    }

    public updateDueDateOfTask() {
        const taskDueDate = new Date(this.projectHeaderForm.get('taskDueDate').value).toISOString().substring(0,10);
        this.taskService.updateDueDateOfTask(this.task.task_id, taskDueDate).subscribe(
          res => this.messageService.add({severity:'success', summary:'Update Due Date Of Task', detail:'Due date of task has successfully been updated'}),
          error => {this.messageService.add({severity:'error', summary:'Update Due Date Of Task', detail: error.message})})
    }

    private loadProjectPropertiesComponent() {
        if(this.propertiesService.getPropsForType(this.project.projectTypeIdName) === undefined) {
            return;
        }
        let componentFactory = this.componentFactoryResolver
            .resolveComponentFactory(this.propertiesService.getPropsForType(this.project.projectTypeIdName));
        let viewContainerRef = this.propertiesHost.viewContainerRef;
        viewContainerRef.clear();
        let componentRef = viewContainerRef.createComponent(componentFactory);
        (<any>componentRef.instance).project = this.project;
      }

    openDelegationForm(): void {
        this.taskService.getCandidates(this.task.task_id).subscribe(
            candidates => {
            this.candidateUsers = candidates;
            this.showDelegateDialog = true;
        });
    }

    delegateTask(selectedUser: PmcUser): void {
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
  