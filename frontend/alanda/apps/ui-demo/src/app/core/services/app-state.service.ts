import { Injectable } from '@angular/core';
import { RxState } from '@rx-angular/state';
import { AlandaUser } from '@alanda/common';

// export interface AlandaState{
//   currentUser: AlandaUser
// }

@Injectable({
  providedIn: "root"
})
export class AppStateService extends RxState<{[prop: string]: any}>{
  state$ = this.select();

  constructor() {
    super();
  }

}
