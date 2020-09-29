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
  AlandaProjectPropertiesService,
} from '@alanda/common';
import { ProjectPropertiesComponent } from './components/project-properties/project-properties.component';
import { ProjectPhasesComponent } from './components/project-phases/project-phases.component';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { FieldsetModule } from 'primeng/fieldset';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';

@NgModule({
  imports: [
    VacationRoutingModule,
    AlandaCommonModule,
    CardModule,
    FieldsetModule,
    CommonModule,
    ButtonModule,
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
  ],
})
export class VacationModule {
  constructor(private propertiesService: AlandaProjectPropertiesService) {
    this.propertiesService.addPropsForType(
      'VACATION',
      ProjectPropertiesComponent,
    );
    // this.propertiesService.addPropsForType('VACATION', ProjectPhasesComponent);
  }
}
