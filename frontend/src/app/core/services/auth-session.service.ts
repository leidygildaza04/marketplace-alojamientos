import { Injectable } from '@angular/core';

export type UserRole = 'guest' | 'host' | 'admin';

@Injectable({ providedIn: 'root' })
export class AuthSessionService {
  private readonly tokenKey = 'estancia-token';
  private readonly roleKey = 'estancia-role';
  private readonly emailKey = 'estancia-username';
  private readonly userIdKey = 'estancia-user-id';

  get role(): UserRole | null {
    return this.getRole();
  }

  get email(): string | null {
    return this.getUsername();
  }

  setSession(token: string, role: UserRole, username: string, userId?: number): void {
    if (typeof localStorage === 'undefined') return;
    localStorage.setItem(this.tokenKey, token);
    localStorage.setItem(this.roleKey, role);
    localStorage.setItem(this.emailKey, username.trim().toLowerCase());
    if (userId != null) localStorage.setItem(this.userIdKey, String(userId));
  }

  getToken(): string | null {
    if (typeof localStorage === 'undefined') return null;
    return localStorage.getItem(this.tokenKey);
  }

  get userId(): number | null {
    if (typeof localStorage === 'undefined') return null;
    const value = Number(localStorage.getItem(this.userIdKey));
    return Number.isSafeInteger(value) && value > 0 ? value : null;
  }

  getRole(): UserRole | null {
    if (typeof localStorage === 'undefined') return null;
    const value = localStorage.getItem(this.roleKey);
    return value === 'guest' || value === 'host' || value === 'admin' ? value : null;
  }

  getUsername(): string | null {
    if (typeof localStorage === 'undefined') return null;
    const value = localStorage.getItem(this.emailKey);
    return value?.includes('@') ? value : null;
  }

  isAuthenticated(): boolean {
    return Boolean(this.getToken());
  }

  clearSession(): void {
    if (typeof localStorage === 'undefined') return;
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.roleKey);
    localStorage.removeItem(this.emailKey);
    localStorage.removeItem(this.userIdKey);
  }

  signOut(): void {
    this.clearSession();
  }
}
