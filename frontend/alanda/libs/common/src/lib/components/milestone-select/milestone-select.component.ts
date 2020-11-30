import { Component, Input, Inject } from '@angular/core';
import { AlandaProject } from '../../api/models/project';
import {
  FormGroup,
  FormBuilder,
  Validators,
  AbstractControl,
} from '@angular/forms';
import { AlandaMilestoneApiService } from '../../api/milestoneApi.service';
import { convertUTCDate } from '../../utils/helper-functions';
import { AlandaUser } from '../../api/models/user';
import { APP_CONFIG, AppSettings } from '../../models/appSettings';
import { PERMISSION_PLACEHOLDER, Authorizations } from '../../permissions';
import {
  combineLatest,
  EMPTY,
  forkJoin,
  Observable,
  of,
  Subject,
  zip,
} from 'rxjs';
import { RxState } from '@rx-angular/state';
import {
  catchError,
  debounce,
  debounceTime,
  filter,
  map,
  startWith,
  switchMap,
  tap,
} from 'rxjs/operators';
import { AlandaMilestone } from '../../api/models/milestone';
import { AlandaCommentApiService } from '../../api/commentApi.service';
import { MessageService } from 'primeng/api';
import { LocaleSettings } from 'primeng/calendar';

interface MilestoneState {
  project: AlandaProject;
  processInstanceId: string;
  msName: string;
  user: AlandaUser;
  fc: Date;
  act: Date;
  commentRequired: boolean;
  showCommentModal: boolean;
  permissionString: string;
  commentMsType: string;
  commentLabel: string;
  commentButtonLabel: string;
  commentPlaceholder: string;
  commentSubject: string;
  comment: string;
}

const initState = {
  permissionString: null,
  showCommentModal: false,
};
@Component({
  selector: 'alanda-milestone-select',
  templateUrl: './milestone-select.component.html',
  styleUrls: ['./milestone-select.component.scss'],
  providers: [RxState],
})
export class AlandaSelectMilestoneComponent {
  @Input() displayName: string;
  @Input() dateFormat: string;
  @Input() disabled = false;
  @Input() showFC = true;
  @Input() showACT = true;
  @Input() delFC = false;
  @Input() delACT = false;
  @Input() fcCommentRequired = false;
  @Input() actCommentRequired = false;

  @Input() set project(project: AlandaProject) {
    this.state.set({ project });
  }

  @Input() set msName(msName: string) {
    this.state.set({ msName });
  }

  @Input() set permissionString(permissionString: string) {
    this.state.set({ permissionString });
  }
  @Input()
  set rootFormGroup(rootFormGroup: FormGroup) {
    if (rootFormGroup) {
      rootFormGroup.addControl(this.displayName, this.milestoneForm);
    }
  }

  @Input() set user(user: AlandaUser) {
    this.state.set({ user });
  }

  locale: LocaleSettings;

  state$ = this.state
    .select()
    .pipe(filter((state) => state.project != null && state.msName != null));

  clickForComment = new Subject<any>();
  saveFromComment = new Subject<any>();

  milestoneForm = this.fb.group({
    fc: [{ value: null, disabled: this.disabled }],
    act: [{ value: null, disabled: this.disabled }],
  });

  commentForm = this.fb.group({
    comment: [null, Validators.required],
    commentFormDate: [null, Validators.required],
  });

  updateFcForm$ = this.state
    .select('fc')
    .pipe(
      tap((fc) =>
        this.milestoneForm.get('fc').patchValue(fc, { emitEvent: false }),
      ),
    );

  updateActForm$ = this.state
    .select('act')
    .pipe(
      tap((act) =>
        this.milestoneForm.get('act').patchValue(act, { emitEvent: false }),
      ),
    );

  handleClickForComment$ = this.clickForComment.pipe(
    map((val) => {
      this.commentForm.get('comment').reset();
      const comment = null;
      const commentMsType = val.ms;
      let commentLabel = 'Reason';
      let commentButtonLabel = 'Reschedule';
      let commentPlaceholder = 'Reason for rescheduling of the Milestone';
      let commentSubject = null;
      if (val.ms === 'ACT' && this.milestoneForm.get('act').value === null) {
        // it is the first time the act milestone is set
        commentLabel = 'Comment';
        commentButtonLabel = 'Post';
        commentPlaceholder =
          'Write a comment to inform partners of your milestone choice';
        commentSubject = `#${this.state.get('msName')}`;
      } else {
        this.commentForm
          .get('commentFormDate')
          .patchValue(
            val.ms === 'FC' ? this.state.get('fc') : this.state.get('act'),
            { emitEvent: false },
          );
      }
      return {
        commentMsType,
        commentLabel,
        commentButtonLabel,
        commentPlaceholder,
        commentSubject,
        comment,
        showCommentModal: true,
      };
    }),
  );

  processInstanceId$ = this.state.select('project').pipe(
    map((project) => {
      return project.processes.find((process) => process.relation === 'MAIN')
        .processInstanceId;
    }),
  );

  loadMilestone$ = combineLatest([
    this.state.select('project'),
    this.state.select('msName'),
  ]).pipe(
    switchMap(([project, msName]) => {
      return this.milestoneService.getByProjectAndMsIdName(
        project.projectId,
        msName,
      );
    }),
    map((ms: AlandaMilestone) => {
      const fc = ms?.fc ? new Date(ms.fc) : null;
      const act = ms?.act ? new Date(ms.act) : null;
      return { fc: fc, act: act };
    }),
  );

  saveMileStones$ = this.milestoneForm.valueChanges.pipe(
    map((value) => [
      this.milestoneForm.get('fc').value,
      this.milestoneForm.get('act').value,
    ]),
    filter(([fc, act]) => fc !== null || act !== null),
    debounceTime(300),
    switchMap(([fc, act]) => {
      const reason = this.state.get('comment');
      return zip(
        of(fc),
        of(act),
        this.saveMileStone(fc, act, reason).pipe(
          catchError((error) => {
            this.messageService.add({
              severity: 'error',
              summary: 'Set Milestone failed',
              detail: `The milestone ${this.displayName} could not been set! ${error.statusText}`,
            });
            return EMPTY;
          }),
        ),
      );
    }),
    tap(([fc, act, voidResponse]) => this.state.set({ fc, act })),
  );

  updatePermissions$ = combineLatest([
    this.state.select('project'),
    this.state.select('user'),
    this.state.select('msName'),
    this.state.select('permissionString'),
  ]).pipe(
    tap(([project, user, msName, permissionString]) => {
      if (
        !Authorizations.hasPermission(
          user,
          this.getPermissionString(project, msName, permissionString, 'fc'),
        )
      ) {
        this.milestoneForm.get('fc').disable();
      }
      if (
        !Authorizations.hasPermission(
          user,
          this.getPermissionString(project, msName, permissionString, 'act'),
        )
      ) {
        this.milestoneForm.get('act').disable();
      }
    }),
  );

  handleSaveFromComment$ = this.saveFromComment.pipe(
    switchMap(({ msType, commentFormDate, comment }) => {
      let { fc, act } = this.state.get();
      const { commentSubject, processInstanceId } = this.state.get();
      if (msType === 'ACT' && this.state.get('commentLabel') === 'Comment') {
        // it was the first time setting ACT
        return zip(
          of(fc),
          of(commentFormDate),
          forkJoin([
            this.commentService.postComment({
              subject: commentSubject,
              text: comment,
              taskId: null,
              procInstId: processInstanceId,
            }),
            this.saveMileStone(fc, commentFormDate, null),
          ]),
        );
      } else {
        // all other cases
        if (msType === 'ACT') {
          act = commentFormDate;
        } else {
          fc = commentFormDate;
        }
        return zip(of(fc), of(act), this.saveMileStone(fc, act, comment));
      }
    }),
    map(([fc, act, callResponse]) => {
      return { fc, act, showCommentModal: false };
    }),
  );

  constructor(
    private state: RxState<MilestoneState>,
    private readonly milestoneService: AlandaMilestoneApiService,
    private commentService: AlandaCommentApiService,
    private readonly fb: FormBuilder,
    private messageService: MessageService,
    @Inject(APP_CONFIG) config: AppSettings,
  ) {
    this.state.set(initState);
    this.state.hold(this.updateFcForm$);
    this.state.hold(this.updateActForm$);
    this.state.connect(this.loadMilestone$);
    this.state.connect(this.handleClickForComment$);
    this.state.connect('processInstanceId', this.processInstanceId$);
    this.state.hold(this.updatePermissions$);
    this.state.hold(this.saveMileStones$);
    this.state.connect(this.handleSaveFromComment$);
    if (!this.dateFormat) {
      this.dateFormat = config.DATE_FORMAT_PRIME;
    }
    this.locale = config.LOCALE_PRIME;
  }

  saveMileStone(fc: Date, act: Date, reason: string): Observable<void> {
    const { project, msName } = this.state.get();
    const fcStr =
      fc != null ? convertUTCDate(fc).toISOString().substring(0, 10) : null;
    const actStr =
      act != null ? convertUTCDate(act).toISOString().substring(0, 10) : null;
    return this.milestoneService.updateByProjectAndMsIdName(
      project.projectId,
      msName,
      fcStr,
      actStr,
      reason,
      this.delFC,
      this.delACT,
    );
  }

  getPermissionString(
    project: AlandaProject,
    msName: string,
    permissionString: string,
    type?: string,
  ): string {
    if (permissionString) {
      return permissionString;
    }
    return `ms:${project.authBase.replace(
      PERMISSION_PLACEHOLDER,
      'write',
    )}:${msName}:${type}`;
  }

  get commentFormDate(): AbstractControl {
    return this.commentForm.get('commentFormDate');
  }

  get comment(): AbstractControl {
    return this.commentForm.get('comment');
  }
}
