import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { Router, ActivatedRoute } from '@angular/router';
import { AlandaProjectApiService } from '../../api/projectApi.service';
import { AlandaProjectType } from '../../api/models/projectType';
import { AlandaProject } from '../../api/models/project';
import { mergeMap, tap } from 'rxjs/operators';

@Component({
  selector: 'alanda-create-project',
  templateUrl: './create-project.component.html',
  styleUrls: ['./create-project.component.scss'],
})

export class AlandaCreateProjectComponent implements OnInit {
  showDialog = true;
  projectTypes: AlandaProjectType[] = [];
  allowedTagList: any[];
  selectedProjectType: AlandaProjectType = {};
  project: AlandaProject = {};
  formGroup: FormGroup;
  isLoading = false;

  constructor (public readonly projectService: AlandaProjectApiService,
    private readonly messageService: MessageService,
    private readonly router: Router, private readonly activatedRoute: ActivatedRoute) {
  }

  ngOnInit (): void {
    this.projectService.searchCreateAbleProjectType().subscribe((pTypes: AlandaProjectType[]) => {
      this.projectTypes = pTypes;
    });
    this.showDialog = true;
  }

  onProjectTypeSelected (): void {
    if (Object.keys(this.selectedProjectType).length !== 0) {
      this.showDialog = false;
      this.project.pmcProjectType = this.selectedProjectType;
      this.allowedTagList = this.selectedProjectType.allowedTagList.map(tag => { return { value: tag } });
      this.initFormGroup();
    }
  }


  private initFormGroup (): void {
    this.formGroup = new FormGroup({
      tag: new FormControl(null, { validators: [Validators.required] }),
      prio: new FormControl(null, { validators: [Validators.required] }),
      projectDueDate: new FormControl(),
      projectTitle: new FormControl(null, { validators: [Validators.required] }),
      projectDetails: new FormControl(null, { validators: [Validators.required] }),
    });
  }


  public onSubmit (): void {
    if (this.formGroup.valid) {
      this.project.dueDate = this.formGroup.get('projectDueDate').value;
      this.project.title = this.formGroup.get('projectTitle').value;
      this.project.priority = (this.formGroup.get('prio').value).value;
      this.project.properties = [];
      this.project.comment = this.formGroup.get('projectDetails').value;
      this.project.tag = [(this.formGroup.get('tag').value).value];
      this.isLoading = true;
      this.projectService.createProject(this.project).subscribe(
        project => {
          this.isLoading = false;
          this.messageService.add({ severity: 'success', summary: 'Create Project', detail: 'Project has been created' });
          this.router.navigate([`projectdetails/${project.projectId}`]);
        },
        error => { this.isLoading = false; this.messageService.add({ severity: 'error', summary: 'Create Project', detail: error.message }) });
    }
  }
}
