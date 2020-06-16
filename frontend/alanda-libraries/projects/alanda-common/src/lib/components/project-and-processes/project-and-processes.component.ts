import { Component, Input, OnInit } from '@angular/core';
import { TreeNode, MenuItem } from 'primeng/api';
import { map, toArray, concatMap } from 'rxjs/operators';
import { from, Observable } from 'rxjs';
import {
  ProjectAndProcessesService,
  TreeNodeData,
} from './project-and-processes.service';
import {Project} from '../../models/project';
import {PmcTask} from '../../models/pmcTask';
import {ProjectServiceNg} from '../../api/project.service';
import {Process} from '../../models/process';
import {ProjectState} from '../../enums/project-status.enum';
import {ProcessRelation} from '../../enums/process-relation.enum';

@Component({
  selector: 'alanda-project-and-processes',
  templateUrl: './project-and-processes.component.html',
  styleUrls: ['./project-and-processes.component.css'],
})
export class AlandaProjectAndProcessesComponent implements OnInit {
  @Input() project: Project;
  @Input() task: PmcTask;

  optionItems: MenuItem[];
  treeStructure: TreeNode[] = [];
  // TODO: create interface for tasksAndProcesses response
  allowedProcesses: { [projectGuid: number]: any[] } = {};
  projectWithParentsAndChildren: Project;
  loading: boolean;

  constructor(
    private readonly projectService: ProjectServiceNg,
    private readonly projectAndProcessesService: ProjectAndProcessesService
  ) {}

  ngOnInit() {}

  getIconClass(type: string): string {
    switch (type) {
      case 'project':
      case 'parent':
      case 'child':
        return 'fa fa-book';
      case 'task':
        return 'fa fa-user';
      case 'process':
        return 'fa fa-random';
      case 'activity':
        return 'fa fa-clock';
      default:
        return '';
    }
  }

  onSubprocessSelected(
    rowData: TreeNodeData,
    selection: { processName: string; processDefinitionKey: string }
  ) {
    const process: Process = {};
    process.status = ProjectState.NEW;
    process.relation = ProcessRelation.CHILD;
    process.workDetails = '';
    process.processKey = selection.processDefinitionKey;
    process.label = selection.processName;
    if (!rowData.refObject) {
      process.businessObject = rowData.relatedProject.refObjectIdName;
    }

    this.projectService
      .saveProjectProcess(rowData.relatedProject.guid, process)
      .subscribe((res) => {
        this.loadProjectAndProcesses();
      });

  }

  loadProjectAndProcesses(collapsed?: boolean) {
    if (collapsed) {
      return;
    }
    this.loading = true;
    this.projectService
      .getProjectByGuid(this.project.guid, true)
      .subscribe((projectTree) => {
        from(this.flattenProjects(projectTree))
          .pipe(
            concatMap((flattenProject) => {
              return this.getProjectWithProcessesAndTasks(flattenProject).pipe(
                map((project) => {
                  return this.projectAndProcessesService.mapProjectToTreeNode(
                    project,
                    this.project
                  );
                })
              );
            }),
            toArray(),
            map((mappedProjects) => this.updateTreeStructure(mappedProjects))
          )
          .subscribe((treeNodes) => {
            this.treeStructure = treeNodes;
            this.loading = false;
          });
      });
  }

  private getProjectWithProcessesAndTasks(
    project: Project
  ): Observable<Project> {
    return this.projectService
      .getProcessesAndTasksForProject(project.guid)
      .pipe(
        map((result: any) => {
          project.processes = result.active;
          this.allowedProcesses[
            project.guid
          ] = result.allowed.default.map((p) => ({
            label: p.processName,
            processKey: p.processDefinitionKey,
          }));
          return project;
        })
      );
  }

  private updateTreeStructure(projects: TreeNode[]): TreeNode[] {
    const updatedTreeStructure: TreeNode[] = [];
    const parents = projects.filter((node) => node.data.type === 'parent');
    const children = projects.filter((node) => node.data.type === 'child');
    /* .map(filteredNode => this.projectAndProcessesService
      .mapNewProcessToTreeNode(filteredNode, null)); */

    const currentProject = projects.filter(
      (node) => node.data.type === 'project'
    );
    /* .map(filteredNode => this.projectAndProcessesService
      .mapNewProcessToTreeNode(filteredNode, null)); */

    if (parents.length) {
      updatedTreeStructure.push({
        data: { label: `Parent Projects (${parents.length})` },
        children: parents,
      });
    }
    updatedTreeStructure.push(...currentProject);
    if (children.length) {
      updatedTreeStructure.push({
        data: { label: `Child Projects (${children.length})` },
        children,
      });
    }
    return updatedTreeStructure;
  }

  private flattenProjects(project: Project): Project[] {
    const projects: Project[] = [];
    if (!project.parentIds) {
      project.parentIds = [];
    }
    if (!project.childrenIds) {
      project.childrenIds = [];
    }
    if (project.parents) {
      project.parents.forEach((parent) => {
        parent.childrenIds
          ? parent.childrenIds.push(project.guid)
          : (parent.childrenIds = [project.guid]);
        projects.push(parent);
      });
    }
    projects.push(project);

    if (project.children) {
      project.children.forEach((child) => {
        child.parentIds
          ? child.parentIds.push(project.guid)
          : (child.parentIds = [project.guid]);
        projects.push(child);
      });
    }
    return projects;
  }
}
