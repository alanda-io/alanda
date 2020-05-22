import { FormGroup } from '@angular/forms';

export interface AlandaTaskFormPanel {
  registerForm(formGroup: FormGroup, formName: string): void
}
