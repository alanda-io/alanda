import { Component } from '@angular/core';
import { AlandaMenuItem } from '@alanda/common';
import { UserAdapter } from './core/services/user.adapter';
import { Subject } from 'rxjs';

@Component({
  selector: 'alanda-app-root',
  templateUrl: './app.component.html',
})
export class AppComponent {
  user$ = this.userAdapter.currentUser$;
  releaseRunAsClick$ = new Subject<void>();

  items: AlandaMenuItem[] = [
    {
      label: 'Home',
      routerLink: ['/'],
      icon: 'fa fa-home',
      routerLinkActiveOptions: { exact: true },
    },
    { label: 'Tasks', routerLink: ['/tasks/list'], icon: 'fa fa-briefcase' },
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

  logoPath = '/assets/default-logo.png'

  constructor(private userAdapter: UserAdapter) {
    this.userAdapter.connectReleaseRunAs(
      this.releaseRunAsClick$.asObservable(),
    );
  }
}
