import { Component, Input } from '@angular/core';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'alanda-alanda-snooze',
  templateUrl: './snooze.component.html',
})
export class AlandaSnoozeComponent {

  defaultSnoozedDays: number = 10 * 52 * 7
  @Input()
  pmcSelectId
  @Input()
  valuesToCheck
  @Input()
  rootFormGroup: FormGroup

  constructor() { }

  isInteger(value): boolean {
    return typeof value === 'number' && isFinite(value) && Math.floor(value) === value;
  }

  getDuration (): number {
    let snoozeDuration = 0;
    if (this.pmcSelectId && this.rootFormGroup) {
      const selectValue = this.rootFormGroup.get(this.pmcSelectId).value;
      for (const valueToCheck in this.valuesToCheck) {
        if (selectValue === valueToCheck) {
          if (this.isInteger(selectValue)) {
            snoozeDuration = selectValue
          } else {
            snoozeDuration = this.defaultSnoozedDays
          }
        }
      }
    } else {
      snoozeDuration = 0;
    }
    return snoozeDuration
  }
}
