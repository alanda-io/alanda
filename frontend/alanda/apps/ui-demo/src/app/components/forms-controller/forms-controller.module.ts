import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserEnrichedFormsControllerComponent } from './user-enriched-forms-controller.component';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { AlandaCommonModule } from '@alanda/common';
import { RouterModule } from '@angular/router';
import { MessageModule } from 'primeng/message';

@NgModule({
  declarations: [UserEnrichedFormsControllerComponent],
  imports: [
    CommonModule,
    ProgressSpinnerModule,
    AlandaCommonModule,
    RouterModule,
    MessageModule,
  ],
})
export class FormsControllerModule {}
