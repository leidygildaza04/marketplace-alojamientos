import { AbstractControl, ValidationErrors } from '@angular/forms';

export function adultValidator(control: AbstractControl): ValidationErrors | null {
  if (!control.value) {
    return null;
  }

  const [year, month, day] = String(control.value).split('-').map(Number);
  const birthDate = new Date(year, month - 1, day);
  const isValidDate =
    birthDate.getFullYear() === year &&
    birthDate.getMonth() === month - 1 &&
    birthDate.getDate() === day;

  if (!isValidDate) {
    return { invalidBirthDate: true };
  }

  const today = new Date();
  const eighteenthBirthday = new Date(
    year + 18,
    month - 1,
    Math.min(day, new Date(year + 18, month, 0).getDate()),
  );

  return eighteenthBirthday <= today ? null : { underage: true };
}

export function matchingPasswordsValidator(control: AbstractControl): ValidationErrors | null {
  const password = control.get('contrasenia')?.value;
  const confirmation = control.get('confirmarContrasenia')?.value;

  return confirmation && password !== confirmation ? { passwordMismatch: true } : null;
}

export function todayAsDateInput(): string {
  const today = new Date();
  const month = String(today.getMonth() + 1).padStart(2, '0');
  const day = String(today.getDate()).padStart(2, '0');
  return `${today.getFullYear()}-${month}-${day}`;
}
