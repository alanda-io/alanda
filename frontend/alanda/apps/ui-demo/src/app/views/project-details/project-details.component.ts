import { Component } from '@angular/core';
import { AlandaProject, AlandaUser } from '@alanda/common';
import { RxState } from '@rx-angular/state';
import { AlandaTitleService } from '@alanda/common';
import { UserEnrichedProjectControllerState } from './components/projects-controller/user-enriched-projects-controller.component';

@Component({
  selector: 'alanda-project-details-component',
  templateUrl: './project-details.component.html',
  styleUrls: [],
})
export class ProjectDetailsComponent {
  project: AlandaProject;
  pid: string;
  user: AlandaUser;
  constructor(
    public state: RxState<UserEnrichedProjectControllerState>,
    private titleService: AlandaTitleService,
  ) {
    this.project = this.state.get().project;
    this.pid = this.state.get().pid;
    this.user = this.state.get().user;
    this.titleService.setProjectTitle(this.project);
  }
}
