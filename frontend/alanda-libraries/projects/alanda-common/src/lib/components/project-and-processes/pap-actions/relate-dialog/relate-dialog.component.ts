import { Component, OnInit } from '@angular/core';
import { DynamicDialogRef, DynamicDialogConfig } from 'primeng/dynamicdialog/';
import { AlandaProject } from '../../../../api/models/project';
import { ServerOptions } from '../../../../models/serverOptions';
import { AlandaProjectType } from '../../../../api/models/projectType';
import { LazyLoadEvent } from 'primeng/api/public_api';
import { AlandaProjectApiService } from '../../../../api/projectApi.service';

@Component({
  selector: 'alanda-relate-dialog',
  templateUrl: './relate-dialog.component.html',
})
export class RelateDialogComponent implements OnInit {
  projects: AlandaProject[] = [];
  projectTypes: AlandaProjectType[] = [];
  loading = true;
  selectedProject: AlandaProject;

  serverOptions: ServerOptions = {
    pageNumber: 1,
    pageSize: 15,
    filterOptions: {},
    sortOptions: {}
  };

  constructor(private readonly projectService: AlandaProjectApiService, public ref: DynamicDialogRef, public config: DynamicDialogConfig) {}

  ngOnInit() {}

  loadProjectsLazy(event: LazyLoadEvent) {
    let sortOptions = {};
    sortOptions['project.projectId'] = { dir: 'desc', prio: 0 };
    if (event.sortField) {
      sortOptions = {};
      const dir = event.sortOrder === 1 ? 'asc' : 'desc';
      sortOptions[event.sortField] = { dir, prio: 0 };
    }
    this.serverOptions.sortOptions = sortOptions;
    this.serverOptions.filterOptions = {};
    if (this.config.data.types) {
      this.serverOptions.filterOptions['project.pmcProjectType.idName.raw'] = this.config.data.types;
    }
    if (this.config.data.guid) {
      this.serverOptions.filterOptions['project.childrenIds'] = this.config.data.guid;
    }
    for (const key in event.filters) {
      if (event.filters[key]) {
        this.serverOptions.filterOptions[key] = event.filters[key].value;
      }
    }
    this.serverOptions.pageNumber = event.first / this.serverOptions.pageSize + 1;
    this.loadProjects(this.serverOptions);
  }

  private loadProjects(serverOptions: ServerOptions) {
    this.loading = true;
    this.projectService.loadProjects(serverOptions).subscribe(
      res => {
        this.projects = [];
        res.results.forEach(value => this.projects.push(value.project));
        this.loading = false;
      },
      error => this.loading = false
    );
  }
}
