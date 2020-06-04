import { Injectable, OnDestroy } from '@angular/core';
import { RxState } from '@rx-angular/state';
import {
  Router,
  ActivatedRouteSnapshot,
  RouterEvent,
  NavigationEnd,
} from '@angular/router';
import {
  filter,
  map,
  switchMap,
  concatMap,
  tap,
  catchError,
} from 'rxjs/operators';

import { of, Observable, EMPTY, PartialObserver, throwError } from 'rxjs';
import { FormBuilder } from '@angular/forms';
import { AlandaTask } from '../api/models/task';
import { AlandaProject } from '../api/models/project';
import { AlandaProjectApiService } from '../api/projectApi.service';
import { AlandaTaskApiService } from '../api/taskApi.service';
import { MessageService } from 'primeng/api';

export interface AlandaTaskFormState {
  task?: AlandaTask;
  project?: AlandaProject;
  //  rootFormData: { [controlName: string]: any }
}

@Injectable()
export class AlandaTaskFormService extends RxState<AlandaTaskFormState>
  implements OnDestroy {
  state$ = this.select();

  rootForm = this.fb.group({});

  // routerParams$ = this.route.params;

  routerParams$ = this.router.events.pipe(
    filter((event: RouterEvent): boolean => event instanceof NavigationEnd),
    map(() => this.router.routerState.snapshot.root),
    // @TODO if we get away from global task managing delete this line and move code
    map((snapshot) => this.collectParams(snapshot))
    // tap((sn) => console.log("sn", sn))
  );

  urlTaskId$ = this.routerParams$.pipe(map((p) => p.taskId));

  fetchTaskById$ = this.urlTaskId$.pipe(
    switchMap((tid) => {
      return this.taskService.getTask(tid);
    }),
    concatMap((task: AlandaTask) => {
      if (task.pmcProjectGuid) {
        return this.projectService
          .getProjectByGuid(task.pmcProjectGuid)
          .pipe(map((project) => ({ task, project })));
      }
      return of({ task });
    })
  );

  constructor (
    private readonly router: Router,
    private readonly fb: FormBuilder,
    private readonly taskService: AlandaTaskApiService,
    private readonly projectService: AlandaProjectApiService,
    private readonly messageService: MessageService
  ) {
    super();
    this.connect(this.fetchTaskById$);
  }

  private collectParams (root: ActivatedRouteSnapshot): any {
    const params: any = {};
    (function mergeParamsFromSnapshot (snapshot: ActivatedRouteSnapshot) {
      Object.assign(params, snapshot.params);
      snapshot.children.forEach(mergeParamsFromSnapshot);
    })(root);
    return params;
  }

  addValidators (validators): void {
    this.rootForm.setValidators(validators);
  }

  submit (alternate?: Observable<any>): Observable<any> {
    this.rootForm.markAllAsTouched();
    if (this.rootForm.valid) {
      return this.taskService.complete(this.get().task.task_id).pipe(
        catchError((error) => {
          console.log('in catchError', error);
          this.messageService.add({
            severity: 'error',
            summary: 'Task completion failed',
            detail: `The task ${
              this.get().task.task_name
            } could not be completed: ${error}`,
          });
          return EMPTY;
        }),
        tap((resp) =>
          this.messageService.add({
            severity: 'success',
            summary: 'Task completed',
            detail: `The task ${
              this.get().task.task_name
            } has been successfully completed!`,
          })
        ),
        switchMap((val) => {
          if (alternate != null) {
            return alternate;
          } else {
            return of(['/']);
          }
        }),
        tap((val) => {
          console.log('in tap, val');
          this.router.navigate(val).catch(() => {});
        })
      );
    } else {
      console.log('errors', this.rootForm.errors);
      return of(this.rootForm.errors);
    }
  }
}
