
import { Component } from "@angular/core";
import { FormsRegisterService, AlandaTaskTemplateComponent } from "projects/alanda-common/src/public_api";

@Component({
    selector: 'perform-handover-activities',
    templateUrl: './default-task-template.html',
    styleUrls: [],
  })
export class PerformHandoverActivitiesComponent extends AlandaTaskTemplateComponent {

  constructor(formsRegisterService: FormsRegisterService){
    super(formsRegisterService);
  }

}