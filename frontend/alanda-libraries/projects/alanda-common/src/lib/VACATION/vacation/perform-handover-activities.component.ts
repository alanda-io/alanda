
import { Component } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { MessageService } from "primeng/api";
import { Router } from "@angular/router";
import { BaseFormComponent } from "../../core/tasks/base-form/base-form.component";
import { TaskServiceNg } from "../../core/api/task.service";
import { FormsRegisterService } from "../../core/services/forms-register.service";


@Component({
    selector: 'perform-handover-activities',
    templateUrl: './default-task-template.html',
    styleUrls: [],
  })
export class PerformHandoverActivitiesComponent extends BaseFormComponent{
 
  constructor(fb: FormBuilder, taskService: TaskServiceNg, messageService: MessageService, router: Router,
              formsRegisterService: FormsRegisterService){
    super(fb, taskService, messageService, router, formsRegisterService);
  }
}