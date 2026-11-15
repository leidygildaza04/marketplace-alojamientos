import { Component } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { IdentityApiService, IdentityRole } from '../../../../core/services/identity-api.service';
import { AuthSessionService, UserRole } from '../../../../core/services/auth-session.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  email = '';
  password = '';
  error = '';
  isSubmitting = false;
  showingPasswordRecovery = false;
  recoveryEmail = '';
  recoveryToken = '';
  newPassword = '';
  confirmNewPassword = '';
  recoveryError = '';
  recoveryMessage = '';
  passwordResetComplete = false;

  constructor(
    private readonly router: Router,
    private readonly session: AuthSessionService,
    private readonly identityApi: IdentityApiService,
  ) {}

  enter(): void {
    this.error = '';
    if (this.isSubmitting) return;
    this.isSubmitting = true;
    this.session.clearSession();

    this.identityApi.login(this.email, this.password).subscribe({
      next: (response) => {
        const role = this.toUserRole(response.role);
        if (!response.token || !role) {
          this.session.clearSession();
          this.error = 'Identity devolvió una respuesta de inicio de sesión inválida.';
          this.isSubmitting = false;
          return;
        }

        this.session.setSession(response.token, role, this.email);
        this.identityApi.getMe().subscribe({
          next: (user) => {
            this.session.setSession(response.token, role, user.email, user.id);
            this.isSubmitting = false;
            const destination = role === 'host' ? '/anfitrion' : role === 'admin' ? '/administracion' : '/inicio';
            void this.router.navigateByUrl(destination);
          },
          error: (error: unknown) => {
            this.session.clearSession();
            this.error = this.loginError(error);
            this.isSubmitting = false;
          },
        });
      },
      error: (error: unknown) => {
        this.session.clearSession();
        this.error = this.loginError(error);
        this.isSubmitting = false;
      },
    });
  }

  showPasswordRecovery(): void {
    this.showingPasswordRecovery = true;
    this.recoveryEmail = this.email;
    this.recoveryError = '';
    this.recoveryMessage = '';
    this.recoveryToken = '';
    this.passwordResetComplete = false;
  }

  showLogin(): void {
    this.showingPasswordRecovery = false;
    this.recoveryError = '';
    this.recoveryMessage = '';
    this.recoveryToken = '';
    this.passwordResetComplete = false;
  }

  requestPasswordRecovery(): void {
    this.recoveryError = '';
    this.recoveryMessage = '';
    if (!this.recoveryEmail.trim() || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.recoveryEmail.trim())) {
      this.recoveryError = 'Ingresa un correo válido.';
      return;
    }
    this.isSubmitting = true;
    this.identityApi.requestPasswordRecovery(this.recoveryEmail.trim().toLowerCase()).subscribe({
      next: (response) => {
        this.recoveryToken = response.token;
        this.recoveryMessage = response.message;
        this.isSubmitting = false;
      },
      error: () => { this.recoveryError = 'No se pudo iniciar la recuperación. Verifica el correo e inténtalo de nuevo.'; this.isSubmitting = false; },
    });
  }

  resetPassword(): void {
    this.recoveryError = '';
    this.recoveryMessage = '';
    if (!this.isBackendPasswordValid(this.newPassword)) {
      this.recoveryError = 'Usa entre 8 y 72 caracteres, con mayúscula, minúscula, número y símbolo.';
      return;
    }
    if (this.newPassword !== this.confirmNewPassword) {
      this.recoveryError = 'Las contraseñas no coinciden.';
      return;
    }
    this.isSubmitting = true;
    this.identityApi.resetPassword(this.recoveryToken, this.newPassword).subscribe({
      next: () => { this.passwordResetComplete = true; this.recoveryMessage = 'Contraseña actualizada. Ya puedes iniciar sesión.'; this.isSubmitting = false; },
      error: () => { this.recoveryError = 'El token es inválido, venció o ya fue utilizado.'; this.isSubmitting = false; },
    });
  }

  private isBackendPasswordValid(value: string): boolean {
    return value.length >= 8 && value.length <= 72
      && /[a-z]/.test(value)
      && /[A-Z]/.test(value)
      && /\d/.test(value)
      && /[^a-zA-Z0-9\s]/.test(value);
  }

  private toUserRole(role: IdentityRole | string): UserRole | null {
    switch (role) {
      case 'HOST': return 'host';
      case 'ADMIN': return 'admin';
      case 'GUEST': return 'guest';
      default: return null;
    }
  }

  private loginError(error: unknown): string {
    if (!(error instanceof HttpErrorResponse)) {
      return 'No fue posible iniciar sesión. Inténtalo nuevamente.';
    }
    if (error.status === 0) {
      return 'No se pudo conectar con Identity. Verifica que el servicio esté disponible.';
    }
    if (error.status === 401) {
      return 'Correo o contraseña incorrectos. Inténtalo de nuevo.';
    }
    if (error.status === 403) {
      return 'La cuenta está desactivada. Contacta al administrador.';
    }
    if (error.status === 400) {
      return 'Ingresa un correo y una contraseña válidos.';
    }
    return 'No fue posible iniciar sesión. Inténtalo nuevamente.';
  }
}
