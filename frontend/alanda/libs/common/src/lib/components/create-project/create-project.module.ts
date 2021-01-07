import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AlandaCreateProjectComponent } from './create-project.component';
import { CardModule } from 'primeng/card';
import { DropdownModule } from 'primeng/dropdown';
import { CalendarModule } from 'primeng/calendar';
import { InputTextModule } from 'primeng/inputtext';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { LabelModule } from '../label/label.module';
import { RippleModule } from 'primeng/ripple';

@NgModule({
  declarations: [AlandaCreateProjectComponent],
  imports: [
    CommonModule,
    CardModule,
    DropdownModule,
    CalendarModule,
    InputTextModule,
    InputTextareaModule,
    ButtonModule,
    DialogModule,
    ProgressSpinnerModule,
    FormsModule,
    RouterModule,
    ReactiveFormsModule,
    AutoCompleteModule,
    LabelModule,
    RippleModule,
  ],
  exports: [AlandaCreateProjectComponent],
})
export class CreateProjectModule {}
