import { NgModule } from "@angular/core";
import { VacationProjectDetailsComponent } from "./components/project-details/vacation-project-details.component";
import { PrepareVacationRequestComponent } from "./components/task-forms/prepare-vacation-request.component";
import { CheckVacationRequestComponent } from "./components/task-forms/check-vacation-request.component";
import { ModifyVacationRequestComponent } from "./components/task-forms/modify-vacation-request.component";
import { ProjectPropertiesVacationComponent } from "./components/project-properties/project.properties.vacation.component";
import { FieldsetModule } from "primeng/fieldset";
import { AlandaCommonModule } from "projects/alanda-common/src/public_api";
import { CardModule } from "primeng/card";
import { CommonModule } from "@angular/common";
import { ButtonModule } from "primeng/button";
import { DefaultTaskComponent } from "./components/task-forms/default-task-template.component";

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
    ProjectPropertiesVacationComponent,
    DefaultTaskComponent
  ],
  exports: [
    VacationProjectDetailsComponent,
    PrepareVacationRequestComponent,
    CheckVacationRequestComponent,
    ModifyVacationRequestComponent,
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
    ProjectPropertiesVacationComponent,
    DefaultTaskComponent
  ]
})
export class VacationModule { 

  constructor() {}
}
