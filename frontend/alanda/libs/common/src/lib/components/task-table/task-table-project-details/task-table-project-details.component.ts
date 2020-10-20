/* tslint:disable: member-ordering */
import { Component, Input, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RxState } from '@rx-angular/state';
import { EMPTY, Subject } from 'rxjs';
import {
  catchError,
  filter,
  map,
  startWith,
  switchMap,
  tap,
} from 'rxjs/operators';
import { MessageService } from 'primeng/api';
import { AlandaProject } from '../../../api/models/project';
import { AlandaProjectApiService } from '../../../api/projectApi.service';
import { ProjectDetailsMode } from './projectDetailsMode.enum';
import { isNil, isEmpty } from '../../../utils/helper-functions';

interface TaskTableProjectDetailsState {
  mode: ProjectDetailsMode;
  project: AlandaProject;
  projectGuid: number;
  working: boolean;
}

const NOT_EMITTABLE = { emitEvent: false };
const initState = {
  mode: ProjectDetailsMode.DETAILS,
  working: false,
};

@Component({
  selector: 'alanda-task-table-project-details',
  templateUrl: './task-table-project-details.component.html',
  styleUrls: ['./task-table-project-details.component.scss'],
  providers: [RxState],
})
export class TaskTableProjectDetailsComponent {
  state$ = this.state.select();
  saveClickEvent$ = new Subject();
  viewMode = ProjectDetailsMode;
  mainForm: FormGroup = this.fb.group({
    text: ['', [Validators.required]],
  });

  @Input() set mode(mode: ProjectDetailsMode) {
    this.state.set({ mode });
  }
  @Input() set project(project: AlandaProject) {
    this.state.set({ project });
    const mode = this.state?.get()?.mode;
    if (project) {
      this.mainForm
        .get('text')
        .patchValue(
          mode === ProjectDetailsMode.DETAILS
            ? project.details
            : project.guStatus,
          NOT_EMITTABLE,
        );
    }
  }
  @Input() set projectGuid(projectGuid: number) {
    this.state.set({ projectGuid });
  }
  @Output() close = new Subject<AlandaProject>();

  /**
   * Enables/disables main form based on working flag
   */
  enableDisableMainForm$ = this.state
    .select('working')
    .pipe(
      tap((working) =>
        working
          ? this.mainForm.disable(NOT_EMITTABLE)
          : this.mainForm.enable(NOT_EMITTABLE),
      ),
    );

  /**
   * Loads project from its guid when available
   *
   * @param guid number
   * @returns data object { project: AlandaProject, working: boolean }
   */
  loadProjectByGuid$ = this.state.select('projectGuid').pipe(
    filter((guid) => !isNil(guid)),
    switchMap((guid) =>
      this.projectService.getProjectByGuid(guid).pipe(
        tap((project) =>
          this.mainForm
            .get('text')
            .patchValue(
              this.state.get()?.mode === ProjectDetailsMode.DETAILS
                ? project?.details
                : project?.guStatus,
              NOT_EMITTABLE,
            ),
        ),
        map((project) => ({ project, working: false })),
        startWith({ working: true }),
        catchError((err) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Load Project By Guid',
            detail: err?.message,
          });
          return EMPTY;
        }),
      ),
    ),
  );

  /**
   * Persists updated project data to server.
   *
   * @param mode ProjectDetailsMode
   * @param form object the main form group value
   * @returns data object { project AlandaProject, working: boolean }
   */
  updateProject$ = this.saveClickEvent$.pipe(
    map(() => ({
      mode: this.state.get()?.mode,
      project: this.state.get()?.project,
    })),
    filter(({ project }) => this.mainForm.valid && !isEmpty(project)),
    map(({ mode, project }) => {
      mode === ProjectDetailsMode.DETAILS
        ? (project.details = this.mainForm.get('text')?.value)
        : (project.guStatus = this.mainForm.get('text')?.value);
      return project;
    }),
    switchMap((project) =>
      this.projectService.updateProject(project).pipe(
        tap(() => this.close.next(project)),
        map((updateProject) => ({ project: updateProject, working: false })),
        catchError((err) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Update Project Details',
            detail: err?.message,
          });
          return EMPTY;
        }),
      ),
    ),
    startWith({ working: true }),
  );

  constructor(
    private state: RxState<TaskTableProjectDetailsState>,
    private fb: FormBuilder,
    private messageService: MessageService,
    private projectService: AlandaProjectApiService,
  ) {
    this.state.connect(this.loadProjectByGuid$);
    this.state.connect(this.updateProject$);
    this.state.hold(this.saveClickEvent$);
    this.state.hold(
      this.close.pipe(tap(() => this.mainForm.reset(NOT_EMITTABLE))),
    );
    this.state.hold(this.enableDisableMainForm$);
    this.state.set(initState);
  }
}
