import { AlandaTaskFormService } from '@alanda/common';
import { Component } from '@angular/core';
import { UserStoreImpl } from '../../store/user';

@Component({
  templateUrl: './user-enriched-forms-controller.component.html',
  styleUrls: ['./user-enriched-forms-controller.component.scss'],
  providers: [AlandaTaskFormService],
})
export class UserEnrichedFormsControllerComponent {
  state$ = this.taskFormService.state$;
  user$ = this.userStore.currentUser$;
  rootForm = this.taskFormService.rootForm;
  activeTabIndex = 0;

  constructor(
    private readonly taskFormService: AlandaTaskFormService,
    private userStore: UserStoreImpl,
  ) {}
}
