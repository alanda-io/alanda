import { Store } from '../store.interface';
import { Action } from '../action';
import { AlandaUser } from '@alanda/common';

export interface UserStore extends Store<AlandaUser> {
  createLoadUserAction(): Action<AlandaUser>;
  createReleaseRunAsUserAction(): Action<AlandaUser>;
  createRunAsUserAction(name: string): Action<AlandaUser>;
}

export enum ActionType {
  LOAD_USER = 'LOAD_USER',
  RUN_AS_USER = 'RUN_AS_USER',
  RELEASE_RUN_AS_USER = 'RELEASE_RUN_AS_USER',
  USER_LOADED = 'USER_LOADED',
}
