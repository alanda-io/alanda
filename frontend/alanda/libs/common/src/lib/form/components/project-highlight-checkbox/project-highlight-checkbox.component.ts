import { Component, OnInit, Input } from '@angular/core';
import { FormGroup, FormBuilder, AbstractControl } from '@angular/forms';
import { AlandaPropertyApiService } from '../../../api/propertyApi.service';
import { AlandaProject } from '../../../api/models/project';
import { AlandaUser } from '../../../api/models/user';
import { Authorizations } from '../../../permissions';
import { AlandaProjectApiService } from '../../../api/projectApi.service';

const SELECTOR = 'alanda-project-highlight-checkbox';

@Component({
  selector: SELECTOR,
  templateUrl: './project-highlight-checkbox.component.html',
  styleUrls: [],
})
export class AlandaPropCheckboxComponent implements OnInit {
  @Input() project: AlandaProject;
  @Input() label: string;
  @Input() existingValue: boolean;
  @Input() readonly: boolean;
  @Input() user: AlandaUser;
  canWrite: boolean;
  @Input() rootFormGroup: FormGroup;

  checkboxForm = this.fb.group({
    checked: [null],
  });

  constructor(
    private readonly projectService: AlandaProjectApiService,
    private readonly fb: FormBuilder,
  ) {}

  ngOnInit(): void {
    if (this.readonly === true) {
      this.checked.disable({ emitEvent: false });
    }
    if (this.rootFormGroup) {
      this.rootFormGroup.addControl(
        '' + SELECTOR + (this.label != null ? this.label : ''),
        this.checkboxForm,
      );
    }
    if (this.user != null) {
      const authStr = `field:write:${this.project.pmcProjectType.idName}:highlight`;
      this.canWrite = Authorizations.hasPermission(this.user, authStr, 'write');
    } else {
      this.canWrite = false;
    }
    if (this.existingValue != null) {
      this.checked.setValue(this.existingValue);
    } else {
      this.projectService
        .getProjectHighlight(this.project.projectId)
        .subscribe((resp) => {
          this.checked.setValue(resp);
        });
    }
  }

  save(): void {
    this.projectService
      .updateProjectHighlight(this.project.projectId, this.checked.value)
      .subscribe();
  }

  get checked(): AbstractControl {
    return this.checkboxForm.get('checked');
  }
}
