import { Component, OnInit, Input, Inject } from "@angular/core";
import { APP_CONFIG, AppSettings } from "../../models/appSettings";
import { AlandaProcessService } from '../../api/alandaProcess.service';
import { AlandaTask } from '../../api/models/alandaTask';

@Component({
    selector: 'pio-component',
    templateUrl: './pio.component.html',
    styleUrls: [],
})
export class PioComponent implements OnInit{

    @Input() pid: string;
    @Input() task: AlandaTask;
    processName = '';
    endpointUrl: string;

    constructor(@Inject(APP_CONFIG) private config: AppSettings, private processService: AlandaProcessService) {
        this.endpointUrl = config.API_ENDPOINT + "/pmc-process";
    };

    ngOnInit(){
        this.getProcessInfo(this.pid);
    }

    getProcessInfo(pid) {
        this.processService.getProcessInfoForProcessInstance(pid).subscribe((res) => {
            //this.processName = res.processName;
        });
    }
}
