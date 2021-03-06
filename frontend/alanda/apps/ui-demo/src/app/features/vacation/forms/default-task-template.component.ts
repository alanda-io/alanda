import { Component, AfterViewInit } from '@angular/core';
import { BaseFormComponent, AlandaTaskFormService } from '@alanda/common';

@Component({
  selector: 'alanda-default-task-template',
  templateUrl: './default-task-template.component.html',
})
export class DefaultTaskComponent implements BaseFormComponent, AfterViewInit {
  state$ = this.taskFormService.state$;
  rootForm = this.taskFormService.rootForm;

  constructor(private readonly taskFormService: AlandaTaskFormService) {}

  submit(): void {
    this.taskFormService.submit();
  }

  ngAfterViewInit(): void {
    // this.formManagerService.addValidators();
  }
}
