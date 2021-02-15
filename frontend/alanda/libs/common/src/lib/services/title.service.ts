import { Inject, Injectable } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { AlandaProject } from '../api/models/project';
import { AlandaTask } from '../api/models/task';
import { filter, map } from 'rxjs/operators';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { APP_CONFIG, AppSettings } from '../models/appSettings';

@Injectable({
  providedIn: 'root',
})
export class AlandaTitleService {
  titlePrefix: string;

  constructor(
    private titleService: Title,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    @Inject(APP_CONFIG) config: AppSettings,
  ) {
    this.titlePrefix = config.APP_NAME || this.getTitle() || 'Alanda';
  }

  getTitle(): string {
    return this.titleService.getTitle();
  }

  setTitle(newTitle?: string) {
    newTitle = newTitle
      ? `${this.titlePrefix} - ${newTitle}`
      : this.titlePrefix;
    this.titleService.setTitle(newTitle);
  }

  setProjectTitle(project: AlandaProject) {
    this.setTitle(`${project.projectId}:${project.projectType}`);
  }

  setCustomTitle(newTitle?: string) {
    this.titleService.setTitle(newTitle);
  }

  setTaskTitle(task: AlandaTask) {
    this.setTitle(`${task.object_name}:${task.task_name}`);
  }

  setRouterTitle() {
    this.router.events
      .pipe(
        filter((event) => event instanceof NavigationEnd),
        map(() => {
          let child = this.activatedRoute.firstChild;
          while (child.firstChild) {
            child = child.firstChild;
          }
          if (child.snapshot.data['title']) {
            return child.snapshot.data['title'];
          }

          return '';
        }),
      )
      .subscribe((routerTitle: string) => {
        if (routerTitle) {
          this.setTitle(routerTitle);
        }
      });
  }
}
