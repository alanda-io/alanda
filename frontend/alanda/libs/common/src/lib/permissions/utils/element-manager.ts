import { ElementRef } from '@angular/core';

export interface ElementManager {
  applyForbiddenBehavior(element: HTMLElement): void;

  applyGrantedBehavior(element: HTMLElement): void;

  worksForElement(element: HTMLElement): boolean;
}

const attributeManager: ElementManager & { disabledAttribute: string } = {
  disabledAttribute: 'disabled',

  applyGrantedBehavior(element: HTMLElement): void {
    element.removeAttribute(this.disabledAttribute);
  },

  applyForbiddenBehavior(element: HTMLElement): void {
    element.setAttribute(this.disabledAttribute, 'true');
  },

  worksForElement(element: HTMLElement): boolean {
    return ['FIELDSET', 'INPUT', 'SELECT', 'BUTTON'].includes(element.tagName);
  },
};

const classManager: ElementManager & { disabledClass: string } = {
  disabledClass: 'disabled',

  applyGrantedBehavior(element: HTMLElement): void {
    element.classList.remove(this.disabledClass);
  },

  applyForbiddenBehavior(element: HTMLElement): void {
    element.classList.add(this.disabledClass);
  },

  worksForElement(element: HTMLElement): boolean {
    return ['DIV'].includes(element.tagName);
  },
};

const elementManagers: ElementManager[] = [classManager, attributeManager];
const defaultManager: ElementManager[] = [classManager];

export function getManagersByElementRef(
  elementRef: ElementRef,
): ElementManager[] {
  const managers = elementManagers.filter((manager) =>
    manager.worksForElement(elementRef.nativeElement),
  );
  return managers.length ? defaultManager : managers;
}
