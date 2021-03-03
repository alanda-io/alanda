import { Component, Input } from '@angular/core';
import {
  AlandaPageTab,
  AlandaProject,
  AlandaProjectApiService,
  AlandaTask,
  AlandaUser,
  BaseState,
} from '@alanda/common';
import { map, switchMap } from 'rxjs/operators';
import { RxState } from '@rx-angular/state';
import { FormGroup } from '@angular/forms';

export interface VacationPageHeaderState extends BaseState {
  pid: string;
  user: AlandaUser;
  tabs: AlandaPageTab[];
}

@Component({
  selector: 'alanda-vacation-page-header',
  templateUrl: './vacation-page-header.component.html',
})
export class VacationPageHeaderComponent {
  @Input() set project(project: AlandaProject) {
    this.state.set({ project });
  }
  @Input() set task(task: AlandaTask) {
    if (task) {
      this.state.set({ task });
    } else {
      this.state.set({ task: null });
    }
  }
  @Input() set user(user: AlandaUser) {
    this.state.set({ user });
  }
  @Input() rootFormGroup: FormGroup;

  activeTabIndex = 0;
  activePhase: string;

  overviewTab: AlandaPageTab = {
    name: 'Overview',
    icon: 'pi pi-bookmark',
    phase: null,
  };

  pioTab: AlandaPageTab = {
    name: 'Pio',
    icon: 'pi pi-eye',
    phase: null,
  };

  historyLogTab: AlandaPageTab = {
    name: 'History Log',
    icon: 'pi pi-clock',
    phase: null,
  };

  peopleTab: AlandaPageTab = {
    name: 'People',
    icon: 'pi pi-users',
    phase: null,
  };

  getPidFromProject$ = this.state.select('project').pipe(
    map((project: AlandaProject) => {
      return project.processes.find((proc) => proc.relation === 'MAIN')
        .processInstanceId;
    }),
  );

  tabs$ = this.state.select('project').pipe(
    switchMap((project) =>
      this.projectApiService.getPhasesForProject(project.guid),
    ),
    map((response) => {
      const phases = response.filter(
        (phase) => phase.pmcProjectPhaseDefinition.idName !== 'PHASE1',
      );

      const phaseTabs: AlandaPageTab[] = phases.map((phase) => {
        return {
          name: phase.pmcProjectPhaseDefinition.displayName,
          icon: 'pi pi-play',
          phase: phase,
        };
      });

      return [
        this.overviewTab,
        this.pioTab,
        this.historyLogTab,
        ...phaseTabs,
        this.peopleTab,
      ];
    }),
  );

  constructor(
    private projectApiService: AlandaProjectApiService,
    public state: RxState<VacationPageHeaderState>,
  ) {
    this.state.connect('tabs', this.tabs$);
    this.state.connect('pid', this.getPidFromProject$);
  }

  phaseChange(event) {
    this.activePhase = event;
  }
}
