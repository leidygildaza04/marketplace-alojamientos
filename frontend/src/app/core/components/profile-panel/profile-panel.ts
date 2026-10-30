import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthSessionService, UserProfile } from '../../services/auth-session.service';

@Component({
  selector: 'app-profile-panel',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './profile-panel.html',
  styleUrl: './profile-panel.css',
})
export class ProfilePanel {
  private readonly session = inject(AuthSessionService);
  open = false;
  profile: UserProfile | null = null;
  fullName = '';
  phone = '';
  error = '';
  saved = false;

  show(): void {
    this.profile = this.session.getProfile();
    this.fullName = this.profile?.fullName ?? '';
    this.phone = this.profile?.phone ?? '';
    this.error = '';
    this.saved = false;
    this.open = true;
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
    if (this.phone.trim() && !/^\+?[0-9][0-9\s()-]{5,18}$/.test(this.phone.trim())) {
      this.error = 'Ingresa un número de teléfono válido.';
      return;
    }
    this.session.updateProfile(this.fullName, this.phone);
    this.profile = this.session.getProfile();
    this.saved = true;
  }

  get roleLabel(): string {
    switch (this.profile?.role) {
      case 'host': return 'Anfitrión';
      case 'admin': return 'Administrador';
      default: return 'Huésped';
    }
  }
}
