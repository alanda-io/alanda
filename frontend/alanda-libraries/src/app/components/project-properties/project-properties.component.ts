import { Component } from '@angular/core';
import { AlandaProject } from 'projects/alanda-common/src/lib/api/models/project';
import { AlandaTaskFormService } from 'projects/alanda-common/src/public-api';

@Component({
  templateUrl: './project-properties.component.html',
})
export class ProjectPropertiesComponent {
  project: AlandaProject;
  state$ = this.taskFormService.state$;
  rootForm = this.taskFormService.rootForm;

  constructor(private taskFormService: AlandaTaskFormService) {}
}
