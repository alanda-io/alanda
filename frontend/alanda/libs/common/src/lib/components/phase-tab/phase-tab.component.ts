import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MenuItem } from 'primeng/api';
import { AlandaSimplePhase } from '../../api/models/simplePhase';
import { AlandaProject } from '../../api/models/project';
import { AlandaProjectApiService } from '../../api/projectApi.service';
import { map, switchMap } from 'rxjs/operators';
import { RxState } from '@rx-angular/state';

export interface AlandaPhaseTabState {
  simplePhases: AlandaSimplePhase[];
  project: AlandaProject;
}

@Component({
  selector: 'alanda-phase-tab',
  templateUrl: './phase-tab.component.html',
  styleUrls: ['./phase-tab.component.scss'],
  providers: [RxState],
})
export class AlandaPhaseTabComponent {
  @Input()
  set project(project: AlandaProject) {
    this.state.set({ project });
  }
  @Input() activePhaseIndex = 0;
  @Output() activePhaseIndexChange = new EventEmitter<number>();

  overviewTab: AlandaSimplePhase = {
    active: false,
    enabled: false,
    pmcProjectPhaseDefinition: {
      allowedProcesses: null,
      displayName: 'Overview',
      guid: null,
      idName: null,
    },
    guid: null,
    idName: null,
    updateUser: null,
    updateDate: null,
  };

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

  state$ = this.state.select();

  simplePhases$ = this.state.select('project').pipe(
    switchMap((project: AlandaProject) =>
      this.projectApiService.getPhasesForProject(project.guid),
    ),
    map((simplePhases) => {
      return [this.overviewTab, ...simplePhases];
    }),
  );

  constructor(
    private state: RxState<AlandaPhaseTabState>,
    private readonly projectApiService: AlandaProjectApiService,
  ) {
    this.state.connect('simplePhases', this.simplePhases$);
  }

  setActivePhaseIndex(index: number): void {
    if (this.activePhaseIndex === index) {
      return;
    }

    this.activePhaseIndex = index;
    this.activePhaseIndexChange.emit(this.activePhaseIndex);
  }

  getPhaseStatus(
    phase: AlandaSimplePhase,
  ): { label: string; styleClass: string } {
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
    const projectGuid = this.state.get().project.guid;
    const phaseDefidName = this.state.get().simplePhases[this.activePhaseIndex]
      .pmcProjectPhaseDefinition.idName;

    this.projectApiService
      .setPhaseEnabled(projectGuid, phaseDefidName, enabled)
      .subscribe((response) => {
        const newSimplePhases = this.state.get().simplePhases;
        newSimplePhases[this.activePhaseIndex].enabled = enabled;
        this.state.set({
          simplePhases: newSimplePhases,
        });
      });
  }
}
