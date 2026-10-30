import { Injectable } from '@angular/core';

export type UserRole = 'guest' | 'host' | 'admin';

export interface UserProfile {
  username: string;
  fullName: string;
  email: string;
  phone: string;
  role: UserRole;
}

@Injectable({ providedIn: 'root' })
export class AuthSessionService {
  private readonly storageKey = 'estancia-role';
  private readonly usernameKey = 'estancia-username';

  get role(): UserRole | null {
    if (typeof localStorage === 'undefined') return null;
    const value = localStorage.getItem(this.storageKey);
    return value === 'guest' || value === 'host' || value === 'admin' ? value : null;
  }

  signIn(username: string, role: UserRole): void {
    if (typeof localStorage === 'undefined') return;
    localStorage.setItem(this.storageKey, role);
    localStorage.setItem(this.usernameKey, username);
  }

  signOut(): void {
    if (typeof localStorage === 'undefined') return;
    localStorage.removeItem(this.storageKey);
    localStorage.removeItem(this.usernameKey);
  }

  getProfile(): UserProfile | null {
    if (typeof localStorage === 'undefined') return null;
    const username = localStorage.getItem(this.usernameKey);
    const role = this.role;
    if (!username || !role) return null;

    const profileKey = `estancia-profile:${username}`;
    const saved = localStorage.getItem(profileKey);
    if (saved) {
      try {
        const profile = JSON.parse(saved) as UserProfile;
        return { ...profile, username, role };
      } catch {
        localStorage.removeItem(profileKey);
      }
    }

    const examples: Record<string, Omit<UserProfile, 'username' | 'role'>> = {
      user: { fullName: 'Usuario de prueba', email: 'user@travesia.test', phone: '' },
      host: { fullName: 'Anfitrión de prueba', email: 'host@travesia.test', phone: '' },
      admin: { fullName: 'Administrador de prueba', email: 'admin@travesia.test', phone: '' },
    };
    return { username, role, ...(examples[username] ?? { fullName: username, email: '', phone: '' }) };
  }

  updateProfile(fullName: string, phone: string): void {
    const profile = this.getProfile();
    if (typeof localStorage === 'undefined' || !profile) return;
    localStorage.setItem(
      `estancia-profile:${profile.username}`,
      JSON.stringify({ ...profile, fullName: fullName.trim(), phone: phone.trim() }),
    );
  }
}
