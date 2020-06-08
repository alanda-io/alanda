import { Injectable, Type } from '@angular/core';
import { Component } from '@angular/compiler/src/core';

@Injectable()
export class AlandaProjectPropertiesService {
  private readonly propertyComponents: Map<string, Type<any>>;

  constructor() {
    this.propertyComponents = new Map<string, Type<any>>();
  }

  getPropsForType(key: string): Type<Component> {
    return this.propertyComponents.get(key);
  }
}
