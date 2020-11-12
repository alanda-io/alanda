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
import { APP_CONFIG, AppSettings } from '../../models/appSettings';
import { RxState } from '@rx-angular/state';
import { exportAsCsv } from '../../utils/helper-functions';
import { AlandaTableColumnDefinition } from '../../api/models/tableColumnDefinition';
import { Router } from '@angular/router';

const defaultLayoutInit = 0;
const EXPORT_FILE_NAME = 'download';
interface AlandaProjectTableState {
  serverOptions: ServerOptions;
}

@Component({
  selector: 'alanda-project-table',
  templateUrl: './project-table.component.html',
  styleUrls: ['./project-table.component.scss'],
  providers: [RxState],
})
export class AlandaProjectTableComponent implements OnInit {
  state$ = this.state.select();
  private _defaultLayout = defaultLayoutInit;
  @Input() set defaultLayout(defaultLayout: number) {
    this._defaultLayout = defaultLayout;
    if (this.layouts && this.turboTable) {
      this.selectedLayout = this.layouts[this._defaultLayout];
      this.loadProjectsLazy(this.turboTable);
    }
  }
  @Input() layouts: AlandaTableLayout[];
  @Input() tableStyle: object;
  @Input() autoLayout = false;
  @Input() resizableColumns = true;
  @Input() responsive = true;
  @Input() dateFormat: string;
  @Input() editablePageSize = false;
  @Input() target = '_self';
  @Input() targetDblClick = '_blank';
  @Input() routerBasePath = '/projectdetails';
  @Output() layoutChanged = new Subject<AlandaTableLayout>();

  projectsData: AlandaListResult<AlandaProject>;
  selectedLayout: AlandaTableLayout;
  loading = true;
  serverOptions: ServerOptions;
  menuItems: MenuItem[];
  dateFormatPrime: string;
  hiddenColumns = {};
  filteredColumns: AlandaTableColumnDefinition[] = [];

  @ViewChild('tt') turboTable: Table;

  constructor(
    private readonly projectService: AlandaProjectApiService,
    @Inject(APP_CONFIG) config: AppSettings,
    private state: RxState<AlandaProjectTableState>,
    private router: Router
  ) {
    if (!this.dateFormat) {
      this.dateFormat = config.DATE_FORMAT;
    }
    this.dateFormatPrime = config.DATE_FORMAT_PRIME;
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
  }

  ngOnInit() {
    if (!this.selectedLayout) {
      this.selectedLayout = this.layouts[this._defaultLayout];
      this.filteredColumns = this.layouts[this._defaultLayout].columnDefs;
      this.menuItems = this.updateMenu(this.filteredColumns);
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

    this.state.set({ serverOptions: this.serverOptions });
    this.loadProjects(this.serverOptions);
  }

  onChangeLayout() {
    this.loadProjectsLazy(this.turboTable);
    this.layoutChanged.next(this.selectedLayout);
    this.filteredColumns = this.selectedLayout.columnDefs;
    this.menuItems = this.updateMenu(this.filteredColumns);
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

  openProject(projectId: string): void {
    const url = this.router.createUrlTree([this.getProjectPath(projectId)]);
    window.open(url.toString(), this.targetDblClick);
  }

  getProjectPath(projectId: string): string {
    return `${this.routerBasePath}/${projectId}`;
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

  exportAllData() {
    const serverOptions = this.state.get('serverOptions');
    serverOptions.pageNumber = 1;
    serverOptions.pageSize = this.projectsData.total;
    this.projectService.loadProjects(serverOptions).subscribe((res) => {
      const data = [...res.results];
      exportAsCsv(data, this.selectedLayout.columnDefs, EXPORT_FILE_NAME);
    });
  }

  exportCurrentPageData(){
    exportAsCsv(this.projectsData.results,this.selectedLayout.columnDefs,EXPORT_FILE_NAME);
  }

  updateMenu(columnDefs: AlandaTableColumnDefinition[]): MenuItem[] {
    this.hiddenColumns = {};
    const columnMenuItems: MenuItem[] = [];
    columnDefs.forEach((column) => {
      columnMenuItems.push({
        label: column.displayName,
        icon: 'pi pi-eye',
        command: () => this.toggleColumn(column.name),
      });
    });

    return [
      {
        label: 'Primary Actions',
        items: [
          {
            label: 'Download CSV visible page',
            icon: 'pi pi-fw pi-download',
            command: () => this.exportCurrentPageData(),
          },
          {
            label: 'Download CSV all pages',
            icon: 'pi pi-fw pi-download',
            command: () => this.exportAllData(),
          },
          {
            label: 'Reset all filters',
            icon: 'pi pi-fw pi-times',
            command: () => this.turboTable.clear(),
          },
        ],
      },
      {
        label: 'Column display',
        items: columnMenuItems,
      },
    ];
  }

  toggleColumn(name: string): void {
    if (this.hiddenColumns.hasOwnProperty(name)) {
      const index = this.hiddenColumns[name];
      delete this.hiddenColumns[name];
      this.menuItems[1].items[index].icon = 'pi pi-eye';
    } else {
      this.selectedLayout.columnDefs.some((column, index) => {
        if (column.name === name) {
          this.hiddenColumns[column.name] = index;
          this.menuItems[1].items[index].icon = 'pi pi-eye-slash';
          return true;
        }
      });
    }
    this.filteredColumns = this.selectedLayout.columnDefs.filter(
      (column, index) => {
        return !this.hiddenColumns.hasOwnProperty(column.name);
      },
    );
  }
}
