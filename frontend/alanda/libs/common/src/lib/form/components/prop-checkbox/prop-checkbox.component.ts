import { Component, OnInit, Input } from '@angular/core';
import { FormGroup, FormBuilder, AbstractControl } from '@angular/forms';
import { AlandaPropertyApiService } from '../../../shared/api/propertyApi.service';
import { AlandaProject } from '../../../shared/api/models/project';

const SELECTOR = 'alanda-prop-checkbox';

@Component({
  selector: SELECTOR,
  templateUrl: './prop-checkbox.component.html',
  styleUrls: [],
})
export class AlandaPropCheckboxComponent implements OnInit {
  @Input() propertyName: string;
  @Input() project: AlandaProject;
  @Input() label: string;
  @Input() existingValue: boolean;
  type = 'BOOLEAN';

  @Input()
  set rootFormGroup(rootFormGroup: FormGroup) {
    if (rootFormGroup) {
      rootFormGroup.addControl(
        `${SELECTOR}-${this.propertyName}`,
        this.checkboxForm,
      );
    }
  }

  checkboxForm = this.fb.group({
    checked: [null],
  });

  constructor(
    private readonly propertyService: AlandaPropertyApiService,
    private readonly fb: FormBuilder,
  ) {}

  ngOnInit(): void {
    if (this.existingValue != null) {
      this.checked.setValue(this.existingValue);
    } else {
      this.propertyService
        .get(null, null, this.project.guid, this.propertyName)
        .subscribe((resp) => {
          this.checked.setValue(resp.value);
        });
    }
  }

  save(): void {
    this.propertyService
      .set(
        null,
        null,
        this.project.guid,
        this.propertyName,
        this.checked.value,
        this.type,
      )
      .subscribe();
  }

  get checked(): AbstractControl {
    return this.checkboxForm.get('checked');
  }
}
