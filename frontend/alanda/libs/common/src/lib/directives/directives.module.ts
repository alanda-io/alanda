import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProcessConfigDirective } from './process.config.directive';
import { ProjectPropertiesDirective } from './project.properties.directive';

@NgModule({
  declarations: [ProcessConfigDirective, ProjectPropertiesDirective],
  imports: [CommonModule],
})
export class DirectivesModule {}
