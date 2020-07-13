import { NgModule } from '@angular/core';
import { PrepareVacationRequestComponent } from './forms/prepare-vacation-request.component';
import { CheckVacationRequestComponent } from './forms/check-vacation-request.component';
import { ModifyVacationRequestComponent } from './forms/modify-vacation-request.component';
import { DefaultTaskComponent } from './forms/default-task-template.component';
import { VacationRoutingModule } from './vacation-routing.module';
import { SharedModule } from '../../shared/shared.module';
import { InformSubstituteComponent } from './forms/inform-substitute.component';
import { PerformHandoverActivitiesComponent } from './forms/perform-handover-activities.component';
import { CommentsModule } from '@alanda/common';

@NgModule({
  imports: [VacationRoutingModule, SharedModule, CommentsModule],
  declarations: [
    PrepareVacationRequestComponent,
    CheckVacationRequestComponent,
    ModifyVacationRequestComponent,
    InformSubstituteComponent,
    PerformHandoverActivitiesComponent,
    DefaultTaskComponent,
  ],
  exports: [],
  providers: [],
})
export class VacationModule {
  constructor() {}
}