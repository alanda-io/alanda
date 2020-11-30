import {
  Component,
  Inject,
  Input,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import { EMPTY, merge, of, Subject } from 'rxjs';
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
  exhaustMap,
  filter,
  map,
  startWith,
  switchMap,
  tap,
} from 'rxjs/operators';
import { ExportType } from '../../enums/exportType.enum';

const DEFAULT_LAYOUT_INIT = 0;
const EXPORT_FILE_NAME = 'download';

interface AlandaProjectTableState {
  serverOptions: ServerOptions;
  loading: boolean;
  selectedProject: AlandaProject;
  showProjectDetailsModal: boolean;
  projectsData: AlandaListResult<AlandaProject>;
  selectedPageSize: number;
  selectedLayout: AlandaTableLayout;
  filteredColumns: AlandaTableColumnDefinition[];
  defaultLayout: number;
  layouts: AlandaTableLayout[];
  menuItems: MenuItem[];
  singleRowSelectionEnabled: boolean;
  selectionMode: string;
  selection: any;
}

const initState = {
  projectsData: {
    total: 0,
    results: [],
  },
  selectedPageSize: 15,
  defaultLayout: DEFAULT_LAYOUT_INIT,
  layouts: [],
  menuItem: [],
  singleRowSelection: false,
  selectionMode: 'single',
  selection: {},
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
  @Input() set defaultLayout(defaultLayout: number) {
    this.state.set({ defaultLayout });
  }
  @Input() set layouts(layouts: AlandaTableLayout[]) {
    this.state.set({ layouts });
  }
  @Input() set singleRowSelection(singleRowSelectionEnabled: boolean) {
    this.state.set({ singleRowSelectionEnabled });
    this.state.set({ selectionMode: 'single' });
  }
  @Input() tableStyle: object;
  @Input() autoLayout = false;
  @Input() resizableColumns = true;
  @Input() responsive = true;
  @Input() editablePageSize = false;
  @Input() target = '_self';
  @Input() targetDblClick = '_blank';
  @Input() routerBasePath = '/projectdetails';
  @Output() layoutChanged = new Subject<AlandaTableLayout>();
  @Output() selectionChange = new Subject<AlandaProject>();
  @Input() hideMenuButton = false;

  dateFormat: string;
  hiddenColumns = {};
  tableLazyLoadEvent$ = new Subject<LazyLoadEvent>();
  needReloadEvent$ = new Subject();
  setupProjectDetailsModalEvent$ = new Subject<AlandaProject>();
  closeProjectDetailsModalEvent$ = new Subject<AlandaProject>();
  layoutChange$ = new Subject<any>();
  onMenuItemColumnClick$ = new Subject<string>();
  onExportCsvClick$ = new Subject<ExportType>();

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
  rowSelectionChange$ = this.state.select('selection').pipe(
    map((selection) => (selection === null ? null : selection.project)),
    tap((project) => this.selectionChange.next(project)),
  );
  onSelectedLayoutChange$ = this.state.select('selectedLayout').pipe(
    map((selectedLayout) => {
      this.needReloadEvent$.next();
      this.layoutChanged.next(selectedLayout);
      return selectedLayout.columnDefs;
    }),
  );
  updateMenuColumnOptions$ = this.state
    .select('selectedLayout')
    .pipe(map((selectedLayout) => this.updateMenu(selectedLayout.columnDefs)));
  selectedLayoutChanged$ = this.layoutChange$.pipe(map((event) => event.value));
  defaultLayoutChange$ = this.state.select('defaultLayout').pipe(
    filter((defaultLayout) => !this.state.get('layouts')),
    map((defaultLayout) => this.state.get('layouts')[defaultLayout]),
  );
  toggleColumn$ = this.onMenuItemColumnClick$.pipe(
    map((name) => {
      const selectedLayout = this.state.get('selectedLayout');
      const menuItems = this.state.get('menuItems');
      if (this.hiddenColumns.hasOwnProperty(name)) {
        const index = this.hiddenColumns[name];
        delete this.hiddenColumns[name];
        menuItems[1].items[index].icon = 'pi pi-eye';
      } else {
        selectedLayout.columnDefs.some((column, index) => {
          if (column.name === name) {
            this.hiddenColumns[column.name] = index;
            menuItems[1].items[index].icon = 'pi pi-eye-slash';
            return true;
          }
        });
      }
      return {
        menuItems,
        filteredColumns: this.state
          .get('selectedLayout')
          .columnDefs.filter((column, index) => {
            return !this.hiddenColumns.hasOwnProperty(column.name);
          }),
      };
    }),
  );
  exportAllCsv$ = this.onExportCsvClick$.pipe(
    filter((exportType) => exportType === ExportType.ALL_DATA),
    exhaustMap(() => {
      const { serverOptions, projectsData } = this.state.get();
      serverOptions.pageNumber = 1;
      serverOptions.pageSize = projectsData.total;
      this.menuButtonIcon = LOADING_ICON;
      return this.projectService.loadProjects(serverOptions);
    }),
    catchError((err) => {
      console.log(err);
      return EMPTY;
    }),
    tap((result) => {
      const data = [...result.results];
      exportAsCsv(data, this.state.get('filteredColumns'), EXPORT_FILE_NAME);
      this.menuButtonIcon = DEFAULT_BUTTON_MENU_ICON;
    }),
  );

  exportCurrentPageData$ = this.onExportCsvClick$.pipe(
    filter((exportType) => exportType === ExportType.VISIBLE_DATA),
    tap(() =>
      exportAsCsv(
        this.state.get('projectsData').results,
        this.state.get('filteredColumns'),
        EXPORT_FILE_NAME,
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
    this.dateFormat = config.DATE_FORMAT;
    this.state.set(initState);
    this.state.connect('selectedLayout', this.defaultLayoutChange$);
    this.state.connect('selectedLayout', this.selectedLayoutChanged$);
    this.state.connect('filteredColumns', this.onSelectedLayoutChange$);
    this.state.connect('menuItems', this.updateMenuColumnOptions$);
    this.state.hold(merge(this.exportAllCsv$, this.exportCurrentPageData$));
    this.state.hold(this.rowSelectionChange$);
    this.state.connect(this.toggleColumn$);
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
    if (!this.state.get('selectedLayout')) {
      this.state.set({
        selectedLayout: this.state.get('layouts')[
          this.state.get('defaultLayout')
        ],
        filteredColumns: this.state.get('layouts')[
          this.state.get('defaultLayout')
        ].columnDefs,
      });
    }
    this.state.set({
      layouts: this.state
        .get('layouts')
        .sort((a, b) => a.displayName.localeCompare(b.displayName)),
    });
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

    if (this.state.get('selectedLayout').filterOptions) {
      for (const [key, value] of Object.entries(
        this.state.get('selectedLayout').filterOptions,
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

  updateMenu(columnDefs: AlandaTableColumnDefinition[]): MenuItem[] {
    this.hiddenColumns = {};
    const columnMenuItems: MenuItem[] = [];
    columnDefs.forEach((column) => {
      columnMenuItems.push({
        label: column.displayName,
        icon: 'pi pi-eye',
        command: () => this.onMenuItemColumnClick$.next(column.name),
      });
    });

    return [
      {
        label: 'Primary Actions',
        items: [
          {
            label: 'Download CSV visible page',
            icon: 'pi pi-fw pi-download',
            command: () => this.onExportCsvClick$.next(ExportType.VISIBLE_DATA),
          },
          {
            label: 'Download CSV all pages',
            icon: 'pi pi-fw pi-download',
            command: () => this.onExportCsvClick$.next(ExportType.ALL_DATA),
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

  get selection(): any {
    return this.state.get('selection');
  }
  set selection(selection: any) {
    this.state.set({ selection });
  }
}
