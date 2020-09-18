import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MenuItem } from 'primeng/api';
import { AlandaSimplePhase } from '../../api/models/simplePhase';
import { AlandaProject } from '../../api/models/project';
import { AlandaProjectApiService } from '../../api/projectApi.service';
import { map, switchMap, tap } from 'rxjs/operators';
import { RxState } from '@rx-angular/state';
import { AlandaUser } from '../../api/models/user';

export interface AlandaPhaseTabState {
  simplePhases: AlandaSimplePhase[];
  project: AlandaProject;
}
// TODO: add phase permission handling, add start/restart enabled behaviour
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
  @Input() activePhaseIndex: number;
  @Input() phase: string;
  @Output() activePhaseIndexChange = new EventEmitter<number>();
  @Input() user: AlandaUser;

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

  state$ = this.state.select();

  simplePhases$ = this.state.select('project').pipe(
    switchMap((project: AlandaProject) =>
      this.projectApiService.getPhasesForProject(project.guid),
    ),
    map((simplePhases: AlandaSimplePhase[]) => {
      return [this.overviewTab, ...simplePhases];
    }),
    tap((simplePhases: AlandaSimplePhase[]) => {
      if (!this.activePhaseIndex) {
        this.activePhaseIndex = 0;
      }

      if (this.phase) {
        const index = simplePhases.findIndex(
          (phase: AlandaSimplePhase) =>
            phase.pmcProjectPhaseDefinition.displayName.toLowerCase() ===
            this.phase.toLowerCase(),
        );
        if (index > -1) {
          this.activePhaseIndex = index;
        }
      }
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
    const phaseDefIdName = this.state.get().simplePhases[this.activePhaseIndex]
      .pmcProjectPhaseDefinition.idName;

    this.projectApiService
      .setPhaseEnabled(projectGuid, phaseDefIdName, enabled)
      .subscribe((response) => {
        const newSimplePhases = this.state.get().simplePhases;
        newSimplePhases[this.activePhaseIndex].enabled = enabled;
        this.state.set({
          simplePhases: newSimplePhases,
        });
      });
  }

  getMenuItems(phase: AlandaSimplePhase): MenuItem[] {
    const project = this.state.get().project;
    const menuItems: MenuItem[] = [];
    if (!phase.enabled) {
      menuItems.push({
        label: 'Enabled',
        command: () => this.togglePhaseEnabled(true),
      });
    }
    if (phase.enabled) {
      menuItems.push({
        label: 'Disabled',
        command: () => this.togglePhaseEnabled(false),
      });
    }
    if (project.status !== 'CANCELED' && project.status !== 'COMPLETED') {
      if (phase.endDate) {
        menuItems.push({
          label: 'Restart Phase',
          command: () => this.restartPhase(),
        });
      }
      if (phase.frozen && !phase.enabled) {
        menuItems.push({
          label: 'Start Phase',
          command: () => this.startPhase(),
        });
      }
    }
    return menuItems;
  }

  restartPhase() {
    const projectGuid = this.state.get().project.guid;
    const phaseDefIdName = this.state.get().simplePhases[this.activePhaseIndex]
      .pmcProjectPhaseDefinition.idName;

    this.projectApiService
      .restartPhase(projectGuid, phaseDefIdName)
      .subscribe((response: AlandaSimplePhase) => {
        const newSimplePhases = this.state.get().simplePhases;
        newSimplePhases[this.activePhaseIndex] = response;
        this.state.set({
          simplePhases: newSimplePhases,
        });
      });
  }

  startPhase() {
    const projectGuid = this.state.get().project.guid;
    const phaseDefIdName = this.state.get().simplePhases[this.activePhaseIndex]
      .pmcProjectPhaseDefinition.idName;

    this.projectApiService
      .startPhase(projectGuid, phaseDefIdName)
      .subscribe((response: AlandaSimplePhase) => {
        const newSimplePhases = this.state.get().simplePhases;
        newSimplePhases[this.activePhaseIndex] = response;
        this.state.set({
          simplePhases: newSimplePhases,
        });
      });
  }
}
