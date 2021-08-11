import { Observable } from 'rxjs';
import { Action } from './action';

export interface Store<T> {
  dispatch(event: Action<T>): void;
  actions(): Observable<Action<T>>;
}
