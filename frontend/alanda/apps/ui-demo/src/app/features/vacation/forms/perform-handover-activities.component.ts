import { Component, AfterViewInit } from '@angular/core';
import { SelectItem } from 'primeng/api';
import { BaseFormComponent } from '@alanda/common';
import { AlandaTaskFormService } from '@alanda/common';
import { AbstractControl } from '@angular/forms';

@Component({
  selector: 'alanda-perform-handover-activities',
  templateUrl: './perform-handover-activities.component.html',
  styleUrls: [],
})
export class PerformHandoverActivitiesComponent
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
    this.approveRequest.setValidators(null);
  }

  get approveRequest(): AbstractControl {
    return this.rootForm.get('alanda-simple-select-requestApproved.selected');
  }
}
