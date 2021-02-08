import { Component } from '@angular/core';
import { UserEnrichedTaskFormService } from '../../services/userEnrichedTaskForm.service';

@Component({
  templateUrl: './user-enriched-forms-controller.component.html',
  styleUrls: ['./user-enriched-forms-controller.component.scss'],
  providers: [UserEnrichedTaskFormService],
})
export class UserEnrichedFormsControllerComponent {
  state$ = this.taskFormService.state$;
  rootForm = this.taskFormService.rootForm;
  activeTab = 0;

  constructor(private readonly taskFormService: UserEnrichedTaskFormService) {}
}
