import { Component, OnInit, Input } from '@angular/core';
import { AlandaTaskApiService } from '../../../api/taskApi.service';

@Component({
  selector: 'alanda-var-display',
  templateUrl: './var-display.component.html',
  styleUrls: [],
})
export class AlandaVarDisplayComponent implements OnInit {
  @Input() variableName: string;
  @Input() task: any;
  @Input() label: string;
  @Input() type?: string;

  value: any;

  constructor(
    private readonly taskService: AlandaTaskApiService,
  ) {}

  ngOnInit(): void {
    if (this.type === '') {
      this.type = 'String';
    }
    this.taskService
      .getVariable(this.task.task_id, this.variableName)
      .subscribe((resp) => {
        this.value = resp.value;
      });
  }
}
