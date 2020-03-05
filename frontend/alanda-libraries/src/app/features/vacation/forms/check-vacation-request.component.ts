import { Component } from '@angular/core';
import { AlandaTaskService, AlandaFormsRegisterService,
         AlandaProjectService,
         AlandaTaskComponent} from 'projects/alanda-common/src/public_api';
import { ActivatedRoute } from '@angular/router';
import { SelectItem } from 'primeng/api';

@Component({
    selector: 'check-vacation-request',
    templateUrl: './check-vacation-request.component.html',
    styleUrls: [],
  })
export class CheckVacationRequestComponent extends AlandaTaskComponent {

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
