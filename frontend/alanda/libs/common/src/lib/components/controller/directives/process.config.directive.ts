import { Directive, ViewContainerRef } from '@angular/core';

@Directive({
  selector: '[processconfig-host]',
})
export class ProcessConfigDirective {
  constructor(public viewContainerRef: ViewContainerRef) {}
}
