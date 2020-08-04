import { Component, AfterViewInit } from '@angular/core';
import { AlandaTaskFormService } from '../alanda-task-form.service';

@Component({
  selector: 'alanda-forms-controller',
  templateUrl: './forms-controller.component.html',
  styleUrls: [],
  providers: [AlandaTaskFormService],
})
export class AlandaFormsControllerComponent implements AfterViewInit {
  state$ = this.taskFormService.state$;
  rootForm = this.taskFormService.rootForm;
  activeTab = 0;

  constructor(private readonly taskFormService: AlandaTaskFormService) {}

  ngAfterViewInit() {}
}
