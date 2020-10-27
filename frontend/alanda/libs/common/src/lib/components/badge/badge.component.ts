import { Component, Input } from '@angular/core';

enum ValueStyleMapper {
  ACTIVE = 'p-tag-success',
  CANCELED = 'p-tag-danger',
  DELETED = 'p-tag-danger',
  NEW = 'p-tag-info',
  NORMAL = 'p-tag',
}

@Component({
  selector: 'alanda-badge',
  templateUrl: './badge.component.html',
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
