
import { Component } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { Router } from "@angular/router";
import { MessageService } from "primeng/api";
import { BaseFormComponent, TaskServiceNg } from "projects/alanda-common/src/public_api";


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