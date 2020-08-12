import {Component, OnDestroy } from '@angular/core';
import {ChecklistTemplateApiService} from '../../api/checklist-template.service';
import {CheckListItemDefinition, CheckListTemplate} from '../../models/checklist.model';
import {EMPTY, merge, Observable, Subject} from 'rxjs';
import {catchError, debounceTime, switchMap} from 'rxjs/operators';
import {MessageService} from 'primeng/api';

@Component({
  selector: 'alanda-checklist-menu',
  templateUrl: './checklist-menu.component.html',
  styleUrls: ['./checklist-menu.component.scss']
})
export class AlandaChecklistMenuComponent implements OnDestroy {

  selectedTemplate: CheckListTemplate;
  updateCheck$: Subject<void> = new Subject<void>();
  updateOrder$: Subject<void> = new Subject<void>();
  updateRequired$: Subject<void> = new Subject<void>();

  constructor(readonly templateAPI: ChecklistTemplateApiService, private readonly messageService: MessageService) {
    this.updateCheck$.pipe(
      debounceTime(400),
      switchMap(() => this.updateTemplate(this.selectedTemplate))
    ).subscribe(() => this.messageService.add({severity: 'success', summary: 'Update Template', detail: 'Template has been updated'}));

    merge(this.updateRequired$, this.updateOrder$).pipe(
      switchMap(() => this.updateTemplate(this.selectedTemplate))
    ).subscribe();
  }

  ngOnDestroy(): void {
    this.updateCheck$.unsubscribe();
    this.updateOrder$.unsubscribe();
    this.updateRequired$.unsubscribe();
  }

  addItem(name: string, required: boolean): void {
    if (!name || !name.trim().length) {
      return;
    }
    if (this.selectedTemplate.itemDefinitions.filter(val => val.displayText.toLowerCase().trim() === name.toLowerCase().trim()).length) {
      return;
    }
    const item: CheckListItemDefinition = {
      custom: false,
      displayText: name,
      key: name.toLowerCase().trim(),
      required: required
    };
    const copyTemplate = JSON.parse(JSON.stringify(this.selectedTemplate));
    copyTemplate.itemDefinitions.push(item);
    this.updateTemplate(copyTemplate).subscribe(() => {
      this.messageService.add({severity: 'success', summary: 'Update Template', detail: 'Template has been updated'});
      this.selectedTemplate.itemDefinitions.push(item);
    });
  }

  private updateTemplate(template: CheckListTemplate): Observable<void> {
    return this.templateAPI.updateCheckListTemplate(template).pipe(
      catchError(err => {
        this.messageService.add({severity: 'error', summary: 'Update template', detail: 'Could not update template'});
        return EMPTY;
      })
    );
}


}
