import { Component, Input } from '@angular/core';

enum ValueStyleMapper {
  ACTIVE = 'alanda-badge-active',
  CANCELED = 'alanda-badge-canceled',
  DELETED = 'alanda-badge-deleted',
  NEW = 'alanda-badge-new',
  NORMAL = 'alanda-badge-normal',
}

@Component({
  selector: 'alanda-badge',
  templateUrl: './badge.component.html',
  styleUrls: ['./badge.component.scss'],
})
export class AlandaBadgeComponent {
  private _label: string;

  @Input() set label(label: string) {
    this._label = label;
  }
  get label(): string {
    return this._label;
  }

  getStyleClass(): string {
    const styleClass = ValueStyleMapper[this._label];
    return styleClass ? styleClass : ValueStyleMapper.NORMAL;
  }
}
