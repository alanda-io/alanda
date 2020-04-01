import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SelectItem } from 'primeng/api';
import { AlandaFormsRegisterService, AlandaProjectApiService, AlandaTaskApiService, AlandaTaskComponent } from 'projects/alanda-common/src/public-api';

@Component({
    selector: 'prepare-vacation-request',
    templateUrl: './prepare-vacation-request.component.html',
    styleUrls: [],
  })
  export class PrepareVacationRequestComponent extends AlandaTaskComponent {

    items: SelectItem[];

    constructor(formsRegisterService: AlandaFormsRegisterService, route: ActivatedRoute, taskService: AlandaTaskApiService,
                projectService: AlandaProjectApiService) {
      super(formsRegisterService, route, taskService, projectService);
      this.items = [
        {label: 'Yes', value: true},
        {label: 'No', value: false}
      ];
    }
  }
