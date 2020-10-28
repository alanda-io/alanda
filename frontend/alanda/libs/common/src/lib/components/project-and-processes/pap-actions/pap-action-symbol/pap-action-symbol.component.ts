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
        color: #f44336;
      }
      .success {
        color: #689f38;
      }
      .pi-lg {
        font-size: 1.35rem;
      }
    </style>
    <ng-container *ngIf="!readOnly" [ngSwitch]="type">
      <span
        *ngSwitchCase="'OPTIONS'"
        class="pi pi-cog pi-lg info"
        (click)="clicked.emit($event)"
      ></span>
      <span
        *ngSwitchCase="'CANCEL'"
        class="pi pi-pause pi-lg danger"
        (click)="clicked.emit($event)"
      ></span>
      <span
        *ngSwitchCase="'REMOVE'"
        class="pi pi-window-minimize pi-lg danger"
        (click)="clicked.emit($event)"
      ></span>
      <span
        *ngSwitchCase="'START'"
        class="pi pi-play pi-lg success"
        (click)="clicked.emit($event)"
      ></span>
      <span
        *ngSwitchCase="'INFO'"
        class="pi pi-info-circle pi-lg danger"
        (click)="clicked.emit($event)"
      ></span>
    </ng-container>
    <ng-container *ngIf="readOnly" [ngSwitch]="type">
      <span *ngSwitchCase="'OPTIONS'" class="pi pi-cog pi-lg"></span>
      <span *ngSwitchCase="'CANCEL'" class="pi pi-pause pi-lg"></span>
      <span *ngSwitchCase="'REMOVE'" class="pi pi-window-minimize pi-lg"></span>
      <span *ngSwitchCase="'START'" class="pi pi-play pi-lg"></span>
      <span
        *ngSwitchCase="'INFO'"
        class="pi pi-info-circle pi-lg"
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
