import { NgModule } from "@angular/core";
import { VacationProjectDetailsComponent } from "./components/project-details/vacation-project-details.component";
import { PrepareVacationRequestComponent } from "./components/task-forms/prepare-vacation-request.component";
import { CheckVacationRequestComponent } from "./components/task-forms/check-vacation-request.component";
import { ModifyVacationRequestComponent } from "./components/task-forms/modify-vacation-request.component";
import { PerformHandoverActivitiesComponent } from "./components/task-forms/perform-handover-activities.component";
import { InformSubstituteComponent } from "./components/task-forms/inform-substitute.component";
import { ProjectPropertiesVacationComponent } from "./components/project-properties/project.properties.vacation.component";
import { FieldsetModule } from "primeng/fieldset";
import { AlandaCommonModule } from "projects/alanda-common/src/public_api";
import { CardModule } from "primeng/card";
import { CommonModule } from "@angular/common";
import { ButtonModule } from "primeng/button";

@NgModule({
  imports:[
    AlandaCommonModule,
    CardModule,
    FieldsetModule,
    CommonModule,
    ButtonModule
  ],
  declarations: [
    VacationProjectDetailsComponent,
    PrepareVacationRequestComponent,
    CheckVacationRequestComponent,
    ModifyVacationRequestComponent,
    PerformHandoverActivitiesComponent,
    InformSubstituteComponent,
    ProjectPropertiesVacationComponent,
  ],
  exports: [
    VacationProjectDetailsComponent,
    PrepareVacationRequestComponent,
    CheckVacationRequestComponent,
    ModifyVacationRequestComponent,
    PerformHandoverActivitiesComponent,
    InformSubstituteComponent,
    ProjectPropertiesVacationComponent,
    FieldsetModule,
    CardModule,
    ButtonModule,
  ],
  providers: [],
  entryComponents: [
    VacationProjectDetailsComponent,
    PrepareVacationRequestComponent,
    CheckVacationRequestComponent,
    ModifyVacationRequestComponent,
    PerformHandoverActivitiesComponent,
    InformSubstituteComponent,
    ProjectPropertiesVacationComponent,
  ]
})
export class VacationModule { 

  constructor() {}
}
