
import { Component } from "@angular/core";
import { BaseFormComponent } from "../baseForm.component";
import { FormBuilder } from "@angular/forms";
import { TaskServiceNg } from "../../../../services/rest/task.service";
import { MessageService } from "primeng/api";
import { Router } from "@angular/router";
import { FormsRegisterService } from "../../../../services/forms-register.service";


@Component({
    selector: 'perform-handover-activities',
    templateUrl: './default-task-template.html',
    styleUrls: [],
  })
export class PerformHandoverActivitiesComponent extends BaseFormComponent{
 
  constructor(fb: FormBuilder, taskService: TaskServiceNg, messageService: MessageService, router: Router, formsRegisterService: FormsRegisterService){
    super(fb, taskService, messageService, router, formsRegisterService);
  }
}