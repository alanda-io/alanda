
import { Component } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { TaskServiceNg } from "../../../../services/rest/task.service";
import { Router } from "@angular/router";
import { MessageService } from "primeng/api";
import { BaseFormComponent } from "../baseForm.component";
import { FormsRegisterService } from "../../../../services/forms-register.service";


@Component({
    selector: 'inform-substitute',
    templateUrl: './default-task-template.html',
    styleUrls: [],
  })
export class InformSubstituteComponent extends BaseFormComponent {

    constructor(fb: FormBuilder, taskService: TaskServiceNg, messageService: MessageService, router: Router, formsRegisterService: FormsRegisterService){
      super(fb, taskService, messageService, router, formsRegisterService);
    }
  }