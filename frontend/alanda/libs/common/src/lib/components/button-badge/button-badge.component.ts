import { Component, Input, Output, EventEmitter } from '@angular/core';
import { RxState } from '@rx-angular/state';

export enum BadgeColor {
  PRIMARY = 'p-badge-primary',
  SECONDARY = 'p-badge-secondary',
  GREEN = 'p-badge-success',
  BLUE = 'p-badge-info',
  YELLOW = 'p-badge-warning',
  RED = 'p-badge-danger',
}
export enum BadgeSize {
  DEFAULT = '',
  LARGE = 'p-badge-lg',
  EXTRA_LARGE = 'p-badge-xl',
}
export enum PositionType {
  RIGHT = 'right',
  LEFT = 'left',
}
export interface BadgeConfig {
  value: string;
  color: BadgeColor;
  size: BadgeSize;
}
export interface IconButtonConfig {
  icon: string;
  position: PositionType;
}
interface ButtonBadgeState {
  label: string;
  disabled: boolean;
  iconConfig: IconButtonConfig;
  badgeConfig: BadgeConfig;
}
const initState = {
  disabled: false,
  label: '',
  iconConfig: {
    icon: '',
    position: PositionType.LEFT,
  },
  badgeConfig: {
    value: '',
    color: BadgeColor.YELLOW,
    size: BadgeSize.DEFAULT,
  },
};
@Component({
  selector: 'alanda-button-badge',
  templateUrl: './button-badge.component.html',
})
export class AlandaButtonBadgeComponent {
  state$ = this.state.select();

  @Input() set label(label: string) {
    this.state.set({ label });
  }

  @Input() set iconConfig(config: Partial<IconButtonConfig>) {
    this.state.set({ iconConfig: this.merge(initState.iconConfig, config) });
  }

  @Input() set badgeConfig(config: Partial<BadgeConfig>) {
    this.state.set({ badgeConfig: this.merge(initState.badgeConfig, config) });
  }

  @Output() click = new EventEmitter();

  constructor(private state: RxState<ButtonBadgeState>) {
    this.state.set(initState);
  }

  merge(allValues: any, partial: any): any {
    return { ...allValues, ...partial };
  }
}
