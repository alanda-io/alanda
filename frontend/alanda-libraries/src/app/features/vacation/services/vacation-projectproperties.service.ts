import { Injectable, Type } from "@angular/core";
import { Component } from "@angular/compiler/src/core";
import { VacationProjectPropertiesComponent } from "../components/vacation-project-properties/vacation-project-properties.component";

@Injectable()
export class VacationProjectPropertiesService {

  private propertyComponents: Map<string, Type<any>>

  constructor() {
    this.propertyComponents = new Map<string, Type<any>>();
    this.propertyComponents.set('VACATION', VacationProjectPropertiesComponent);
  }

  getPropsForType(key : string): Type<Component> {
    return this.propertyComponents.get(key);
  }
}
