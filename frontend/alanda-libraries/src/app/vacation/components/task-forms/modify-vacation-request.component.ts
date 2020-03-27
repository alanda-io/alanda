
import { Component } from "@angular/core";
import { SelectItem } from "primeng/api";
import { AlandaTaskTemplateComponent, FormsRegisterService } from 'projects/alanda-common/src/public-api';


@Component({
    selector: 'modify-vacation-request',
    templateUrl: './modify-vacation-request.component.html',
    styleUrls: [],
  })
export class ModifyVacationRequestComponent extends AlandaTaskTemplateComponent {

  items: SelectItem[];

  constructor(formsRegisterService: FormsRegisterService){
    super(formsRegisterService);
    this.items = [
      {label: 'Yes', value: true},
      {label: 'No', value: false}
    ];
  }
} 