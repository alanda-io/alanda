import { Injectable, Type, Component } from '@angular/core';

@Injectable()
export class AlandaProjectPropertiesService {

  private propertyComponents: Map<string, Type<any>>;

  constructor() {
    this.propertyComponents = new Map<string, Type<any>>();
  }

  getPropsForType(key: string): Type<Component> {
    return this.propertyComponents.get(key);
  }
}
