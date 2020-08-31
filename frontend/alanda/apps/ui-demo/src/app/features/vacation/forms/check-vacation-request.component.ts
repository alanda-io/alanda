import { Component, AfterViewInit } from '@angular/core';
import { SelectItem } from 'primeng/api';
import { BaseFormComponent } from '@alanda/common';
import { AlandaTaskFormService } from '@alanda/common';
import { UserEnrichedTaskFormService } from '../../../services/userEnrichedTaskForm.service';

@Component({
  selector: 'alanda-check-vacation-request',
  templateUrl: './check-vacation-request.component.html',
  styleUrls: [],
  providers: [UserEnrichedTaskFormService],
})
export class CheckVacationRequestComponent
  implements BaseFormComponent, AfterViewInit {
  state$ = this.taskFormService.state$;
  rootForm = this.taskFormService.rootForm;
  items: SelectItem[];

  constructor(private readonly taskFormService: UserEnrichedTaskFormService) {
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
