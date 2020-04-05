import { Component } from '@angular/core';
import { AlandaTaskFormComponent, AlandaFormsRegisterService,
         AlandaProjectApiService,
         AlandaTaskApiService} from 'projects/alanda-common/src/public-api';
import { ActivatedRoute } from '@angular/router';
import { SelectItem } from 'primeng/api';


@Component({
    selector: 'modify-vacation-request',
    templateUrl: './modify-vacation-request.component.html',
    styleUrls: [],
  })
export class ModifyVacationRequestComponent extends AlandaTaskFormComponent {

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
