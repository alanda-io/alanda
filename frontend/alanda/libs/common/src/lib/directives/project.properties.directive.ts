import { Directive, ViewContainerRef } from '@angular/core';

@Directive({
  selector: '[alandaProjectPropertiesHost]',
})
export class ProjectPropertiesDirective {
  constructor(public viewContainerRef: ViewContainerRef) {}
}
