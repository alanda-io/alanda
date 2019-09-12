
import { Component } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { SelectItem, MessageService } from "primeng/api";
import { BaseFormComponent } from "../baseForm.component";
import { TaskServiceNg } from "../../../../services/rest/task.service";
import { Router } from "@angular/router";
import { FormsRegisterService } from "../../../../services/forms-register.service";


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