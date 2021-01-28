import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AlandaTaskFormService } from '../../form/alanda-task-form.service';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'alanda-complete-task',
  templateUrl: './complete-task.component.html',
  styleUrls: ['./complete-task.component.scss'],
})
export class CompleteTaskComponent {
  @Input() disabled: boolean;
  @Input() icon = 'pi pi-check';
  @Input() label = 'Complete Task';
  @Input() buttonClass = 'p-button-success';
  @Input() fluid = true;
  @Output() complete: EventEmitter<Event> = new EventEmitter();

  state$ = this.taskFormService.select();

  constructor(private taskFormService: AlandaTaskFormService) {}
}
