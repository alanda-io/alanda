import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { QuickEditProjectDetailsComponent } from './quick-edit-project-details.component';
import { ReactiveFormsModule } from '@angular/forms';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { ButtonModule } from 'primeng/button';
import { ProgressSpinnerModule } from 'primeng/progressspinner';

@NgModule({
  declarations: [QuickEditProjectDetailsComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    InputTextareaModule,
    ButtonModule,
    ProgressSpinnerModule,
  ],
  exports: [QuickEditProjectDetailsComponent],
})
export class QuickEditProjectDetailsModule {}
