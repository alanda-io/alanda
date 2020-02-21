import { Component } from '@angular/core';
import { AlandaTaskTemplateComponent, AlandaTaskService, AlandaFormsRegisterService,
         AlandaProjectService } from 'projects/alanda-common/src/public_api';
import { ActivatedRoute } from '@angular/router';
import { SelectItem } from 'primeng/api';

@Component({
    selector: 'prepare-vacation-request',
    templateUrl: './prepare-vacation-request.component.html',
    styleUrls: [],
  })
  export class PrepareVacationRequestComponent extends AlandaTaskTemplateComponent {

    items: SelectItem[];

    constructor(formsRegisterService: AlandaFormsRegisterService, route: ActivatedRoute, taskService: AlandaTaskService,
                projectService: AlandaProjectService) {
      super(formsRegisterService, route, taskService, projectService);
      this.items = [
        {label: 'Yes', value: true},
        {label: 'No', value: false}
      ];
    }
  }
