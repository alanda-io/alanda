import { Component, Input, OnInit } from '@angular/core';
import { TreeNode, MenuItem } from 'primeng/api';
import { map, mergeMap, toArray } from 'rxjs/operators';
import { from, Observable, of } from 'rxjs';
import { AlandaProject } from '../../api/models/project';
import { AlandaTask } from '../../api/models/task';
import { AlandaProjectApiService } from '../../api/projectApi.service';
import { AlandaTaskApiService } from '../../api/taskApi.service';
import { AlandaProcess } from '../../api/models/process';
import { ProjectAndProcessesService, TreeNodeData } from '../../services/project-and-processes.service';

@Component({
  selector: 'alanda-project-and-processes',
  templateUrl: './project-and-processes.component.html',
  styleUrls : ['./project-and-processes.component.css'],
})
export class AlandaProjectAndProcessesComponent implements OnInit {

  @Input() project: AlandaProject;
  @Input() task: AlandaTask;

  optionItems: MenuItem[];
  treeStructure: TreeNode[] = [];
  allowedProcesses: { [projectGuid: number]: AlandaProcess[]; } = {};
  projectWithParentsAndChildren: AlandaProject;

  constructor(private projectService: AlandaProjectApiService, private projectAndProcessesService: ProjectAndProcessesService) {}

  ngOnInit() {}

  getIconClass(type: string): string {
    switch (type) {
      case 'project': return 'fa fa-book';
      case 'task': return 'fa fa-user';
      case 'process': return 'fa fa-random';
      case 'activity': return 'fa fa-clock';
    }
  }

  onSubprocessSelected(rowData: TreeNodeData, selection) {
    selection.stats = 'NEW';
    selection.relation = 'CHILD';
    if (!rowData.refObject) {
      selection.businessObject = rowData.project.refObjectIdName;
    }
    this.projectService.saveProjectProcess(rowData.project.guid, selection).subscribe();
  }

  loadProjectAndProcesses(collapsed?: boolean) {
    if (collapsed) {
      return;
    }
    this.projectService.getProjectByGuid(this.project.guid, true).subscribe(projectTree => {
      from(this.flattenProjects(projectTree)).pipe(
        mergeMap(flattenProject => {
          return this.getProjectWithProcessesAndTasks(flattenProject).pipe(
            map(project => {
              return this.projectAndProcessesService.mapProjectToTreeNode(project, this.project.guid);
            })
          );
        }),
        toArray(),
      ).subscribe(treeNodes => {
        this.treeStructure.push(...treeNodes);
        this.sortTreeStructure();
      });
    });
  }

  private sortTreeStructure() {
    const parents = this.treeStructure.filter(node => node.data.type === 'parent-project');
    const projects = this.treeStructure.filter(node => node.data.type === 'project');
    const updatedTreeStructure: TreeNode[] = [];
    if (parents.length > 0) {
      const dummyParentHolder = {
        data: {
          label: `Parent Projects (${parents.length})`,
        },
        children: parents
      };
      updatedTreeStructure.push(dummyParentHolder);
    }
    projects.forEach(value => {
      updatedTreeStructure.push(value);
      updatedTreeStructure.push(this.projectAndProcessesService
        .mapAllowedProcessesToTreeNode(this.allowedProcesses[value.data.project.guid], value.data.project.guid));
    });
    this.treeStructure = updatedTreeStructure;
  }

  private getProjectWithProcessesAndTasks(project: AlandaProject): Observable<AlandaProject> {
    return this.projectService.getProcessesAndTasksForProject(project.guid).pipe(
      map((result: any) => {
        project.processes = result.active;
        this.allowedProcesses[project.guid] = result.allowed;
        return project;
      }));
  }

  // TODO: improve this method to support more complex project relations
  // Find better solution for childrenIds, parentIds used in project-and-processes.service.ts
  private flattenProjects(project: AlandaProject): AlandaProject[] {
    const flattenProjectList: AlandaProject[] = [];
    if (!project.parentIds) {
      project.parentIds = [];
    }
    if (!project.childrenIds) {
      project.childrenIds = [];
    }
    if (project.parents?.length > 0) {
      project.parents.forEach(parent => {
        if (!parent.childrenIds) {
          parent.childrenIds = [];
        }
        if (!parent.parentIds) {
          parent.parentIds = [];
        }
        parent.childrenIds.push(project.guid);
        project.parentIds.push(parent.guid);
        flattenProjectList.push(parent);
      });
    }
    flattenProjectList.push(project);
    if (project.children?.length > 0) {
      project.children.forEach(child => {
        if (!child.parentIds) {
          child.parentIds = [];
        }
        if (!child.childrenIds) {
          child.childrenIds = [];
        }
        project.childrenIds.push(child.guid);
        child.parentIds.push(project.guid);
        flattenProjectList.push(child);
      });
    }
    return flattenProjectList;
  }

}
