import { Component, Input } from '@angular/core';
import { MenuItem } from 'primeng/api';
import { AlandaSimplePhase } from '../../api/models/simplePhase';
import { AlandaProject } from '../../api/models/project';
import { AlandaProjectApiService } from '../../api/projectApi.service';
import { switchMap } from 'rxjs/operators';
import { RxState } from '@rx-angular/state';

export interface AlandaPhaseTabState {
  simplePhases: AlandaSimplePhase[];
  project: AlandaProject;
}

@Component({
  selector: 'alanda-phase-tab',
  templateUrl: './phase-tab.component.html',
  styleUrls: ['./phase-tab.component.scss'],
})
export class AlandaPhaseTabComponent extends RxState<AlandaPhaseTabState> {
  @Input()
  set project(project: AlandaProject) {
    this.set({ project });
  }

  activePhaseIndex = 0;

  phaseStatusMap = {
    active: {
      text: 'active',
      styleClass: 'active'
    },
    completed: {
      text: 'completed',
      styleClass: 'completed'
    },
    error: {
      text: 'error',
      styleClass: 'error'
    },
    starting: {
      text: 'starting',
      styleClass: 'starting'
    },
    required: {
      text: 'required',
      styleClass: 'required'
    },
    notRequired: {
      text: 'not required',
      styleClass: 'not-required'
    },
    notSet: {
      text: 'not set',
      styleClass: 'not-set'
    }
  }

  menuItems: MenuItem[] = [
    {
      label: 'Enabled',
      command: () => this.togglePhaseEnabled(true),
    },
    {
      label: 'Disabled',
      command: () => this.togglePhaseEnabled(false),
    },
  ];

  simplePhases$ = this.select('project').pipe(
    switchMap((project: AlandaProject) => {
      return this.projectApiService.getPhasesForProject(project.guid);
    }),
  );

  constructor(private readonly projectApiService: AlandaProjectApiService) {
    super();
    this.connect('simplePhases', this.simplePhases$);
  }

  setActivePhaseIndex(index): void {
    this.activePhaseIndex = index;
  }

  getPhaseStatus(phase: AlandaSimplePhase): object {
    if (phase.active) {
      return this.phaseStatusMap.active;
    } else if (phase.endDate) {
      return this.phaseStatusMap.completed;
    } else if (phase.frozen && phase.enabled === null) {
      return this.phaseStatusMap.error;
    } else if (phase.frozen && phase.enabled) {
      return this.phaseStatusMap.starting;
    } else if (phase.frozen && !phase.enabled) {
      return this.phaseStatusMap.notRequired;
    } else if (phase.enabled === null) {
      return this.phaseStatusMap.notSet;
    } else if (!phase.enabled) {
      return this.phaseStatusMap.notRequired;
    } else if (phase.enabled) {
      return this.phaseStatusMap.required;
    }
    return this.phaseStatusMap.notSet;
  }

  togglePhaseEnabled(enabled: boolean): void {
    const projectGuid = this.get().project.guid;
    const phaseDefidName = this.get().simplePhases[this.activePhaseIndex]
      .pmcProjectPhaseDefinition.idName;

    this.projectApiService
      .setPhaseEnabled(projectGuid, phaseDefidName, enabled)
      .subscribe((response) => {
        const newSimplePhases = this.get().simplePhases;
        newSimplePhases[this.activePhaseIndex].enabled = enabled;
        this.set({
          simplePhases: newSimplePhases,
        });
      });
  }
}
