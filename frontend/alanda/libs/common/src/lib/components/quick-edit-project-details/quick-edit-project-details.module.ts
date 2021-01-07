import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { QuickEditProjectDetailsComponent } from './quick-edit-project-details.component';
import { ReactiveFormsModule } from '@angular/forms';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { ButtonModule } from 'primeng/button';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { RippleModule } from 'primeng/ripple';

@NgModule({
  declarations: [QuickEditProjectDetailsComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    InputTextareaModule,
    ButtonModule,
    ProgressSpinnerModule,
    RippleModule,
  ],
  exports: [QuickEditProjectDetailsComponent],
})
export class QuickEditProjectDetailsModule {}
