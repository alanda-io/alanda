import { Component, OnInit, Input } from '@angular/core';
import { AlandaProject } from '../../../../shared/api/models/project';
import { FormGroup, FormBuilder } from '@angular/forms';
import { AlandaMilestoneApiService } from '../../../../shared/api/milestoneApi.service';
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
  @Input()
  set rootFormGroup(rootFormGroup: FormGroup) {
    if (rootFormGroup) {
      rootFormGroup.addControl(this.displayName, this.milestoneForm);
    }
  }

  milestoneForm = this.fb.group({
    fc: [null],
    act: [null],
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
          this.milestoneForm.get('fc').setValue(ms.fc);
        }
        if (ms?.act) {
          this.milestoneForm.get('act').setValue(ms.act);
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
}
