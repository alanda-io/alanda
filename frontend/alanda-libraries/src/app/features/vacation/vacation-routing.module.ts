import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { PrepareVacationRequestComponent } from "./forms/prepare-vacation-request.component";
import { CheckVacationRequestComponent } from "./forms/check-vacation-request.component";
import { ModifyVacationRequestComponent } from "./forms/modify-vacation-request.component";
import { ProjectDetailsComponent } from "src/app/components/project-details/project-details.component";
import { InformSubstituteComponent } from "./forms/inform-substitute.component";
import { PerformHandoverActivitiesComponent } from "./forms/perform-handover-activities.component";

const routes: Routes = [
  { path: "", component: ProjectDetailsComponent },
  {
    path: "prepare-vacation-request/:taskId",
    component: PrepareVacationRequestComponent,
  },
  {
    path: "check-vacation-request/:taskId",
    component: CheckVacationRequestComponent,
  },
  {
    path: "modify-vacation-request/:taskId",
    component: ModifyVacationRequestComponent,
  },
  { path: "inform-substitute/:taskId", component: InformSubstituteComponent },
  {
    path: "perform-handover-activities/:taskId",
    component: PerformHandoverActivitiesComponent,
  },
  { path: "**", redirectTo: "" },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class VacationRoutingModule {}
