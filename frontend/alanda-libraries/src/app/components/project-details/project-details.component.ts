
import { Component } from '@angular/core';
import { AlandaProject } from 'projects/alanda-common/src/lib/api/models/project';


@Component({
    selector: 'project-details-component',
    templateUrl: './project-details.component.html',
    styleUrls: [],
  })
  export class ProjectDetailsComponent {

    project: AlandaProject;
    pid: string;

  }
