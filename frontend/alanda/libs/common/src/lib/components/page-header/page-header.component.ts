import { Component, Input, Output, ViewChild } from '@angular/core';
import { AlandaProject } from '../../api/models/project';
import { RxState } from '@rx-angular/state';
import {
  concatMap,
  filter,
  map,
  take,
  tap,
  withLatestFrom,
} from 'rxjs/operators';
import { EventEmitter } from '@angular/core';
import { AlandaSimplePhase } from '../../api/models/simplePhase';
import { AlandaUser } from '../../api/models/user';
import { Subject } from 'rxjs';
import { AlandaProjectApiService } from '../../api/projectApi.service';
import { MenuItem, MessageService } from 'primeng/api';
import { Authorizations } from '../../permissions';
import { Menu } from 'primeng/menu';

interface PageHeaderState {
  project: AlandaProject;
  pageTabs: AlandaPageTab[];
  activePageTab: AlandaPageTab;
  simplePhases: AlandaSimplePhase[];
  readOnlyPhases: string[];
  activePhase: AlandaSimplePhase;
  phase: string;
}

export interface AlandaPageTab {
  name: string;
  icon: string;
  phase: AlandaSimplePhase;
}

@Component({
  selector: 'alanda-page-header',
  templateUrl: './page-header.component.html',
  styleUrls: ['./page-header.component.scss'],
})
export class PageHeaderComponent {
  @Input() set project(project: AlandaProject) {
    this.state.set({ project });
    if (project) {
      this.authBase = `project:${project?.authBase}:phase`;
    }
  }
  @Input() set tabs(pageTabs: AlandaPageTab[]) {
    this.state.set({ pageTabs });
  }
  @Input() activeTabIndex = 0;
  @Output() activeTabIndexChange = new EventEmitter<number>();

  @Input() set simplePhases(simplePhases: AlandaSimplePhase[]) {
    this.state.set({ simplePhases });
  }
  @Input() set readOnlyPhases(readOnlyPhases: string[]) {
    this.state.set({ readOnlyPhases });
  }
  @Input() phase: string;
  @Input() user: AlandaUser;

  @Output() activePhaseChange = new EventEmitter<string>();

  @ViewChild('menu') menu: Menu;

  authBase: string;
  activePageTabChangeEvent = new Subject<AlandaPageTab>();

  phaseStatusMap = {
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

  /** set the initial phase tab */
  activePageTab$ = this.state.select('pageTabs').pipe(
    take(1), // we only set the initial phase once
    map((pageTabs: AlandaPageTab[]) => {
      const tabs = [...pageTabs];
      let activeTab = tabs[0];
      if (this.phase) {
        const initTab = tabs.find(
          (tab: AlandaPageTab) =>
            tab.phase?.pmcProjectPhaseDefinition.idName === this.phase,
        );
        if (initTab != null) {
          activeTab = initTab;
        }
      }
      return activeTab;
    }),
  );

  /** If a tab is clicked or the phase set using phase input, we change the activePhase and emit an event */
  activePageTabChange$ = this.activePageTabChangeEvent.pipe(
    withLatestFrom(this.state.select('activePageTab')),
    filter(([pageTab, activePageTab]) => pageTab.name !== activePageTab.name),
    map(([pageTab, _]) => pageTab),
  );

  emitActiveTabIndexChange$ = this.state.select('activePageTab').pipe(
    withLatestFrom(this.state.select('pageTabs')),
    tap(([activePageTab, pageTabs]) => {
      const index = pageTabs.findIndex((tab) => {
        return tab.name === activePageTab.name;
      });
      this.activeTabIndexChange.emit(index);

      if (activePageTab.phase) {
        this.activePhaseChange.emit(
          activePageTab.phase?.pmcProjectPhaseDefinition.idName,
        );
      }
    }),
  );

  constructor(
    public state: RxState<PageHeaderState>,
    private readonly projectApiService: AlandaProjectApiService,
    private messageService: MessageService,
  ) {
    this.state.connect('activePageTab', this.activePageTab$);
    this.state.connect('activePageTab', this.activePageTabChange$);
    this.state.hold(this.emitActiveTabIndexChange$);
  }

  updateTabIndex(value: number): void {
    this.activeTabIndexChange.emit(value);
  }

  getPhaseStatus(
    phase: AlandaSimplePhase,
  ): { label: string; styleClass: string } {
    if (phase.active) {
      return this.phaseStatusMap.active;
    } else if (phase.endDate != null) {
      return this.phaseStatusMap.completed;
    } else if (phase.frozen && phase.enabled === null) {
      return this.phaseStatusMap.error;
    } else if (phase.frozen && phase.enabled === true) {
      return this.phaseStatusMap.starting;
    } else if (phase.enabled === false) {
      return this.phaseStatusMap.notRequired;
    } else if (phase.enabled === null) {
      return this.phaseStatusMap.notSet;
    } else if (phase.enabled === true) {
      return this.phaseStatusMap.required;
    }
    return this.phaseStatusMap.notSet; // cannot happen because at least one of the 3 enabled states must match
  }

  getMenuItems(phase: AlandaSimplePhase): MenuItem[] {
    const menuItems: MenuItem[] = [];
    const startEnabled: boolean = this.startEnabled(phase);
    const restartEnabled: boolean = this.restartEnabled(phase);
    if (!phase.enabled && !startEnabled && !restartEnabled) {
      menuItems.push({
        label: 'Required',
        icon: 'pi pi-check',
        command: () => this.togglePhaseEnabled(phase, true),
      });
    }
    if (
      (phase.enabled || phase.enabled === null) &&
      !startEnabled &&
      !restartEnabled
    ) {
      menuItems.push({
        label: 'Not required',
        icon: 'pi pi-ban',
        command: () => this.togglePhaseEnabled(phase, false),
      });
    }
    if (restartEnabled) {
      menuItems.push({
        label: 'Restart Phase',
        icon: 'pi pi-refresh',
        command: () => this.restartPhase(phase),
      });
    }
    if (startEnabled) {
      menuItems.push({
        label: 'Start Phase',
        icon: 'pi pi-play',
        command: () => this.startPhase(phase),
      });
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

  startEnabled(phase: AlandaSimplePhase): boolean {
    if (
      this.hasAuth(phase, 'start') &&
      this.state.get('project').status !== 'CANCELED' &&
      this.state.get('project').status !== 'COMPLETED' &&
      phase.frozen === true &&
      phase.enabled === false &&
      phase.pmcProjectPhaseDefinition.phaseProcessKey != null
    ) {
      return true;
    }
    return false;
  }

  isReadOnly(phase: AlandaSimplePhase): boolean {
    if (phase == null) {
      return true;
    }
    let readOnly = false;
    const roPhases =
      this.state.get('readOnlyPhases') != null
        ? this.state.get('readOnlyPhases')
        : [];
    if (
      !this.hasAuth(phase, 'write') ||
      phase.guid == null ||
      roPhases.findIndex(
        (phaseIdName) => phaseIdName === phase.pmcProjectPhaseDefinition.idName,
      ) > -1 ||
      this.state.get('project').status === 'CANCELED' ||
      this.state.get('project').status === 'COMPLETED'
    ) {
      readOnly = true;
    } else if (
      (phase.startDate != null || phase.frozen === true) &&
      !this.startEnabled(phase) &&
      !this.restartEnabled(phase)
    ) {
      readOnly = true;
    }
    return readOnly;
  }

  restartEnabled(phase: AlandaSimplePhase): boolean {
    if (
      this.hasAuth(phase, 'restart') &&
      this.state.get('project').status !== 'CANCELED' &&
      this.state.get('project').status !== 'COMPLETED' &&
      phase.endDate !== null &&
      phase.pmcProjectPhaseDefinition.phaseProcessKey != null
    ) {
      return true;
    }
    return false;
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

  updatePhasesInState(response: AlandaSimplePhase) {
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

  hasAuth(phase: AlandaSimplePhase, accessLevel: string): boolean {
    return Authorizations.hasPermission(
      this.user,
      `${this.authBase}:${phase.pmcProjectPhaseDefinition.idName}`,
      accessLevel,
    );
  }

  toggleMenu(event: MouseEvent): void {
    event.stopPropagation();
    this.menu.toggle(event)
  }
}
