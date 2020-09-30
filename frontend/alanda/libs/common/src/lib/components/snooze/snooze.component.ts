import { Component, Input, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup } from '@angular/forms';
import { map, tap } from 'rxjs/operators';
import { RxState } from '@rx-angular/state';
import { Observable } from 'rxjs';

const defaultSnoozedDays = 3640; // 10 years

interface AlandaSnoozeState {
  variableName: string;
  duration: number;
}

@Component({
  selector: 'alanda-snooze',
  templateUrl: './snooze.component.html',
  providers: [RxState],
})
export class AlandaSnoozeComponent implements OnInit {
  @Input() variableControl: AbstractControl;
  @Input()
  valuesToCheck: string[];
  @Input()
  set rootFormGroup(rootFormGroup: FormGroup) {
    if (rootFormGroup) {
      rootFormGroup.addControl(`alanda-snooze`, this.snoozeForm);
    }
  }

  snoozeForm = this.fb.group({
    snooze: null,
  });

  duration$: Observable<any>;

  constructor(
    private readonly fb: FormBuilder,
    private state: RxState<AlandaSnoozeState>,
  ) {
    this.state.connect('duration', this.duration$);
  }

  ngOnInit(): void {
    this.duration$ = this.variableControl.valueChanges.pipe(
      map((value) => {
        let snoozeDuration = 0;
        for (const valueToCheck in this.valuesToCheck) {
          if (value === valueToCheck) {
            if (Number.isInteger(value)) {
              snoozeDuration = value;
            } else {
              snoozeDuration = defaultSnoozedDays;
            }
            break;
          }
        }
        return snoozeDuration;
      }),
      tap((duration) => {
        this.snoozeForm.get('snooze').setValue(duration);
      }),
    );
  }
}
