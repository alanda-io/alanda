import { Component, AfterViewInit } from '@angular/core';
import { SelectItem } from 'primeng/api';
import { BaseFormComponent } from '@alanda/common';
import { AbstractControl, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { commentRequiredValidator } from '@alanda/common';
import { UserEnrichedTaskFormService } from '../../../services/userEnrichedTaskForm.service';
import { tap } from 'rxjs/operators';

@Component({
  selector: 'alanda-prepare-vacation-request',
  templateUrl: './prepare-vacation-request.component.html',
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
    if (this.snooze.value > 0) {
      this.taskFormService.snooze(this.snooze.value).subscribe();
    } else {
      this.taskFormService.submit().subscribe();
    }
  }

  ngAfterViewInit(): void {
    // console.log('form1', this.rootForm);
    // An example how we can react to changes in some form component
    // whenever the value of the dropdown "handover checks required"
    // changes, we adapt the Validators of the requestor field
    this.taskFormService.taskFormService.hold(
      this.handover.valueChanges.pipe(
        tap((value) => {
          if (value === true) {
            this.requestor?.setValidators([Validators.required]);
          } else {
            this.requestor?.setValidators([]);
          }
          this.requestor?.updateValueAndValidity();
        }),
      ),
    );
    this.rootForm.setValidators([
      commentRequiredValidator(this.comments, this.handover, [false]),
    ]);
    this.rootForm.updateValueAndValidity();
  }

  change(event) {
    // console.log('change', event);
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

  get requestor(): AbstractControl {
    return this.rootForm.get('alanda-role-select-vacation-requestor.selected');
  }

  get snooze(): AbstractControl {
    return this.rootForm.get('alanda-snooze.snooze');
  }
}
