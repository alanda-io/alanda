import { Component } from '@angular/core';

@Component({
  selector: 'alanda-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent {
  fontSize: number;
  htmlElement: HTMLElement;

  hasFluidDesign: boolean;
  contentElement: HTMLElement;

  constructor() {
    this.htmlElement = document.querySelector('html');
    this.fontSize =
      parseInt(getComputedStyle(this.htmlElement).fontSize, 10) || 14;
    this.contentElement = document.querySelector('.content');
    this.hasFluidDesign = this.contentElement.classList.contains('p-fluid');
  }

  changeFontSize(scale: number): void {
    this.fontSize = this.fontSize + scale;
    this.htmlElement.style.fontSize = `${this.fontSize}px`;
  }

  toggleFluidDesign(value): void {
    this.hasFluidDesign = value;
    this.contentElement.classList.toggle('p-fluid', value);
  }
}
