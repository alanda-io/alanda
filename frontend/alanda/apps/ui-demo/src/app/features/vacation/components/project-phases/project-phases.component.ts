import { Component, OnInit } from '@angular/core';
import {
  AlandaProject,
  AlandaProjectApiService,
  AlandaUser,
  AlandaSimplePhase,
} from '@alanda/common';

@Component({
  selector: 'alanda-project-phases',
  templateUrl: './project-phases.component.html',
})
export class ProjectPhasesComponent implements OnInit {
  project: AlandaProject;
  phase: string;
  activePhase: string;
  user: AlandaUser;
  phases: AlandaSimplePhase[];

  overviewTab: AlandaSimplePhase = {
    active: false,
    enabled: false,
    pmcProjectPhaseDefinition: {
      allowedProcesses: null,
      displayName: 'Overview',
      guid: 0,
      idName: 'OVERVIEW',
    },
    guid: null,
    updateUser: null,
    updateDate: null,
  };

  constructor(private projectApiService: AlandaProjectApiService) {}

  ngOnInit(): void {
    this.projectApiService
      .getPhasesForProject(this.project.guid)
      .subscribe((resp) => {
        const ps = resp.filter(
          (p) => p.pmcProjectPhaseDefinition.idName !== 'PHASE1',
        );
        this.phases = [this.overviewTab, ...ps];
      });
  }

  phaseChange(event) {
    this.activePhase = event;
  }
}
