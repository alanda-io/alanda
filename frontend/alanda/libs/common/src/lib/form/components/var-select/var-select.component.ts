import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { SelectItem } from 'primeng/api';
import {
  FormGroup,
  FormBuilder,
  Validators,
  AbstractControl,
} from '@angular/forms';
import { AlandaTaskApiService } from '../../../api/taskApi.service';
import { RxState } from '@rx-angular/state';

const SELECTOR = 'alanda-var-select';

@Component({
  selector: SELECTOR,
  templateUrl: './var-select.component.html',
  styleUrls: [],
  providers: [RxState]
})
export class AlandaVarSelectComponent implements OnInit {
  @Input() items: SelectItem[];
  @Input() variableName: string;
  @Input() task: any;
  @Input() label: string;
  @Input() type?: string;
  @Output() valueChange: EventEmitter<any> = new EventEmitter();

  @Input() rootFormGroup: FormGroup;

  selectForm = this.fb.group({
    selected: [null, Validators.required],
  });

  constructor(
    private state: RxState<any>,
    private readonly taskService: AlandaTaskApiService,
    private readonly fb: FormBuilder,
  ) {}

  ngOnInit() {
    if (this.rootFormGroup) {
      this.rootFormGroup.addControl(
        `${SELECTOR}-${this.variableName}`,
        this.selectForm,
      );
    }
    if (!this.type) {
      this.type = 'String';
    }
    this.taskService
      .getVariable(this.task.task_id, this.variableName)
      .subscribe((resp) => {
        this.selected.setValue(resp.value);
      });
  }

  save(event) {
    this.taskService
      .setVariable(this.task.task_id, this.variableName, {
        value: this.selected.value,
        type: this.type,
      })
      .subscribe();
    this.valueChange.emit({
      originalEvent: event.originalEvent,
      value: this.selected.value,
    });
  }

  get selected(): AbstractControl {
    return this.selectForm.get('selected');
  }
}
