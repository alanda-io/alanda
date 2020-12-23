import { Component, OnInit, Input, Inject } from '@angular/core';
import {
  FormGroup,
  FormBuilder,
  AbstractControl,
  Validators,
} from '@angular/forms';
import { AlandaProject } from '../../../api/models/project';
import { AlandaTaskApiService } from '../../../api/taskApi.service';
import { AppSettings, APP_CONFIG } from '../../../models/appSettings';
import { LocaleSettings } from 'primeng/calendar';

const SELECTOR = 'alanda-var-datepicker';

@Component({
  selector: SELECTOR,
  templateUrl: './var-datepicker.component.html',
  styleUrls: [],
})
export class AlandaVarDatepickerComponent implements OnInit {
  @Input() variableName: string;
  @Input() project: AlandaProject;
  @Input() task: any;
  @Input() label: string;

  @Input() rootFormGroup: FormGroup;

  locale: LocaleSettings;

  datepickerForm = this.fb.group({
    pickedDate: [null, Validators.required],
  });

  constructor(
    private readonly taskService: AlandaTaskApiService,
    private readonly fb: FormBuilder,
    @Inject(APP_CONFIG) config: AppSettings,
  ) {
    this.locale = config.LOCALE_PRIME;
  }

  ngOnInit(): void {
    if (this.rootFormGroup) {
      this.rootFormGroup.addControl(
        `${SELECTOR}-${this.variableName}`,
        this.datepickerForm,
      );
    }
    this.taskService
      .getVariable(this.task.task_id, this.variableName)
      .subscribe((resp) => {
        this.pickedDate.setValue(resp.value);
      });
  }

  save(): void {
    this.taskService
      .setVariable(this.task.task_id, this.variableName, {
        value: this.pickedDate.value,
        type: 'String',
      })
      .subscribe();
  }

  delete(): void {
    this.taskService
      .deleteVariable(this.task.task_id, this.variableName)
      .subscribe();
  }

  get pickedDate(): AbstractControl {
    return this.datepickerForm.get('pickedDate');
  }
}
