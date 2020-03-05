import { Component } from '@angular/core';
import { AlandaTaskComponent, AlandaTaskService, AlandaFormsRegisterService,
         AlandaProjectApiService } from 'projects/alanda-common/src/public_api';
import { ActivatedRoute } from '@angular/router';
import { SelectItem } from 'primeng/api';


@Component({
    selector: 'modify-vacation-request',
    templateUrl: './modify-vacation-request.component.html',
    styleUrls: [],
  })
export class ModifyVacationRequestComponent extends AlandaTaskComponent {

  items: SelectItem[];

  constructor(formsRegisterService: AlandaFormsRegisterService, route: ActivatedRoute, taskService: AlandaTaskService,
              projectService: AlandaProjectApiService) {
    super(formsRegisterService, route, taskService, projectService);
    this.items = [
      {label: 'Yes', value: true},
      {label: 'No', value: false}
    ];
  }
}
