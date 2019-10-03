
import { Component } from "@angular/core";
import { FormsRegisterService, AlandaTaskTemplateComponent } from "projects/alanda-common/src/public_api";


@Component({
    selector: 'inform-substitute',
    templateUrl: './default-task-template.html',
    styleUrls: [],
  })
export class InformSubstituteComponent extends AlandaTaskTemplateComponent {

  constructor(formsRegisterService: FormsRegisterService) {
    super(formsRegisterService)
  }

}