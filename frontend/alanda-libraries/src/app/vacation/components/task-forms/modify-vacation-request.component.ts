
import { Component } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { Router } from "@angular/router";
import { MessageService, SelectItem } from "primeng/api";
import { BaseFormComponent, TaskServiceNg } from "projects/alanda-common/src/public_api";


@Component({
    selector: 'modify-vacation-request',
    templateUrl: './modify-vacation-request.component.html',
    styleUrls: [],
  })
export class ModifyVacationRequestComponent extends BaseFormComponent {

    items: SelectItem[];

    constructor(fb: FormBuilder, taskService: TaskServiceNg, messageService: MessageService, router: Router){
      super(fb, taskService, messageService, router);
        this.items = [
          {label: 'Yes', value: true},
          {label: 'No', value: false}
        ];
      }

  }