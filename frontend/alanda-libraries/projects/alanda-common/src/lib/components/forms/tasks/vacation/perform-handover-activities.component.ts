
import { Component } from "@angular/core";
import { BaseFormComponent } from "../forms-controller/baseForm.component";
import { FormBuilder } from "@angular/forms";
import { TaskServiceNg } from "../../../../services/rest/task.service";
import { MessageService } from "primeng/api";
import { Router } from "@angular/router";


@Component({
    selector: 'perform-handover-activities',
    templateUrl: './default-task-template.html',
    styleUrls: [],
  })
export class PerformHandoverActivitiesComponent extends BaseFormComponent{
 
  constructor(fb: FormBuilder, taskService: TaskServiceNg, messageService: MessageService, router: Router){
    super(fb, taskService, messageService, router);
  }
}