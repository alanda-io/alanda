import { Injectable } from '@angular/core';
import { Observable, combineLatest } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import {
  AlandaPageTab,
  AlandaProjectApiService,
  AlandaTaskFormService,
} from '@alanda/common';
import { UserStoreImpl } from '../store/user';

@Injectable()
export class UserEnrichedTaskFormService {
  rootForm = this.taskFormService.rootForm;

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

  state$ = combineLatest([
    this.taskFormService.state$,
    this.userStore.currentUser$,
  ]).pipe(
    map(([{ task, project, tabs }, user]) => ({ task, project, tabs, user })),
  );

  tabs$ = this.taskFormService.select('project').pipe(
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
    public taskFormService: AlandaTaskFormService,
    private userStore: UserStoreImpl,
    private projectApiService: AlandaProjectApiService,
  ) {
    this.taskFormService.connect('tabs', this.tabs$);
  }

  submit(alternate?: Observable<any>): Observable<any> {
    return this.taskFormService.submit(alternate);
  }

  snooze(days: number): Observable<any> {
    return this.taskFormService.snooze(days);
  }
}
