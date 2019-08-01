import { Component, OnInit, ComponentFactoryResolver, ViewChild } from "@angular/core";
import { ActivatedRoute, ParamMap, Router } from "@angular/router";
import { ProjectServiceNg } from "../../../../services/rest/project.service";
import { ProjectDetailsDirective } from "../../directives/project-details-controller.directive";
import { switchMap } from "rxjs/operators";
import { ProjectDetailsServiceNg } from "../../../../services/projectdetails.service";
import { MessageService } from "primeng/api";


@Component({
    selector: 'projects-controller-component',
    templateUrl: './projects-controller.component.html',
    styleUrls: [],
  })
  export class ProjectsControllerComponent implements OnInit{

    project: any;
    pid: string;
    activeTab = 0;
    @ViewChild(ProjectDetailsDirective) projectDetailsHost: ProjectDetailsDirective;

    constructor(private router: Router, private route: ActivatedRoute, private projectService: ProjectServiceNg,
                private componentFactoryResolver: ComponentFactoryResolver, private projectDetailsService: ProjectDetailsServiceNg,
                private messageService: MessageService){
    }

    ngOnInit(){
        this.route.paramMap.pipe(
            switchMap((params: ParamMap) => 
                this.projectService.getProjectByProjectId(params.get('projectId')))
        ).subscribe(
          (project) => {
            this.project = project;
            this.loadProjectDetailsComponent(project);
          },
          error => this.messageService.add({severity:'error', summary:'Get Project By ProjectID', detail: error.message}));
    }

    loadProjectDetailsComponent(project: any) {
      let componentFactory = this.componentFactoryResolver
          .resolveComponentFactory(this.projectDetailsService.getPropsForType(project.projectTypeIdName));
      let viewContainerRef = this.projectDetailsHost.viewContainerRef;
      viewContainerRef.clear();
      this.projectService.getProjectMainProcess(project.guid).subscribe(
        (process) => {
          this.pid = process.processInstanceId;
          let componentRef = viewContainerRef.createComponent(componentFactory);
          (<any>componentRef.instance).project = project;
          (<any>componentRef.instance).pid = process.processInstanceId;
        },
        error => this.messageService.add({severity:'error', summary:'Get Project Mainprocess', detail: error.message}));
    }
  }