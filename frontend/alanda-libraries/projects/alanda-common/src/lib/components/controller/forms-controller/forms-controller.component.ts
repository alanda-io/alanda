/* import { Component, ViewChild, ComponentFactoryResolver, AfterViewInit } from "@angular/core";
import { ActivatedRoute, ParamMap } from "@angular/router";
import { switchMap } from "rxjs/operators";
import { PmcTask } from "../../../models/pmcTask";
import { FormsControllerDirective } from "../directives/forms-controller.directive";
import { ProjectServiceNg } from "../../../api/alandaProject.service";
import { FormsServiceNg } from "../../../services/forms.service";
import { TaskServiceNg } from "../../../api/alandaTask.service";
import { Project } from "../../../models/project";
import { AlandaTaskTemplateComponent } from "../../task/template/alanda-task-template.component";
import { MessageService } from "primeng/api";

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
                private componentFactoryResolver: ComponentFactoryResolver, private formsService: FormsServiceNg, private messageService: MessageService){
    }

    ngAfterViewInit() {
        this.route.paramMap.pipe(
            switchMap((params: ParamMap) =>
                this.taskService.getTask(params.get('taskId')))
        ).subscribe(task => {
            this.task = task;
            this.projectService.getProjectByGuid(task.pmcProjectGuid).subscribe(project => {
                this.loadTaskFormComponent(task, project);
            }, err => this.messageService.add({severity: 'error', summary: 'project not found', detail: err.message}));
        }, err => this.messageService.add({severity: 'error', summary: 'task not found', detail: err.message}));
    }

    loadTaskFormComponent(task: PmcTask, project: Project) {
        let componentFactory = this.componentFactoryResolver
            .resolveComponentFactory(this.formsService.getFormByKey(task.formKey));
        let viewContainerRef = this.formsHost.viewContainerRef;
        viewContainerRef.clear();
        let componentRef = viewContainerRef.createComponent(componentFactory);
        (<AlandaTaskTemplateComponent>componentRef.instance).task = task;
        (<AlandaTaskTemplateComponent>componentRef.instance).project = project;
      }
  }
 */
