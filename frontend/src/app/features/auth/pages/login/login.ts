import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthSessionService, UserRole } from '../../../../core/services/auth-session.service';

interface TestAccount {
  username: string;
  email: string;
  password: string;
  path: string;
  role: UserRole;
}

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  readonly accounts: TestAccount[] = [
    { username: 'user', email: 'user@travesia.test', password: 'user123', path: '/inicio', role: 'guest' },
    { username: 'host', email: 'host@travesia.test', password: 'host123', path: '/anfitrion', role: 'host' },
    { username: 'admin', email: 'admin@travesia.test', password: 'admin123', path: '/administracion', role: 'admin' },
  ];

  username = '';
  password = '';
  error = '';
  showingPasswordRecovery = false;
  recoveryEmail = '';
  newPassword = '';
  confirmNewPassword = '';
  recoveryError = '';
  recoveryMessage = '';

  constructor(private readonly router: Router, private readonly session: AuthSessionService) {}

  enter(): void {
    const account = this.accounts.find(
      (item) => item.username === this.username.trim() && this.getAccountPassword(item) === this.password,
    );
    if (!account) {
      this.error = 'Usuario o contraseña incorrectos. Inténtalo de nuevo.';
      return;
    }
    this.error = '';
    this.session.signIn(account.username, account.role);
    void this.router.navigateByUrl(account.path);
  }

  showPasswordRecovery(): void {
    this.showingPasswordRecovery = true;
    this.recoveryError = '';
    this.recoveryMessage = '';
  }

  showLogin(): void {
    this.showingPasswordRecovery = false;
    this.recoveryError = '';
    this.recoveryMessage = '';
  }

  resetPassword(): void {
    this.recoveryError = '';
    this.recoveryMessage = '';
    const account = this.accounts.find(
      (item) => item.email.toLowerCase() === this.recoveryEmail.trim().toLowerCase(),
    );

    if (!account) {
      this.recoveryError = 'No encontramos una cuenta de prueba con ese correo.';
      return;
    }
    if (this.newPassword.length < 8) {
      this.recoveryError = 'La nueva contraseña debe tener al menos 8 caracteres.';
      return;
    }
    if (this.newPassword !== this.confirmNewPassword) {
      this.recoveryError = 'Las contraseñas no coinciden.';
      return;
    }

    if (typeof localStorage !== 'undefined') {
      localStorage.setItem(this.passwordStorageKey(account), this.newPassword);
    }
    this.recoveryMessage = 'Contraseña actualizada. Ya puedes iniciar sesión con ella.';
    this.username = account.username;
    this.password = this.newPassword;
    this.newPassword = '';
    this.confirmNewPassword = '';
  }

  private getAccountPassword(account: TestAccount): string {
    if (typeof localStorage === 'undefined') return account.password;
    return localStorage.getItem(this.passwordStorageKey(account)) ?? account.password;
  }

  private passwordStorageKey(account: TestAccount): string {
    return `travesia-test-password:${account.username}`;
  }
}
