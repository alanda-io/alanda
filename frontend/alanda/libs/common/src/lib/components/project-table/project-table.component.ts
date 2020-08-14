import { Component, OnInit, Input, ViewChild } from '@angular/core';
import { LazyLoadEvent, MenuItem, MessageService } from 'primeng/api';
import { Table } from 'primeng/table';
import { ServerOptions } from '../../models/serverOptions';
import { AlandaProjectApiService } from '../../api/projectApi.service';
import { AlandaTableLayout } from '../../api/models/tableLayout';

const defaultLayoutInit = 0;

@Component({
  selector: 'alanda-project-table',
  templateUrl: './project-table.component.html',
  styleUrls: ['./project-table.component.scss'],
})
export class AlandaProjectTableComponent implements OnInit {
  @Input() defaultLayout = defaultLayoutInit;
  @Input() layouts: AlandaTableLayout[];

  projectsData: any = {};
  selectedLayout: any = {};
  selectedColumns: any = [];
  loading = true;
  serverOptions: ServerOptions;
  menuItems: MenuItem[];

  @ViewChild('tt') turboTable: Table;

  constructor(
    private readonly projectService: AlandaProjectApiService,
    public messageService: MessageService,
  ) {
    this.serverOptions = {
      pageNumber: 1,
      pageSize: 15,
      filterOptions: {},
      sortOptions: {},
    };

    this.menuItems = [
      {
        label: 'Download CSV',
        icon: 'pi pi-fw pi-download',
        command: (onclick) => this.turboTable.exportCSV(),
      },
      {
        label: 'Reset all filters',
        icon: 'pi pi-fw pi-times',
        command: (onclick) => this.turboTable.reset(),
      },
    ];
  }

  ngOnInit() {
    if (this.defaultLayout === defaultLayoutInit) {
      const layoutAllIndex = this.layouts.findIndex(
        (layout) => layout.name.toLowerCase() === 'all',
      );
      if (layoutAllIndex !== -1) {
        this.defaultLayout = layoutAllIndex;
      }
    }

    this.selectedLayout = this.layouts[this.defaultLayout];
    this.layouts.sort((a, b) => a.displayName.localeCompare(b.displayName));
  }

  loadProjects(serverOptions: ServerOptions) {
    this.loading = true;
    this.projectService.loadProjects(serverOptions).subscribe((res) => {
      this.projectsData = res;
      this.loading = false;
    });
  }

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
    for (const key of Object.keys(this.selectedLayout.filterOptions)) {
      this.serverOptions.filterOptions[key] = this.selectedLayout.filterOptions[
        key
      ];
    }
    for (const key in event.filters) {
      if (event.filters.hasOwnProperty(key) && event.filters[key].value) {
        this.serverOptions.filterOptions[key] = event.filters[key].value;
      }
    }

    this.serverOptions.pageNumber =
      event.first / this.serverOptions.pageSize + 1;
    this.loadProjects(this.serverOptions);
  }

  onChangeLayout() {
    this.serverOptions.pageNumber = 1;
    this.serverOptions.filterOptions = {};
    for (const key of Object.keys(this.selectedLayout.filterOptions)) {
      this.serverOptions.filterOptions[key] = this.selectedLayout.filterOptions[
        key
      ];
    }
    this.loadProjects(this.serverOptions);
  }

  public getCondition(obj, condition) {
    if (condition === undefined) return '';
    const props = Object.keys(obj).reduce((acc, next) => `${acc} , ${next}`);
    const evalCon = new Function(
      ` return function ({${props}})  { return ${condition}} `,
    );
    return evalCon()(obj);
  }

  openProject(projectId: string) {
    return '/projectdetails/' + projectId;
  }
}
