import { Component } from '@angular/core';
import { AlandaProject, AlandaUser } from '@alanda/common';
import { UserEnrichedTaskFormService } from '../../../../services/userEnrichedTaskForm.service';
import { FormGroup } from '@angular/forms';

@Component({
  templateUrl: './project-properties.component.html',
})
export class ProjectPropertiesComponent {
  project: AlandaProject;
  user: AlandaUser;
  rootForm: FormGroup;
  // state$ = this.taskFormService.state$;
  // rootForm = this.taskFormService.rootForm;

  constructor() {}
}
