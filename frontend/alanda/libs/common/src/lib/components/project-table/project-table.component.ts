import {
  Component,
  OnInit,
  Input,
  Output,
  ViewChild,
  Inject,
} from '@angular/core';
import { merge, of, Subject } from 'rxjs';
import { LazyLoadEvent, MenuItem, MessageService } from 'primeng/api';
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
import {
  catchError,
  delay,
  filter,
  map,
  startWith,
  switchMap,
  tap,
} from 'rxjs/operators';
import { exhaustMap } from 'rxjs/operators';

const defaultLayoutInit = 0;
const EXPORT_FILE_NAME = 'download';

interface AlandaProjectTableState {
  serverOptions: ServerOptions;
  loading: boolean;
  selectedProject: AlandaProject;
  showProjectDetailsModal: boolean;
  projectsData: AlandaListResult<AlandaProject>;
  selectedPageSize: number;
}

const initState = {
  projectsData: {
    total: 0,
    results: [],
  },
  selectedPageSize: 15,
};

const DEFAULT_BUTTON_MENU_ICON = 'pi pi-bars';
const LOADING_ICON = 'pi pi-spin pi-spinner';
@Component({
  selector: 'alanda-project-table',
  templateUrl: './project-table.component.html',
  styleUrls: ['./project-table.component.scss'],
  providers: [RxState],
})
export class AlandaProjectTableComponent implements OnInit {
  state$ = this.state.select();
  menuButtonIcon = DEFAULT_BUTTON_MENU_ICON;
  private _defaultLayout = defaultLayoutInit;
  @Input() set defaultLayout(defaultLayout: number) {
    this._defaultLayout = defaultLayout;
    if (this.layouts && this.turboTable) {
      this.selectedLayout = this.layouts[this._defaultLayout];
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

  selectedLayout: AlandaTableLayout;
  menuItems: MenuItem[];
  dateFormatPrime: string;
  hiddenColumns = {};
  filteredColumns: AlandaTableColumnDefinition[] = [];
  tableLazyLoadEvent$ = new Subject<LazyLoadEvent>();
  needReloadEvent$ = new Subject();
  setupProjectDetailsModalEvent$ = new Subject<AlandaProject>();
  closeProjectDetailsModalEvent$ = new Subject<AlandaProject>();

  @ViewChild('tt') turboTable: Table;

  loadProjectFromServer$ = merge(
    this.tableLazyLoadEvent$,
    this.needReloadEvent$.pipe(
      delay(1000),
      map(() => this.turboTable),
    ),
  ).pipe(
    map((event) => this.buildServerOptions(event)),
    switchMap((serverOptions) =>
      this.projectService.loadProjects(serverOptions).pipe(
        map((projectsData) => ({
          serverOptions,
          projectsData,
          loading: false,
        })),
        startWith({ loading: true }),
        catchError((err) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Load Tasks',
            detail: err.message,
          });
          const projectsData = this.state.get().projectsData;
          return of({ serverOptions, projectsData, loading: false });
        }),
      ),
    ),
  );

  constructor(
    private readonly projectService: AlandaProjectApiService,
    @Inject(APP_CONFIG) config: AppSettings,
    private state: RxState<AlandaProjectTableState>,
    public messageService: MessageService,
    private router: Router,
  ) {
    if (!this.dateFormat) {
      this.dateFormat = config.DATE_FORMAT;
    }
    this.dateFormatPrime = config.DATE_FORMAT_PRIME;
    this.state.set(initState);

    this.state.connect(
      this.setupProjectDetailsModalEvent$.pipe(
        map((selectedProject) => ({
          selectedProject,
          showProjectDetailsModal: true,
        })),
      ),
    );
    this.state.connect(
      'showProjectDetailsModal',
      this.closeProjectDetailsModalEvent$.pipe(map(() => false)),
    );
    this.state.connect(this.loadProjectFromServer$);
    this.state.hold(
      this.closeProjectDetailsModalEvent$.pipe(
        filter((project) => !!project),
        delay(1000),
        tap(() => this.needReloadEvent$.next()),
      ),
    );

    this.state.hold(this.needReloadEvent$);
    this.state.hold(this.tableLazyLoadEvent$);
  }

  ngOnInit() {
    if (!this.selectedLayout) {
      this.selectedLayout = this.layouts[this._defaultLayout];
      this.filteredColumns = this.layouts[this._defaultLayout].columnDefs;
      this.menuItems = this.updateMenu(this.filteredColumns);
    }
    this.layouts.sort((a, b) => a.displayName.localeCompare(b.displayName));
  }

  buildServerOptions(event: LazyLoadEvent): ServerOptions {
    const serverOptions: ServerOptions = {
      pageNumber: event.first / event.rows + 1,
      pageSize: event.rows,
      filterOptions: {},
      sortOptions: {},
    };

    let sortOptions = {};
    sortOptions['project.projectId'] = { dir: 'desc', prio: 0 };
    if (event.sortField) {
      sortOptions = {};
      const dir = event.sortOrder === 1 ? 'asc' : 'desc';
      sortOptions[event.sortField] = { dir, prio: 0 };
    }
    serverOptions.sortOptions = sortOptions;

    if (this.selectedLayout.filterOptions) {
      for (const [key, value] of Object.entries(
        this.selectedLayout.filterOptions,
      )) {
        serverOptions.filterOptions[key] = value;
      }
    }

    for (const key in event.filters) {
      if (event.filters.hasOwnProperty(key) && event.filters[key].value) {
        serverOptions.filterOptions[key] = event.filters[key].value;
      }
    }

    return serverOptions;
  }

  onChangeLayout() {
    this.needReloadEvent$.next();
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
    const baseUrl = window.location.href?.replace(this.router.url, '');
    window.open(
      baseUrl.concat(this.getProjectPath(projectId)),
      this.targetDblClick,
    );
  }

  getProjectPath(projectId: string): string {
    return `${this.routerBasePath}/${projectId}`;
  }

  changePageSize(pageSize: number) {
    this.state.set({ selectedPageSize: pageSize });
    this.needReloadEvent$.next();
  }

  exportAllData() {
    const { serverOptions, projectsData } = this.state.get();
    serverOptions.pageNumber = 1;
    serverOptions.pageSize = projectsData.total;
    this.menuButtonIcon = LOADING_ICON;
    this.projectService
      .loadProjects(serverOptions)
      .pipe(
        exhaustMap((result) => {
          const data = [...result.results];
          exportAsCsv(data, this.selectedLayout.columnDefs, EXPORT_FILE_NAME);
          this.menuButtonIcon = DEFAULT_BUTTON_MENU_ICON;
          return of(true);
        }),
      )
      .subscribe();
  }

  exportCurrentPageData() {
    const projectsData = this.state.get('projectsData');
    exportAsCsv(
      projectsData.results,
      this.selectedLayout.columnDefs,
      EXPORT_FILE_NAME,
    );
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
