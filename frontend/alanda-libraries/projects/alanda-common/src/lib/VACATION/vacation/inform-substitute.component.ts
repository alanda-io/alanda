
import { Component } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { Router } from "@angular/router";
import { MessageService } from "primeng/api";
import { BaseFormComponent } from "../../core/tasks/base-form/base-form.component";
import { TaskServiceNg } from "../../core/api/task.service";
import { FormsRegisterService } from "../../core/services/forms-register.service";


@Component({
    selector: 'inform-substitute',
    templateUrl: './default-task-template.html',
    styleUrls: [],
  })
export class InformSubstituteComponent extends BaseFormComponent {

    constructor(fb: FormBuilder, taskService: TaskServiceNg, messageService: MessageService, router: Router,
                formsRegisterService: FormsRegisterService){
      super(fb, taskService, messageService, router, formsRegisterService);
    }
  }