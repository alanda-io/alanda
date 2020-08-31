import { Component } from '@angular/core';
import { AlandaProject } from '@alanda/common';
import { AlandaTaskFormService } from '@alanda/common';
import { UserEnrichedTaskFormService } from 'apps/ui-demo/src/app/services/userEnrichedTaskForm.service';

@Component({
  templateUrl: './project-properties.component.html',
})
export class ProjectPropertiesComponent {
  project: AlandaProject;
  state$ = this.taskFormService.state$;
  rootForm = this.taskFormService.rootForm;

  constructor(private readonly taskFormService: UserEnrichedTaskFormService) {}
}
