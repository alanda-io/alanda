import { Component, AfterViewInit } from '@angular/core';
import { SelectItem } from 'primeng/api';
import { BaseFormComponent } from 'projects/alanda-common/src/lib/form/base-form.component.interface';
import { AlandaTaskFormService } from 'projects/alanda-common/src/lib/form/alanda-task-form.service';
import { Observable, of } from 'rxjs';

@Component({
  selector: 'inform-substitute',
  templateUrl: './inform-substitute.component.html',
  styleUrls: [],
})
export class InformSubstituteComponent
implements BaseFormComponent, AfterViewInit {
  state$ = this.taskFormService.state$;
  rootForm = this.taskFormService.rootForm;
  items: SelectItem[];

  constructor(private readonly taskFormService: AlandaTaskFormService) {
    this.items = [
      { label: 'Yes', value: true },
      { label: 'No', value: false },
    ];
  }

  submit(): void {
    const alt: Observable<any> = of(['monitor', 'projects']);
    this.taskFormService.submit(alt).subscribe();
  }

  ngAfterViewInit(): void {
    // this.formManagerService.addValidators();
  }
}
