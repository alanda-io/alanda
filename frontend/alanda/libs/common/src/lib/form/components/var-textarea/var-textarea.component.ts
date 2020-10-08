import {Component, Input, OnInit} from '@angular/core';
import {AlandaProject, AlandaTaskApiService} from "../../../..";
import {AbstractControl, FormBuilder, FormGroup} from "@angular/forms";


const SELECTOR = 'alanda-var-textarea';

@Component({
  selector: SELECTOR,
  templateUrl: './alanda-var-textarea.component.html',
})
export class AlandaVarTextareaComponent implements OnInit {

  @Input() variableName: string;
  @Input() project: AlandaProject;
  @Input() task: any;
  @Input() label: string;
  @Input() existingValue: string;
  type = 'String';

  @Input()
  rootFormGroup: FormGroup

  textArea = this.fb.group({
    text: "",
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
    return this.textArea.get('text');
  }
}
