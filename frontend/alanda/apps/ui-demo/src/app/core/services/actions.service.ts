import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs';
import { RxState } from '@rx-angular/state';

export interface Action {
  name: string;
  payload?: object;
}

@Injectable({
  providedIn: 'root',
})
export class ActionService extends RxState<any> {
  private readonly actionsSubject = new Subject<Action>();

  actions$ = this.actionsSubject.asObservable();

  constructor() {
    super();
  }

  dispatch(action: Action): void {
    this.actionsSubject.next(action);
  }

  connect(action$: Observable<Action>): void {
    this.hold(action$, (a) => this.dispatch(a));
  }
}
