import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { AlandaProjectHeaderComponent } from './project-header.component';
import { FilterPipeModule } from '../../pipes/filter.pipe';
import { PanelModule } from 'primeng/panel';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { CalendarModule } from 'primeng/calendar';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { DropdownModule } from 'primeng/dropdown';
import { ButtonModule } from 'primeng/button';
import { DirectivesModule } from '../../directives/directives.module';
import { LabelModule } from '../label/label.module';

@NgModule({
  imports: [
    CommonModule,
    PanelModule,
    RouterModule,
    FilterPipeModule,
    ReactiveFormsModule,
    DropdownModule,
    AutoCompleteModule,
    CalendarModule,
    InputTextModule,
    InputTextareaModule,
    DialogModule,
    ButtonModule,
    DirectivesModule,
    LabelModule,
  ],
  declarations: [AlandaProjectHeaderComponent],
  exports: [AlandaProjectHeaderComponent],
})
export class ProjectHeaderModule {}
