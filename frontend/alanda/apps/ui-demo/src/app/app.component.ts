import { Component, OnInit } from '@angular/core';
import { AlandaMenuItem, AlandaTitleService } from '@alanda/common';
import { Subject } from 'rxjs';
import { UserStoreImpl } from './store/user';
import { RxState } from '@rx-angular/state';
import { tap } from 'rxjs/operators';
import { PrimeNGConfig } from 'primeng/api';

@Component({
  selector: 'alanda-app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent extends RxState<any> implements OnInit {
  user$ = this.userStore.currentUser$;
  releaseRunAsClick$ = new Subject<void>();

  items: AlandaMenuItem[] = [
    {
      label: 'Home',
      routerLink: ['/'],
      icon: 'pi pi-home',
      routerLinkActiveOptions: { exact: true },
    },
    {
      label: 'Tasks',
      routerLink: ['/tasks/list'],
      icon: 'pi pi-check-circle',
    },
    {
      label: 'Create',
      icon: 'pi pi-plus',
      items: [
        {
          label: 'Project',
          routerLink: ['/create/project'],
          icon: 'pi pi-bars',
        },
      ],
    },
    {
      label: 'Monitor',
      icon: 'pi pi-eye',
      items: [
        {
          label: 'Projects',
          routerLink: ['/monitor/projects'],
          icon: 'pi pi-bars',
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
          icon: 'pi pi-user',
        },
        {
          label: 'Groups',
          routerLink: ['/admin/groups'],
          icon: 'pi pi-users',
        },
        {
          label: 'Roles',
          routerLink: ['/admin/roles'],
          icon: 'pi pi-tags',
        },
        {
          label: 'Permissions',
          routerLink: ['/admin/permissions'],
          icon: 'pi pi-key',
        },
      ],
    },
  ];

  logoPath = '/assets/default-logo.png';

  constructor(
    private userStore: UserStoreImpl,
    private titleService: AlandaTitleService,
    private primengConfig: PrimeNGConfig,
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

  ngOnInit() {
    this.primengConfig.ripple = true;
  }
}
