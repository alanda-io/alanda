import {
  Component,
  OnInit,
  Input,
  Output,
  ViewChild,
  Inject,
} from '@angular/core';
import { Subject } from 'rxjs';
import { LazyLoadEvent, MenuItem } from 'primeng/api';
import { Table } from 'primeng/table';
import { ServerOptions } from '../../models/serverOptions';
import { AlandaProjectApiService } from '../../api/projectApi.service';
import { AlandaTableLayout } from '../../api/models/tableLayout';
import { AlandaListResult } from '../../api/models/listResult';
import { AlandaProject } from '../../api/models/project';
import { getTableDefaultLayout } from '../../utils/helper-functions';
import { APP_CONFIG, AppSettings } from '../../models/appSettings';

const defaultLayoutInit = 0;

@Component({
  selector: 'alanda-project-table',
  templateUrl: './project-table.component.html',
  styleUrls: ['./project-table.component.scss'],
})
export class AlandaProjectTableComponent implements OnInit {
  private _defaultLayout = defaultLayoutInit;
  @Input() set defaultLayout(defaultLayout: number) {
    this._defaultLayout = defaultLayout;
    if (this.layouts) {
      this.selectedLayout = this.layouts[this._defaultLayout];
      this.onChangeLayout();
    }
  }
  @Input() layouts: AlandaTableLayout[];
  @Input() tableLayout = 'auto';
  @Input() dateFormat: string;
  @Input() editablePageSize = false;
  @Input() target = '_self';
  @Output() layoutChanged = new Subject<AlandaTableLayout>();

  projectsData: AlandaListResult<AlandaProject>;
  selectedLayout: AlandaTableLayout;
  loading = true;
  serverOptions: ServerOptions;
  menuItems: MenuItem[];

  @ViewChild('tt') turboTable: Table;

  constructor(
    private readonly projectService: AlandaProjectApiService,
    @Inject(APP_CONFIG) config: AppSettings,
  ) {
    if (!this.dateFormat) {
      this.dateFormat = config.DATE_FORMAT;
    }
    this.projectsData = {
      total: 0,
      results: [],
    };

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
        command: (onclick) => this.turboTable.clear(),
      },
    ];
  }

  ngOnInit() {
    if (!this.selectedLayout) {
      this.selectedLayout = this.layouts[this._defaultLayout];
    }
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
    if (this.selectedLayout.filterOptions) {
      for (const [key, value] of Object.entries(
        this.selectedLayout.filterOptions,
      )) {
        this.serverOptions.filterOptions[key] = value;
      }
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
    if (this.selectedLayout.filterOptions) {
      for (const [key, value] of Object.entries(
        this.selectedLayout.filterOptions,
      )) {
        this.serverOptions.filterOptions[key] = value;
      }
    }
    this.loadProjects(this.serverOptions);
    this.layoutChanged.next(this.selectedLayout);
  }

  public getCondition(obj, condition) {
    if (condition === undefined) {
      return '';
    }
    const props = Object.keys(obj).reduce((acc, next) => `${acc} , ${next}`);
    const evalCon = new Function(
      ` return function ({${props}})  { return ${condition}} `,
    );
    return evalCon()(obj);
  }

  openProject(projectId: string) {
    return '/projectdetails/' + projectId;
  }

  changePageSize(pageSize: number) {
    const oldPageSize = this.serverOptions.pageSize;
    const oldPageNumber = this.serverOptions.pageNumber;

    // first entry should be visible after changing rows per page
    const firstEntry = oldPageSize * (oldPageNumber - 1) + 1;

    this.serverOptions.pageSize = pageSize;
    this.serverOptions.pageNumber = Math.ceil(firstEntry / pageSize);

    this.loadProjects(this.serverOptions);
  }
}
