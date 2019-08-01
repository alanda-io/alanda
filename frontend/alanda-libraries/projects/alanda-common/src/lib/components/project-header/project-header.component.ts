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

@Component({
    selector: 'project-header-component',
    templateUrl: './project-header.component.html',
    styleUrls: [],
  })
  export class ProjectHeaderComponent implements OnInit, AfterViewInit{

    @ViewChild(ProjectPropertiesDirective) propertiesHost: ProjectPropertiesDirective;
    snoozedTask: boolean;
    candidateUsers: PmcUser[];
    showDelegateDialog: boolean;
    allowedTagList: string[];
    currentUser: PmcUser;
    @Input() project: any;
    @Input() task: PmcTask;
    priorities = [{label: '0 - Emergency', value: 0}, {label: '1 - Urgent', value: 1}, {label: '2 - Normal', value: 2}];

    projectHeaderForm: FormGroup;
    @Input() formGroup: FormGroup;

    constructor(private componentFactoryResolver: ComponentFactoryResolver, private propertiesService: ProjectPropertiesServiceNg,
                private projectService: ProjectServiceNg, private taskService: TaskServiceNg,
                private cdRef:ChangeDetectorRef, private userService: PmcUserServiceNg, private messageService: MessageService, 
                private fb: FormBuilder) {
                }

    ngOnInit() {
        this.userService.getCurrentUser().subscribe(
            user => this.currentUser = user,
           error => this.messageService.add({severity:'error', summary:'Get Current User', detail: error.message})
        );
        this.allowedTagList = this.project.pmcProjectType.allowedTagList.map(tag => {return {value: tag}});
        this.project.tag = {value: this.project.tag[0]};
        this._initFormGroup();
    }

    ngAfterViewInit() {
        this.loadProjectPropertiesComponent();
        this.cdRef.detectChanges();
    }

    _initFormGroup(){
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
        if(this.formGroup) {
            this.formGroup.addControl('projectHeaderForm', this.projectHeaderForm);
        }
    }

    public updateProject() {
        /* let updatedProject = Object.assign({},this.project);
        updatedProject.tag = [this.formGroup.get('tag').value.value];
        updatedProject.priority = this.formGroup.get('prio').value.value;
        updatedProject.title = this.formGroup.get('projectTitle').value;
        updatedProject.details = this.formGroup.get('projectDetails').value; */

        /* this.project.tag = [this.project.tag.value];
        this.project.dueDate = this.formGroup.get('projectDueDate').value.toISOString().substring(0,10);
        this.projectService.updateProject(this.project).subscribe(res => {
            if(res.data.version){
                this.project.version = res.data.version;
            }},
            error => this.messageService.add({severity:'error', summary:'Update Project', detail: error.message})); */
    }

    public updateDueDateOfTask() {
        const taskDueDate = new Date(this.formGroup.get('taskDueDate').value).toISOString().substring(0,10);
        console.log(taskDueDate);
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

      public openDelegationForm(): void {
        this.taskService.getCandidates(this.task.task_id).subscribe(
          candidates => {
            this.candidateUsers = candidates;
            this.showDelegateDialog = true;
          }
        );
      }

      public delegateTask(selectedUser: PmcUser): void {
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
  