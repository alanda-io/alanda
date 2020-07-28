import { Component, OnInit, Input, Inject } from '@angular/core';
import { APP_CONFIG, AppSettings } from '../../shared/models/appSettings';
import { AlandaProcessApiService } from '../../shared/api/processApi.service';
import { AlandaTask } from '../../shared/api/models/task';

@Component({
  selector: 'alanda-pio-component',
  templateUrl: './pio.component.html',
  styleUrls: [],
})
export class AlandaPioComponent implements OnInit {
  @Input() pid: string;
  @Input() task: AlandaTask;
  processName = '';
  endpointUrl: string;

  constructor(
    @Inject(APP_CONFIG) private readonly config: AppSettings,
    private readonly processService: AlandaProcessApiService,
  ) {
    this.endpointUrl = config.API_ENDPOINT + '/pmc-process';
  }

  ngOnInit() {
    this.getProcessInfo(this.pid);
  }

  getProcessInfo(pid) {
    this.processService
      .getProcessInfoForProcessInstance(pid)
      .subscribe((res) => {
        // this.processName = res.processName;
      });
  }
}
