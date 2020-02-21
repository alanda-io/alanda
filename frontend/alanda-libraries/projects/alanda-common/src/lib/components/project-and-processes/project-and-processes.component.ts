import { OnInit, Component, Input } from "@angular/core";
import { ProcessRelation } from "../../enums/processRelation.enum";
import { AlandaProject } from '../../api/models/alandaProject';
import { AlandaProjectService } from '../../api/alandaProject.service';
import { AlandaProcess } from '../../api/models/alandaProcess';
import { AlandaTask } from '../../api/models/alandaTask';

interface ProjectTree {
  id: number;
  title: string;
  projectId: string;
  isMainViewProject: boolean;
  nodes: ProjectTree[];
}

export interface FlattenProjectResult {
  project: AlandaProject,
  level: number,
  initExpanded: boolean
  processesAndTasks: Map<string, any>;
}

@Component({
    selector: 'project-and-processes',
    templateUrl: './project-and-processes.component.html' ,
    styles: []
  })
  export class ProjectAndProcessesComponent implements OnInit {

    @Input() project: AlandaProject;
    @Input() task: AlandaTask;

    mainProcess: AlandaProcess;
    pid: string;
    projectTree: ProjectTree[] = [];
    projectList: FlattenProjectResult[] = [];
    parentList: AlandaProject[] = [];

    constructor(private pmcProjectService: AlandaProjectService) {}

    ngOnInit() {
      this.mainProcess = this.getMainProcess(this.project.processes);
      this.pid = this.task ? this.task.process_instance_id : this.mainProcess.processInstanceId;
      this.pmcProjectService.getProjectTreeByGuid(this.project.guid).subscribe(project => {
        for(let parent of project.parents) {
          this.parentList.push(parent);
        }
        this.flattenChildProjects(project, 0, []);
        this.loadProjectTree(project, this.project.guid);
        //TODO: flattenChildProjects, push to parentList
      });
    }

    private loadProjectTree(parentProject: AlandaProject, mainViewProjectGuid: number) {
      this.projectTree.push(this.parsePmcProjectForTree(parentProject, mainViewProjectGuid))
    }

    private parsePmcProjectForTree(project: AlandaProject, mainViewProjectGuid: number): ProjectTree {
      let isMainViewProject = false;
      if(project.guid === mainViewProjectGuid) {
        isMainViewProject = true;
        this.project = project;
      }

      let result: ProjectTree = {
        id: project.guid,
        title: project.projectId + ' (' + project.pmcProjectType.name + ')',
        projectId: project.projectId,
        isMainViewProject: isMainViewProject,
        nodes: []
      };
      // TODO: iterate over children, recursive call
      return result;
    }

    private getMainProcess(processes: AlandaProcess[]): AlandaProcess {
      return processes.filter(process => process.relation == ProcessRelation.MAIN)[0];
    }

    private flattenChildProjects(project: AlandaProject, level: number, result: FlattenProjectResult[]) {
      this.pmcProjectService.getProcessesAndTasksForProject(project.guid).subscribe(res => {
        let flattenProject: FlattenProjectResult = {project: project, level: level, initExpanded: !level, processesAndTasks: res};
        this.projectList.push(flattenProject);
        for(let child of project.children) {
          this.flattenChildProjects(child, level + 1, result);
        }
      });
    }

  }
