import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { adultValidator, matchingPasswordsValidator, todayAsDateInput } from '../../auth.validators';

@Component({
  selector: 'app-registro',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './registro.html',
  styleUrl: './registro.css',
})
export class Registro {
  private fb = inject(FormBuilder);
  mensaje = '';

  readonly maxBirthDate = todayAsDateInput();

  form = this.fb.nonNullable.group({
    nombres: ['', [Validators.required, Validators.minLength(2)]],
    apellidos: ['', [Validators.required, Validators.minLength(2)]],
    telefono: ['', [Validators.required, Validators.pattern(/^\+?[0-9][0-9\s()-]{5,18}$/)]],
    correo: ['', [Validators.required, Validators.email]],
    fechaNacimiento: ['', [Validators.required, adultValidator]],
    contrasenia: ['', [Validators.required, Validators.minLength(8)]],
    confirmarContrasenia: ['', Validators.required],
  }, { validators: matchingPasswordsValidator });

  onSubmit(): void {
    this.mensaje = '';
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.mensaje = 'Formulario completado correctamente.';
  }
}
