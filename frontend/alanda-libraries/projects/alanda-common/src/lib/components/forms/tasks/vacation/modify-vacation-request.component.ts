
import { Component } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { TaskServiceNg } from "../../../../services/rest/task.service";
import { Router } from "@angular/router";
import { MessageService, SelectItem } from "primeng/api";
import { BaseFormComponent } from "../baseForm.component";
import { FormsServiceNg } from "../../../../services/forms.service";
import { FormsRegisterService } from "../../../../services/forms-register.service";


@Component({
    selector: 'modify-vacation-request',
    templateUrl: './modify-vacation-request.component.html',
    styleUrls: [],
  })
export class ModifyVacationRequestComponent extends BaseFormComponent {

    items: SelectItem[];

    constructor(fb: FormBuilder, taskService: TaskServiceNg, messageService: MessageService, router: Router, formsRegisterService: FormsRegisterService){
      super(fb, taskService, messageService, router, formsRegisterService);
        this.items = [
          {label: 'Yes', value: true},
          {label: 'No', value: false}
        ];
      }

  }