import { Component, OnInit, Input } from '@angular/core';
import { PmcTask } from '../../models/pmcTask';
import { ChecklistApiService } from '../../api/checklist.service';
import { finalize } from 'rxjs/operators';
import { CheckList } from '../../models/checklist.model';

@Component({
  selector: 'alanda-checklist-panel',
  templateUrl: './checklist-panel.component.html',
  styleUrls: ['./checklist-panel.component.scss']
})
export class AlandaChecklistPanelComponent implements OnInit {

  constructor(readonly checklistAPI: ChecklistApiService) { }

  @Input() task: PmcTask;
  checklists: CheckList[] = [];
  loading: boolean;

  ngOnInit(): void {
    this.loadChecklists();
  }

  loadChecklists(collapsed?: boolean): void {
    if (collapsed) {
      return;
    }
    this.checklistAPI.getCheckListsForUserTaskInstance(this.task.process_instance_id)
    .pipe(
      finalize(() => this.loading = false)
    )
    .subscribe(res => this.checklists = res);
  }

}
