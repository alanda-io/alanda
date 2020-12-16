import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MenuItem, MessageService } from 'primeng/api';
import { AlandaSimplePhase } from '../../api/models/simplePhase';
import { AlandaProject } from '../../api/models/project';
import { AlandaProjectApiService } from '../../api/projectApi.service';
import { concatMap, filter, map, take, tap } from 'rxjs/operators';
import { RxState } from '@rx-angular/state';
import { AlandaUser } from '../../api/models/user';
import { Subject } from 'rxjs';
import { Authorizations } from '../../permissions';

export interface AlandaPhaseTabState {
  simplePhases: AlandaSimplePhase[];
  project: AlandaProject;
  activePhase: AlandaSimplePhase;
  phase: string;
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
    this.authBase = `project:${project.authBase}:phase`;
  }
  @Input() set simplePhases(simplePhases: AlandaSimplePhase[]) {
    this.state.set({ simplePhases });
  }
  @Input() phase: string;
  @Output() activePhaseChange = new EventEmitter<string>();
  @Input() user: AlandaUser;

  authBase: string;
  phaseChanges = new Subject<any>();

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

  phaseStatusMap = {
    overview: {
      label: '',
      styleClass: 'overview pi pi-bookmark',
    },
    active: {
      label: 'active',
      styleClass: 'pi pi-play p-button p-button-text',
    },
    completed: {
      label: 'completed',
      styleClass: 'pi pi-check-circle p-button p-button-text p-button-success',
    },
    error: {
      label: 'error',
      styleClass:
        'pi pi-exclamation-circle p-button p-button-text p-button-danger',
    },
    starting: {
      label: 'starting',
      styleClass: 'starting pi pi-spinner pi-spin p-button p-button-text',
    },
    required: {
      label: 'required',
      styleClass: 'pi pi-check',
    },
    notRequired: {
      label: 'not required',
      styleClass: 'pi pi-ban',
    },
    notSet: {
      label: 'not set',
      styleClass: 'not-set pi pi-question-circle',
    },
  };

  state$ = this.state.select();

  /** set the initial phase tab */
  simplePhases$ = this.state.select('simplePhases').pipe(
    take(1), // we only set the initial phase once
    map((simplePhases: AlandaSimplePhase[]) => {
      const phases = [...simplePhases];
      let activePhase = phases[0];
      if (this.phase) {
        const initPhase = phases.find(
          (phase: AlandaSimplePhase) =>
            phase.pmcProjectPhaseDefinition.idName === this.phase,
        );
        if (initPhase != null) {
          activePhase = initPhase;
        }
      }
      return {
        activePhase: activePhase,
      };
    }),
    tap((phaseState) => {
      this.activePhaseChange.emit(
        phaseState.activePhase.pmcProjectPhaseDefinition.idName,
      );
    }),
  );

  /** If a tab is clicked or the phase set using phase input, we change the activePhase and emit an event */
  phaseChange$ = this.phaseChanges.pipe(
    filter(
      (phase: AlandaSimplePhase) =>
        phase.pmcProjectPhaseDefinition.idName !==
        this.state.get('activePhase').pmcProjectPhaseDefinition.idName,
    ),
    tap((phase: AlandaSimplePhase) => {
      this.activePhaseChange.emit(phase.pmcProjectPhaseDefinition.idName);
    }),
  );

  constructor(
    private state: RxState<AlandaPhaseTabState>,
    private readonly projectApiService: AlandaProjectApiService,
    private messageService: MessageService,
  ) {
    this.state.connect(this.simplePhases$);
    this.state.connect('activePhase', this.phaseChange$);
  }

  getPhaseStatus(
    phase: AlandaSimplePhase,
  ): { label: string; styleClass: string } {
    if (!phase.guid) {
      return this.phaseStatusMap.overview;
    } else if (phase.active) {
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

  togglePhaseEnabled(phase: AlandaSimplePhase, enabled: boolean): void {
    const projectGuid = this.state.get().project.guid;
    const phaseDefIdName = phase.pmcProjectPhaseDefinition.idName;

    this.projectApiService
      .setPhaseEnabled(projectGuid, phaseDefIdName, enabled)
      .subscribe((response) => {
        this.updatePhasesInState(response);
      });
  }

  getMenuItems(phase: AlandaSimplePhase): MenuItem[] {
    const project = this.state.get().project;
    const menuItems: MenuItem[] = [];
    if (!phase.enabled) {
      menuItems.push({
        label: 'Required',
        icon: 'pi pi-check',
        command: () => this.togglePhaseEnabled(phase, true),
      });
    }
    if (phase.enabled || phase.enabled === null) {
      menuItems.push({
        label: 'Not required',
        icon: 'pi pi-ban',
        command: () => this.togglePhaseEnabled(phase, false),
      });
    }
    if (project.status !== 'CANCELED' && project.status !== 'COMPLETED') {
      if (phase.endDate && this.hasAuth(phase, 'restart')) {
        menuItems.push({
          label: 'Restart Phase',
          icon: 'pi pi-refresh',
          command: () => this.restartPhase(phase),
        });
      }
      if (phase.frozen && !phase.enabled && this.hasAuth(phase, 'start')) {
        menuItems.push({
          label: 'Start Phase',
          icon: 'pi pi-play',
          command: () => this.startPhase(phase),
        });
      }
    }
    return menuItems;
  }

  restartPhase(phase: AlandaSimplePhase) {
    const projectGuid = this.state.get().project.guid;
    const phaseDefIdName = phase.pmcProjectPhaseDefinition.idName;

    this.projectApiService
      .restartPhase(projectGuid, phaseDefIdName)
      .pipe(
        concatMap(() => {
          return this.projectApiService.getPhase(projectGuid, phaseDefIdName);
        }),
      )
      .subscribe(
        (response: AlandaSimplePhase) => {
          this.messageService.add({
            severity: 'success',
            summary: 'Phase restarted',
            detail: `The phase has been successfully restarted!`,
          });
          this.updatePhasesInState(response);
        },
        (error) => {
          this.messageService.add({
            key: 'center',
            severity: 'error',
            summary: 'Phase restart failed',
            detail: `The phase could not be restarted: ${error.message}`,
            sticky: true,
          });
        },
      );
  }

  startPhase(phase: AlandaSimplePhase) {
    const projectGuid = this.state.get().project.guid;
    const phaseDefIdName = phase.pmcProjectPhaseDefinition.idName;

    this.projectApiService
      .startPhase(projectGuid, phaseDefIdName)
      .pipe(
        concatMap(() => {
          return this.projectApiService.getPhase(projectGuid, phaseDefIdName);
        }),
      )
      .subscribe(
        (response: AlandaSimplePhase) => {
          this.messageService.add({
            severity: 'success',
            summary: 'Task completed',
            detail: `The phase has been successfully started!`,
          });
          this.updatePhasesInState(response);
        },
        (error) => {
          this.messageService.add({
            key: 'center',
            severity: 'error',
            summary: 'Phase start failed',
            detail: `The phase could not be started: ${error.message}`,
            sticky: true,
          });
        },
      );
  }

  hasAuth(phase: AlandaSimplePhase, accessLevel: string): boolean {
    return Authorizations.hasPermission(
      this.user,
      `${this.authBase}:${phase.pmcProjectPhaseDefinition.idName}`,
      accessLevel,
    );
  }

  private updatePhasesInState(response: AlandaSimplePhase) {
    const newSimplePhases = [...this.state.get().simplePhases];
    const i = newSimplePhases.findIndex(
      (phase) => phase.guid === response.guid,
    );
    if (i !== -1) {
      newSimplePhases[i] = response;
      this.state.set(() => ({
        simplePhases: newSimplePhases,
      }));
    }
  }
}
