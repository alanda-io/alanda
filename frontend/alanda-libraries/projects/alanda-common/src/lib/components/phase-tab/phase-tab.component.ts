import { Component, ComponentFactoryResolver, Input, OnInit } from '@angular/core';
import { MenuItem } from 'primeng/api';
import { ProjectPropertiesDirective } from '../controller/directives/project.properties.directive';
import { AlandaSimplePhase } from '../../api/models/simplePhase';

export interface AlandaPhaseTabItem {
  header: string;
  content?: string;
  component?: string;
  simplePhase: AlandaSimplePhase;
  state: string;
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
export class AlandaPhaseTabComponent implements OnInit {
  @Input() items: AlandaPhaseTabItem[];
  @Input() activeItemIndex = 0;

  propertiesHost: ProjectPropertiesDirective;

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

  constructor(
    private readonly componentFactoryResolver: ComponentFactoryResolver,
  ) {}

  ngOnInit(): void {
    this.loadComponent();
  }

  selectTab(index, event): void {
    console.log(event, index);
    this.activeItemIndex = index;
    // this.loadComponent();
  }

  setStatus(event): void {
    this.items[this.activeItemIndex].simplePhase.enabled = !this.items[this.activeItemIndex].simplePhase.enabled;
    console.log(event, this.activeItemIndex);
  }

  private loadComponent(): void {
    const item: AlandaPhaseTabItem = this.items[this.activeItemIndex];

    const componentFactory = this.componentFactoryResolver.resolveComponentFactory(item.component);
    const viewContainerRef = this.propertiesHost.viewContainerRef;
    viewContainerRef.clear();

    const componentRef = viewContainerRef.createComponent(componentFactory);
    (<PhaseComponent>componentRef.instance).data = item.data;
  }
}
