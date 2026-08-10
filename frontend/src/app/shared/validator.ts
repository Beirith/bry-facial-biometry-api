import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export function cpfFieldValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value = control.value;

    if (!value) {
      return null;
    }

    const isValid = /^\d{11}$/.test(value);

    return isValid ? null : { onlyNumbers: true };
  };
}
