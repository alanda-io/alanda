import { Component } from '@angular/core';
import { AlandaProject } from '@alanda-libraries/common';
import { RxState } from '@rx-angular/state';
import { ProjectControllerState } from '@alanda-libraries/common';

@Component({
  selector: 'project-details-component',
  templateUrl: './project-details.component.html',
  styleUrls: [],
})
export class ProjectDetailsComponent {
  project: AlandaProject;
  pid: string;

  constructor(public state: RxState<ProjectControllerState>) {
    this.project = this.state.get().project;
    this.pid = this.state.get().pid;
  }
}
