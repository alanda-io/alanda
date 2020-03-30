import { Injectable, Type } from '@angular/core';
import { Component } from '@angular/compiler/src/core';
import { VacationProjectDetailsComponent } from 'src/app/components/vacation-project-details/vacation-project-details.component';

@Injectable()
export class ProjectDetailsService {

  private propertyComponents: Map<string, Type<any>>;

  constructor() {
    this.propertyComponents = new Map<string, Type<any>>();
    this.propertyComponents.set('VACATION', VacationProjectDetailsComponent);
  }

  getPropsForType(key: string): Type<Component> {
    console.log("test", key);
    return this.propertyComponents.get(key);
  }
}
