import { Directive, ViewContainerRef } from "@angular/core";

@Directive({
  selector: '[project-details]'
})
export class ProjectDetailsDirective {

  constructor(public viewContainerRef: ViewContainerRef) {

  }
}