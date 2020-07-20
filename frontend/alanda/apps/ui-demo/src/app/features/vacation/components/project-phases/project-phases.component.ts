import { Component, OnInit } from '@angular/core';
import { AlandaProject } from '@alanda/common';

@Component({
  selector: 'alanda-project-phases',
  templateUrl: './project-phases.component.html'
})
export class ProjectPhasesComponent implements OnInit {
  project: AlandaProject

  activeIndex = 0;

  constructor() { }

  ngOnInit(): void {
  }

}
