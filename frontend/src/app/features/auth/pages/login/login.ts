import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthSessionService, UserRole } from '../../../../core/services/auth-session.service';

interface TestAccount {
  username: string;
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
    { username: 'user', password: 'user123', path: '/inicio', role: 'guest' },
    { username: 'host', password: 'host123', path: '/anfitrion', role: 'host' },
    { username: 'admin', password: 'admin123', path: '/administracion', role: 'admin' },
  ];

  username = '';
  password = '';
  error = '';

  constructor(private readonly router: Router, private readonly session: AuthSessionService) {}

  enter(): void {
    const account = this.accounts.find(
      (item) => item.username === this.username.trim() && item.password === this.password,
    );
    if (!account) {
      this.error = 'Usuario o contraseña incorrectos. Inténtalo de nuevo.';
      return;
    }
    this.error = '';
    this.session.signIn(account.role);
    void this.router.navigateByUrl(account.path);
  }
}
