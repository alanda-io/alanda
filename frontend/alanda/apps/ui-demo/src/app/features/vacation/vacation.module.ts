import { NgModule } from '@angular/core';
import { PrepareVacationRequestComponent } from './forms/prepare-vacation-request.component';
import { CheckVacationRequestComponent } from './forms/check-vacation-request.component';
import { ModifyVacationRequestComponent } from './forms/modify-vacation-request.component';
import { DefaultTaskComponent } from './forms/default-task-template.component';
import { VacationRoutingModule } from './vacation-routing.module';
import { InformSubstituteComponent } from './forms/inform-substitute.component';
import { PerformHandoverActivitiesComponent } from './forms/perform-handover-activities.component';
import {
  AlandaCommonModule,
  CompleteTaskModule,
  FormModule,
} from '@alanda/common';
import { ProjectPropertiesComponent } from './components/project-properties/project-properties.component';
import { ProjectPhasesComponent } from './components/project-phases/project-phases.component';
import { InputTextModule } from 'primeng/inputtext';
import { VacationProjectDetailsComponent } from './components/vacation-project-details/vacation-project-details.component';
import { CommonModule } from '@angular/common';
import { VacationPageHeaderComponent } from './components/vacation-page-header/vacation-page-header.component';
import { ReactiveFormsModule } from '@angular/forms';

@NgModule({
  imports: [
    VacationRoutingModule,
    AlandaCommonModule,
    InputTextModule,
    CompleteTaskModule,
    FormModule,
    CommonModule,
    ReactiveFormsModule,
  ],
  declarations: [
    PrepareVacationRequestComponent,
    CheckVacationRequestComponent,
    ModifyVacationRequestComponent,
    InformSubstituteComponent,
    PerformHandoverActivitiesComponent,
    DefaultTaskComponent,
    ProjectPropertiesComponent,
    ProjectPhasesComponent,
    VacationProjectDetailsComponent,
    VacationPageHeaderComponent,
  ],
  exports: [ProjectPropertiesComponent],
})
export class VacationModule {}
