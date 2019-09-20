
import { Component } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { SelectItem, MessageService } from "primeng/api";
import { Router } from "@angular/router";
import { FormsRegisterService } from "../../core/services/forms-register.service";
import { BaseFormComponent } from "../../core/tasks/base-form/base-form.component";
import { TaskServiceNg } from "../../core/api/task.service";


@Component({
    selector: 'prepare-vacation-request',
    templateUrl: './prepare-vacation-request.component.html',
    styleUrls: [],
    providers: [FormsRegisterService]
  })
  export class PrepareVacationRequestComponent extends BaseFormComponent {

    items: SelectItem[];

    constructor(fb: FormBuilder, taskService: TaskServiceNg, messageService: MessageService, router: Router, formsRegisterService: FormsRegisterService){
      super(fb, taskService, messageService, router, formsRegisterService);
      this.items = [
        {label: 'Yes', value: true},
        {label: 'No', value: false}
      ];
    }
  }