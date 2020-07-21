import { NgModule } from '@angular/core';
import { CardModule } from 'primeng/card';
import { FieldsetModule } from 'primeng/fieldset';
import { CommonModule } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { AlandaCommonModule } from '@alanda/common';
import { ReactiveFormsModule } from '@angular/forms';
import { PanelModule } from 'primeng/panel';
import { MessageModule } from 'primeng/message';

@NgModule({
  imports: [
    CommonModule,
    CardModule,
    FieldsetModule,
    ButtonModule,
    PanelModule,
    AlandaCommonModule,
    MessageModule,
    ReactiveFormsModule,
  ],
  exports: [
    CommonModule,
    CardModule,
    FieldsetModule,
    ButtonModule,
    MessageModule,
    AlandaCommonModule,
    ReactiveFormsModule,
    PanelModule,
  ],
  declarations: [],
})
export class SharedModule {
  constructor() {}
}
