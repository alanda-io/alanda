import { Component, OnInit, Input } from '@angular/core';
import { CheckList, CheckListItem, CheckListItemDefinition } from '../../../models/checklist.model';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { ChecklistApiService } from '../../../api/checklist.service';
import { switchMap, catchError, retry, finalize } from 'rxjs/operators';
import { MessageService } from 'primeng/api';
import { throwError } from 'rxjs';
import {Router} from '@angular/router';

@Component({
  selector: 'alanda-checklist',
  templateUrl: './checklist.component.html',
  styleUrls: ['./checklist.component.scss']
})
export class AlandaChecklistComponent implements OnInit {

  @Input() checklist: CheckList;
  mandatoryChecks: CheckListItem[] = [];
  optionalChecks: CheckListItem[] = [];
  completedTasks = 0;
  formGroup: FormGroup = new FormGroup({});

  constructor(private readonly fb: FormBuilder,
    private readonly checklistAPI: ChecklistApiService,
    private readonly messageService: MessageService,
    private readonly router: Router) {
  }

  ngOnInit(): void {
    this._loadChecks();
  }

  goToAdministration() {
    this.router.navigate(['/checklist-administration']);
  }

  addItem(name: string, required: boolean): void {
    if (!name || !name.trim().length) {
      return;
    }
    if (this.checklist.checkListItems.filter(val => val.definition.displayText.toLowerCase().trim() === name.toLowerCase().trim()).length) {
      return;
    }
    const item: CheckListItemDefinition = {
      custom: true,
      displayText: name,
      key: name.toLowerCase().trim(),
      required: required
    };
    this.checklistAPI.addCheckListItemToCheckList(this.checklist.id, item)
    .subscribe(res => {
      this.checklist.checkListItems.push({status: false, definition: item});
      this._loadChecks();
    }, error => {
      this.messageService.add({severity: 'error', summary: 'New check', detail: 'Could not create new check'});
    });
  }

  removeItem(item: CheckListItem): void {
    this.checklistAPI.removeCheckListItemFromCheckList(this.checklist.id, item.definition.key).subscribe(res => {
      const index = this.checklist.checkListItems.indexOf(item);
      this.checklist.checkListItems.splice(index, 1);
      this._loadChecks();
    }, error => {
      this.messageService.add({severity: 'error', summary: 'Remove check', detail: 'Could not remove check'});
    });
  }

  private _loadChecks(): void {
    this.mandatoryChecks = this.checklist.checkListItems.filter(item => item.definition.required);
    this.optionalChecks = this.checklist.checkListItems.filter(item => !item.definition.required);
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
