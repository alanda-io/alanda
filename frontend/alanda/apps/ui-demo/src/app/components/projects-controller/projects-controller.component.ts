import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { switchMap, tap, map, distinctUntilChanged } from 'rxjs/operators';
import { Subscription } from 'rxjs';
import { RxState } from '@rx-angular/state';
import { AlandaProjectApiService, AlandaUser, BaseState } from '@alanda/common';
import { UserStoreImpl } from '../../store/user/user.store';

export interface ProjectControllerState extends BaseState {
  pid: string;
  user: AlandaUser;
}

@Component({
  templateUrl: './projects-controller.component.html',
  providers: [RxState],
})
export class ProjectsControllerComponent {
  paramSub: Subscription;

  routerParams$ = this.route.params.pipe(
    map((p) => p.projectId),
    distinctUntilChanged(),
  );

  fetchProjectByProjectId$ = this.routerParams$.pipe(
    switchMap((projectId) => {
      return this.projectService.getProjectByProjectId(projectId);
    }),
  );

  forwardByType$ = this.state.select('project').pipe(
    tap((project) => {
      this.router.navigate([project.projectTypeIdName.toLowerCase()], {
        relativeTo: this.route,
        skipLocationChange: true,
      });
    }),
  );

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly projectService: AlandaProjectApiService,
    private projectApiService: AlandaProjectApiService,
    public state: RxState<ProjectControllerState>,
    private userStore: UserStoreImpl,
  ) {
    this.state.connect('project', this.fetchProjectByProjectId$);
    this.state.connect('user', this.userStore.currentUser$);
    this.state.hold(this.forwardByType$);
  }
}
