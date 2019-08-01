
import { Component } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { TaskServiceNg } from "../../../../services/rest/task.service";
import { Router } from "@angular/router";
import { MessageService } from "primeng/api";
import { BaseFormComponent } from "../forms-controller/baseForm.component";


@Component({
    selector: 'inform-substitute',
    templateUrl: './default-task-template.html',
    styleUrls: [],
  })
export class InformSubstituteComponent extends BaseFormComponent {

    constructor(fb: FormBuilder, taskService: TaskServiceNg, messageService: MessageService, router: Router){
      super(fb, taskService, messageService, router);
    }
  }