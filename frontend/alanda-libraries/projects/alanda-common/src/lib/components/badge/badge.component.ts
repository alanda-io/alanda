import { Component, Input } from '@angular/core';

@Component({
  selector: 'alanda-badge',
  templateUrl: './badge.component.html',
  styleUrls: ['./badge.component.scss']
})
export class AlandaBadgeComponent {
  @Input() label: any;
}
