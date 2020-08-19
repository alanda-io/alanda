import { Injectable } from '@angular/core';
import { AlandaUser } from '../../../api/models/user';
import { AlandaUserApiService } from '../../../api/userApi.service';
import { RxState } from '@rx-angular/state';
import { Observable, Subject } from 'rxjs';
import { filter, exhaustMap, switchMap, map } from 'rxjs/operators';
import { isNil } from 'lodash';
import { UserStore, ActionType } from './user.store.types';
import { Action } from '../action';

interface UserStoreState {
  currentUser: AlandaUser;
}
const initState = {};

@Injectable({
  providedIn: 'root',
})
export class UserStoreImpl implements UserStore {
  private readonly userActions = new Subject<Action<AlandaUser>>();
  private readonly actions$ = this.userActions.asObservable();
  public readonly currentUser$ = this.state.select('currentUser');

  /** Resolvers (Action handlers) */
  loadUser$ = this.actions$.pipe(
    filter((action) => action.type === ActionType.LOAD_USER),
    exhaustMap(() => this.userApiService.getCurrentUser()),
  );
  runAsUser$ = this.actions$.pipe(
    filter((action) => action.type === ActionType.RUN_AS_USER),
    switchMap((action) =>
      this.userApiService.runAsUser((action.payload as any).name),
    ),
  );
  releaseRunAsUser$ = this.actions$.pipe(
    filter((action) => action.type === ActionType.RELEASE_RUN_AS_USER),
    switchMap(() => this.userApiService.releaseRunAs()),
  );

  /** Local dispatchers */
  dispatchUser$ = this.state.select('currentUser').pipe(
    filter((user) => !isNil(user)),
    map((user) => this.dispatch(new Action(ActionType.USER_LOADED, user))),
  );

  constructor(
    private state: RxState<UserStoreState>,
    private userApiService: AlandaUserApiService,
  ) {
    this.state.hold(this.userActions);
    this.state.connect('currentUser', this.loadUser$);
    this.state.connect('currentUser', this.runAsUser$);
    this.state.connect('currentUser', this.releaseRunAsUser$);
    this.state.hold(this.dispatchUser$);
  }

  /** Actions Factory */
  createRunAsUserAction(name: string): Action<AlandaUser> {
    return new Action(ActionType.RUN_AS_USER, { name } as AlandaUser);
  }
  createLoadUserAction(): Action<AlandaUser> {
    return new Action(ActionType.LOAD_USER);
  }
  createReleaseRunAsUserAction(): Action<AlandaUser> {
    return new Action(ActionType.RELEASE_RUN_AS_USER);
  }

  /** Store main methods */
  dispatch(action: Action<AlandaUser>): void {
    this.userActions.next(action);
  }
  actions(): Observable<Action<AlandaUser>> {
    return this.actions$;
  }
}
