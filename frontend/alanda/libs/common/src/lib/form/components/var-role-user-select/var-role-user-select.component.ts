import { Component, OnInit, Input } from '@angular/core';
import {
  FormGroup,
  FormBuilder,
  AbstractControl,
} from '@angular/forms';
import { AlandaTaskApiService } from '../../../api/taskApi.service';
import { AlandaUserApiService } from '../../../api/userApi.service';
import { AlandaUser } from '../../../api/models/user';
import { tap, concatMap } from 'rxjs/operators';

const SELECTOR = 'alanda-var-role-user-select';

@Component({
  selector: SELECTOR,
  templateUrl: './var-role-user-select.component.html',
  styleUrls: [],
})
export class AlandaVarRoleUserSelectComponent implements OnInit {
  @Input() variableName: string;
  @Input() task: any;
  @Input() label: string;
  @Input() role: string;
  items: AlandaUser[];
  type = 'long';

  @Input()
  set rootFormGroup(rootFormGroup: FormGroup) {
    if (rootFormGroup != null) {
      rootFormGroup.addControl(
        `${SELECTOR}-${this.variableName}`,
        this.selectForm
      );
    }
  }

  selectForm = this.fb.group({
    selected: [null],
  });

  constructor(
    private readonly taskService: AlandaTaskApiService,
    private readonly userService: AlandaUserApiService,
    private readonly fb: FormBuilder
  ) {
    this.items = [];
  }

  ngOnInit() {
    this.userService
      .searchUsers('', this.role)
      .pipe(
        tap((ret) => {
          this.items = ret;
        }),
        concatMap((ret) => {
          return this.taskService.getVariable(
            this.task.task_id,
            this.variableName
          );
        })
      )
      .subscribe((resp) => {
        const user = this.items.find((item) => {
          return item.guid === resp.value;
        });
        this.selected.setValue(user);
        console.log(this.selected.value);
      });
  }

  save() {
    this.taskService
      .setVariable(this.task.task_id, this.variableName, {
        value: this.selected.value.guid,
        type: this.type,
      })
      .subscribe();
  }

  get selected(): AbstractControl {
    return this.selectForm.get('selected');
  }
}
