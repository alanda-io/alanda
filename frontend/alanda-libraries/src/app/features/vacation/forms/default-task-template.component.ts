import { Component } from '@angular/core';
import { AlandaTaskFormComponent, AlandaFormsRegisterService,
         AlandaProjectApiService,
         AlandaTaskApiService} from 'projects/alanda-common/src/public-api';
import { ActivatedRoute } from '@angular/router';

@Component({
    selector: 'default-task',
    templateUrl: './default-task-template.component.html',
    styleUrls: [],
  })
export class DefaultTaskComponent extends AlandaTaskFormComponent {

  constructor(formsRegisterService: AlandaFormsRegisterService, route: ActivatedRoute, taskService: AlandaTaskApiService,
              projectService: AlandaProjectApiService) {
    super(formsRegisterService, route, taskService, projectService);
  }

}
