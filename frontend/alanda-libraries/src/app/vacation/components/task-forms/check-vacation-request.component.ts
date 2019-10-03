
import { Component } from "@angular/core";
import { SelectItem } from "primeng/api";
import { FormsRegisterService, AlandaTaskTemplateComponent } from "projects/alanda-common/src/public_api";

@Component({
    selector: 'check-vacation-request',
    templateUrl: './check-vacation-request.component.html',
    styleUrls: [],
  })
export class CheckVacationRequestComponent extends AlandaTaskTemplateComponent {

  items: SelectItem[];

  constructor(formsRegisterService: FormsRegisterService){
    super(formsRegisterService);
    this.items = [
      {label: 'Yes', value: true},
      {label: 'No', value: false}
    ];
  }

  /**
   * override default validation
   */

  /* 
  submitTask() {
    this.formsRegisterService.$formGroup.get('projectHeaderForm').clearValidators();
    this.formsRegisterService.submit(this.task);
  } 
  */
}