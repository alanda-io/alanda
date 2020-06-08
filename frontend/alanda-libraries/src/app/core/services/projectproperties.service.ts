import { Injectable, Type } from '@angular/core';
import { Component } from '@angular/compiler/src/core';
import { ProjectPropertiesComponent } from 'src/app/components/project-properties/project-properties.component';

@Injectable()
export class ProjectPropertiesService {
  private readonly propertyComponents: Map<string, Type<any>>;

  constructor() {
    this.propertyComponents = new Map<string, Type<any>>();
    this.propertyComponents.set('VACATION', ProjectPropertiesComponent);
  }

  getPropsForType(key: string): Type<Component> {
    return this.propertyComponents.get(key);
  }
}
