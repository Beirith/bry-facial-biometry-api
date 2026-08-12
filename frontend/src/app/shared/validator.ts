import {AbstractControl, ValidationErrors, ValidatorFn} from '@angular/forms';

export function cpfFieldValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value = control.value;

    if (!value) {
      return null;
    }

    if (!/^\d+$/.test(value)) {
      return {onlyNumbers: true};
    }

    if (value.length !== 11) {
      return {invalidLength: true};
    }

    if (!isValidCpf(value)) {
      return {invalidCpf: true};
    }

    return null;
  };
}

function isValidCpf(cpf: string): boolean {
  if (/^(\d)\1{10}$/.test(cpf)) {
    return false;
  }

  let sum = 0;
  for (let i = 0; i < 9; i++) {
    sum += parseInt(cpf.charAt(i)) * (10 - i);
  }
  let remainder = (sum * 10) % 11;
  if (remainder === 10 || remainder === 11) {
    remainder = 0;
  }
  if (remainder !== parseInt(cpf.charAt(9))) {
    return false;
  }

  sum = 0;
  for (let i = 0; i < 10; i++) {
    sum += parseInt(cpf.charAt(i)) * (11 - i);
  }
  remainder = (sum * 10) % 11;
  if (remainder === 10 || remainder === 11) {
    remainder = 0;
  }
  return remainder === parseInt(cpf.charAt(10));
}
