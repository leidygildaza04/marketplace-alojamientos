import { Injectable } from '@angular/core';

export type UserRole = 'guest' | 'host' | 'admin';

@Injectable({ providedIn: 'root' })
export class AuthSessionService {
  private readonly roleKey = 'estancia-role';
  private readonly emailKey = 'estancia-username';

  get role(): UserRole | null {
    if (typeof localStorage === 'undefined') return null;
    const value = localStorage.getItem(this.roleKey);
    return value === 'guest' || value === 'host' || value === 'admin' ? value : null;
  }

  get email(): string | null {
    if (typeof localStorage === 'undefined') return null;
    const value = localStorage.getItem(this.emailKey);
    return value?.includes('@') ? value : null;
  }

  signIn(email: string, role: UserRole): void {
    if (typeof localStorage === 'undefined') return;
    localStorage.setItem(this.roleKey, role);
    localStorage.setItem(this.emailKey, email.trim().toLowerCase());
  }

  signOut(): void {
    if (typeof localStorage === 'undefined') return;
    localStorage.removeItem(this.roleKey);
    localStorage.removeItem(this.emailKey);
  }
}
