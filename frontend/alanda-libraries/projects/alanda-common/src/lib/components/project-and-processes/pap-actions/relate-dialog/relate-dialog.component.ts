import { Component, OnInit } from '@angular/core';
import {Project} from '../../../../models/project';
import {ProjectType} from '../../../../models/projectType';
import {ServerOptions} from '../../../../models/serverOptions';
import {ProjectServiceNg} from '../../../../api/project.service';
import {DynamicDialogConfig, DynamicDialogRef, LazyLoadEvent} from 'primeng/api';

@Component({
  selector: 'alanda-relate-dialog',
  templateUrl: './relate-dialog.component.html',
})
export class RelateDialogComponent implements OnInit {
  projects: Project[] = [];
  projectTypes: ProjectType[] = [];
  loading = true;
  selectedProject: Project;

  serverOptions: ServerOptions = {
    pageNumber: 1,
    pageSize: 15,
    filterOptions: {},
    sortOptions: {}
  };

  constructor(private readonly projectService: ProjectServiceNg, public ref: DynamicDialogRef, public config: DynamicDialogConfig) {}

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
    if (this.config.data.filterOptions) {
      for (const key of Object.keys(this.config.data.filterOptions)) {
        this.serverOptions.filterOptions[key] = this.config.data.filterOptions[key];
      }
    }
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
