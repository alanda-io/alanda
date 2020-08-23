import { Component, AfterViewInit } from '@angular/core';
import { SelectItem } from 'primeng/api';
import { AlandaTaskFormService, BaseFormComponent } from '@alanda/common';
import { AbstractControl } from '@angular/forms';
import { Router } from '@angular/router';
import { commentRequiredValidator } from '@alanda/common';
import { UserEnrichedTaskFormService } from '../../../services/userEnrichedTaskForm.service';

@Component({
  selector: 'alanda-prepare-vacation-request',
  templateUrl: './prepare-vacation-request.component.html',
  styleUrls: ['./prepare-vacation-request.component.scss'],
  providers: [UserEnrichedTaskFormService],
})
export class PrepareVacationRequestComponent
  implements BaseFormComponent, AfterViewInit {
  state$ = this.taskFormService.state$;
  rootForm = this.taskFormService.rootForm;
  items: SelectItem[];

  constructor(
    private readonly taskFormService: UserEnrichedTaskFormService,
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
    // this.roleSelector.setValidators([Validators.required]);
    // this.roleSelector.updateValueAndValidity();
    this.rootForm.setValidators([
      commentRequiredValidator(this.comments, this.handover, [false]),
    ]);
    // this.rootForm.setValidators([
    //   commentRequiredValidator(this.rootForm, [false]),
    // ]);
    this.rootForm.updateValueAndValidity();
    // this.formManagerService.addValidators();
  }

  get roleSelector(): AbstractControl {
    return this.rootForm.get('alanda-role-select-vacation-approver.selected');
  }

  get comments(): AbstractControl {
    return this.rootForm.get('alanda-task-has-comment');
  }

  get handover(): AbstractControl {
    return this.rootForm.get('alanda-var-select-handoverRequired.selected');
  }
}
