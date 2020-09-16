import { Component, HostListener, Inject, Input, Output } from '@angular/core';
import {
  state,
  style,
  transition,
  animate,
  trigger,
} from '@angular/animations';
import { RxState } from '@rx-angular/state';
import { Observable, isObservable, Subject, combineLatest } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { AlandaMenuItem } from '../../api/models/menuItem';
import { AlandaUser } from '../../api/models/user';
import { Authorizations } from '../../permissions/utils/permission-checks';
import { APP_CONFIG, AppSettings } from '../../models/appSettings';

interface AlandaHeaderState {
  user: AlandaUser;
  items: AlandaMenuItem[];
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
  set user(user: Observable<AlandaUser> | AlandaUser) {
    if (isObservable(user)) {
      this.rxState.connect('user', user);
    } else {
      this.rxState.set({ user });
    }
  }
  @Input()
  set items(items: Observable<AlandaMenuItem[]> | AlandaMenuItem[]) {
    if (isObservable(items)) {
      this.rxState.connect('items', items);
    } else {
      this.rxState.set({ items });
    }
  }
  @Input() logoPath?: string;

  @Output()
  releaseRunAsClick = new Subject<void>();

  value: Date;
  autoHide = true;
  private scrollPos = 0;

  avatarBasePath: string;
  avatarExtension: string;
  defaultAvatarPath = 'assets/default-avatar.png';
  avatarPath = this.defaultAvatarPath;

  user$ = this.rxState.select('user');

  items$ = combineLatest([this.user$, this.rxState.select('items')]).pipe(
    map(([user, items]) =>
      items.filter((item) =>
        Authorizations.hasPermissionForMenuItem(item, user),
      ),
    ),
  );

  avatar$ = this.user$.pipe(
    map(
      (user) => `${this.avatarBasePath}/${user.guid}.${this.avatarExtension}`,
    ),
    startWith(this.defaultAvatarPath),
  );

  constructor(
    public rxState: RxState<AlandaHeaderState>,
    @Inject(APP_CONFIG) config: AppSettings,
  ) {
    this.avatarBasePath = config.AVATAR_BASE_PATH;
    this.avatarExtension = config.AVATAR_EXT;
  }

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

  fallbackImage(event): void {
    if (event.target.src !== this.defaultAvatarPath) {
      event.target.src = this.defaultAvatarPath;
    }
  }
}
