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
  selector: 'project-and-processes-new',
  templateUrl: './project-and-processes-new.component.html',
  styles: [],
})
export class ProjectAndProcessesNewComponent implements OnInit {

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
      children: project.processes.map(process => ProjectAndProcessesNewComponent.mapProcessToTreeNode(process)),
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
      children: process.tasks.map(task => ProjectAndProcessesNewComponent.mapTaskToTreeNode(task)),
    };
  }

  static mapTaskToTreeNode(task: PmcTask): TreeNode {
    return {
      data: {
        label: task.task_name,
        refObject: task.process_definition_key,
        assignee: task.assignee,
        comment: task.comment,
      },
    };
  }

  ngOnInit() {
    this.getProjectWithProcessesAndTasks(this.project.guid).pipe(
      map(project => {
        return ProjectAndProcessesNewComponent.mapProjectToTreeNode(project);
      }),
    ).subscribe(treeNode => {
      this.projectTree = [treeNode];
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
}
