import { OnInit, Component, Input, ViewChild } from "@angular/core";
import { Panel } from "primeng/panel";
import { ProjectServiceNg } from "../../services/rest/project.service";
import { MessageService } from "primeng/api";

@Component({
    selector: 'project-and-processes',
    templateUrl: './project-and-processes.component.html' ,
    styles: []
  })
  export class ProjectAndProcessesComponent implements OnInit {

    @Input() project: any;
    @Input() task :any;
    @ViewChild('panel') panel: Panel;

    processInstanceId: string;
    projectTree: any[] = [];
    projectList: any[];

    constructor(private pmcProjectService: ProjectServiceNg, private messageService: MessageService) {

    }

    ngOnInit() {
      let process = this.getMainProcess(this.project.processes);
      if(process) {
        this.processInstanceId = process.processInstanceId;
      } else if(this.task) {
        this.processInstanceId = this.task.process_instance_id;
      }
      this.pmcProjectService.getProjectTreeByGuid(this.project.guid).subscribe(
        project => {
          //TODO: parentProjects, flattenChildProjects etc
          this.loadProjectTree(project, this.project.guid);
          /* for key of data
          promise[key] = data[key]
          return data */


          /* for parent in pmcProject.parents
          scope.parentList.push(parent)
        if scope.parentList.length > 0
          scope.parentsExpanded = true

        f = flattenChildProjects(pmcProject, 0, [])
        pmcProject.visible = true
        scope.projectList.length = 0
        for c in f
          scope.projectList.push(c)

        loadProjectTree(pmcProject, scope.pmcProject.guid)
        scope.loadingInProgress-- */

        });
        // TODO: handle error
       

    }

    loadProjectTree(parentProject, mainViewProjectGuid) {
      this.projectTree.push(this.parsePmcProjectForTree(parentProject, mainViewProjectGuid))
    }

    parsePmcProjectForTree(project, mainViewProjectGuid) {
      let isMainViewProject = false;
      if(project.guid == mainViewProjectGuid) {
        isMainViewProject = true;
        this.project = project;
      }

      let result:any  = {};
      result.id = project.guid;
      result.title = project.projectId + " (" + project.pmcProjectType.name + ")";
      result.projectId = project.projectId;
      result.isMainViewProject = isMainViewProject;
      result.nodes = [];
      if(project.children) {
        project.children.forEach(child => {
          result.nodes.push(this.parsePmcProjectForTree(child, mainViewProjectGuid));
        });
      }
      return result;
    }

    getMainProcess(processes): any {
      processes.forEach(p => {
        if(p.status == 'MAIN') {
          return p;
        }
      });
      return null;
    }
    

    /* proc = getMainProcess(scope.pmcProject.processes)
    if proc?
      scope.processInstanceId = proc.processInstanceId
    else if scope.task?
      scope.processInstanceId = scope.task.process_instance_id

    scope.loadingInProgress++
    pmcProjectService.getProjectTreeByGuid(scope.pmcProject.guid) */

    togglePanel(): void {
      if(this.panel.collapsed){
        // load
      }
    }


  }