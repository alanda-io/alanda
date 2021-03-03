import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProjectsControllerComponent } from './projects-controller.component';
import { RouterModule } from '@angular/router';
import { AlandaCommonModule } from '@alanda/common';
import { VacationModule } from '../../features/vacation/vacation.module';

@NgModule({
  declarations: [ProjectsControllerComponent],
  imports: [RouterModule, AlandaCommonModule, CommonModule, VacationModule],
  exports: [ProjectsControllerComponent],
})
export class ProjectsControllerModule {}
