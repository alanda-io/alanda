import { Component, ViewChild, ComponentFactoryResolver, AfterViewInit } from "@angular/core";
import { ActivatedRoute, ParamMap } from "@angular/router";
import { switchMap } from "rxjs/operators";
import { PmcTask } from "../../../models/pmcTask";
import { FormsControllerDirective } from "../directives/forms-controller.directive";
import { ProjectServiceNg } from "../../../core/api/project.service";
import { FormsServiceNg } from "../../../core/services/forms.service";
import { TaskServiceNg } from "../../../core/api/task.service";
import { Project } from "../../../models/project";
import { AlandaTaskTemplate } from "../../task/models/alanda-task-template";

@Component({
    selector: 'forms-controller-component',
    templateUrl: './forms-controller.component.html',
    styleUrls: [],
  })
  export class FormsControllerComponent implements AfterViewInit {

    task: PmcTask;
    activeTab = 0;
    @ViewChild(FormsControllerDirective) formsHost: FormsControllerDirective;    
    
    constructor(private route: ActivatedRoute, private taskService: TaskServiceNg, private projectService: ProjectServiceNg,
                private componentFactoryResolver: ComponentFactoryResolver, private formsService: FormsServiceNg){
    }

    ngAfterViewInit() {
        this.route.paramMap.pipe(
            switchMap((params: ParamMap) => 
                this.taskService.getTask(params.get('taskId')))
        ).subscribe(task => {
            this.task = task;
            this.projectService.getProjectByGuid(task.pmcProjectGuid).subscribe(project => {
                this.loadTaskFormComponent(task, project);
            })
        });
    }

    loadTaskFormComponent(task: PmcTask, project: Project) {
        let componentFactory = this.componentFactoryResolver
            .resolveComponentFactory(this.formsService.getFormByKey(task.formKey));
        let viewContainerRef = this.formsHost.viewContainerRef;
        viewContainerRef.clear();
        let componentRef = viewContainerRef.createComponent(componentFactory);
        (<AlandaTaskTemplate>componentRef.instance).task = task;
        (<AlandaTaskTemplate>componentRef.instance).project = project;
      }
  }