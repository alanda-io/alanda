import { Component } from '@angular/core';
import { AlandaProject, AlandaUser } from '@alanda/common';
import { RxState } from '@rx-angular/state';
import { AlandaTitleService } from '@alanda/common';
import { ProjectControllerState } from '../../../../components/projects-controller/projects-controller.component';

@Component({
  selector: 'alanda-vacation-project-details-component',
  templateUrl: './vacation-project-details.component.html',
  styleUrls: [],
})
export class VacationProjectDetailsComponent {
  project: AlandaProject;
  pid: string;
  user: AlandaUser;
  constructor(
    public state: RxState<ProjectControllerState>,
    private titleService: AlandaTitleService,
  ) {
    this.project = this.state.get().project;
    this.pid = this.state.get().pid;
    this.user = this.state.get().user;
    this.titleService.setProjectTitle(this.project);
  }
}
