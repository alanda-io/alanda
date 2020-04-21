import {Injectable} from '@angular/core';
import {RxState} from 'ngx-rx-state';
import {ActivatedRoute, Router} from '@angular/router';
import {filter, map, switchMap} from 'rxjs/operators';

import {of} from "rxjs";
import {FormBuilder} from "@angular/forms";
import { AlandaTask } from '../api/models/task';
import { AlandaProject } from '../api/models/project';
import { AlandaProjectApiService } from '../api/projectApi.service';
import { AlandaTaskApiService } from '../api/taskApi.service';

export interface AlandaTaskFormState {
  task?: AlandaTask;
  project?: AlandaProject;
//  rootFormData: { [controlName: string]: any }
}

@Injectable()
export class AlandaTaskFormService extends RxState<AlandaTaskFormState> {

  state$ = this.select();

  rootForm = this.fb.group({});

  routerParams$ = this.route.params;
  /*
  routerParams$ = this.router.events
    .pipe(filter((event: RouterEvent): boolean => (event instanceof NavigationEnd)),
      map(() => this.router.routerState.snapshot.root),
      // @TODO if we get away from global task managing dete this line and move coed
      map(snapshot => this.collectParams(snapshot))
    );*/
  urlTaskId$ = this.routerParams$.pipe(map(p => p.taskId));

  fetchTaskById$ = this.urlTaskId$.pipe(
    switchMap((tid) => {
      return this.taskService.getTask(tid);
    })
  );

  fetchProjectByGuid$ = this.select('task').pipe(
    filter((task: AlandaTask) => {
      return !!task.pmcProjectGuid}),
    switchMap((task: AlandaTask) => {
      return this.projectService.getProjectByGuid(task.pmcProjectGuid)
    })
  )

  constructor(
    private route: ActivatedRoute,
    private fb: FormBuilder,
    private taskService: AlandaTaskApiService,
    private projectService: AlandaProjectApiService) {
      super();
      this.connect('task', this.fetchTaskById$);
      this.connect('project', this.fetchProjectByGuid$);
  }

  /*
    private collectParams(root: ActivatedRouteSnapshot): any {
      const params: any = {};
      (function mergeParamsFromSnapshot(snapshot: ActivatedRouteSnapshot) {
        Object.assign(params, snapshot.params);
        snapshot.children.forEach(mergeParamsFromSnapshot);
      })(root);
      return (params);
    }
  */

  addValidators(validators) {
    this.rootForm.setValidators(validators);
  }

  submit(): void {
    if (this.rootForm.valid) {
      this.taskService.complete(this.getState().task.task_id)
    }
  }

}
