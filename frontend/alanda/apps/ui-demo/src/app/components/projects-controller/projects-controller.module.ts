import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProjectsControllerComponent } from './projects-controller.component';
import { RouterModule } from '@angular/router';
import { AlandaCommonModule } from '@alanda/common';

@NgModule({
  declarations: [ProjectsControllerComponent],
  imports: [RouterModule, AlandaCommonModule, CommonModule],
  exports: [ProjectsControllerComponent],
})
export class ProjectsControllerModule {}
