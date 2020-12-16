import { Inject, Injectable, OnDestroy } from '@angular/core';
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
  delay,
} from 'rxjs/operators';

import { of, Observable, EMPTY } from 'rxjs';
import { FormBuilder } from '@angular/forms';
import { AlandaTask } from '../api/models/task';
import { AlandaProject } from '../api/models/project';
import { AlandaProjectApiService } from '../api/projectApi.service';
import { AlandaTaskApiService } from '../api/taskApi.service';
import { MessageService } from 'primeng/api';
import { AlandaTitleService } from '../services/title.service';
import { AppSettings, APP_CONFIG } from '../models/appSettings';

export interface AlandaTaskFormState {
  task?: AlandaTask;
  project?: AlandaProject;
  loading?: number;
  closeAfterComplete?: boolean;
  //  rootFormData: { [controlName: string]: any }
}

@Injectable({ providedIn: 'root' })
export class AlandaTaskFormService extends RxState<AlandaTaskFormState>
  implements OnDestroy {
  state$ = this.select().pipe(filter((state) => state.task != null));

  closeAfterComplete = false;

  rootForm = this.fb.group({});

  // routerParams$ = this.route.params;

  routerParams$ = this.router.events.pipe(
    filter((event: RouterEvent): boolean => event instanceof NavigationEnd),
    map(() => this.router.routerState.snapshot.root),
    // @TODO if we get away from global task managing delete this line and move code
    map((snapshot) => this.collectParams(snapshot)),
  );

  urlTaskId$ = this.routerParams$.pipe(map((p) => p.taskId));

  fetchTaskById$ = this.urlTaskId$.pipe(
    switchMap((tid) => {
      return this.taskService.getTask(tid);
    }),
    concatMap((task: AlandaTask) => {
      this.titleService.setTaskTitle(task);
      if (task.pmcProjectGuid) {
        return this.projectService
          .getProjectByGuid(task.pmcProjectGuid)
          .pipe(map((project) => ({ task, project })));
      }
      return of({ task });
    }),
    catchError((error) => {
      console.log(error, this.messageService);
      this.messageService.add({
        key: 'center',
        severity: 'error',
        summary: 'Task load failed',
        detail: `The task could not be loaded: ${error.message}`,
        sticky: true,
      });
      return EMPTY;
    }),
  );

  constructor(
    private readonly router: Router,
    private readonly fb: FormBuilder,
    private readonly taskService: AlandaTaskApiService,
    private readonly projectService: AlandaProjectApiService,
    private readonly messageService: MessageService,
    private readonly titleService: AlandaTitleService,
    @Inject(APP_CONFIG) config: AppSettings,
  ) {
    super();
    this.closeAfterComplete = config.CLOSE_AFTER_COMPLETE;
    this.set({ loading: 0 });
    this.connect(this.fetchTaskById$);
  }

  private collectParams(root: ActivatedRouteSnapshot): any {
    const params: any = {};
    (function mergeParamsFromSnapshot(snapshot: ActivatedRouteSnapshot) {
      Object.assign(params, snapshot.params);
      snapshot.children.forEach(mergeParamsFromSnapshot);
    })(root);
    return params;
  }

  addValidators(validators): void {
    this.rootForm.setValidators(validators);
  }

  submit(alternate?: Observable<any>): Observable<any> {
    this.rootForm.markAllAsTouched();
    if (this.rootForm.valid) {
      this.setLoading(true);
      return this.taskService.complete(this.get().task.task_id).pipe(
        catchError((error) => {
          if (error.error != null) {
            error.message = error.error.message;
          }
          this.messageService.add({
            key: 'center',
            severity: 'error',
            summary: 'Task completion failed',
            detail: `The task ${
              this.get().task.task_name
            } could not be completed: ${error.message}`,
            sticky: true,
          });
          this.setLoading(false);
          return EMPTY;
        }),
        tap((resp) => {
          this.messageService.add({
            severity: 'success',
            summary: 'Task completed',
            detail: `The task ${
              this.get().task.task_name
            } has been successfully completed!`,
          });
          this.setLoading(false);
          console.log('loading', this.get('loading'));
        }),
        concatMap((val) => {
          if (alternate != null) {
            return alternate;
          } else {
            if (this.closeAfterComplete) {
              return of(null).pipe(delay(2000));
            } else {
              return of(['/']);
            }
          }
        }),
        tap((val) => {
          if (val === null) {
            window.close();
          } else {
            this.router.navigate(val).catch(() => {});
          }
        }),
      );
    } else {
      return of(this.rootForm.errors);
    }
  }

  snooze(days: number): Observable<any> {
    return this.taskService.snoozeTask(this.get().task.task_id, days).pipe(
      catchError((error) => {
        this.messageService.add({
          key: 'center',
          severity: 'error',
          summary: 'Task snooze failed',
          detail: `The task ${
            this.get().task.task_name
          } could not be snoozed: ${error}`,
        });
        return EMPTY;
      }),
      tap((resp) =>
        this.messageService.add({
          severity: 'success',
          summary: 'Task snoozed',
          detail: `The task ${
            this.get().task.task_name
          } has been successfully snoozed for ${days} days!`,
        }),
      ),
      delay(1000),
      tap((val) => {
        console.log('cAC', this.closeAfterComplete, window.opener);
        if (this.closeAfterComplete) {
          window.close();
        } else {
          this.router.navigate(['/']).catch(() => {});
        }
      }),
    );
  }

  connectLoadingState(o$: Observable<boolean>): void {
    this.connect('loading', o$, (oldState, isLoading) => {
      return isLoading ? ++oldState.loading : --oldState.loading;
    });
  }

  setLoading(isLoading: boolean): void {
    this.set('loading', (oldState) => {
      return isLoading ? ++oldState.loading : --oldState.loading;
    });
  }
}
