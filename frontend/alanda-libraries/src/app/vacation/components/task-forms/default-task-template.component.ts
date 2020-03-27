
import { Component } from "@angular/core";
import { AlandaTaskTemplateComponent, FormsRegisterService } from 'projects/alanda-common/src/public-api';

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