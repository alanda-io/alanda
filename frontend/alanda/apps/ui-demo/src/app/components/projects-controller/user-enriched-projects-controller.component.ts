import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { switchMap, tap, map, distinctUntilChanged } from 'rxjs/operators';
import { Subscription } from 'rxjs';
import { RxState } from '@rx-angular/state';
import {
  AlandaPageTab,
  AlandaProject,
  AlandaProjectApiService,
  AlandaUser,
  BaseState,
} from '@alanda/common';
import { UserStoreImpl } from '../../store/user/user.store';

export interface UserEnrichedProjectControllerState extends BaseState {
  pid: string;
  user: AlandaUser;
  tabs: AlandaPageTab[];
}

@Component({
  templateUrl: './user-enriched-projects-controller.component.html',
  styleUrls: [],
  providers: [RxState],
})
export class UserEnrichedProjectsControllerComponent {
  activeTab = 0;
  paramSub: Subscription;

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

  // routerParams$ = this.router.events
  // .pipe(
  //   filter((event: RouterEvent): boolean => (event instanceof NavigationEnd)),
  //   map(() => this.router.routerState.snapshot.root),
  //   // @TODO if we get away from global task managing dete this line and move coed
  //   map(snapshot => this.collectParams(snapshot)),
  //   tap(snapshot => console.log('sn', snapshot)),
  //   distinctUntilChanged(sn => sn.projectId),
  //   switchMap((params) => {
  //     console.log(params);
  //     return this.projectService.getProjectByProjectId(params.projectId);
  //   })
  // );

  routerParams$ = this.route.params.pipe(
    map((p) => p.projectId),
    distinctUntilChanged(),
  );

  fetchProjectByProjectId$ = this.routerParams$.pipe(
    switchMap((projectId) => {
      return this.projectService.getProjectByProjectId(projectId);
    }),
  );

  getPidFromProject$ = this.state.select('project').pipe(
    map((project: AlandaProject) => {
      return project.processes.find((proc) => proc.relation === 'MAIN')
        .processInstanceId;
    }),
  );

  forwardByType$ = this.state.select('project').pipe(
    tap((project: AlandaProject) => {
      this.router.navigate([project.projectTypeIdName.toLowerCase()], {
        relativeTo: this.route,
        skipLocationChange: true,
      });
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
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly projectService: AlandaProjectApiService,
    private projectApiService: AlandaProjectApiService,
    public state: RxState<UserEnrichedProjectControllerState>,
    private userStore: UserStoreImpl,
  ) {
    this.state.connect('project', this.fetchProjectByProjectId$);
    this.state.connect('pid', this.getPidFromProject$);
    this.state.connect('user', this.userStore.currentUser$);
    this.state.connect('tabs', this.tabs$);
    this.state.hold(this.forwardByType$);
  }

  phaseChange(event) {
    this.activePhase = event;
  }
}
