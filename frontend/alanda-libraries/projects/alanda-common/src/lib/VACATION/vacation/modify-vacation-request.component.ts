
import { Component } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { Router } from "@angular/router";
import { MessageService, SelectItem } from "primeng/api";
import { BaseFormComponent } from "../../core/tasks/base-form/base-form.component";
import { TaskServiceNg } from "../../core/api/task.service";
import { FormsRegisterService } from "../../core/services/forms-register.service";


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