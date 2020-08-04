import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { MenuItem } from 'primeng/api/menuitem';

type Actions =
  | 'CANCEL-PROJECT'
  | 'CANCEL-PROCESS'
  | 'START-SUBPROCESS'
  | 'REMOVE-SUBPROCESS'
  | 'CONFIGURE-SUBPROCESS'
  | 'CREATE-SUBPROJECT'
  | 'RELATE-SUBPROJECT'
  | 'RELATE-ME-TO'
  | 'UNRELATE-ME'
  | 'MOVE-ME-TO'
  | 'RELATE-PROJECTS';

export interface PapConfigValues {
  readOnly?: boolean;
  display?: boolean;
}

export type PapActionConfig = {
  [action in Actions]?: PapConfigValues;
};

@Component({
  selector: 'alanda-pap-actions',
  templateUrl: './pap-actions.component.html',
  styleUrls: ['./pap-actions.component.css'],
})
export class PapActionsComponent implements OnInit {
  @Input() config: PapActionConfig = {};
  @Input() disabled: boolean;
  @Input() status: string;

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

  optionItems: MenuItem[] = [];
  projectTypeConfig: any = {};

  constructor() {}

  ngOnInit() {
    this.optionItems = [
      {
        label: 'Create subproject',
        icon: 'fa fa-angle-right',
        visible: this.config['CREATE-SUBPROJECT']
          ? this.config['CREATE-SUBPROJECT'].display
          : false,
        command: (onclick) => this.createSubproject.emit(),
      },
      {
        label: 'Relate subproject',
        icon: 'fa fa-angle-right',
        visible: this.config['RELATE-SUBPROJECT']
          ? this.config['RELATE-SUBPROJECT'].display
          : false,
        command: (onclick) => this.relateSubproject.emit(),
      },
      {
        label: 'Relate me to',
        icon: 'fa fa-angle-right',
        visible: this.config['RELATE-ME-TO']
          ? this.config['RELATE-ME-TO'].display
          : false,
        command: (onclick) => this.relateMeTo.emit(),
      },
      {
        label: 'Unrelate me',
        icon: 'fa fa-angle-right',
        visible: this.config['UNRELATE-ME']
          ? this.config['UNRELATE-ME'].display
          : false,
        command: (onclick) => this.unrelateMe.emit(),
      },
      {
        label: 'Move me to',
        icon: 'fa fa-angle-right',
        visible: this.config['MOVE-ME-TO']
          ? this.config['MOVE-ME-TO'].display
          : false,
        command: (onclick) => this.moveMeTo.emit(),
      },
    ];
  }
}
