import { Component, Inject, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { Router, ActivatedRoute } from '@angular/router';
import { AlandaProjectApiService } from '../../api/projectApi.service';
import { AlandaProjectType } from '../../api/models/projectType';
import { AlandaProject } from '../../api/models/project';
import {
  catchError,
  concatMap,
  debounceTime,
  map,
  mergeMap,
  switchMap,
  tap,
} from 'rxjs/operators';
import { APP_CONFIG, AppSettings } from '../../models/appSettings';
import { formatDate, Location } from '@angular/common';
import { RxState } from '@rx-angular/state';
import { AlandaRefObject } from '../../api/models/refObject';
import { EMPTY, of, Subject, zip } from 'rxjs';
import { AlandaRoleApiService } from '../../api/roleApi.service';
import { AlandaTaskApiService } from '../../api/taskApi.service';
import { LocaleSettings } from 'primeng/calendar';

interface CreateState {
  refObject: AlandaRefObject;
  refObjectList: AlandaRefObject[];
  parentProjectGuid: number;
  parentProject: AlandaProject;
}

@Component({
  selector: 'alanda-create-project',
  templateUrl: './create-project.component.html',
  styleUrls: ['./create-project.component.scss'],
  providers: [RxState],
})
export class AlandaCreateProjectComponent implements OnInit {
  showDialog = true;
  projectTypes: AlandaProjectType[] = [];
  allowedTagList: any[];
  selectedProjectType: AlandaProjectType = {};
  project: AlandaProject = {};
  formGroup: FormGroup;
  isLoading = false;
  locale: LocaleSettings;
  dateFormat: string;

  state$ = this.state.select();
  searchRefObjectEvent$ = new Subject<string>();
  selectRefObjectEvent$ = new Subject<string>();

  searchRefObjects$ = this.searchRefObjectEvent$.pipe(
    debounceTime(300),
    switchMap((searchTerm) => {
      return this.projectService.autocompleteRefObjects(
        searchTerm,
        this.selectedProjectType.objectType.toLowerCase(),
      );
    }),
  );

  parentProjectGuid$ = this.activatedRoute.queryParamMap.pipe(
    map((paramMap) => {
      return parseInt(paramMap.get('parentProjectGuid'), 10) || null;
    }),
  );

  parentProject$ = this.parentProjectGuid$.pipe(
    switchMap((guid) => {
      return this.projectService.getProjectByGuid(guid);
    }),
    map((project) => {
      return project || null;
    }),
  );

  constructor(
    public readonly projectService: AlandaProjectApiService,
    private taskService: AlandaTaskApiService,
    private readonly messageService: MessageService,
    private roleService: AlandaRoleApiService,
    private readonly router: Router,
    private readonly activatedRoute: ActivatedRoute,
    private state: RxState<CreateState>,
    private location: Location,
    @Inject(APP_CONFIG) config: AppSettings,
  ) {
    this.locale = config.LOCALE_PRIME;
    this.dateFormat = config.DATE_FORMAT;
    this.state.set({ refObjectList: [] });
    this.state.connect('refObjectList', this.searchRefObjects$);
    this.state.connect('parentProjectGuid', this.parentProjectGuid$);
    this.state.connect('parentProject', this.parentProject$);
    // this.state.hold(this.searchRefObjectEvent$);
  }

  ngOnInit(): void {
    const parentProjectGuid = this.activatedRoute.snapshot.paramMap.get(
      'projectGuid',
    );
    if (parentProjectGuid) {
      this.projectService
        .getProjectByGuid(Number(parentProjectGuid))
        .pipe(
          tap((project) => (this.project.parents = [project])),
          mergeMap((_) => this.projectService.searchCreateAbleProjectType()),
        )
        .subscribe((types) => {
          this.projectTypes = types;
          this.selectedProjectType = types[0];
        });
    } else {
      this.projectService.searchCreateAbleProjectType().subscribe((types) => {
        this.projectTypes = types;
        this.selectedProjectType = types[0];
      });
    }
  }

  onProjectTypeSelected(): void {
    this.showDialog = false;
    this.project.pmcProjectType = this.selectedProjectType;
    this.allowedTagList = this.selectedProjectType.allowedTagList.map((tag) => {
      return { value: tag };
    });
    this.initFormGroup();
  }

  private initFormGroup(): void {
    this.formGroup = new FormGroup({
      tag: new FormControl(null, { validators: [Validators.required] }),
      prio: new FormControl(null, { validators: [Validators.required] }),
      selectedRefObject: new FormControl(null, {
        validators: [],
      }),
      projectDueDate: new FormControl(),
      projectTitle: new FormControl(null, {
        validators: [Validators.required],
      }),
      projectDetails: new FormControl(null, {
        validators: [Validators.required],
      }),
    });
  }

  public onSubmit(): void {
    this.formGroup.markAllAsTouched();
    if (this.formGroup.valid) {
      let projectDueDate = this.formGroup.get('projectDueDate').value;
      if (projectDueDate) {
        projectDueDate = formatDate(projectDueDate, this.dateFormat, 'en');
      }
      this.project.dueDate = projectDueDate;
      this.project.title = this.formGroup.get('projectTitle').value;
      this.project.priority = this.formGroup.get('prio').value.value;
      this.project.properties = [];
      this.project.comment = this.formGroup.get('projectDetails').value;
      this.project.tag = [this.formGroup.get('tag').value.value];
      this.project.parents = [this.state.get('parentProject')];
      this.isLoading = true;
      this.projectService
        .createProject(this.project)
        .pipe(
          catchError((error) => {
            this.messageService.add({
              severity: 'error',
              summary: 'Create Project',
              detail: error.message,
            });
            this.isLoading = false;
            return EMPTY;
          }),
          concatMap((project) => {
            this.messageService.add({
              severity: 'success',
              summary: 'Create Project',
              detail: 'Project has been created',
            });
            return zip(
              of(project),
              this.taskService.loadTasks({
                filterOptions: { 'project.projectId': project.projectId },
              }),
            );
          }),
          tap(([project, result]) => {
            this.isLoading = false;
            if (result.total <= 0) {
              this.router.navigate([`projectdetails/${project.projectId}`]);
            } else {
              const task = result.results[0].task;
              this.router.navigate([`forms/${task.formKey}/${task.task_id}`]);
            }
          }),
        )
        .subscribe();
    } else {
      Object.keys(this.formGroup.controls).forEach((key) => {
        this.formGroup.controls[key].markAsDirty();
      });
    }
  }

  goBack(): void {
    this.location.back();
  }
}
