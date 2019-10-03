
import { Component } from "@angular/core";
import { SelectItem } from "primeng/api";
import { FormsRegisterService, AlandaTaskTemplateComponent } from "projects/alanda-common/src/public_api";


@Component({
    selector: 'prepare-vacation-request',
    templateUrl: './prepare-vacation-request.component.html',
    styleUrls: [],
  })
  export class PrepareVacationRequestComponent extends AlandaTaskTemplateComponent {

    items: SelectItem[];

    constructor(formsRegisterService: FormsRegisterService){
      super(formsRegisterService);
      this.items = [
        {label: 'Yes', value: true},
        {label: 'No', value: false}
      ];
    }
  }