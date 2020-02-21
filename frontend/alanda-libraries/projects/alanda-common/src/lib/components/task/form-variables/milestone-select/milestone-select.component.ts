
import { Component, OnInit, Input } from "@angular/core";
import { FormGroup, FormBuilder, Validators } from "@angular/forms";
import { FormsRegisterService } from "../../../../services/alandaFormsRegister.service";
import { Project } from "../../../../models/project";
import { MilestoneService } from "../../../../api/alandaMilestone.service";
import { Utils } from "../../../../utils/helper-functions";

@Component({
    selector: 'milestone-select',
    templateUrl: './milestone-select.component.html',
    styleUrls: [],
  })
export class SelectMilestoneComponent implements OnInit {

    @Input() project: Project;
    @Input() displayName: string;
    @Input() msName: string;

    milestoneForm: FormGroup;

    constructor(private milestoneService: MilestoneService, private fb: FormBuilder, private formsRegisterService: FormsRegisterService){}

    ngOnInit(){
      this.milestoneService.getByProjectAndMsIdName(this.project.projectId, this.msName).subscribe();
      this.initMilestoneFormGroup();

    }

    private initMilestoneFormGroup() {
      this.milestoneForm = this.fb.group({
        fc: [null, Validators.required],
        act: [null, Validators.required]
      });
      this.formsRegisterService.registerForm(this.milestoneForm, `${this.displayName}`);
    }

    onChange() {
      const fc = this.milestoneForm.get('fc').value ? Utils.convertUTCDate(new Date(this.milestoneForm.get('fc').value)).toISOString().substring(0,10) : null;
      const act = this.milestoneForm.get('act').value ? Utils.convertUTCDate(new Date(this.milestoneForm.get('act').value)).toISOString().substring(0,10) : null;
      this.milestoneService.updateByProjectAndMsIdName(this.project.projectId, this.msName, fc, act, null, false, false).subscribe();
    }

  }
