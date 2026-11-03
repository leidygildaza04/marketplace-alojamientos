import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { IdentityDemoService } from '../../../../core/services/identity-demo.service';
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
  private readonly identityDemo = inject(IdentityDemoService);
  mensaje = '';
  error = '';

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
    try {
      this.identityDemo.register({
        email,
        password: value.contrasenia,
        fullName: `${value.nombres.trim()} ${value.apellidos.trim()}`,
        role: 'HOST',
      });
      void this.router.navigate(['/login'], { queryParams: { email } });
    } catch (error) {
      this.error = error instanceof Error ? error.message : 'No se pudo crear la cuenta de anfitrión.';
    }
  }
}
