import { Component } from '@angular/core';
import { AlandaProject } from '@alanda-libraries/common';
import { AlandaTaskFormService } from '@alanda-libraries/common';

@Component({
  templateUrl: './project-properties.component.html',
})
export class ProjectPropertiesComponent {
  project: AlandaProject;
  state$ = this.taskFormService.state$;
  rootForm = this.taskFormService.rootForm;

  constructor(private readonly taskFormService: AlandaTaskFormService) {}
}
