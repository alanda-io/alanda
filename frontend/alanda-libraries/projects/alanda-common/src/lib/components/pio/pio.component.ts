import { Component, OnInit, Input, Inject } from "@angular/core";
import { ProcessServiceNg } from "../../api/process.service";
import { PmcTask } from "../../models/pmcTask";
import { APP_CONFIG, AppSettings } from "../../models/appSettings";

@Component({
    selector: 'pio-component',
    templateUrl: './pio.component.html',
    styleUrls: [],
})
export class PioComponent implements OnInit{

    @Input() pid: string;
    @Input() task: PmcTask;
    processName = '';
    endpointUrl: string;
    
    constructor(@Inject(APP_CONFIG) private config: AppSettings, private processService: ProcessServiceNg) {
        this.endpointUrl = config.API_ENDPOINT + "/pmc-process";
    };

    ngOnInit(){
        this.getProcessInfo(this.pid);
    }

    getProcessInfo(pid) {
        this.processService.getProcessInfoForProcessInstance(pid).subscribe((res) => {
            this.processName = res.processName;
        });
    }
}
