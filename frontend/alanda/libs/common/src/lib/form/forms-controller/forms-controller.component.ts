import { Component } from '@angular/core';
import { AlandaTaskFormService } from '../alanda-task-form.service';

@Component({
  selector: 'alanda-forms-controller',
  templateUrl: './forms-controller.component.html',
  styleUrls: ['./forms-controller.component.scss'],
  providers: [AlandaTaskFormService],
})
export class AlandaFormsControllerComponent {
  state$ = this.taskFormService.state$;
  rootForm = this.taskFormService.rootForm;
  activeTab = 0;

  constructor(private readonly taskFormService: AlandaTaskFormService) {}
}
