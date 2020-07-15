import { Component, OnInit, HostListener, Input, Output } from '@angular/core';
import {
  state,
  style,
  transition,
  animate,
  trigger,
} from '@angular/animations';
import { MenuItem } from 'primeng/api/menuitem';
import { RxState } from '@rx-angular/state';
import { Observable, isObservable, Subject } from 'rxjs';
import { AlandaUser } from '@alanda/common';

@Component({
  selector: 'alanda-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
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
export class HeaderComponent extends RxState<{ user: AlandaUser }> implements OnInit {
  items: MenuItem[];
  value: Date;
  title = 'alanda-ui-demo';
  private scrollPos = 0;
  autoHide = true;

  user$ = this.select('user');

  @Input()
  set user(user: Observable<AlandaUser>) {
    isObservable(user) ? this.connect('user', user) : this.set({ user });
  }

  @Output()
  releaseRunAsClick = new Subject<void>();

  constructor() {
    super();
  }

  ngOnInit(): void {
    // this.userService.getCurrentUser().subscribe();
    this.items = [
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
}
