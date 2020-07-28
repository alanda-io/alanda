import { Component, OnInit, Input } from '@angular/core';
import { FormGroup, FormBuilder, AbstractControl } from '@angular/forms';
import { AlandaProject } from '../../../shared/api/models/project';
import { AlandaTaskApiService } from '../../../shared/api/taskApi.service';

const SELECTOR = 'alanda-var-checkbox';

@Component({
  selector: SELECTOR,
  templateUrl: './var-checkbox.component.html',
  styleUrls: [],
})
export class AlandaVarCheckboxComponent implements OnInit {
  @Input() variableName: string;
  @Input() project: AlandaProject;
  @Input() task: any;
  @Input() label: string;
  @Input() existingValue: boolean;
  type = 'BOOLEAN';

  @Input()
  set rootFormGroup(rootFormGroup: FormGroup) {
    if (rootFormGroup) {
      rootFormGroup.addControl(
        `${SELECTOR}-${this.variableName}`,
        this.checkboxForm,
      );
    }
  }

  checkboxForm = this.fb.group({
    checked: [null],
  });

  constructor(
    private readonly taskService: AlandaTaskApiService,
    private readonly fb: FormBuilder,
  ) {}

  ngOnInit(): void {
    if (this.existingValue != null) {
      this.checked.setValue(this.existingValue);
    } else {
      this.taskService
        .getVariable(this.task.task_id, this.variableName)
        .subscribe((resp) => {
          this.checked.setValue(resp.value);
        });
    }
  }

  save(): void {
    this.taskService
      .setVariable(this.task.task_id, this.variableName, {
        value: this.checked.value,
        type: this.type,
      })
      .subscribe();
  }

  get checked(): AbstractControl {
    return this.checkboxForm.get('checked');
  }
}
