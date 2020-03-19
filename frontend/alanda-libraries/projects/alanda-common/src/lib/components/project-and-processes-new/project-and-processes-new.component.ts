import { Input, OnInit, Component } from '@angular/core';
import { Project } from '../../models/project';
import { PmcTask } from '../../models/pmcTask';
import { TreeNode } from 'primeng/api';
import { ProjectServiceNg } from '../../api/project.service';
import { Process } from '../../models/process';
import { map, mergeMap, toArray } from 'rxjs/operators';
import { TaskServiceNg } from '../../api/task.service';
import { Observable, from } from 'rxjs';

@Component({
    selector: 'project-and-processes-new',
    templateUrl: './project-and-processes-new.component.html' ,
    styles: []
  })
  export class ProjectAndProcessesNewComponent implements OnInit {

    @Input() project: Project;
    @Input() task: PmcTask;

    projectTree: TreeNode[] = [];

    constructor(private pmcProjectService: ProjectServiceNg, private taskService: TaskServiceNg) {}

    ngOnInit() {
      this.getProjectWithProcessesAndTasks(this.project.guid).pipe(
        map(project => this.setupTreeNodes(project)),
      )
      .subscribe(treeNodes => {
        this.projectTree = treeNodes;
      });
    }

    // this method should return a project observable with all processes and tasks
    private getProjectWithProcessesAndTasks(guid: number): Observable<Project> {
      return this.pmcProjectService.getProjectByGuid(guid, true).pipe(
        mergeMap(project => this.getProcessesAndTasks(project.processes).pipe(
          map(processes => {
            project.processes = processes;
            return project;
          })
        )
      ));
    }

    private getProcessesAndTasks(processes: Process[]): Observable<Process[]> {
      return from(processes).pipe(
        mergeMap(proc =>
          this.taskService.search(proc.processInstanceId).pipe(
            map(tasks => {
              proc.tasks = tasks;
              return proc;
            })
          )
        ),
        toArray()
      );
    }


      /* private addTasksToProcesses(processes: Process[]): any {
      const requestList: Observable<PmcTask[]>[] = [];
      processes.forEach(proc => {
        requestList.push(this.taskService.search(proc.processInstanceId));
      });
      forkJoin(requestList);
    } */


    private setupTreeNodes(project: Project): TreeNode[] {
      console.log("complete project", project);
      /* this.pmcProjectService.getProcessesAndTasksForProject(project.guid).subscribe(res => {
        res.active.forEach(proc => {
          // proc.label
          proc.tasks.forEach(task => {
            // task.task_name
          });
        });
      }); */


      const childProjects: TreeNode[] = [];
      const parentProject: TreeNode = {};
      const tempTree = [];
      parentProject.data = project;
      project.children.forEach(child => {
        childProjects.push({data: child, parent: parentProject});
      });
      parentProject.children = childProjects;
      tempTree.push(parentProject);
      return tempTree;
    }

    private processOrTaskToTreeNode(n: Process | PmcTask): TreeNode {
      if (this.isProcess(n)) {

      } else {

      }

      return null;
    }

    private isProcess(p: any): p is Process {
      return p.processInstanceId !== undefined;
    }

    private isTask(p: any): p is PmcTask {
      return p.task_id !== undefined;
    }
  }
