import { Injectable } from '@angular/core';
import { AlandaUserApiService, AlandaUser } from '@alanda/common';
import { AppStateService } from './app-state.service';
import { ActionService, Action } from './actions.service';
import { Observable } from 'rxjs';
import { filter, exhaustMap, map, switchMap, tap, mapTo } from 'rxjs/operators';

const loadUser = 'loadUser';
const LOAD_USER = (): Action => ({
  name: loadUser,
});

const runAsUser = 'runAsUser';
const RUNAS_USER = (name: string): Action => ({
  name: runAsUser,
  payload: { name },
});

const releaseRunAsUser = 'releaseRunAsUser';
const RELEASE_RUNAS_USER = (): Action => ({
  name: releaseRunAsUser,
});

export interface UserAdapterState {
  currentUser: AlandaUser;
}

export function getCurrentUser(s: UserAdapterState): AlandaUser {
  return s.currentUser;
}

@Injectable({
  providedIn: 'root',
})
export class UserAdapter {
  public currentUser$ = this.appState.select(map(getCurrentUser));

  loadUser$ = this.actionService.actions$.pipe(
    filter((val) => val.name === loadUser),
    exhaustMap(() => this.userApiService.getCurrentUser()),
  );

  runAsUser$ = this.actionService.actions$.pipe(
    filter((val) => val.name === runAsUser),
    switchMap((a) => this.userApiService.runAsUser((a.payload as any).name)),
  );

  releaseRunAsUser$ = this.actionService.actions$.pipe(
    filter((val) => val.name === releaseRunAsUser),
    switchMap((a) => this.userApiService.releaseRunAs()),
  );

  constructor(
    private userApiService: AlandaUserApiService,
    private appState: AppStateService,
    private actionService: ActionService,
  ) {
    this.appState.connect('currentUser', this.loadUser$);
    this.appState.connect('currentUser', this.runAsUser$);
    this.appState.connect('currentUser', this.releaseRunAsUser$);

    this.actionService.dispatch(LOAD_USER());
  }

  connectRunAs(trigger: Observable<string>) {
    this.actionService.connect(trigger.pipe(map((name) => RUNAS_USER(name))));
  }

  connectReleaseRunAs(trigger: Observable<void>) {
    this.actionService.connect(trigger.pipe(mapTo(RELEASE_RUNAS_USER())));
  }
}
