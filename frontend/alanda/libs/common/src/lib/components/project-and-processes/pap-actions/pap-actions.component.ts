import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { MenuItem } from 'primeng/api/menuitem';
import { PapActions } from '../project-and-processes.service';

@Component({
  selector: 'alanda-pap-actions',
  templateUrl: './pap-actions.component.html',
  styleUrls: ['./pap-actions.component.css'],
})
export class PapActionsComponent implements OnInit {
  @Input() disabled: boolean;
  @Input() actions: PapActions[] = [];
  @Input() status: string;
  @Input() loading: boolean;
  @Input() readOnly: boolean;
  @Output() createSubproject: EventEmitter<void> = new EventEmitter();
  @Output() relateSubproject: EventEmitter<void> = new EventEmitter();
  @Output() relateMeTo: EventEmitter<void> = new EventEmitter();
  @Output() unrelateMe: EventEmitter<void> = new EventEmitter();
  @Output() moveMeTo: EventEmitter<void> = new EventEmitter();
  @Output() cancelProject: EventEmitter<void> = new EventEmitter();
  @Output() cancelProcess: EventEmitter<void> = new EventEmitter();
  @Output() removeSubprocess: EventEmitter<void> = new EventEmitter();
  @Output() startSubprocess: EventEmitter<void> = new EventEmitter();
  @Output() configureProcess: EventEmitter<void> = new EventEmitter();

  menuItems: MenuItem[] = [];

  constructor() {}

  ngOnInit() {
    this.menuItems = [
      {
        label: 'Create subproject',
        icon: 'fa fa-angle-right',
        disabled: this.loading || this.readOnly,
        command: (onclick) => this.createSubproject.emit(),
      },
      {
        label: 'Relate subproject',
        icon: 'fa fa-angle-right',
        disabled: this.loading || this.readOnly,
        command: (onclick) => this.relateSubproject.emit(),
      },
      {
        label: 'Relate me to',
        icon: 'fa fa-angle-right',
        disabled: this.loading || this.readOnly,
        command: (onclick) => this.relateMeTo.emit(),
      },
      {
        label: 'Unrelate me',
        icon: 'fa fa-angle-right',
        disabled: this.loading || this.readOnly,
        command: (onclick) => this.unrelateMe.emit(),
      },
      {
        label: 'Move me to',
        icon: 'fa fa-angle-right',
        disabled: this.loading || this.readOnly,
        command: (onclick) => this.moveMeTo.emit(),
      },
    ];
  }
}
