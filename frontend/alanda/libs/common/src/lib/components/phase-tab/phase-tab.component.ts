import { Component, Input } from '@angular/core';
import { MenuItem } from 'primeng/api';
import { AlandaSimplePhase } from '../../api/models/simplePhase';
import { AlandaProject } from '../../api/models/project';

export interface AlandaPhaseTabItem {
  header: string;
  content?: string;
  component?: string;
  simplePhase?: AlandaSimplePhase;
  state?: string;
  data?: any;
}

export interface PhaseComponent {
  data: any;
}

@Component({
  selector: 'alanda-phase-tab',
  templateUrl: './phase-tab.component.html',
  styleUrls: ['./phase-tab.component.scss']
})
export class AlandaPhaseTabComponent {
  @Input() items: AlandaPhaseTabItem[];
  @Input() activeItemIndex = 0;
  @Input() project: AlandaProject;

  menuItems: MenuItem[] = [
    {
      label: 'Enabled',
      command: (event) => this.setStatus(event),
    },
    {
      label: 'Disabled',
      command: (event) => this.setStatus(event)
    }
  ];

  selectTab(index, event): void {
    console.log(event, index);
    this.activeItemIndex = index;
  }

  setStatus(event): void {
    this.items[this.activeItemIndex].simplePhase.enabled = !this.items[this.activeItemIndex].simplePhase.enabled;
    console.log(event, this.activeItemIndex);
  }
}
