import { Component, OnInit, Input } from '@angular/core';
import { CheckList, CheckListItem } from '../../../models/checklist.model';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { ChecklistApiService } from '../../../api/checklist.service';
import { switchMap, catchError, retry } from 'rxjs/operators';
import { MessageService } from 'primeng/api';
import { throwError } from 'rxjs';

@Component({
  selector: 'alanda-checklist',
  templateUrl: './checklist.component.html',
  styleUrls: ['./checklist.component.scss']
})
export class AlandaChecklistComponent implements OnInit {

  @Input() checklist: CheckList;
  completedTasks = 0;
  formGroup: FormGroup = new FormGroup({});

  constructor(private readonly fb: FormBuilder,
    private readonly checklistAPI: ChecklistApiService,
    private readonly messageService: MessageService) {
  }

  ngOnInit(): void {
    this.formGroup = this._getFormGroup();
    (<any>Object).entries(this.formGroup.controls).forEach(([key, formGroup]) => {
      formGroup.valueChanges.pipe(
        switchMap((v: any) => this.checklistAPI.setCheckListItemStatus(this.checklist.id, key, v.status).pipe(
          catchError(err => {
            this.formGroup.get(key).setValue({status: !v.status}, {emitEvent: false});
            this.messageService.add({severity: 'error', summary: 'Update Checklist', detail: 'Could not update check'});
            return throwError(err);
          }),
        )),
        retry()
      ).subscribe(res => {
          this.messageService.add({severity: 'success', summary: 'Update Checklist', detail: 'Check has been updated'});
          this.completedTasks = (<any>Object).values(this.formGroup.controls).filter(control => control.value.status).length;
      });
    });
  }

  private _getFormGroup(): FormGroup {
    const formControls = this.checklist.checkListItems.reduce((controls, item: CheckListItem) => {
      controls[item.definition.key] = this.fb.group({status: [item.status, item.definition.required ? Validators.required : null]});
      return controls;
    }, {});

    return this.fb.group(formControls);
  }





}
