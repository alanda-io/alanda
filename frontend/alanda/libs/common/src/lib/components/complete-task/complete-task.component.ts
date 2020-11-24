import { Component, EventEmitter, Input, Output } from '@angular/core';

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
  @Output() complete: EventEmitter<Event> = new EventEmitter();

  constructor() {}
}
