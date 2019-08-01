import { Component, Input, OnInit } from "@angular/core";

@Component({
  templateUrl: './project.properties.vacation.component.html'
})
export class ProjectPropertiesVacationComponent implements OnInit {

  @Input() project: any;

  constructor() {}

  ngOnInit() {}

}