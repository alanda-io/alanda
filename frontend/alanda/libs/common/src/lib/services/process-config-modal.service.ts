import { Injectable, Type } from '@angular/core';
import { Component } from '@angular/compiler/src/core';

@Injectable()
export class AlandaProcessConfigModalService {
  private readonly configTemplateComponents: Map<string, Type<any>>;

  constructor() {
    this.configTemplateComponents = new Map<string, Type<any>>();
  }

  getTemplate(key: string): Type<Component> {
    return this.configTemplateComponents.get(key);
  }
}
