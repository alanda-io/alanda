import { Component, Input, OnInit } from '@angular/core';
import { AlandaTaskFormService } from '../../..';

@Component({
  selector: 'alanda-alanda-snooze',
  templateUrl: './snooze.component.html',
})
export class AlandaSnoozeComponent implements OnInit {

  defaultSnoozedDays: number = 10 * 52 * 7
  @Input()
  pmcSelectId
  @Input()
  valuesToCheck

  constructor(private formService: AlandaTaskFormService) { }

  ngOnInit(): void {
  }

  isInteger(value): boolean {
    return typeof value === 'number' && isFinite(value) && Math.floor(value) === value;
  }

  getDuration (): number {
    let snoozeDuration = 0;
    if (this.pmcSelectId) {
      const selValue = this.formService.getFormComponent(this.pmcSelectId).selectedOption.value;
      for (const valueToCheck in this.valuesToCheck) {
        if (selValue === valueToCheck) {

          if (this.isInteger(selValue)) {
            snoozeDuration = selValue
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
