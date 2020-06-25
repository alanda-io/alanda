import { NgModule } from '@angular/core';
import { CardModule } from 'primeng/card';
import { FieldsetModule } from 'primeng/fieldset';
import { CommonModule } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { AlandaCommonModule } from '@alanda-libraries/common';
import { ReactiveFormsModule } from '@angular/forms';
import { PanelModule } from 'primeng/panel';

@NgModule({
  imports: [
    CommonModule,
    CardModule,
    FieldsetModule,
    ButtonModule,
    PanelModule,
    AlandaCommonModule,
    ReactiveFormsModule,
  ],
  exports: [
    CommonModule,
    CardModule,
    FieldsetModule,
    ButtonModule,
    AlandaCommonModule,
    ReactiveFormsModule,
    PanelModule,
  ],
  declarations: []
})
export class SharedModule {
  constructor() {}
}
