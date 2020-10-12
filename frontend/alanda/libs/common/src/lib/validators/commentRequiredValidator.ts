import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export function commentRequiredValidator(
  commentControl: AbstractControl,
  valueControl: AbstractControl,
  values: any[],
): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const comment = commentControl.value;
    const value = valueControl.value;
    if (
      values.findIndex((val) => val === value) !== -1 &&
      comment.hasComment !== true
    ) {
      return {
        commentRequired: {
          text: 'Please add a comment with the reason for your decision!',
        },
      };
    }
    return null;
  };
}
