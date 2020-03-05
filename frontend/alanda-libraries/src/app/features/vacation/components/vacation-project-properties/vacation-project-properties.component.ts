import { Component } from "@angular/core";
import { AlandaProject } from 'projects/alanda-common/src/lib/api/models/alandaProject';

@Component({
  templateUrl: './vacation-project-properties.component.html'
})
export class VacationProjectPropertiesComponent {

  project: AlandaProject;

  constructor() {}

}
