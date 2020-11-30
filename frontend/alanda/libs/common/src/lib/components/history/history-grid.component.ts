import { Component, OnInit, Input } from '@angular/core';
import { AlandaHistoryApiService } from '../../api/historyApi.service';
import { combineLatest, EMPTY, Subject } from 'rxjs';
import { catchError, distinctUntilChanged, map, startWith, switchMap } from 'rxjs/operators';
import { RxState } from '@rx-angular/state';

interface HistoryGridComponentState {
  projectGuid: number;
  loadingInProgress: boolean;
  data: any[];
  totalItems: number;
  serverOptions : {
    pageNumber: number,
    pageSize: number,
    filterOptions: {},
    sortOptions: {},
  },
  columnDefs: any[],
};
const columnDefs = [
  { displayName: 'Project ID', name: 'Project ID', field: 'projectId' },
  {
    displayName: 'RefObject ID Name',
    name: 'RefObject ID Name',
    field: 'refObjectIdName',
  },
  { displayName: 'Type', name: 'Type', field: 'type' },
  { displayName: 'Action', width: 100, name: 'Action', field: 'action' },
  { displayName: 'Field Name', name: 'Field Name', field: 'fieldName' },
  { displayName: 'Old Value', name: 'Old Value', field: 'oldValue' },
  { displayName: 'New Value', name: 'New Value', field: 'newValue' },
  { displayName: 'Text', name: 'Text', field: 'text' },
  { displayName: 'User', name: 'User', field: 'user' },
  { displayName: 'Log Date', name: 'Log Date', field: 'logDate' },
];

const initState = {
  loadingInProgress: false,
  serverOptions: {
    pageNumber: 1,
    pageSize: 20,
    filterOptions: {},
    sortOptions: {},
  },
  columnDefs: columnDefs,
};

@Component({
  selector: 'alanda-history-grid-component',
  templateUrl: './history-grid.component.html',
})
export class AlandaHistoryGridComponent {
  state$ = this.state.select();
  onLazyLoadEvent$ = new Subject<any>();

  @Input() set projectGuid ( projectGuid : number){
    this.state.set({ projectGuid });
  };

  updateServerOptions$ = this.state.select('projectGuid').pipe(
    distinctUntilChanged(),
    map( ( projectGuid) => {
      const serverOptions = this.state.get('serverOptions');
      serverOptions.filterOptions['pmcProjectGuid'] = projectGuid;
      return serverOptions;
    }),
  );

  loadLazy$ = combineLatest([
    this.state.select('serverOptions'),
    this.onLazyLoadEvent$,
  ]).pipe(
    switchMap( ([{filterOptions, pageNumber, pageSize},_]) => {
      return this.historyService.search(filterOptions,pageNumber,pageSize);
    }),
    catchError( (err => {
      console.log(err);
      this.state.set( {loadingInProgress: false})
      return EMPTY;
    })),
    map( (searchResult : any) => {
      return {
        data: searchResult.results,
        totalItems: searchResult.total,
        loadingInProgress: false,
      };
    }),
    startWith( {loadingInProgress: true}),
  );

  constructor(
    private readonly historyService: AlandaHistoryApiService,
    private state: RxState<HistoryGridComponentState>,
  ) {
    this.state.set(initState);
    this.state.connect(this.loadLazy$);
    this.state.connect('serverOptions',this.updateServerOptions$);
  };
}
