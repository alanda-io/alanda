import {AccessLevels} from '../interfaces-and-types';
import {ElementRef} from '@angular/core';

export interface ElementManager {
  element: HTMLElement;

  applyForbiddenBehavior(accessLevel: AccessLevels): void;

  applyGrantedBehavior(accessLevel: AccessLevels): void;
}

export function getManagersByElementRef(elementRef: ElementRef): ElementManager[] {
  const managers = [];
  const nativeElement = elementRef.nativeElement;
  const tagName = nativeElement.tagName;

  if (['FIELDSET', 'INPUT', 'SELECT'].includes(tagName)) {
    managers.push(new AttributeManager(nativeElement));
  } else if (['DIV'].includes(tagName)) {
    managers.push(new ClassManager(nativeElement));
  } else {
    managers.push(new ClassManager(nativeElement));
  }

  return managers;
}

class AttributeManager implements ElementManager {
  disabledAttribute = 'disabled';

  constructor(public element: HTMLElement) {
  }

  applyGrantedBehavior(accessLevel: AccessLevels): void {
    this.element.removeAttribute(this.disabledAttribute);
  }

  applyForbiddenBehavior(accessLevel: AccessLevels): void {
    this.element.setAttribute(this.disabledAttribute, this.disabledAttribute);
  }

}

class ClassManager implements ElementManager {
  disabledClass = 'disabled';

  constructor(public element: HTMLElement) {
  }

  applyGrantedBehavior(accessLevel: AccessLevels): void {
    this.element.classList.remove(this.disabledClass);
  }

  applyForbiddenBehavior(accessLevel: AccessLevels): void {
    this.element.classList.add(this.disabledClass);
  }

}

