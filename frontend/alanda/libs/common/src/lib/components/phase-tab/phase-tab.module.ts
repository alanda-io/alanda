import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AlandaPhaseTabComponent } from './phase-tab.component';
import { MenuModule } from 'primeng/menu';

@NgModule({
  imports: [CommonModule, MenuModule],
  declarations: [AlandaPhaseTabComponent],
  exports: [AlandaPhaseTabComponent],
})
export class PhaseTabModule {}
