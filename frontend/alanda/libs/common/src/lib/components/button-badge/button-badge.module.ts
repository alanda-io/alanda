import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AlandaButtonBadgeComponent } from './button-badge.component';
import { RxState } from '@rx-angular/state';
import { ButtonModule } from 'primeng/button';

@NgModule({
  declarations: [AlandaButtonBadgeComponent],
  imports: [CommonModule, ButtonModule],
  exports: [AlandaButtonBadgeComponent],
  providers: [RxState],
})
export class ButtonBadgeModule {}
