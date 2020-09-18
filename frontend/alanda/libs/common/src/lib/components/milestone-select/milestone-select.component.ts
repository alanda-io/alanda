import { Component, OnInit, Input, Inject } from '@angular/core';
import { AlandaProject } from '../../api/models/project';
import { FormGroup, FormBuilder } from '@angular/forms';
import { AlandaMilestoneApiService } from '../../api/milestoneApi.service';
import { convertUTCDate } from '../../utils/helper-functions';
import { AlandaUser } from '../../api/models/user';
import { APP_CONFIG, AppSettings } from '../../models/appSettings';
import { PERMISSION_PLACEHOLDER, Authorizations } from '../../permissions';

@Component({
  selector: 'alanda-milestone-select',
  templateUrl: './milestone-select.component.html',
  styleUrls: ['./milestone-select.component.scss'],
})
export class AlandaSelectMilestoneComponent implements OnInit {
  @Input() project: AlandaProject;
  @Input() displayName: string;
  @Input() msName: string;
  @Input() dateFormat: string;
  @Input() disabled = false;
  @Input() permissionString: string;
  @Input()
  set rootFormGroup(rootFormGroup: FormGroup) {
    if (rootFormGroup) {
      rootFormGroup.addControl(this.displayName, this.milestoneForm);
    }
  }
  @Input() showFC = true;
  @Input() showACT = true;
  @Input() user: AlandaUser;

  milestoneForm = this.fb.group({
    fc: [{ value: null, disabled: this.disabled }],
    act: [{ value: null, disabled: this.disabled }],
  });

  constructor(
    private readonly milestoneService: AlandaMilestoneApiService,
    private readonly fb: FormBuilder,
    @Inject(APP_CONFIG) config: AppSettings,
  ) {
    this.dateFormat = config.DATE_FORMAT_STR_PRIME;
  }

  ngOnInit() {
    if (
      !Authorizations.hasPermission(this.user, this.getPermissionString('fc'))
    ) {
      this.milestoneForm.get('fc').disable();
    }
    if (
      !Authorizations.hasPermission(this.user, this.getPermissionString('act'))
    ) {
      this.milestoneForm.get('act').disable();
    }
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
    return `ms:${this.project.authBase.replace(
      PERMISSION_PLACEHOLDER,
      'write',
    )}:${this.msName}:${type}`;
  }
}
