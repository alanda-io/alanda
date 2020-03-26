import { Component, Input, OnInit } from '@angular/core';
import { Project } from '../../models/project';
import { PmcTask } from '../../models/pmcTask';
import { TreeNode } from 'primeng/api';
import { ProjectServiceNg } from '../../api/project.service';
import { Process } from '../../models/process';
import { map, mergeMap, toArray } from 'rxjs/operators';
import { TaskServiceNg } from '../../api/task.service';
import { from, Observable } from 'rxjs';

@Component({
  selector: 'project-and-processes',
  templateUrl: './project-and-processes.component.html',
  styles: [],
})
export class ProjectAndProcessesComponent implements OnInit {

  @Input() project: Project;
  @Input() task: PmcTask;

  projectTree: TreeNode[] = [];

  constructor(private pmcProjectService: ProjectServiceNg, private taskService: TaskServiceNg) {
  }

  static mapProjectToTreeNode(project: Project): TreeNode {
    return {
      data: {
        label: project.projectId,
        refObject: project.refObjectIdName,
        assignee: project.ownerName,
        start: project.createDate,
        comment: project.comment,
      },
      children: project.processes.map(process => ProjectAndProcessesComponent.mapProcessToTreeNode(process)),
      expanded: true
    };
  }

  static mapProcessToTreeNode(process: Process): TreeNode {
    return {
      data: {
        label: process.label,
        refObject: process.processKey,
        start: process.startTime,
        end: process.endTime,
        comment: process.resultComment,
      },
      children: process.tasks.map(task => ProjectAndProcessesComponent.mapTaskToTreeNode(task)),
      expanded: true
    };
  }

  static mapTaskToTreeNode(task: PmcTask): TreeNode {
    return {
      data: {
        label: task.task_name,
        refObject: task.process_definition_key,
        assignee: task.assignee,
        comment: task.comment,
        routerLink: `/forms/${task.formKey}/${task.task_id}`
      },
    };
  }

  ngOnInit() {
    this.flattenProjects(this.project).forEach(flattenProject => {
      this.getProjectWithProcessesAndTasks(flattenProject.guid).pipe(
        map(project => {
          return ProjectAndProcessesComponent.mapProjectToTreeNode(project);
        }),
      ).subscribe(treeNode => {
        this.projectTree = [treeNode];
      });
    });
  }

  private getProjectWithProcessesAndTasks(guid: number): Observable<Project> {
    return this.pmcProjectService.getProjectByGuid(guid, true).pipe(
      mergeMap(project => this.getProcessesAndTasks(project.processes).pipe(
        map(processes => {
          project.processes = processes;
          return project;
        }),
        ),
      ));
  }

  private getProcessesAndTasks(processes: Process[]): Observable<Process[]> {
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
  private flattenProjects(project: Project): Project[] {
    const flattenProjectList: Project[] = [];

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
