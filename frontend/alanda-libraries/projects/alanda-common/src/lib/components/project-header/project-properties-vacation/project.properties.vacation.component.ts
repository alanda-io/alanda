import { Component, Input } from "@angular/core";
import { Project } from "../../../models/project.model";

@Component({
  templateUrl: './project.properties.vacation.component.html'
})
export class ProjectPropertiesVacationComponent {

  @Input() project: Project;

  constructor() {}

}