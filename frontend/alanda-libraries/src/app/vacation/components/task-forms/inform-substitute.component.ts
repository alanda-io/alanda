
import { Component } from "@angular/core";
import { FormsRegisterService } from "projects/alanda-common/src/public_api";
import { AlandaTaskTemplate } from "projects/alanda-common/src/lib/components/task/models/alanda-task-template";


@Component({
    selector: 'inform-substitute',
    templateUrl: './default-task-template.html',
    styleUrls: [],
  })
export class InformSubstituteComponent extends AlandaTaskTemplate {

  constructor(formsRegisterService: FormsRegisterService) {
    super(formsRegisterService)
  }

}