
import { Component } from "@angular/core";
import { SelectItem } from "primeng/api";
import { FormsRegisterService } from "projects/alanda-common/src/public_api";
import { AlandaTaskTemplate } from "projects/alanda-common/src/lib/components/task/models/alanda-task-template";

@Component({
    selector: 'check-vacation-request',
    templateUrl: './check-vacation-request.component.html',
    styleUrls: [],
  })
export class CheckVacationRequestComponent extends AlandaTaskTemplate {

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