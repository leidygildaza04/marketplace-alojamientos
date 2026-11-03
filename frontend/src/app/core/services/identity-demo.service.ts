import { Injectable } from '@angular/core';

export type DemoApiRole = 'GUEST' | 'HOST' | 'ADMIN';

export interface DemoUserResponse {
  id: number;
  email: string;
  fullName: string;
  phone: string | null;
  role: DemoApiRole;
  active: boolean;
}

export interface DemoRegisterRequest {
  email: string;
  password: string;
  fullName: string;
  role: 'GUEST' | 'HOST';
}

interface DemoUser extends DemoUserResponse {
  password: string;
}

interface LocalResetToken {
  email: string;
  expiresAt: string;
}

@Injectable({ providedIn: 'root' })
export class IdentityDemoService {
  private readonly usersKey = 'travesia-demo-users';
  private readonly tokensKey = 'travesia-demo-reset-tokens';

  login(email: string, password: string): { token: string; role: DemoApiRole; expiresInMinutes: number } | null {
    const user = this.readUsers().find(
      (item) => item.email.toLowerCase() === email.trim().toLowerCase()
        && item.password === password
        && item.active,
    );
    return user
      ? { token: `demo-${user.id}-${Date.now()}`, role: user.role, expiresInMinutes: 60 }
      : null;
  }

  register(request: DemoRegisterRequest): DemoUserResponse {
    const users = this.readUsers();
    const email = request.email.trim().toLowerCase();
    if (users.some((user) => user.email.toLowerCase() === email)) {
      throw new Error('Ya existe una cuenta con ese correo.');
    }

    const user: DemoUser = {
      id: Date.now(),
      email,
      password: request.password,
      fullName: request.fullName.trim(),
      phone: null,
      role: request.role,
      active: true,
    };
    users.push(user);
    this.writeUsers(users);
    return this.toResponse(user);
  }

  getMe(email: string): DemoUserResponse | null {
    const user = this.readUsers().find((item) => item.email.toLowerCase() === email.toLowerCase());
    return user ? this.toResponse(user) : null;
  }

  updateMe(email: string, fullName: string, phone: string | null): DemoUserResponse | null {
    const users = this.readUsers();
    const user = users.find((item) => item.email.toLowerCase() === email.toLowerCase());
    if (!user) return null;
    user.fullName = fullName.trim();
    user.phone = phone?.trim() || null;
    this.writeUsers(users);
    return this.toResponse(user);
  }

  requestRecovery(email: string): { message: string; token: string; expiresAt: string } {
    const normalizedEmail = email.trim().toLowerCase();
    if (!this.readUsers().some((user) => user.email.toLowerCase() === normalizedEmail)) {
      throw new Error('No existe una cuenta con ese correo.');
    }

    const randomBytes = new Uint8Array(32);
    globalThis.crypto.getRandomValues(randomBytes);
    const token = Array.from(randomBytes, (byte) => byte.toString(16).padStart(2, '0')).join('');
    const expiresAt = new Date(Date.now() + 30 * 60 * 1000).toISOString();
    const tokens = this.readTokens();
    tokens[token] = { email: normalizedEmail, expiresAt };
    this.writeTokens(tokens);
    return { message: 'Token de demostración generado; no se envió correo.', token, expiresAt };
  }

  resetPassword(token: string, newPassword: string): boolean {
    const tokens = this.readTokens();
    const reset = tokens[token.trim()];
    if (!reset || Date.parse(reset.expiresAt) <= Date.now()) return false;

    const users = this.readUsers();
    const user = users.find((item) => item.email.toLowerCase() === reset.email);
    if (!user) return false;
    user.password = newPassword;
    delete tokens[token.trim()];
    this.writeUsers(users);
    this.writeTokens(tokens);
    return true;
  }

  private readUsers(): DemoUser[] {
    if (typeof localStorage !== 'undefined') {
      try {
        const stored: unknown = JSON.parse(localStorage.getItem(this.usersKey) ?? 'null');
        if (Array.isArray(stored)) return stored as DemoUser[];
      } catch { /* Recupera un estado vacío si el almacenamiento local no puede leerse. */ }
      const seed = this.seedUsers();
      this.writeUsers(seed);
      return seed;
    }
    return this.seedUsers();
  }

  private writeUsers(users: DemoUser[]): void {
    if (typeof localStorage !== 'undefined') localStorage.setItem(this.usersKey, JSON.stringify(users));
  }

  private readTokens(): Record<string, LocalResetToken> {
    if (typeof localStorage === 'undefined') return {};
    try {
      const stored: unknown = JSON.parse(localStorage.getItem(this.tokensKey) ?? '{}');
      return stored && typeof stored === 'object' && !Array.isArray(stored)
        ? stored as Record<string, LocalResetToken>
        : {};
    } catch {
      return {};
    }
  }

  private writeTokens(tokens: Record<string, LocalResetToken>): void {
    if (typeof localStorage !== 'undefined') localStorage.setItem(this.tokensKey, JSON.stringify(tokens));
  }

  private toResponse(user: DemoUser): DemoUserResponse {
    const { password: _password, ...response } = user;
    return response;
  }

  private seedUsers(): DemoUser[] {
    return [
      { id: 1, email: 'user@travesia.test', password: 'user123', fullName: 'Usuario de prueba', phone: null, role: 'GUEST', active: true },
      { id: 2, email: 'host@travesia.test', password: 'host123', fullName: 'Anfitrión de prueba', phone: null, role: 'HOST', active: true },
      { id: 3, email: 'admin@travesia.test', password: 'admin123', fullName: 'Administrador de prueba', phone: null, role: 'ADMIN', active: true },
    ];
  }
}
