import { Injectable } from "@angular/core";
import { FormGroup } from "@angular/forms";

@Injectable({
  providedIn: 'root'
})
export class FormsRegisterService {

  private formGroup: FormGroup;

  constructor() {}

  // TODO: remove init()
  public init() {
    this.formGroup = new FormGroup({});
  }

  public isValid(): boolean {
    return this.formGroup.valid;
  }

  get $formGroup(): FormGroup {
    return this.formGroup;
  }

  public registerForm(formGroup: FormGroup, name: string): void {
    if(!this.formGroup) {
      this.init();
    }
    this.formGroup.addControl(name, formGroup);
  }

  public clear() {
    this.formGroup.reset();
  }

  public touch() {
    Object.keys(this.formGroup.controls).forEach(key => {
      const nestedForm = this.formGroup.get(key) as FormGroup;
      Object.keys(nestedForm.controls).forEach(key => {
        nestedForm.get(key).markAsTouched();
      })
    });
  }



}