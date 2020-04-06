import { Component, Input, OnInit } from '@angular/core';
import { TreeNode } from 'primeng/api';
import { map, mergeMap, toArray } from 'rxjs/operators';
import { from, Observable } from 'rxjs';
import { AlandaProject } from '../../api/models/project';
import { AlandaTask } from '../../api/models/task';
import { AlandaProjectApiService } from '../../api/projectApi.service';
import { AlandaTaskApiService } from '../../api/taskApi.service';
import { AlandaProcess } from '../../api/models/process';
import { ProjectAndProcessesService } from '../../services/project-and-processes.service';

@Component({
  selector: 'alanda-project-and-processes',
  templateUrl: './project-and-processes.component.html',
  styles: [],
})
export class AlandaProjectAndProcessesComponent implements OnInit {

  @Input() project: AlandaProject;
  @Input() task: AlandaTask;

  projectTree: TreeNode[] = [];

  constructor(private projectService: AlandaProjectApiService, private taskService: AlandaTaskApiService,
    private projectAndProcessesService: ProjectAndProcessesService) {
  }


  ngOnInit() {
    from(this.flattenProjects(this.project)).pipe(
      mergeMap(flattenProject => {
        return this.getProjectWithProcessesAndTasks(flattenProject.guid).pipe(
          map(project => {
            return this.projectAndProcessesService.mapProjectToTreeNode(project);
          })
        );
      }),
      toArray()
    ).subscribe(treeNodes => this.projectTree = treeNodes);
  }

  private getProjectWithProcessesAndTasks(guid: number): Observable<AlandaProject> {
    return this.projectService.getProjectByGuid(guid, true).pipe(
      mergeMap(project => this.getProcessesAndTasks(project.processes).pipe(
        map(processes => {
          project.processes = processes;
          return project;
        }),
        ),
      ));
  }

  private getProcessesAndTasks(processes: AlandaProcess[]): Observable<AlandaProcess[]> {
    return from(processes).pipe(
      mergeMap(proc =>
        this.taskService.search(proc.processInstanceId).pipe(
          map(tasks => {
            proc.tasks = tasks;
            return proc;
          }),
        )),
      toArray(),
    );
  }

  // TODO: improve this method to support nested and more complex project family structures
  private flattenProjects(project: AlandaProject): AlandaProject[] {
    const flattenProjectList: AlandaProject[] = [];

    if (project.parents && project.parents.length > 0) {
      project.parents.forEach(parent => {
        flattenProjectList.push(parent);
      });
    }
    flattenProjectList.push(project);
    if (project.children && project.children.length > 0) {
      project.children.forEach(child => {
        flattenProjectList.push(child);
      });
    }
    return flattenProjectList;
  }
}
