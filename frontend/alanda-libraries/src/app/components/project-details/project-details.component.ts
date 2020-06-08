
import { Component } from '@angular/core';
import { AlandaProject } from '../../../../projects/alanda-common/src/lib/api/models/project';
import { RxState } from '@rx-angular/state';
import { ProjectControllerState } from '../../../../projects/alanda-common/src/lib/components/controller/projects-controller/projects-controller.component';


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
