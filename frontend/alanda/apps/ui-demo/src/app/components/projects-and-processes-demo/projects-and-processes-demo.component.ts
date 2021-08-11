import { Component, OnInit } from '@angular/core';
import { RxState } from '@rx-angular/state';
import { switchMap, tap, map, distinctUntilChanged } from 'rxjs/operators';
import { AlandaProject, AlandaProjectApiService } from '@alanda/common';
import { ActivatedRoute } from '@angular/router';

interface ProjectsAndProcessesDemoComponentState {
  project: AlandaProject;
}

@Component({
  selector: 'alanda-projects-and-processes-demo',
  templateUrl: './projects-and-processes-demo.component.html',
  providers: [RxState],
})
export class ProjectsAndProcessesDemoComponent {
  state$ = this.state.select();

  routerParams$ = this.route.params.pipe(
    map((p) => p.projectId),
    distinctUntilChanged(),
  );

  fetchProjectByProjectId$ = this.routerParams$.pipe(
    switchMap((projectId) => {
      return this.projectService.getProjectByProjectId(projectId);
    }),
  );

  constructor(
    private readonly route: ActivatedRoute,
    private state: RxState<ProjectsAndProcessesDemoComponentState>,
    private readonly projectService: AlandaProjectApiService,
  ) {
    this.state.connect('project', this.fetchProjectByProjectId$);
    this.state.hold(this.routerParams$);
  }
}
