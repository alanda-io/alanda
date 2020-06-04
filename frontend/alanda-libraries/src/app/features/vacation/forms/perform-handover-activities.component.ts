import { Component, AfterViewInit } from '@angular/core';
import { SelectItem } from 'primeng/api';
import { BaseFormComponent } from 'projects/alanda-common/src/lib/form/base-form.component.interface';
import { AlandaTaskFormService } from 'projects/alanda-common/src/lib/form/alanda-task-form.service';

@Component({
  selector: 'perform-handover-activities',
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
    // this.formManagerService.addValidators();
  }
}
