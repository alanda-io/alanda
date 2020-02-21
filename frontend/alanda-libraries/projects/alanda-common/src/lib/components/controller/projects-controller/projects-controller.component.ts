/* import { Component, OnInit, ComponentFactoryResolver, ViewChild } from "@angular/core";
import { ActivatedRoute, ParamMap } from "@angular/router";
import { switchMap } from "rxjs/operators";
import { AlandaProjectService } from '../../../api/alandaProject.service';
import { AlandaProject } from '../../../api/models/alandaProject';


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

    constructor(private route: ActivatedRoute, private projectService: AlandaProjectService,
                private componentFactoryResolver: ComponentFactoryResolver, private projectDetailsService: ProjectDetailsServiceNg){
    }

    ngOnInit(){
        this.route.paramMap.pipe(
            switchMap((params: ParamMap) =>
                this.projectService.getProjectByProjectId(params.get('projectId')))
        ).subscribe(
          project => {
            this.project = project;
            this.loadProjectDetailsComponent(project);
        })
    }

    private loadProjectDetailsComponent(project: AlandaProject) {
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
        })
      }
  }
 */
