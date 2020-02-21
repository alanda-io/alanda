import { Injectable, Type } from "@angular/core";
import { Component } from "@angular/compiler/src/core";
import { PrepareVacationRequestComponent } from "../forms/prepare-vacation-request.component";
import { CheckVacationRequestComponent } from "../forms/check-vacation-request.component";
import { ModifyVacationRequestComponent } from "../forms/modify-vacation-request.component";
import { DefaultTaskComponent } from "../forms/default-task-template.component";

@Injectable()
export class VacationFormsService {

  private propertyComponents: Map<string, Type<any>>

  constructor() {
    this.propertyComponents = new Map<string, Type<any>>();
    this.propertyComponents.set('vacation.prepare-vacation-request', PrepareVacationRequestComponent);
    this.propertyComponents.set('vacation.check-vacation-request', CheckVacationRequestComponent);
    this.propertyComponents.set('vacation.modify-vacation-request', ModifyVacationRequestComponent);
    this.propertyComponents.set('vacation.perform-handover-activities', DefaultTaskComponent);
    this.propertyComponents.set('vacation.inform-substitute', DefaultTaskComponent);
  }

  getFormByKey(key : string): Type<Component> {
    return this.propertyComponents.get(key);
  }
}
