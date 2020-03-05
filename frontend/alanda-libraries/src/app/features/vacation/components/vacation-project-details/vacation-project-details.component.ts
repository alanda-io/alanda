
import { Component } from '@angular/core';
import { AlandaProject } from 'projects/alanda-common/src/lib/api/models/alandaProject';


@Component({
    selector: 'vacation-project-details-component',
    templateUrl: './vacation-project-details.component.html',
    styleUrls: [],
  })
  export class VacationProjectDetailsComponent {

    project: AlandaProject;
    pid: string;

  }
