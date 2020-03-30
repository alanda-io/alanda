import { NgModule } from '@angular/core';
import { CardModule } from 'primeng/card';
import { FieldsetModule } from 'primeng/fieldset';
import { CommonModule } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { AlandaCommonModule } from 'projects/alanda-common/src/public_api';
;

@NgModule({
  imports: [
    CommonModule,
    CardModule,
    FieldsetModule,
    ButtonModule,
    AlandaCommonModule,
  ],
  exports: [
    CommonModule,
    CardModule,
    FieldsetModule,
    ButtonModule,
    AlandaCommonModule,
  ],
  declarations: []
})
export class SharedModule {

  constructor() {}

}
