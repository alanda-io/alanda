import { Component, OnInit, Input } from '@angular/core';
import { AlandaProject } from 'projects/alanda-common/src/lib/api/models/project';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { AlandaMilestoneApiService } from 'projects/alanda-common/src/lib/api/milestoneApi.service';
import { AlandaFormsRegisterService } from 'projects/alanda-common/src/lib/services/formsRegister.service';
import { convertUTCDate } from 'projects/alanda-common/src/lib/utils/helper-functions';

@Component({
    selector: 'alanda-milestone-select',
    templateUrl: './milestone-select.component.html',
    styleUrls: [],
  })
export class AlandaSelectMilestoneComponent implements OnInit {

    @Input() project: AlandaProject;
    @Input() displayName: string;
    @Input() msName: string;

    milestoneForm: FormGroup;

    constructor(private milestoneService: AlandaMilestoneApiService, private fb: FormBuilder, private formsRegisterService: AlandaFormsRegisterService){}

    ngOnInit() {
      this.initMilestoneFormGroup();
      this.milestoneService.getByProjectAndMsIdName(this.project.projectId, this.msName).subscribe(ms => {
        if (ms && ms.fc) {
          this.milestoneForm.get('fc').setValue(ms.fc);
        }
        if (ms && ms.act) {
          this.milestoneForm.get('act').setValue(ms.act);
        }
      });
    }

    private initMilestoneFormGroup() {
      this.milestoneForm = this.fb.group({
        fc: [null],
        act: [null]
      });
      this.formsRegisterService.registerForm(this.milestoneForm, `${this.displayName}`);
    }

    onChange() {
      const fc = this.milestoneForm.get('fc').value ? convertUTCDate(new Date(this.milestoneForm.get('fc').value)).toISOString().substring(0,10) : null;
      const act = this.milestoneForm.get('act').value ? convertUTCDate(new Date(this.milestoneForm.get('act').value)).toISOString().substring(0,10) : null;
      this.milestoneService.updateByProjectAndMsIdName(this.project.projectId, this.msName, fc, act, null, false, false).subscribe();
    }

  }
