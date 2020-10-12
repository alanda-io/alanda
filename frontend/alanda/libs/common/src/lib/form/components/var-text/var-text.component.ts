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
  type = 'String';

  @Input()
  set rootFormGroup(rootFormGroup: FormGroup) {
    if (rootFormGroup) {
      rootFormGroup.addControl(
        `${SELECTOR}-${this.variableName}`,
        this.textBox,
      );
    }
  }

  textBox = this.fb.group({
    text: '',
  });

  constructor(
    private readonly taskService: AlandaTaskApiService,
    private readonly fb: FormBuilder,
  ) {}

  ngOnInit(): void {
    if (this.existingValue != null) {
      this.text.setValue(this.existingValue);
    } else {
      this.taskService
        .getVariable(this.task.task_id, this.variableName)
        .subscribe((resp) => {
          this.text.setValue(resp.value);
        });
    }
  }

  save(): void {
    this.taskService
      .setVariable(this.task.task_id, this.variableName, {
        value: this.text.value,
        type: this.type,
      })
      .subscribe();
  }

  get text(): AbstractControl {
    return this.textBox.get('text');
  }
}
