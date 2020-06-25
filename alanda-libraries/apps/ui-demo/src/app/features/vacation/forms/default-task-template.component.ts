import { Component, AfterViewInit } from '@angular/core';
import {
  BaseFormComponent,
  AlandaTaskFormService
} from '@alanda-libraries/common';

@Component({
  selector: 'default-task',
  templateUrl: './default-task-template.component.html',
  styleUrls: ['./default-task-template.component.scss'],
})
export class DefaultTaskComponent implements BaseFormComponent, AfterViewInit {
  state$ = this.taskFormService.state$;
  rootForm = this.taskFormService.rootForm;

  constructor(private readonly taskFormService: AlandaTaskFormService) {

  }

  submit(): void {
    this.taskFormService.submit();
  };

  ngAfterViewInit(): void {
    // this.formManagerService.addValidators();
  }
}
