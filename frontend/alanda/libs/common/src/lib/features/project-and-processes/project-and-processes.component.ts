import { Component, Input, OnInit } from '@angular/core';
import { TreeNode, MenuItem } from 'primeng/api';
import { map, toArray, switchMap, finalize, exhaustMap } from 'rxjs/operators';
import { from, Observable, of } from 'rxjs';
import { AlandaProject } from '../../shared/api/models/project';
import { AlandaTask } from '../../shared/api/models/task';
import { AlandaProjectApiService } from '../../shared/api/projectApi.service';
import { AlandaProcess } from '../../shared/api/models/process';
import { ProjectAndProcessesService } from './project-and-processes.service';

@Component({
  selector: 'alanda-project-and-processes',
  templateUrl: './project-and-processes.component.html',
  styleUrls: ['./project-and-processes.component.css'],
})
export class AlandaProjectAndProcessesComponent implements OnInit {
  @Input() project: AlandaProject;
  @Input() task: AlandaTask;
  @Input() dateFormat = 'yyyy-MM-dd';

  optionItems: MenuItem[];
  treeStructure: TreeNode[] = [];
  allowedProcesses: { [projectGuid: number]: AlandaProcess[] } = {};
  projectWithParentsAndChildren: AlandaProject;
  loading: boolean;

  constructor(
    private readonly projectService: AlandaProjectApiService,
    private readonly papService: ProjectAndProcessesService,
  ) {}

  ngOnInit() {}

  getIconClass(type: string): string {
    switch (type) {
      case 'project':
      case 'child':
      case 'parent':
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

  onSubprocessSelected(rowData: any, rowNode: any) {
    const selectedProcess: AlandaProcess = rowData;
    if (selectedProcess) {
      /* this.projectService
      .saveProjectProcess(rowData.relatedProject.guid, selectedProcess)
      .subscribe(() => {
        this.loadNode(rowNode.parent);
      }); */
    } else {
      /* const process: AlandaProcess = {};
      process.status = ProjectState.NEW;
      process.relation = ProcessRelation.CHILD;
      process.workDetails = '';
      process.processKey = selection.processKey;
      process.label = selection.label;
      if (!rowData.refObject) {
        process.businessObject = rowData.relatedProject.refObjectIdName;
      }
      this.projectService
        .saveProjectProcess(rowData.relatedProject.guid, process)
        .subscribe(() => {
          this.loadNode(rowNode.parent);
        });*/
    }
  }

  loadProjectAndProcesses(collapsed?: boolean) {
    if (collapsed) {
      return;
    }
    this.loading = true;
    this.projectService
      .getProjectByGuid(this.project.guid, true)
      .pipe(
        switchMap((project) => from(this.flattenProjects(project))),
        map((flattenProject) =>
          this.papService.mapProjectToTreeNode(flattenProject, this.project),
        ),
        toArray(),
        finalize(() => (this.loading = false)),
      )
      .subscribe(
        (treeNodes) =>
          (this.treeStructure = this.updateTreeStructure(treeNodes)),
      );
  }

  onNodeExpand(event) {
    const node = event.node;
    if (node.data.type === 'project' || node.data.type === 'child') {
      this.loadNode(node);
    }
  }

  private getProjectWithProcessesAndTasks(
    project: AlandaProject,
  ): Observable<AlandaProject> {
    return this.projectService
      .getProcessesAndTasksForProject(project.guid)
      .pipe(
        map((result: any) => {
          this.allowedProcesses[project.guid] = result.allowed.default;
          project.processes = result.active;
          return project;
        }),
      );
  }

  private updateTreeStructure(projects: TreeNode[]): TreeNode[] {
    const updatedTreeStructure: TreeNode[] = [];
    const parents: TreeNode[] = projects.filter(
      (node) => node.data.type === 'parent',
    );
    const children: TreeNode[] = projects.filter(
      (node) => node.data.type === 'child',
    );
    const currentProject: TreeNode[] = projects.filter(
      (node) => node.data.type === 'project',
    );

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

  private flattenProjects(project: AlandaProject): AlandaProject[] {
    const projects: AlandaProject[] = [];
    if (!project.parentIds) {
      project.parentIds = [];
    }
    if (!project.childrenIds) {
      project.childrenIds = [];
    }
    project.parents?.forEach((parent) => {
      parent.childrenIds
        ? parent.childrenIds.push(project.guid)
        : (parent.childrenIds = [project.guid]);
      projects.push(parent);
    });
    projects.push(project);
    project.children?.forEach((child) => {
      child.parentIds
        ? child.parentIds.push(project.guid)
        : (child.parentIds = [project.guid]);
      projects.push(child);
    });
    return projects;
  }

  private loadNode(node) {
    this.loading = true;
    this.getProjectWithProcessesAndTasks(node.data.value)
      .pipe(
        exhaustMap((project) => from(project.processes)),
        map((process) =>
          this.papService.mapProcessToTreeNode(process, node.data.value),
        ),
        toArray(),
        finalize(() => (this.loading = false)),
      )
      .subscribe((processTreeNodes) => {
        processTreeNodes.push(
          this.papService.mapNewProcessToTreeNode(null, node.data.value),
        );
        node.children = processTreeNodes;
        this.treeStructure = [...this.treeStructure];
      });
  }
}
