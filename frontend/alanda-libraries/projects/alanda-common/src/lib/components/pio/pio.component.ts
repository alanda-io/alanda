import { Component, OnInit, Input, Inject } from "@angular/core";
import { AppSettings, APP_CONFIG } from "../../models/AppSettings";
import { ProcessServiceNg } from "../../services/rest/process.service";
import { PmcTask } from "../../models/pmcTask";

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
