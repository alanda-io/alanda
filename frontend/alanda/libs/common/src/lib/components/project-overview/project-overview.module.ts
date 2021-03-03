import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProjectOverviewComponent } from './project-overview.component';
import { ReactiveFormsModule } from '@angular/forms';
import { PanelModule } from 'primeng/panel';
import { DirectivesModule } from '../../directives/directives.module';
import { LabelModule } from '../label/label.module';
import { InputTextModule } from 'primeng/inputtext';
import { DropdownModule } from 'primeng/dropdown';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { CalendarModule } from 'primeng/calendar';
import { InputTextareaModule } from 'primeng/inputtextarea';

@NgModule({
  declarations: [ProjectOverviewComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    PanelModule,
    DirectivesModule,
    LabelModule,
    InputTextModule,
    DropdownModule,
    AutoCompleteModule,
    CalendarModule,
    InputTextareaModule,
  ],
  exports: [ProjectOverviewComponent],
})
export class ProjectOverviewModule {}
