import { Component } from '@angular/core';
import { AppStateService } from './core/services/app-state.service';
import { AlandaUserApiService } from '@alanda/common';
import { UserAdapter } from './core/services/user.adapter';
import { Subject } from 'rxjs';
import { tap } from 'rxjs/operators';

@Component({
  selector: 'alanda-app-root',
  templateUrl: './app.component.html',
})
export class AppComponent {
  user$ = this.userAdapter.currentUser$;
  releaseRunAsClick$ = new Subject<void>();

  constructor(private userAdapter: UserAdapter) {
    this.userAdapter.connectReleaseRunAs(
      this.releaseRunAsClick$.asObservable(),
    );
  }
}
