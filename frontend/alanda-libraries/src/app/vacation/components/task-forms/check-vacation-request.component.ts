
import { Component } from "@angular/core";
import { SelectItem, MessageService } from "primeng/api";
import { FormBuilder } from "@angular/forms";
import { Router } from "@angular/router";
import { BaseFormComponent, TaskServiceNg } from "projects/alanda-common/src/public_api";

@Component({
    selector: 'check-vacation-request',
    templateUrl: './check-vacation-request.component.html',
    styleUrls: [],
  })
export class CheckVacationRequestComponent extends BaseFormComponent {

  items: SelectItem[];

  constructor(fb: FormBuilder, taskService: TaskServiceNg, messageService: MessageService, router: Router){
    super(fb, taskService, messageService, router);
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