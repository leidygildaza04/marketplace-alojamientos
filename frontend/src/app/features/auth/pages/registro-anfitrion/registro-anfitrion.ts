import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import {
  adultValidator,
  matchingPasswordsValidator,
  todayAsDateInput,
} from '../../auth.validators';

@Component({
  selector: 'app-registro-anfitrion',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './registro-anfitrion.html',
  styleUrl: '../registro/registro.css',
})
export class RegistroAnfitrion {
  private fb = inject(FormBuilder);

  readonly maxBirthDate = todayAsDateInput();

  form = this.fb.nonNullable.group(
    {
      nombres: ['', [Validators.required, Validators.minLength(2)]],
      apellidos: ['', [Validators.required, Validators.minLength(2)]],
      cedula: ['', [Validators.required, Validators.pattern(/^[0-9]{5,15}$/)]],
      direccion: ['', [Validators.required, Validators.minLength(6)]],
      telefono: ['', [Validators.required, Validators.pattern(/^\+?[0-9][0-9\s()-]{5,18}$/)]],
      correo: ['', [Validators.required, Validators.email]],
      fechaNacimiento: ['', [Validators.required, adultValidator]],
      contrasenia: ['', [Validators.required, Validators.minLength(8)]],
      confirmarContrasenia: ['', Validators.required],
    },
    { validators: matchingPasswordsValidator },
  );

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    // TODO: conectar con el backend cuando esté listo
    console.info('Formulario de anfitrión validado; pendiente de integración con backend.');
  }
}
