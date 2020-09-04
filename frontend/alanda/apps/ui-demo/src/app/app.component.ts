import { Component } from '@angular/core';
import { AlandaMenuItem, AlandaTitleService } from '@alanda/common';
import { Subject } from 'rxjs';
import { UserStoreImpl } from './store/user';
import { RxState } from '@rx-angular/state';
import { tap } from 'rxjs/operators';

@Component({
  selector: 'alanda-app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent extends RxState<any> {
  user$ = this.userStore.currentUser$;
  releaseRunAsClick$ = new Subject<void>();

  items: AlandaMenuItem[] = [
    {
      label: 'Home',
      routerLink: ['/'],
      icon: 'fa fa-home',
      routerLinkActiveOptions: { exact: true },
    },
    {
      label: 'Tasks',
      routerLink: ['/tasks/list'],
      icon: 'fa fa-briefcase',
    },
    {
      label: 'Create',
      icon: 'fa fa-plus',
      items: [
        {
          label: 'Project',
          routerLink: ['/create/project'],
          icon: 'fa fa-list-alt',
        },
      ],
    },
    {
      label: 'Monitor',
      icon: 'fa fa-eye',
      items: [
        {
          label: 'Projects',
          routerLink: ['/monitor/projects'],
          icon: 'fa fa-list-alt',
        },
      ],
    },
    {
      label: 'Administration',
      icon: 'pi pi-cog',
      items: [
        {
          label: 'Users',
          routerLink: ['/admin/users'],
          icon: 'fa fa-list-alt',
        },
        {
          label: 'Groups',
          routerLink: ['/admin/groups'],
          icon: 'fa fa-list-alt',
        },
        {
          label: 'Roles',
          routerLink: ['/admin/roles'],
          icon: 'fa fa-list-alt',
        },
        {
          label: 'Permissions',
          routerLink: ['/admin/permissions'],
          icon: 'fa fa-list-alt',
        },
      ],
    },
  ];

  logoPath = '/assets/default-logo.png';

  constructor(
    private userStore: UserStoreImpl,
    private titleService: AlandaTitleService,
  ) {
    super();
    this.userStore.dispatch(this.userStore.createLoadUserAction());
    this.hold(
      this.releaseRunAsClick$.pipe(
        tap(() =>
          this.userStore.dispatch(
            this.userStore.createReleaseRunAsUserAction(),
          ),
        ),
      ),
    );

    this.titleService.setRouterTitle();
  }
}
