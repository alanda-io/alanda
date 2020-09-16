import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AlandaPioComponent } from './pio.component';
import { DiagramComponent } from './diagram/diagram.component';
import { ProcessActivitiesComponent } from './process-activities/process-activities.component';
import { PanelModule } from 'primeng/panel';
import { TableModule } from 'primeng/table';
import { InputTextModule } from 'primeng/inputtext';

@NgModule({
  declarations: [
    AlandaPioComponent,
    DiagramComponent,
    ProcessActivitiesComponent,
  ],
  imports: [CommonModule, PanelModule, TableModule, InputTextModule],
  exports: [AlandaPioComponent, DiagramComponent, ProcessActivitiesComponent],
})
export class PioModule {}
