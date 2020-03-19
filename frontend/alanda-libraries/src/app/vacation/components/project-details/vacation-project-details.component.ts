
import { Component } from "@angular/core";
import { Project } from 'projects/alanda-common/src/lib/models/project';


@Component({
    selector: 'vacation-project-details-component',
    templateUrl: './vacation-project-details.component.html',
    styleUrls: [],
  })
  export class VacationProjectDetailsComponent {

    project: Project;
    pid: string;

  }
