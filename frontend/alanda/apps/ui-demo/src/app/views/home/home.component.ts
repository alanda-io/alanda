import { Component } from '@angular/core';
import { PrimeNGConfig } from 'primeng/api';

@Component({
  selector: 'alanda-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent {
  fontSize: number;
  htmlElement: HTMLElement;

  hasFluidDesign: boolean;
  useRippleAnimation: boolean;
  contentElement: HTMLElement;

  constructor(private primengConfig: PrimeNGConfig) {
    this.htmlElement = document.querySelector('html');
    this.fontSize =
      parseInt(getComputedStyle(this.htmlElement).fontSize, 10) || 14;
    this.contentElement = document.querySelector('.content');
    this.hasFluidDesign = this.contentElement.classList.contains('p-fluid');
    this.useRippleAnimation = this.primengConfig.ripple;
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
