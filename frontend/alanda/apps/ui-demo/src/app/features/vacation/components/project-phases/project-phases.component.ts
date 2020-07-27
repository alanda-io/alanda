import { Component } from '@angular/core';
import { AlandaProject } from '@alanda/common';

@Component({
  selector: 'alanda-project-phases',
  templateUrl: './project-phases.component.html'
})
export class ProjectPhasesComponent {
  project: AlandaProject
  activeIndex = 0;
}
