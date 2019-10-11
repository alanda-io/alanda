import { Injectable, Type } from "@angular/core";
import { Component } from "@angular/compiler/src/core";
import { PrepareVacationRequestComponent } from "../components/task-forms/prepare-vacation-request.component";
import { CheckVacationRequestComponent } from "../components/task-forms/check-vacation-request.component";
import { ModifyVacationRequestComponent } from "../components/task-forms/modify-vacation-request.component";
import { DefaultTaskComponent } from "../components/task-forms/default-task-template.component";

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