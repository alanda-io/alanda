import { Component, OnInit, Input } from '@angular/core';
import { AlandaProject } from '../../../../api/models/project';
import { FormGroup, FormBuilder } from '@angular/forms';
import { AlandaMilestoneApiService } from '../../../../api/milestoneApi.service';
import { convertUTCDate } from '../../../../utils/helper-functions';

@Component({
  selector: 'alanda-milestone-select',
  templateUrl: './milestone-select.component.html',
  styleUrls: ['./milestone-select.component.scss'],
})
export class AlandaSelectMilestoneComponent implements OnInit {
  @Input() project: AlandaProject;
  @Input() displayName: string;
  @Input() msName: string;
  @Input() dateFormat = 'dd.mm.yyyy';
  @Input() disabled: false;
  @Input() permissionString: string;
  @Input()
  set rootFormGroup(rootFormGroup: FormGroup) {
    if (rootFormGroup) {
      rootFormGroup.addControl(this.displayName, this.milestoneForm);
    }
  }
  @Input() showFC = true;
  @Input() showACT = true;

  milestoneForm = this.fb.group({
    fc: [{ value: null, disabled: this.disabled }],
    act: [{ value: null, disabled: this.disabled }],
  });

  constructor(
    private readonly milestoneService: AlandaMilestoneApiService,
    private readonly fb: FormBuilder,
  ) {}

  ngOnInit() {
    this.milestoneService
      .getByProjectAndMsIdName(this.project.projectId, this.msName)
      .subscribe((ms) => {
        if (ms?.fc) {
          this.milestoneForm.get('fc').setValue(new Date(ms.fc));
        }
        if (ms?.act) {
          this.milestoneForm.get('act').setValue(new Date(ms.act));
        }
      });
  }

  onChange() {
    const fc = this.milestoneForm.get('fc').value
      ? convertUTCDate(new Date(this.milestoneForm.get('fc').value))
          .toISOString()
          .substring(0, 10)
      : null;
    const act = this.milestoneForm.get('act').value
      ? convertUTCDate(new Date(this.milestoneForm.get('act').value))
          .toISOString()
          .substring(0, 10)
      : null;
    this.milestoneService
      .updateByProjectAndMsIdName(
        this.project.projectId,
        this.msName,
        fc,
        act,
        null,
        false,
        false,
      )
      .subscribe();
  }

  getPermissionString(type?: string): string {
    if (this.permissionString) {
      return this.permissionString;
    }
    return (
      `ms:write:${this.project.projectTypeIdName}:${this.msName}` +
      (type ? `:${type}` : '')
    );
  }
}