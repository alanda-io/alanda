import { Component } from '@angular/core';

@Component({
  selector: 'alanda-home',
  templateUrl: './home.component.html',
})
export class HomeComponent {
  hasFluidDesign = false;
  contentElement: HTMLElement;

  constructor() {
    this.contentElement = document.querySelector('.content');
    this.hasFluidDesign = this.contentElement.classList.contains('ui-fluid');
  }

  toggleFluidDesign(value): void {
    this.hasFluidDesign = value;
    this.contentElement.classList.toggle('ui-fluid', value);
  }
}
