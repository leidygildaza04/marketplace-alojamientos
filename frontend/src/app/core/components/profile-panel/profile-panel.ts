import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DemoApiRole, DemoUserResponse, IdentityDemoService } from '../../services/identity-demo.service';
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
  private readonly identityDemo = inject(IdentityDemoService);
  open = false;
  profile: DemoUserResponse | null = null;
  fullName = '';
  phone = '';
  error = '';
  saved = false;

  show(): void {
    const email = this.session.email;
    this.profile = email ? this.identityDemo.getMe(email) : null;
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
    if (this.fullName.trim().length > 150) {
      this.error = 'El nombre no puede superar 150 caracteres.';
      return;
    }
    if (this.phone.trim() && !/^\+?[0-9][0-9\s().-]{5,29}$/.test(this.phone.trim())) {
      this.error = 'Ingresa un teléfono válido de máximo 30 caracteres.';
      return;
    }

    const email = this.session.email;
    this.profile = email
      ? this.identityDemo.updateMe(email, this.fullName, this.phone.trim() || null)
      : null;
    if (!this.profile) {
      this.error = 'No se pudo actualizar el perfil de demostración.';
      return;
    }
    this.fullName = this.profile.fullName;
    this.phone = this.profile.phone ?? '';
    this.saved = true;
  }

  get roleLabel(): string {
    switch (this.profile?.role as DemoApiRole | undefined) {
      case 'HOST': return 'Anfitrión';
      case 'ADMIN': return 'Administrador';
      default: return 'Huésped';
    }
  }
}
