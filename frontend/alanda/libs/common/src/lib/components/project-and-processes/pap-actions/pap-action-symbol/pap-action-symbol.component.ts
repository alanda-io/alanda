import { Component, Input, Output, EventEmitter } from '@angular/core';

type ActionType = 'CONFIG' | 'CANCEL' | 'REMOVE' | 'START' | 'INFO';

@Component({
  selector: 'alanda-pap-action-symbol',
  template: `
    <style>
      span {
        padding-left: 7px;
        padding-right: 7px;
        font-size: 1.75rem;
      }
      span:hover {
        cursor: pointer;
      }
      .info {
        color: #116fbf;
      }
      .danger {
        color: #c22;
      }
      .success {
        color: #89ad4d;
      }
    </style>
    <ng-container *ngIf="!readOnly" [ngSwitch]="type">
      <span
        *ngSwitchCase="'OPTIONS'"
        class="fa fa-cog fa-lg info"
        (click)="clicked.emit($event)"
      ></span>
      <span
        *ngSwitchCase="'CANCEL'"
        class="fa fa-stop fa-lg danger"
        (click)="clicked.emit($event)"
      ></span>
      <span
        *ngSwitchCase="'REMOVE'"
        class="fa fa-window-close fa-lg danger"
        (click)="clicked.emit($event)"
      ></span>
      <span
        *ngSwitchCase="'START'"
        class="fa fa-play-circle fa-lg success"
        (click)="clicked.emit($event)"
      ></span>
      <span
        *ngSwitchCase="'INFO'"
        class="fa fa-info-circle fa-lg danger"
        (click)="clicked.emit($event)"
      ></span>
    </ng-container>
    <ng-container *ngIf="readOnly" [ngSwitch]="type">
      <span *ngSwitchCase="'OPTIONS'" class="fa fa-cog fa-lg"></span>
      <span *ngSwitchCase="'CANCEL'" class="fa fa-stop fa-lg"></span>
      <span *ngSwitchCase="'REMOVE'" class="fa fa-window-close fa-lg"></span>
      <span *ngSwitchCase="'START'" class="fa fa-play-circle fa-lg"></span>
      <span
        *ngSwitchCase="'INFO'"
        class="fa fa-info-circle fa-lg"
        (click)="clicked.emit($event)"
      ></span>
    </ng-container>
  `,
})
export class PapActionSymbolComponent {
  @Input() type: ActionType;
  @Input() readOnly: boolean;
  @Output() clicked: EventEmitter<MouseEvent> = new EventEmitter();
}
