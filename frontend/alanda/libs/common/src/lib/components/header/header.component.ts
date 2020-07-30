import { Component, HostListener, Input, Output } from '@angular/core';
import {
  state,
  style,
  transition,
  animate,
  trigger,
} from '@angular/animations';
import { RxState } from '@rx-angular/state';
import { Observable, isObservable, Subject, combineLatest } from 'rxjs';
import { map } from 'rxjs/operators';
import { AlandaMenuItem } from '../../api/models/menuItem';
import { AlandaUser } from '../../api/models/user';
import { Authorizations } from '../../permissions/utils/permission-checks'

interface AlandaHeaderState {
  user: AlandaUser,
  items: AlandaMenuItem[],
}

@Component({
  selector: 'alanda-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
  providers: [RxState],
  animations: [
    trigger('autoHide', [
      state(
        'open',
        style({
          top: '0',
        }),
      ),
      state(
        'closed',
        style({
          top: '-50px',
        }),
      ),
      transition('open <=> closed', [animate('0.3s')]),
    ]),
  ],
})
export class AlandaHeaderComponent {
  @Input()
  set user(user: Observable<AlandaUser>) {
    if (isObservable(user)) {
      this.rxState.connect('user', user);
    } else {
      this.rxState.set({ user });
    }
  }
  @Input()
  set items(items: AlandaMenuItem[]) {
    this.rxState.set({ items });
  }
  @Input() title = 'Alanda';

  @Output()
  releaseRunAsClick = new Subject<void>();

  value: Date;
  autoHide = true;
  private scrollPos = 0;

  user$ = this.rxState.select('user');

  items$ = combineLatest([
    this.user$,
    this.rxState.select('items'),
  ]).pipe(
    map(([user, items]) =>
      items.filter(item => Authorizations.filterMenuItems(item, user))
    )
  );

  constructor(
    public rxState: RxState<AlandaHeaderState>
  ) {}

  @HostListener('window:scroll', [])
  onWindowScroll() {
    if (window.pageYOffset > this.scrollPos) {
      this.scrollPos = window.pageYOffset;
      this.autoHide = false;
    } else {
      this.scrollPos = window.pageYOffset;
      this.autoHide = true;
    }
  }
}
