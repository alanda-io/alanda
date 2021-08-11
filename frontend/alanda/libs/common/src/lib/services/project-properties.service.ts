import { Injectable, Type, EventEmitter } from '@angular/core';
import { Component } from '@angular/compiler/src/core';
import { AlandaProject } from '../api/models/project';

@Injectable({
  providedIn: 'root',
})
export class AlandaProjectPropertiesService {
  private readonly propertyComponents: Map<string, Type<any>>;

  constructor() {
    this.propertyComponents = new Map<string, Type<any>>();
  }

  getPropsForType(
    key: string,
  ): Type<Component & { projectChanged: EventEmitter<AlandaProject> }> {
    return this.propertyComponents.get(key);
  }

  addPropsForType(
    key: string,
    propertyComponent: Type<
      any & { projectChanged: EventEmitter<AlandaProject> }
    >,
  ): void {
    this.propertyComponents.set(key, propertyComponent);
  }
}
