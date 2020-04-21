import { Component, AfterViewInit } from '@angular/core';
import { BaseFormComponent,
         AlandaTaskFormService} from 'projects/alanda-common/src/public-api';

@Component({
    selector: 'default-task',
    templateUrl: './default-task-template.component.html',
    styleUrls: [],
    providers: [AlandaTaskFormService]
  })
export class DefaultTaskComponent implements BaseFormComponent, AfterViewInit {

  state$ = this.taskFormService.state$;
  rootForm = this.taskFormService.rootForm;

  constructor(private taskFormService: AlandaTaskFormService) {

  }
  
  submit(): void {
    this.taskFormService.submit();
  };

  ngAfterViewInit(): void {
    // this.formManagerService.addValidators();
  }

}
