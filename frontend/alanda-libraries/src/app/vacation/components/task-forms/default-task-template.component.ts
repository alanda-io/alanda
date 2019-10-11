
import { Component } from "@angular/core";
import { FormsRegisterService, AlandaTaskTemplateComponent } from "projects/alanda-common/src/public_api";

@Component({
    selector: 'default-task',
    templateUrl: './default-task-template.component.html',
    styleUrls: [],
  })
export class DefaultTaskComponent extends AlandaTaskTemplateComponent {

  constructor(formsRegisterService: FormsRegisterService){
    super(formsRegisterService);

  }

}