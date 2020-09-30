import { Component, Input, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup } from '@angular/forms';
import { map, switchMap, tap } from 'rxjs/operators';
import { RxState } from '@rx-angular/state';
import { Observable } from 'rxjs';

const defaultSnoozedDays = 3640; // 10 years

interface AlandaSnoozeState {
  variableControl: AbstractControl;
  duration: number;
}

@Component({
  selector: 'alanda-snooze',
  templateUrl: './snooze.component.html',
  providers: [RxState],
})
export class AlandaSnoozeComponent implements OnInit {
  @Input() set variableControl(control: AbstractControl) {
    this.state.set({ variableControl: control });
  }
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

  duration$ = this.state.select('variableControl').pipe(
    switchMap((control) => control.valueChanges),
    map((value) => {
      let snoozeDuration = 0;
      for (const valueToCheck of this.valuesToCheck) {
        console.log('valcheck', valueToCheck);
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

  constructor(
    private readonly fb: FormBuilder,
    private state: RxState<AlandaSnoozeState>,
  ) {
    this.state.connect('duration', this.duration$);
  }

  ngOnInit(): void {
    // this.duration$ = this.variableControl.valueChanges.pipe(
    //   map((value) => {
    //     let snoozeDuration = 0;
    //     for (const valueToCheck in this.valuesToCheck) {
    //       if (value === valueToCheck) {
    //         if (Number.isInteger(value)) {
    //           snoozeDuration = value;
    //         } else {
    //           snoozeDuration = defaultSnoozedDays;
    //         }
    //         break;
    //       }
    //     }
    //     return snoozeDuration;
    //   }),
    //   tap((duration) => {
    //     this.snoozeForm.get('snooze').setValue(duration);
    //   }),
    // );
  }
}
