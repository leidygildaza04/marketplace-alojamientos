import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { IdentityApiService } from '../../../../core/services/identity-api.service';
import { matchingPasswordsValidator } from '../../auth.validators';

const passwordPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^a-zA-Z0-9\s]).+$/;

@Component({
  selector: 'app-registro-anfitrion',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './registro-anfitrion.html',
  styleUrl: '../registro/registro.css',
})
export class RegistroAnfitrion {
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly identityApi = inject(IdentityApiService);
  mensaje = '';
  error = '';
  isSubmitting = false;

  form = this.fb.nonNullable.group(
    {
      nombres: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(74)]],
      apellidos: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(74)]],
      correo: ['', [Validators.required, Validators.email, Validators.maxLength(255)]],
      contrasenia: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(72), Validators.pattern(passwordPattern)]],
      confirmarContrasenia: ['', Validators.required],
    },
    { validators: matchingPasswordsValidator },
  );

  onSubmit(): void {
    this.mensaje = '';
    this.error = '';
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.getRawValue();
    const email = value.correo.trim().toLowerCase();
    if (this.isSubmitting) return;
    this.isSubmitting = true;
    this.identityApi.register({
        email,
        password: value.contrasenia,
        fullName: `${value.nombres.trim()} ${value.apellidos.trim()}`,
        role: 'HOST',
      }).subscribe({
        next: () => { this.isSubmitting = false; void this.router.navigate(['/login'], { queryParams: { email } }); },
        error: (error: unknown) => { this.isSubmitting = false; this.error = this.registrationError(error); },
      });
  }

  private registrationError(error: unknown): string {
    if (error instanceof HttpErrorResponse && error.status === 0) return 'No se pudo conectar con Identity. Verifica que el servicio esté disponible.';
    if (error instanceof HttpErrorResponse && error.status === 409) return 'Ya existe una cuenta con ese correo.';
    if (error instanceof HttpErrorResponse && error.status === 400) return 'Revisa los datos y la política de contraseña.';
    return 'No se pudo crear la cuenta de anfitrión. Inténtalo nuevamente.';
  }
}
