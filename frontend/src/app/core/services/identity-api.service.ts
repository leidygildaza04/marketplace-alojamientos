import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  fullName: string;
  role: 'GUEST' | 'HOST';
}

export interface IdentityUser {
  id: number;
  email: string;
  fullName: string;
  phone: string | null;
  role: IdentityRole;
  active: boolean;
}

export type IdentityRole = 'GUEST' | 'HOST' | 'ADMIN';

export interface AuthResponse {
  token: string;
  role: IdentityRole;
  expiresInMinutes: number;
}

export interface PasswordRecoveryResponse {
  message: string;
  token: string;
  expiresAt: string;
}

@Injectable({ providedIn: 'root' })
export class IdentityApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.identityApiProxyBaseUrl.replace(/\/$/, '');
  private readonly headers = new HttpHeaders({ 'Content-Type': 'application/json' });

  login(email: string, password: string): Observable<AuthResponse> {
    const request: LoginRequest = { email, password };
    return this.http.post<AuthResponse>(
      `${this.baseUrl}/api/auth/login`,
      request,
      { headers: this.headers },
    );
  }

  register(request: RegisterRequest): Observable<IdentityUser> {
    return this.http.post<IdentityUser>(`${this.baseUrl}/api/auth/register`, request, { headers: this.headers });
  }

  getMe(): Observable<IdentityUser> {
    return this.http.get<IdentityUser>(`${this.baseUrl}/api/users/me`);
  }

  getAdminUsers(): Observable<IdentityUser[]> {
    return this.http.get<IdentityUser[]>(`${this.baseUrl}/api/admin/users`);
  }

  setAdminUserActive(userId: string, active: boolean, motivo: string): Observable<IdentityUser> {
    return this.http.patch<IdentityUser>(`${this.baseUrl}/api/admin/users/${encodeURIComponent(userId)}/status`, {
      active, motivo,
    });
  }

  updateMe(fullName: string, phone: string | null): Observable<IdentityUser> {
    return this.http.put<IdentityUser>(`${this.baseUrl}/api/users/me`, { fullName, phone }, { headers: this.headers });
  }

  requestPasswordRecovery(email: string): Observable<PasswordRecoveryResponse> {
    return this.http.post<PasswordRecoveryResponse>(`${this.baseUrl}/api/auth/password/recovery`, { email }, { headers: this.headers });
  }

  resetPassword(token: string, newPassword: string): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/api/auth/password/reset`, { token, newPassword }, { headers: this.headers });
  }
}
