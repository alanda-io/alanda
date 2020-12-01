import { Component, Input, Output, EventEmitter } from '@angular/core';

type ActionType = 'CONFIG' | 'CANCEL' | 'REMOVE' | 'START' | 'INFO';

@Component({
  selector: 'alanda-pap-action-symbol',
  templateUrl: './pap-action-symbol.component.html',
  styleUrls: ['./pap-action-symbol.component.scss'],
})
export class PapActionSymbolComponent {
  @Input() type: ActionType;
  @Input() readOnly: boolean;
  @Output() clicked: EventEmitter<MouseEvent> = new EventEmitter();
}
