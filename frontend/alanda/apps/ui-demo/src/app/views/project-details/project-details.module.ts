import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProjectDetailsRoutingModule } from './project-details-routing.module';
import { ProjectDetailsComponent } from './project-details.component';
import {
  AttachmentsModule,
  CommentsModule,
  HistoryGridModule,
  PioModule,
  ProjectAndProcessesModule,
  ProjectHeaderModule,
} from '@alanda/common';
import { UserEnrichedProjectsControllerComponent } from './components/projects-controller/user-enriched-projects-controller.component';
import { TabViewModule } from 'primeng/tabview';

@NgModule({
  declarations: [
    ProjectDetailsComponent,
    UserEnrichedProjectsControllerComponent,
  ],
  imports: [
    CommonModule,
    ProjectDetailsRoutingModule,
    ProjectHeaderModule,
    ProjectAndProcessesModule,
    CommentsModule,
    AttachmentsModule,
    PioModule,
    HistoryGridModule,
    TabViewModule,
  ],
  exports: [ProjectDetailsComponent, UserEnrichedProjectsControllerComponent],
})
export class ProjectDetailsModule {}
