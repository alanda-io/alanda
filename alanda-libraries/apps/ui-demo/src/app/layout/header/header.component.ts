import { Component, OnInit, HostListener } from '@angular/core';
import { state, style, transition, animate, trigger } from '@angular/animations';
import { AlandaUserApiService } from '@alanda/common';
import { MenuItem } from 'primeng/api/menuitem';

@Component({
  selector: 'alanda-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
  animations: [
    trigger('autoHide', [
      state('open', style({
        top: '0'
      })),
      state('closed', style({
        top: '-50px'
      })),
      transition('open <=> closed', [
        animate('0.3s')
      ])
    ])
  ]
})
export class HeaderComponent implements OnInit {
  items: MenuItem[];
  value: Date;
  title = 'alanda-ui-demo';
  private scrollPos = 0;
  autoHide = true;

  constructor(public userService: AlandaUserApiService) {}

  ngOnInit(): void {
    this.userService.getCurrentUser().subscribe();
    this.items = [
      { label: 'Home', routerLink: ['/'], icon: 'fa fa-home', routerLinkActiveOptions: { exact: true } },
      { label: 'Tasks', routerLink: ['/tasks/list'], icon: 'fa fa-briefcase' },
      {
        label: 'Create',
        icon: 'fa fa-plus',
        items: [
          { label: 'Project', routerLink: ['/create/project'], icon: 'fa fa-list-alt' }
        ]
      },
      {
        label: 'Monitor',
        icon: 'fa fa-eye',
        items: [
          { label: 'Projects', routerLink: ['/monitor/projects'], icon: 'fa fa-list-alt' }
        ]
      },
      {
        label: 'Administration',
        icon: 'pi pi-cog',
        items: [
          { label: 'Users', routerLink: ['/admin/users'], icon: 'fa fa-list-alt' },
          { label: 'Groups', routerLink: ['/admin/groups'], icon: 'fa fa-list-alt' },
          { label: 'Roles', routerLink: ['/admin/roles'], icon: 'fa fa-list-alt' },
          { label: 'Permissions', routerLink: ['/admin/permissions'], icon: 'fa fa-list-alt' }
        ]
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

  releaseRunAs(): void {
    this.userService.releaseRunAs().subscribe();
  }
}
