import { Component, AfterViewInit } from '@angular/core';
import { AlandaTaskFormService, BaseFormComponent } from '@alanda/common';
import { SelectItem } from 'primeng/api';

@Component({
  selector: 'alanda-modify-vacation-request',
  templateUrl: './modify-vacation-request.component.html',
})
export class ModifyVacationRequestComponent
  implements BaseFormComponent, AfterViewInit {
  state$ = this.taskFormService.state$;
  rootForm = this.taskFormService.rootForm;
  items: SelectItem[];

  constructor(private readonly taskFormService: AlandaTaskFormService) {
    this.items = [
      { label: 'Yes', value: true },
      { label: 'No', value: false },
    ];
  }

  submit(): void {
    this.taskFormService.submit().subscribe();
  }

  ngAfterViewInit(): void {
    // this.formManagerService.addValidators();
  }
}
