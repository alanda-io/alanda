import {Component, OnDestroy, OnInit} from '@angular/core';
import {ChecklistTemplateApiService} from '../../api/checklist-template.service';
import {CheckListItemDefinition, CheckListTemplate} from '../../models/checklist.model';
import {EMPTY, merge, Observable, Subject} from 'rxjs';
import {catchError, debounceTime, exhaustMap, finalize, mergeMap, switchMap, tap} from 'rxjs/operators';
import {MessageService} from 'primeng/api';
import {ProcessServiceNg} from '../../api/process.service';

@Component({
  selector: 'alanda-checklist-administration',
  templateUrl: './checklist-administration.component.html',
  styleUrls: ['./checklist-administration.component.scss']
})
export class AlandaChecklistAdministrationComponent implements OnInit, OnDestroy {

  templates: CheckListTemplate[] = [];
  selectedTemplate: CheckListTemplate;
  updateTemplate$: Subject<void> = new Subject<void>();
  selectedProcess: string;
  selectedTask: string;
  newTaskName: string;
  newTaskRequired = false;
  processes: string[] = [];
  userTasks: string[] = [];
  loading: boolean;

  constructor(private readonly templateAPI: ChecklistTemplateApiService,
              private readonly messageService: MessageService,
              private readonly processService: ProcessServiceNg) {
    this.updateTemplate$.pipe(
      tap(() => this.loading = true),
      exhaustMap(() => this.templateAPI.updateCheckListTemplate(this.selectedTemplate).pipe(
        catchError(err => {
          this.messageService.add({severity: 'error', summary: 'Update template', detail: 'Could not update template'});
          return EMPTY;
        }),
      )),
      finalize(() => this.loading = false)
    ).subscribe();
  }

  ngOnInit(): void {
    this.templateAPI.getAllCheckListTemplates().subscribe(templates => {
      this.templates = templates;
    });
  }

  searchProcess(event): void {
    this.processService.queryProcess(event.query).subscribe(res => {
      this.processes = res;
    });
  }

  searchTask(event): void {
    this.processService.queryUserTasks(this.selectedProcess, event.query).subscribe(res => {
      this.userTasks = res;
    });
  }

  addUserTask(event?: any): void {
    if (!event && this.selectedTemplate.userTasks.filter(ut => ut ===  this.selectedTask).length) {
      return;
    } else if (event) {
      this.selectedTemplate.userTasks.splice(this.selectedTemplate.userTasks.indexOf(event.value), 1);
    } else {
      this.selectedTemplate.userTasks.push(this.selectedTask);
    }
  }

  addItem(name: string, required: boolean): void {
    console.log("required", required);
    if (this.loading || !name.trim().length) {
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
    this.selectedTemplate.itemDefinitions.push(item);
    this.newTaskName = '';
  }

  ngOnDestroy(): void {
    this.updateTemplate$.unsubscribe();
  }
}
