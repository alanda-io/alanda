import { Component, OnInit, Input, Inject, SimpleChanges } from "@angular/core";

import { APP_CONFIG, AppSettings } from "../../../models/AppSettings";
import { HttpClient, HttpParams } from "@angular/common/http";

@Component({
    selector: 'process-activities-component',
    templateUrl: './process-activities.component.html',
    styleUrls: [],
})
export class ProcessActivitiesComponent implements OnInit {

    @Input() pid: string;
    endpointUrl = '';
    jsonResult: any[] = [];
    extendedView = true;
    selectedActivity: any;
    loading: boolean;
    columnDefs = [
        {name: 'Type', field: 'type', width: 150},
        {name: 'Name', field: 'name', width: 230},
        {name: 'Business Key', field: 'business_key', width: 120},
        {name: 'Start', field: 'start', width: 100},
        {name: 'Due', field: 'due', width: 100},
        {name: 'End', field: 'end', width: 100},
        {name: 'Assignee', field: 'assignee'}
    ];

    constructor(@Inject(APP_CONFIG) private config: AppSettings, private http: HttpClient) {
        this.endpointUrl = config.API_ENDPOINT + "/finder/pio/activities";
    }

    ngOnInit() {
        this.loadSearchResults();
    }

    loadSearchResults() {
        let params = new HttpParams();
        params = params.append('pid', this.pid);
        params = params.append('extendedView', String(this.extendedView));
        this.http.get(`${this.endpointUrl}`, {params: params}).subscribe(
            (res: any) => {
                this.jsonResult = res;

            }
        );
    }

    /*   $scope.onRowDblClick = (row) ->
        if row.entity['type'] is 'callActivity'
        $state.go('finder.pio', {pid: row.entity['call_proc_inst_id'], pd: row.entity['process_name'], businessKey: row.entity['business_key']})
    */
 
}