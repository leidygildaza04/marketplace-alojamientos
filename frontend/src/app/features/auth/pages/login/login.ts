import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { IdentityDemoService, DemoApiRole } from '../../../../core/services/identity-demo.service';
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
    private readonly identityDemo: IdentityDemoService,
  ) {}

  enter(): void {
    const response = this.identityDemo.login(this.email, this.password);
    if (!response) {
      this.error = 'Correo o contraseña incorrectos. Inténtalo de nuevo.';
      return;
    }

    this.error = '';
    this.session.signIn(this.email, this.toUserRole(response.role));
    const destination = response.role === 'HOST'
      ? '/anfitrion'
      : response.role === 'ADMIN' ? '/administracion' : '/inicio';
    void this.router.navigateByUrl(destination);
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
    try {
      const response = this.identityDemo.requestRecovery(this.recoveryEmail);
      this.recoveryToken = response.token;
      this.recoveryMessage = 'Token de prueba generado; el backend actual no envía correo.';
    } catch (error) {
      this.recoveryError = error instanceof Error ? error.message : 'No se pudo iniciar la recuperación.';
    }
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
    if (!this.identityDemo.resetPassword(this.recoveryToken, this.newPassword)) {
      this.recoveryError = 'El token es inválido, venció o ya fue utilizado.';
      return;
    }

    this.passwordResetComplete = true;
    this.recoveryMessage = 'Contraseña actualizada. Ya puedes iniciar sesión.';
  }

  private isBackendPasswordValid(value: string): boolean {
    return value.length >= 8 && value.length <= 72
      && /[a-z]/.test(value)
      && /[A-Z]/.test(value)
      && /\d/.test(value)
      && /[^a-zA-Z0-9\s]/.test(value);
  }

  private toUserRole(role: DemoApiRole): UserRole {
    switch (role) {
      case 'HOST': return 'host';
      case 'ADMIN': return 'admin';
      default: return 'guest';
    }
  }
}
