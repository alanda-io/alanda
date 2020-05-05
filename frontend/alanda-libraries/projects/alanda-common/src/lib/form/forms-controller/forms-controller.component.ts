import { Component, AfterViewInit } from "@angular/core";
import { AlandaTaskFormService } from '../alanda-task-form.service';

@Component({
    selector: 'forms-controller-component',
    templateUrl: './forms-controller.component.html',
    styleUrls: [],
    providers: [AlandaTaskFormService],
  })
  export class AlandaFormsControllerComponent implements AfterViewInit {

    state$ = this.taskFormService.state$;
    activeTab = 0;

    constructor(private taskFormService: AlandaTaskFormService) {
    }

    ngAfterViewInit() {
    }

  }

