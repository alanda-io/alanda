import { Component, OnInit, Input } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AlandaHistoryApiService } from '../../shared/api/historyApi.service';

@Component({
  selector: 'alanda-history-grid-component',
  templateUrl: './history-grid.component.html',
})
export class AlandaHistoryGridComponent implements OnInit {
  @Input() projectGuid: number;
  loadingInProgress = false;
  data: any[];
  totalItems: number;
  serverOptions = {
    pageNumber: 1,
    pageSize: 20,
    filterOptions: {},
    sortOptions: {},
  };

  columnDefs = [
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

  constructor(
    private readonly historyService: AlandaHistoryApiService,
    private readonly route: ActivatedRoute,
  ) {}

  ngOnInit() {
    if (this.route.snapshot.params.projectId) {
      this.serverOptions.filterOptions['pmcProjectGuid'] = this.projectGuid;
    } else if (this.route.snapshot.params['banfId']) {
      this.serverOptions.filterOptions[
        'refObjectIdName'
      ] = this.route.snapshot.params['banfId'];
      this.serverOptions.filterOptions['refObjectType'] = 'PO';
    }
  }

  loadLazy(event) {
    this.loadEntries();
  }

  loadEntries() {
    this.loadingInProgress = true;
    this.historyService
      .search(
        this.serverOptions.filterOptions,
        this.serverOptions.pageNumber,
        this.serverOptions.pageSize,
      )
      .subscribe((res: any) => {
        this.data = res.results;
        this.totalItems = res.total;
        this.loadingInProgress = false;
      });
  }
}
