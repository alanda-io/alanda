import { Component, OnInit, Input } from '@angular/core';
import { SelectItem } from 'primeng/api';
import {
  FormGroup,
  FormBuilder,
  Validators,
  AbstractControl,
} from '@angular/forms';
import { AlandaPropertyApiService } from '../../../api/propertyApi.service';
import { AlandaProject } from '../../../api/models/project';
import { AlandaUser } from '../../../api/models/user';
import { Authorizations } from '../../../permissions';
import { AlandaProjectApiService } from '../../../api/projectApi.service';

const SELECTOR = 'alanda-prop-select';

@Component({
  selector: SELECTOR,
  templateUrl: './prop-select.component.html',
  styleUrls: [],
})
export class AlandaPropSelectComponent implements OnInit {
  @Input() items: SelectItem[];
  @Input() propertyName: string;
  @Input() project: AlandaProject;
  @Input() label: string;
  @Input() type?: string;
  @Input() readonly: boolean;
  @Input() user: AlandaUser;
  @Input() rootFormGroup: FormGroup;
  @Input() appendTo: any = 'body';
  @Input() role: string;

  canWrite: boolean;

  selectForm = this.fb.group({
    selected: [null, Validators.required],
  });

  constructor(
    private readonly propertyService: AlandaPropertyApiService,
    private readonly projectService: AlandaProjectApiService,
    private readonly fb: FormBuilder,
  ) {}

  ngOnInit() {
    if (this.readonly === true) {
      this.selected.disable({ emitEvent: false });
    }
    if (this.rootFormGroup) {
      this.rootFormGroup.addControl(
        `${SELECTOR}-${this.propertyName}`,
        this.selectForm,
      );
    }
    if (!this.type) {
      this.type = 'STRING';
    }
    if (this.user != null) {
      this.canWrite = Authorizations.hasRole(this.role, this.user);
    } else {
      this.canWrite = true;
    }
    this.propertyService
      .get(null, null, this.project.guid, this.propertyName)
      .subscribe((resp) => {
        this.selected.setValue(resp.value);
      });
  }

  save() {
    this.projectService.updateProject(this.project);

    this.propertyService
      .set(
        null,
        null,
        this.project.guid,
        this.propertyName,
        this.selected.value,
        this.type,
      )
      .subscribe();
  }

  get selected(): AbstractControl {
    return this.selectForm.get('selected');
  }
}
