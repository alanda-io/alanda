
import { Component } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { MessageService } from "primeng/api";
import { Router } from "@angular/router";
import { BaseFormComponent, TaskServiceNg } from "projects/alanda-common/src/public_api";


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