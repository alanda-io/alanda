
import { Component } from "@angular/core";
import { SelectItem, MessageService } from "primeng/api";
import { BaseFormComponent } from "../baseForm.component";
import { FormBuilder } from "@angular/forms";
import { TaskServiceNg } from "../../../../services/rest/task.service";
import { Router } from "@angular/router";
import { FormsServiceNg } from "../../../../services/forms.service";
import { FormsRegisterService } from "../../../../services/forms-register.service";

@Component({
    selector: 'check-vacation-request',
    templateUrl: './check-vacation-request.component.html',
    styleUrls: [],
  })
export class CheckVacationRequestComponent extends BaseFormComponent {

  items: SelectItem[];

  constructor(fb: FormBuilder, taskService: TaskServiceNg, messageService: MessageService, router: Router, formsRegisterService: FormsRegisterService){
    super(fb, taskService, messageService, router, formsRegisterService);
    this.items = [
      {label: 'Yes', value: true},
      {label: 'No', value: false}
    ];
  }

  // overload only if you want to set new task specific validators
  // for example:
  /*
  submitTask() {
    let projectHeaderForm = this.formGroup.get('projectHeaderForm') as FormGroup;
    projectHeaderForm.get('projectDetails').setValidators([Validators.minLength(5), Validators.required]);
    Object.keys(projectHeaderForm.controls).forEach(key => {
      projecteaderForm.get(key).updateValueAndValidity();
    });
  } */
    
  }