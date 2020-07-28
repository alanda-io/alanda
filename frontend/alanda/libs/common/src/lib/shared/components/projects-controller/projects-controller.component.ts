import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { switchMap, tap, map, distinctUntilChanged } from 'rxjs/operators';
import { AlandaProjectApiService } from '../../api/projectApi.service';
import { AlandaProject } from '../../api/models/project';
import { Subscription } from 'rxjs';
import { RxState } from '@rx-angular/state';
import { BaseState } from '../../../form/base-state';

export interface ProjectControllerState extends BaseState {
  pid: string;
}

@Component({
  templateUrl: './projects-controller.component.html',
  styleUrls: [],
  providers: [RxState],
})
export class AlandaProjectsControllerComponent {
  activeTab = 0;
  paramSub: Subscription;

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
      });
    }),
  );

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly projectService: AlandaProjectApiService,
    public state: RxState<ProjectControllerState>,
  ) {
    this.state.connect('project', this.fetchProjectByProjectId$);
    this.state.connect('pid', this.getPidFromProject$);
    this.state.hold(this.forwardByType$);
  }
}
