import { Component, EventEmitter, Input, Output } from '@angular/core';
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
  @Input() activePhaseIndex = 0;
  @Output() activePhaseIndexChange = new EventEmitter<number>();

  phaseStatusMap = {
    active: {
      label: 'active',
      styleClass: 'active',
    },
    completed: {
      label: 'completed',
      styleClass: 'completed',
    },
    error: {
      label: 'error',
      styleClass: 'error',
    },
    starting: {
      label: 'starting',
      styleClass: 'starting',
    },
    required: {
      label: 'required',
      styleClass: 'required',
    },
    notRequired: {
      label: 'not required',
      styleClass: 'not-required',
    },
    notSet: {
      label: 'not set',
      styleClass: 'not-set',
    },
  };

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

  setActivePhaseIndex(index: number): void {
    if (this.activePhaseIndex === index) {
      return;
    }

    this.activePhaseIndex = index;
    this.activePhaseIndexChange.emit(this.activePhaseIndex);
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
