import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AlandaBadgeComponent } from './badge.component';

@NgModule({
  declarations: [AlandaBadgeComponent],
  imports: [CommonModule],
  exports: [AlandaBadgeComponent],
})
export class BadgeModule {}
