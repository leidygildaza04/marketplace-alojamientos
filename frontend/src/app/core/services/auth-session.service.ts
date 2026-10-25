import { Injectable } from '@angular/core';

export type UserRole = 'guest' | 'host' | 'admin';

@Injectable({ providedIn: 'root' })
export class AuthSessionService {
  private readonly storageKey = 'estancia-role';

  get role(): UserRole | null {
    if (typeof localStorage === 'undefined') return null;
    const value = localStorage.getItem(this.storageKey);
    return value === 'guest' || value === 'host' || value === 'admin' ? value : null;
  }

  signIn(role: UserRole): void {
    if (typeof localStorage !== 'undefined') localStorage.setItem(this.storageKey, role);
  }

  signOut(): void {
    if (typeof localStorage !== 'undefined') localStorage.removeItem(this.storageKey);
  }
}
