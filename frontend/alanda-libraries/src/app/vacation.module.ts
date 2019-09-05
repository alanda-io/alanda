import { NgModule } from "@angular/core";
import { AlandaCommonModule } from "projects/alanda-common/src/public_api";
import { VacationProjectPropertiesService } from "./vacation/services/vacation-projectproperties.service";
import { VacationProjectDetailsService } from "./vacation/services/vacation-projectdetails.service";
import { VacationFormsService } from "./vacation/services/vacation-forms.service";
import { VacationProjectDetailsComponent } from "./vacation/components/project-details/vacation-project-details.component";
import { PrepareVacationRequestComponent } from "./vacation/components/task-forms/prepare-vacation-request.component";
import { CheckVacationRequestComponent } from "./vacation/components/task-forms/check-vacation-request.component";
import { ModifyVacationRequestComponent } from "./vacation/components/task-forms/modify-vacation-request.component";
import { PerformHandoverActivitiesComponent } from "./vacation/components/task-forms/perform-handover-activities.component";
import { InformSubstituteComponent } from "./vacation/components/task-forms/inform-substitute.component";
import { ProjectPropertiesVacationComponent } from "./vacation/components/project-properties/project.properties.vacation.component";
import { FieldsetModule } from "primeng/fieldset";

@NgModule({
  imports:[
    AlandaCommonModule,
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
  ],
  providers: [
    VacationProjectPropertiesService,
    VacationProjectDetailsService,
    VacationFormsService
  ],
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
