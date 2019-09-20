import { Directive, ViewContainerRef } from "@angular/core";

@Directive({
  selector: '[task-forms]'
})
export class FormsControllerDirective {

  constructor(public viewContainerRef: ViewContainerRef) {
  }
}