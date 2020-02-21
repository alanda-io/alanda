import { Component } from '@angular/core';
import { AlandaTaskTemplateComponent, AlandaTaskService, AlandaFormsRegisterService,
         AlandaProjectService } from 'projects/alanda-common/src/public_api';
import { ActivatedRoute } from '@angular/router';

@Component({
    selector: 'default-task',
    templateUrl: './default-task-template.component.html',
    styleUrls: [],
  })
export class DefaultTaskComponent extends AlandaTaskTemplateComponent {

  constructor(formsRegisterService: AlandaFormsRegisterService, route: ActivatedRoute, taskService: AlandaTaskService,
              projectService: AlandaProjectService) {
    super(formsRegisterService, route, taskService, projectService);
  }

}
