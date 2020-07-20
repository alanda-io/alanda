import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AlandaPhaseTabComponent } from './phase-tab.component';
import { MenuModule } from 'primeng/menu';
import { TemplateModule } from '@rx-angular/template';

@NgModule({
  imports: [CommonModule, MenuModule, TemplateModule],
  declarations: [AlandaPhaseTabComponent],
  exports: [AlandaPhaseTabComponent],
})
export class PhaseTabModule {}
