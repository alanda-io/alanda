
import { Component } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { SelectItem, MessageService } from "primeng/api";
import { Router } from "@angular/router";
import { TaskServiceNg, BaseFormComponent } from "projects/alanda-common/src/public_api";


@Component({
    selector: 'prepare-vacation-request',
    templateUrl: './prepare-vacation-request.component.html',
    styleUrls: [],
  })
  export class PrepareVacationRequestComponent extends BaseFormComponent {

    items: SelectItem[];

    constructor(fb: FormBuilder, taskService: TaskServiceNg, messageService: MessageService, router: Router){
      super(fb, taskService, messageService, router);
        this.items = [
          {label: 'Yes', value: true},
          {label: 'No', value: false}
        ];
      }
  }