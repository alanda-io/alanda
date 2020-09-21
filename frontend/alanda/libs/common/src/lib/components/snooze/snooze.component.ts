import { Component, Input } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'alanda-alanda-snooze',
  templateUrl: './snooze.component.html',
})
export class AlandaSnoozeComponent {
  defaultSnoozedDays = 3640;
  @Input()
  selectId;
  @Input()
  valuesToCheck;
  @Input()
  set rootFormGroup(rootFormGroup: FormGroup) {
    if (rootFormGroup) {
      rootFormGroup.addControl(
        `alanda-snooze-${this.selectId}`,
        this.snoozeForm,
      );
    }
  }

  snoozeForm = this.fb.group({
    snooze: [null, Validators.required],
  });

  constructor(private readonly fb: FormBuilder) {}

  isInteger(value): boolean {
    return (
      typeof value === 'number' &&
      isFinite(value) &&
      Math.floor(value) === value
    );
  }

  getDuration(): number {
    let snoozeDuration = 0;
    if (this.selectId && this.rootFormGroup) {
      const selectValue = this.rootFormGroup.get(this.selectId).value;
      for (const valueToCheck in this.valuesToCheck) {
        if (selectValue === valueToCheck) {
          if (this.isInteger(selectValue)) {
            snoozeDuration = selectValue;
          } else {
            snoozeDuration = this.defaultSnoozedDays;
          }
        }
      }
    } else {
      snoozeDuration = 0;
    }
    return snoozeDuration;
  }
}
