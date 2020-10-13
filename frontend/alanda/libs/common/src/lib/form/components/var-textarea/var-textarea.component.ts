import { Component, Input, OnInit } from '@angular/core';
import { AlandaProject } from '../../../api/models/project';
import { AlandaTaskApiService } from '../../../api/taskApi.service';
import { AbstractControl, FormBuilder, FormGroup } from '@angular/forms';

const SELECTOR = 'alanda-var-textarea';

@Component({
  selector: SELECTOR,
  templateUrl: './var-textarea.component.html',
})
export class AlandaVarTextareaComponent implements OnInit {
  @Input() variableName: string;
  @Input() project: AlandaProject;
  @Input() task: any;
  @Input() label: string;
  @Input() existingValue: string;
  @Input() disabled: boolean;
  @Input() rows = 8;
  @Input() cols = 50;
  type = 'String';

  @Input()
  rootFormGroup: FormGroup;

  textArea = this.fb.group({
    text: ''
  });

  constructor(
    private readonly taskService: AlandaTaskApiService,
    private readonly fb: FormBuilder,
  ) {}

  ngOnInit(): void {
    if (this.rootFormGroup) {
      this.rootFormGroup.addControl(
        `${SELECTOR}-${this.variableName}`,
        this.textArea,
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
    if(this.disabled === true){
      this.textArea.disable();
    }
  }

  save(): void {
    if(!this.textArea.invalid) {
      this.taskService
        .setVariable(this.task.task_id, this.variableName, {
          value: this.text.value,
          type: this.type,
        })
        .subscribe();
    }
  }

  get text(): AbstractControl {
    return this.textArea.get('text');
  }
}
