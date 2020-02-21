import { NgModule } from '@angular/core';
import { AlandaCommonModule } from 'projects/alanda-common/src/public_api';
import { CardModule } from 'primeng/card';
import { FieldsetModule } from 'primeng/fieldset';
import { CommonModule } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { VacationProjectDetailsComponent } from './components/project-details/vacation-project-details.component';
import { PrepareVacationRequestComponent } from './forms/prepare-vacation-request.component';
import { CheckVacationRequestComponent } from './forms/check-vacation-request.component';
import { ModifyVacationRequestComponent } from './forms/modify-vacation-request.component';
import { ProjectPropertiesVacationComponent } from './components/project-properties/project.properties.vacation.component';
import { DefaultTaskComponent } from './forms/default-task-template.component';
import { VacationRoutingModule } from './vacation-routing.module';

@NgModule({
  imports: [
    AlandaCommonModule,
    CardModule,
    FieldsetModule,
    CommonModule,
    ButtonModule,
    VacationRoutingModule
  ],
  declarations: [
    VacationProjectDetailsComponent,
    PrepareVacationRequestComponent,
    CheckVacationRequestComponent,
    ModifyVacationRequestComponent,
    ProjectPropertiesVacationComponent,
    DefaultTaskComponent
  ],
/*   exports: [
    VacationProjectDetailsComponent,
    PrepareVacationRequestComponent,
    CheckVacationRequestComponent,
    ModifyVacationRequestComponent,
    ProjectPropertiesVacationComponent,
    FieldsetModule,
    CardModule,
    ButtonModule,
  ], */
  providers: [],
  entryComponents: [
    VacationProjectDetailsComponent,
    PrepareVacationRequestComponent,
    CheckVacationRequestComponent,
    ModifyVacationRequestComponent,
    ProjectPropertiesVacationComponent,
    DefaultTaskComponent
  ]
})
export class VacationModule {
  constructor() {}
}
