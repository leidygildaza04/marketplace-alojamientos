import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { IdentityApiService, IdentityRole, IdentityUser } from '../../services/identity-api.service';
import { AuthSessionService } from '../../services/auth-session.service';

@Component({
  selector: 'app-profile-panel',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './profile-panel.html',
  styleUrl: './profile-panel.css',
})
export class ProfilePanel {
  private readonly session = inject(AuthSessionService);
  private readonly identityApi = inject(IdentityApiService);
  open = false;
  profile: IdentityUser | null = null;
  fullName = '';
  phone = '';
  error = '';
  saved = false;
  loading = false;

  show(): void {
    this.profile = null;
    this.fullName = '';
    this.phone = '';
    this.error = '';
    this.saved = false;
    this.open = true;
    this.loading = true;
    this.identityApi.getMe().subscribe({
      next: (profile) => { this.profile = profile; this.fullName = profile.fullName; this.phone = profile.phone ?? ''; this.loading = false; },
      error: () => { this.error = 'No se pudo cargar el perfil. Inicia sesión nuevamente.'; this.loading = false; },
    });
  }

  close(): void {
    this.open = false;
  }

  save(): void {
    this.error = '';
    this.saved = false;
    if (!this.fullName.trim()) {
      this.error = 'Ingresa tu nombre completo.';
      return;
    }
    if (this.fullName.trim().length > 150) {
      this.error = 'El nombre no puede superar 150 caracteres.';
      return;
    }
    if (this.phone.trim() && !/^\+?[0-9][0-9\s().-]{5,29}$/.test(this.phone.trim())) {
      this.error = 'Ingresa un teléfono válido de máximo 30 caracteres.';
      return;
    }

    this.identityApi.updateMe(this.fullName.trim(), this.phone.trim() || null).subscribe({
      next: (profile) => { this.profile = profile; this.fullName = profile.fullName; this.phone = profile.phone ?? ''; this.saved = true; },
      error: () => { this.error = 'No se pudo actualizar el perfil. Inténtalo nuevamente.'; },
    });
  }

  get roleLabel(): string {
    switch (this.profile?.role as IdentityRole | undefined) {
      case 'HOST': return 'Anfitrión';
      case 'ADMIN': return 'Administrador';
      default: return 'Huésped';
    }
  }
}
