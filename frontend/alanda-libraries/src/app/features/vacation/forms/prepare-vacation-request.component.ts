import { Component, AfterViewInit } from '@angular/core';
import { SelectItem } from 'primeng/api';
import {
  AlandaTaskFormService,
  BaseFormComponent,
} from 'projects/alanda-common/src/public-api';
import { Validators, AbstractControl } from '@angular/forms';
import { NavigationEnd, Router } from '@angular/router';
import { PartialObserver } from 'rxjs';

@Component({
  selector: 'prepare-vacation-request',
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
    private readonly router: Router
  ) {
    this.items = [
      { label: 'Yes', value: true },
      { label: 'No', value: false },
    ];
  }

  submit(): void {
    // const observer: PartialObserver<any> = {
    //   next: (val) => {
    //     this.router.navigate([]).catch(() => {});
    //   },
    // };
    this.taskFormService.submit().subscribe();
    // pipe(
    //   switchMap(wiza)
    // )
    // .subscribe(
    //   (route) =>
    //   NavigationEnd(rout)
    // );
  }

  ngAfterViewInit(): void {
    this.roleSelector.setValidators([Validators.required]);
    this.roleSelector.updateValueAndValidity();
    console.log('rootForm', this.rootForm);
    // this.formManagerService.addValidators();
  }

  get roleSelector(): AbstractControl {
    return this.rootForm.get('alanda-role-select-vacation-approver.selected');
  }
}
