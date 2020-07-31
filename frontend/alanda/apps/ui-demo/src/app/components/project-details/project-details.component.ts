import { Component } from '@angular/core';
import { AlandaProject } from '@alanda/common';
import { RxState } from '@rx-angular/state';
import { ProjectControllerState } from '@alanda/common';
import { AlandaTitleService } from '@alanda/common';

@Component({
  selector: 'alanda-project-details-component',
  templateUrl: './project-details.component.html',
  styleUrls: [],
})
export class ProjectDetailsComponent {
  project: AlandaProject;
  pid: string;
  constructor(public state: RxState<ProjectControllerState>, private titleService: AlandaTitleService) {
    this.project = this.state.get().project;
    this.pid = this.state.get().pid;
    this.titleService.setProjectTitle(this.project);
  }
}
