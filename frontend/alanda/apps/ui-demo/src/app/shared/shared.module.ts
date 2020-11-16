import { NgModule } from '@angular/core';
import { CardModule } from 'primeng/card';
import { FieldsetModule } from 'primeng/fieldset';
import { CommonModule } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { AlandaCommonModule } from '@alanda/common';
import { ReactiveFormsModule } from '@angular/forms';
import { PanelModule } from 'primeng/panel';
import { MessageModule } from 'primeng/message';
import { CalendarModule } from 'primeng/calendar';

@NgModule({
  imports: [
    CommonModule,
    CardModule,
    FieldsetModule,
    CalendarModule,
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
    CalendarModule,
  ],
  declarations: [],
})
export class SharedModule {
  constructor() {}
}
