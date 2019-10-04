import { OnInit, Component, Input, ViewChild, ComponentFactoryResolver, AfterViewInit, ChangeDetectorRef } from "@angular/core";
import { Project } from "../../models/project";
import { PmcTask } from "../../models/pmcTask";
import { PmcUser } from "../../models/pmcUser";
import { FormGroup, FormBuilder, Validators } from "@angular/forms";
import { MessageService } from "primeng/api";
import { ProjectPropertiesDirective } from "../controller/directives/project.properties.directive";
import { ProjectPropertiesServiceNg } from "../../services/project-properties.service";
import { ProjectServiceNg } from "../../api/project.service";
import { FormsRegisterService } from "../../services/forms-register.service";
import { TaskServiceNg } from "../../api/task.service";
import { ProjectState } from "../../enums/project-status.enum";

@Component({
    selector: 'project-header-component',
    templateUrl: './project-header.component.html',
    styleUrls: [],
  })
  export class ProjectHeaderComponent implements OnInit, AfterViewInit {

    @ViewChild(ProjectPropertiesDirective) propertiesHost: ProjectPropertiesDirective;
    @Input() project: Project;
    @Input() task: PmcTask;

    snoozedTask: boolean;
    candidateUsers: PmcUser[];
    showDelegateDialog: boolean;
    allowedTagList: string[];
    priorities = [{label: '0 - Emergency', value: 0}, {label: '1 - Urgent', value: 1}, {label: '2 - Normal', value: 2}];
    projectHeaderForm: FormGroup;

    constructor(private componentFactoryResolver: ComponentFactoryResolver, private propertiesService: ProjectPropertiesServiceNg,
                private taskService: TaskServiceNg, private cdRef:ChangeDetectorRef, private messageService: MessageService, 
                private fb: FormBuilder, private projectService: ProjectServiceNg, private formsRegisterService: FormsRegisterService) {}

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
        if(this.project.status === ProjectState.CANCELED){
        this.projectHeaderForm.disable();
        }
        this.formsRegisterService.registerForm(this.projectHeaderForm, "projectHeaderForm");
    }

    updateProject() {
        this.project.dueDate = this.projectHeaderForm.get('projectDueDate').value.toISOString().substring(0,10);
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
        const taskDueDate = new Date(this.projectHeaderForm.get('taskDueDate').value).toISOString().substring(0,10);
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
  