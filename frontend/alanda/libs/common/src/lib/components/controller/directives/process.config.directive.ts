import { Directive, ViewContainerRef } from '@angular/core';

@Directive({
  selector: '[alandaProcessConfigHost]',
})
export class ProcessConfigDirective {
  constructor(public viewContainerRef: ViewContainerRef) {}
}
