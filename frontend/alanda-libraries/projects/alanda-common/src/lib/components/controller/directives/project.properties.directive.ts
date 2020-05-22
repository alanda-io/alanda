import { Directive, ViewContainerRef } from '@angular/core';

@Directive({
  selector: '[properties-host]'
})
export class ProjectPropertiesDirective {

  constructor(public viewContainerRef: ViewContainerRef) {

  }
}