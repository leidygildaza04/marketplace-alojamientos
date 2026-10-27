
import { Component, inject } from '@angular/core';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  private fb = inject(FormBuilder);
  private router = inject(Router);

  mensajeError = '';

  form = this.fb.nonNullable.group({
    correo: ['', [Validators.required, Validators.email]],
    contrasenia: ['', [Validators.required, Validators.minLength(6)]],
  });

  onSubmit(): void {
    this.mensajeError = '';

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    // Modo de prueba: todavía no valida credenciales con el backend.
    console.log('Datos del login:', this.form.getRawValue());

    void this.router.navigate(['/inicio']);
  }
}
