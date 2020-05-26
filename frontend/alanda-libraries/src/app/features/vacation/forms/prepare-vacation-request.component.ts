import { Component, AfterViewInit } from '@angular/core';
import { SelectItem } from 'primeng/api';
import {
  AlandaTaskFormService,
  BaseFormComponent,
} from 'projects/alanda-common/src/public-api';
import { Validators, AbstractControl } from '@angular/forms';

@Component({
  selector: 'prepare-vacation-request',
  templateUrl: './prepare-vacation-request.component.html',
  styleUrls: [],
})
export class PrepareVacationRequestComponent
implements BaseFormComponent, AfterViewInit {
  state$ = this.taskFormService.state$;
  rootForm = this.taskFormService.rootForm;
  items: SelectItem[];

  constructor (private readonly taskFormService: AlandaTaskFormService) {
    this.items = [
      { label: 'Yes', value: true },
      { label: 'No', value: false },
    ];
  }

  submit (): void {
    this.taskFormService.submit().subscribe();
  }

  ngAfterViewInit (): void {
    this.roleSelector.setValidators([Validators.required]);
    this.roleSelector.updateValueAndValidity();
    console.log('rootForm', this.rootForm);
    // this.formManagerService.addValidators();
  }

  get roleSelector (): AbstractControl {
    return this.rootForm.get('alanda-role-select-vacation-approver.selected');
  }
}
