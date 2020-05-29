import { Component, OnInit, Input } from '@angular/core';
import { SelectItem } from 'primeng/api';
import {
  FormGroup,
  FormBuilder,
  Validators,
  AbstractControl,
} from '@angular/forms';
import { AlandaTaskApiService } from '../../../../api/taskApi.service';

@Component({
  selector: 'alanda-simple-select',
  templateUrl: './simple-select.component.html',
  styleUrls: ['./simple-select.component.scss'],
})
export class AlandaSimpleSelectComponent implements OnInit {
  @Input() items: SelectItem[];
  @Input() variableName: string;
  @Input() task: any;
  @Input() label: string;
  @Input() type?: string;

  @Input()
  set rootFormGroup (rootFormGroup: FormGroup) {
    if (rootFormGroup) {
      rootFormGroup.addControl(
        `alanda-simple-select-${this.variableName}`,
        this.selectForm
      );
    }
  }

  selectForm = this.fb.group({
    selected: [null, Validators.required],
  });

  constructor (
    private readonly taskService: AlandaTaskApiService,
    private readonly fb: FormBuilder
  ) {}

  ngOnInit () {
    if (!this.type) {
      this.type = 'string';
    }
    this.taskService
      .getVariable(this.task.task_id, this.variableName)
      .subscribe((resp) => {
        this.selectForm.get('selected').setValue(resp.value);
      });
  }

  save () {
    this.taskService
      .setVariable(this.task.task_id, this.variableName, {
        value: this.selectForm.get('selected').value,
        type: this.type,
      })
      .subscribe();
  }

  get selected (): AbstractControl {
    return this.selectForm.get('selected');
  }
}
