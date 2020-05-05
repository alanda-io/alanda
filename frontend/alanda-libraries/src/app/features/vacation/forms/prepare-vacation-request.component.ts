import { Component, AfterViewInit } from '@angular/core';
import { SelectItem } from 'primeng/api';
import { AlandaTaskFormService, BaseFormComponent } from 'projects/alanda-common/src/public-api';

@Component({
    selector: 'prepare-vacation-request',
    templateUrl: './prepare-vacation-request.component.html',
    styleUrls: [],
  })
  export class PrepareVacationRequestComponent implements BaseFormComponent, AfterViewInit {

    state$ = this.taskFormService.state$;
    rootForm = this.taskFormService.rootForm;
    items: SelectItem[];

    constructor(private taskFormService: AlandaTaskFormService) {
      this.items = [
        {label: 'Yes', value: true},
        {label: 'No', value: false}
      ];
    }

    submit(): void {
      this.taskFormService.submit();
    };

    ngAfterViewInit(): void {
      // this.formManagerService.addValidators();
    }

  }
