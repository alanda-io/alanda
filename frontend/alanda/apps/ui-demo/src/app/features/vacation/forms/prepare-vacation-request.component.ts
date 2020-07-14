import { Component, AfterViewInit } from '@angular/core';
import { SelectItem } from 'primeng/api';
import { AlandaTaskFormService, BaseFormComponent } from '@alanda/common';
import { Validators, AbstractControl } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'alanda-prepare-vacation-request',
  templateUrl: './prepare-vacation-request.component.html',
  styleUrls: ['./prepare-vacation-request.component.scss'],
})
export class PrepareVacationRequestComponent
  implements BaseFormComponent, AfterViewInit {
  state$ = this.taskFormService.state$;
  rootForm = this.taskFormService.rootForm;
  items: SelectItem[];

  constructor(
    private readonly taskFormService: AlandaTaskFormService,
    private readonly router: Router,
  ) {
    this.items = [
      { label: 'Yes', value: true },
      { label: 'No', value: false },
    ];
  }

  submit(): void {
    this.taskFormService.submit().subscribe();
  }

  ngAfterViewInit(): void {
    this.roleSelector.setValidators([Validators.required]);
    this.roleSelector.updateValueAndValidity();
    // this.formManagerService.addValidators();
  }

  get roleSelector(): AbstractControl {
    return this.rootForm.get('alanda-role-select-vacation-approver.selected');
  }
}
