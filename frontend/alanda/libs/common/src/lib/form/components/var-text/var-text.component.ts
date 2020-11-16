import { Component, OnInit, Input } from '@angular/core';
import { FormGroup, FormBuilder, AbstractControl } from '@angular/forms';
import { AlandaProject } from '../../../api/models/project';
import { AlandaTaskApiService } from '../../../api/taskApi.service';

const SELECTOR = 'alanda-var-text';

@Component({
  selector: SELECTOR,
  templateUrl: './var-text.component.html',
  styleUrls: [],
})
export class AlandaVarTextComponent implements OnInit {
  @Input() variableName: string;
  @Input() project: AlandaProject;
  @Input() task: any;
  @Input() label: string;
  @Input() existingValue: string;
  @Input() disabled: boolean;
  type = 'String';

  @Input()
  rootFormGroup: FormGroup;

  textBox = this.fb.group({
    text: '',
  });

  constructor(
    private readonly taskService: AlandaTaskApiService,
    private readonly fb: FormBuilder,
  ) {}

  ngOnInit(): void {
    if (this.rootFormGroup) {
      this.rootFormGroup.addControl(
        `${SELECTOR}-${this.variableName}`,
        this.textBox,
      );
    }

    if (this.existingValue != null) {
      this.text.setValue(this.existingValue);
    } else {
      this.taskService
        .getVariable(this.task.task_id, this.variableName)
        .subscribe((resp) => {
          this.text.setValue(resp.value);
        });
    }
    if (this.disabled === true) {
      this.textBox.disable();
    }
  }

  save(): void {
    if (!this.textBox.invalid) {
      this.taskService
        .setVariable(this.task.task_id, this.variableName, {
          value: this.text.value,
          type: this.type,
        })
        .subscribe();
    }
  }

  get text(): AbstractControl {
    return this.textBox.get('text');
  }
}
