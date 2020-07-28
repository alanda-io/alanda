import { Directive, ViewContainerRef } from '@angular/core';

@Directive({
  selector: '[alandaPropertiesHost]',
})
export class ProjectPropertiesDirective {
  constructor(public viewContainerRef: ViewContainerRef) {}
}
