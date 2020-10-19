import { Component, Input } from '@angular/core';

@Component({
  selector: 'alanda-label',
  templateUrl: './label.component.html',
  styleUrls: ['./label.component.scss'],
})
export class LabelComponent {
  @Input() label: string;
  @Input() required = false;
}
