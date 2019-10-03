
import { Component } from "@angular/core";
import { SelectItem } from "primeng/api";
import { AlandaTaskTemplate } from "projects/alanda-common/src/lib/components/task/models/alanda-task-template";
import { FormsRegisterService } from "projects/alanda-common/src/public_api";


@Component({
    selector: 'modify-vacation-request',
    templateUrl: './modify-vacation-request.component.html',
    styleUrls: [],
  })
export class ModifyVacationRequestComponent extends AlandaTaskTemplate {

  items: SelectItem[];

  constructor(formsRegisterService: FormsRegisterService){
    super(formsRegisterService);
    this.items = [
      {label: 'Yes', value: true},
      {label: 'No', value: false}
    ];
  }
} 