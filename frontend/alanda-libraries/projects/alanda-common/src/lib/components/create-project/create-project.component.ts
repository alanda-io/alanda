import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { Router } from '@angular/router';
import { AlandaProjectService } from '../../api/alandaProject.service';
import { AlandaProjectType } from '../../api/models/alandaProjectType';
import { AlandaProject } from '../../api/models/alandaProject';

@Component({
  selector: 'create-project-component',
  templateUrl: './create-project.component.html',
  styles: [],
})

export class CreateProjectComponent implements OnInit {

  public showDelegateDialog: boolean;
  public projectTypes: AlandaProjectType[] = [];
  public allowedTagList: string[];
  public selectedProjectType: any;
  public project: AlandaProject;
  public formGroup: FormGroup;
  public isLoading: boolean;
  constructor (private projectService: AlandaProjectService,
               private messageService: MessageService,
               private router: Router) {
  }

  ngOnInit(): void {
    this.load();
    this.showDelegateDialog = true;
  }

  onProjectTypeSelected(): void {
    this.showDelegateDialog = false;
    this.setupProject();
    this.initFormGroup();
  }

  private setupProject() {
    this.project.pmcProjectType = this.selectedProjectType;
    this.allowedTagList = this.selectedProjectType.allowedTagList.map(tag => {return {value: tag}});
  }

  private initFormGroup() {
    this.formGroup = new FormGroup({
      tag: new FormControl(null, {validators: [Validators.required]}),
      prio: new FormControl(null, {validators: [Validators.required]}),
      projectDueDate: new FormControl(),
      projectTitle: new FormControl(null, {validators: [Validators.required]}),
      projectDetails: new FormControl(null, {validators: [Validators.required]}),
  });

  }

  private load(): void {
    this.projectService.searchCreateAbleProjectType().subscribe((pTypes: AlandaProjectType[]) => {
      this.projectTypes = pTypes;
      }
    );
  }

  public onSubmit() {
    this.project.dueDate = this.formGroup.get('projectDueDate').value;
    this.project.title = this.formGroup.get('projectTitle').value;
    this.project.priority = <any>(this.formGroup.get('prio').value).value;
    this.project.properties = [];
    this.project.comment = this.formGroup.get('projectDetails').value;
    this.project.tag = [<any>(this.formGroup.get('tag').value).value];
    this.isLoading = true;
    this.projectService.createProject(this.project).subscribe(
      project => {
        this.isLoading = false;
        this.messageService.add({severity:'success', summary:'Create Project', detail: 'Project has been created'})
        this.router.navigate([`projectdetails/${project.projectId}`]);
      },
      error => {this.isLoading = false; this.messageService.add({severity:'error', summary:'Create Project', detail: error.message})});
  }

}
