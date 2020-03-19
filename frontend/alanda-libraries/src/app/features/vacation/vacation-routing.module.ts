import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PrepareVacationRequestComponent } from './forms/prepare-vacation-request.component';
import { CheckVacationRequestComponent } from './forms/check-vacation-request.component';
import { ModifyVacationRequestComponent } from './forms/modify-vacation-request.component';

const routes: Routes = [
  { path: 'prepare-vacation-request/:taskId', component: PrepareVacationRequestComponent},
  { path: 'check-vacation-request/:taskId', component: CheckVacationRequestComponent},
  { path: 'modify-vacation-request/:taskId', component: ModifyVacationRequestComponent},
  { path: '**', redirectTo: ''}
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class VacationRoutingModule { }
