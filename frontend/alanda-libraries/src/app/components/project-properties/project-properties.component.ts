import { Component } from "@angular/core";
import { AlandaProject } from 'projects/alanda-common/src/lib/api/models/project';

@Component({
  templateUrl: './project-properties.component.html'
})
export class ProjectPropertiesComponent {

  project: AlandaProject;

  constructor() {}

}
