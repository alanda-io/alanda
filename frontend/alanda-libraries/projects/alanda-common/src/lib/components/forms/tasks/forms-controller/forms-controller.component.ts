import { Component, OnInit, ViewChild, ComponentFactoryResolver } from "@angular/core";
import { ActivatedRoute, ParamMap } from "@angular/router";
import { switchMap } from "rxjs/operators";
import { TaskServiceNg } from "../../../../services/rest/task.service";
import { FormsServiceNg } from "../../../../services/forms.service";
import { ProjectServiceNg } from "../../../../services/rest/project.service";
import { FormsControllerDirective } from "../../directives/forms-controller.directive";
import { PmcTask } from "../../../../models/pmcTask";
import { MessageService } from "primeng/api";
import { Project } from "../../../../models/project.model";

@Component({
    selector: 'forms-controller-component',
    templateUrl: './forms-controller.component.html',
    styleUrls: [],
  })
  export class FormsControllerComponent implements OnInit{

    project: Project;
    task: PmcTask;
    activeTab = 0;
    @ViewChild(FormsControllerDirective) formsHost: FormsControllerDirective;    
    
    constructor(private route: ActivatedRoute, private taskService: TaskServiceNg,
                private projectService: ProjectServiceNg, private componentFactoryResolver: ComponentFactoryResolver, 
                private formsService: FormsServiceNg, private messageService: MessageService){
    }

    ngOnInit(){
        this.route.paramMap.pipe(
            switchMap((params: ParamMap) => 
                this.taskService.getTask(params.get('taskId')))
        ).subscribe((task) => {
            this.task = task;
            this.projectService.getProjectByGuid(this.task.pmcProjectGuid).subscribe(
                (project) => {
                    this.project = project;
                    this.loadTaskFormComponent(task, project);
                },
                error => this.messageService.add({severity:'error', summary:'Get Project By Guid', detail: error.message}));
        });
    }

    loadTaskFormComponent(task: PmcTask, project: any) {
        let componentFactory = this.componentFactoryResolver
            .resolveComponentFactory(this.formsService.getFormByKey(this.task.formKey));
        let viewContainerRef = this.formsHost.viewContainerRef;
        viewContainerRef.clear();
        let componentRef = viewContainerRef.createComponent(componentFactory);
        (<any>componentRef.instance).task = task;
        (<any>componentRef.instance).project = project;
      }
  }