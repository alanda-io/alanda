import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AlandaPhaseTabComponent } from './phase-tab.component';
import { MenuModule } from 'primeng/menu';
import { TemplateModule } from '@rx-angular/template';
import { TabViewModule } from 'primeng/tabview';
import { TooltipModule } from 'primeng/tooltip';

@NgModule({
  imports: [
    CommonModule,
    MenuModule,
    TemplateModule,
    TabViewModule,
    TooltipModule,
  ],
  declarations: [AlandaPhaseTabComponent],
  exports: [AlandaPhaseTabComponent],
})
export class PhaseTabModule {}
