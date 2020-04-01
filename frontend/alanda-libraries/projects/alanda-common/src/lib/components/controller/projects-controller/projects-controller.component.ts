import { Component, OnInit, ComponentFactoryResolver, ViewChild } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { switchMap } from 'rxjs/operators';
import { AlandaProjectApiService } from '../../../api/projectApi.service';
import { AlandaProjectDetailsService } from '../../../services/project-details.service';
import { AlandaProject } from '../../../api/models/project';
import { ProjectDetailsDirective } from '../directives/project-details.directive';


@Component({
    templateUrl: './projects-controller.component.html',
    styleUrls: [],
  })
  export class AlandaProjectsControllerComponent implements OnInit {

    project: any;
    pid: string;
    activeTab = 0;
    @ViewChild(ProjectDetailsDirective) projectDetailsHost: ProjectDetailsDirective;

    constructor(private route: ActivatedRoute, private projectService: AlandaProjectApiService,
                private componentFactoryResolver: ComponentFactoryResolver, private projectDetailsService: AlandaProjectDetailsService) {
    }

    ngOnInit() {
        this.route.paramMap.pipe(
            switchMap((params: ParamMap) =>
                this.projectService.getProjectByProjectId(params.get('projectId')))
        ).subscribe(
          project => {
            this.project = project;
            this.loadProjectDetailsComponent(project);
        });
    }

    private loadProjectDetailsComponent(project: AlandaProject) {
      const componentFactory = this.componentFactoryResolver
          .resolveComponentFactory(this.projectDetailsService.getPropsForType(project.projectTypeIdName));
      const viewContainerRef = this.projectDetailsHost.viewContainerRef;
      viewContainerRef.clear();
      this.projectService.getProjectMainProcess(project.guid).subscribe(
        (process) => {
          this.pid = process.processInstanceId;
          const componentRef = viewContainerRef.createComponent(componentFactory);
          (<any>componentRef.instance).project = project;
          (<any>componentRef.instance).pid = process.processInstanceId;
        });
      }
  }

