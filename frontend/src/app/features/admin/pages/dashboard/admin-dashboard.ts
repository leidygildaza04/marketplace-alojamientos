import { DatePipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthSessionService } from '../../../../core/services/auth-session.service';
import { Accommodation, MarketplaceDataService } from '../../../../core/services/marketplace-data.service';

type AdminSection = 'resumen' | 'publicaciones' | 'usuarios' | 'auditoria';
interface MarketplaceUser { id: string; name: string; email: string; role: 'HUÉSPED' | 'ANFITRIÓN'; status: 'ACTIVO' | 'RESTRINGIDO'; }
interface AuditEvent { actor: string; action: string; entity: string; result: string; createdAt: Date; }
interface PendingAction { kind: 'listing' | 'user'; id: string; label: string; }

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [DatePipe, FormsModule, RouterLink],
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.css',
})
export class AdminDashboard {
  private readonly marketplace = inject(MarketplaceDataService);
  private readonly session = inject(AuthSessionService);
  readonly listings = this.marketplace.accommodations;
  readonly users: MarketplaceUser[] = [
    { id: 'u-101', name: 'Camila Ríos', email: 'camila.rios@example.com', role: 'HUÉSPED', status: 'ACTIVO' },
    { id: 'u-102', name: 'Andrés Mora', email: 'andres.mora@example.com', role: 'ANFITRIÓN', status: 'ACTIVO' },
    { id: 'u-103', name: 'Laura Díaz', email: 'laura.diaz@example.com', role: 'HUÉSPED', status: 'ACTIVO' },
  ];
  readonly auditEvents: AuditEvent[] = [
    { actor: 'Sistema', action: 'LOGIN', entity: 'Usuario u-101', result: 'Éxito', createdAt: new Date('2026-10-20T10:35:00') },
    { actor: 'Anfitrión', action: 'PUBLICACION_CREADA', entity: 'apartamento-bogota', result: 'BORRADOR', createdAt: new Date('2026-10-20T11:10:00') },
    { actor: 'Huésped', action: 'COTIZACION', entity: 'cabana-villa-leyva', result: 'Completado', createdAt: new Date('2026-10-21T14:20:00') },
  ];
  section: AdminSection = 'resumen';
  pending: PendingAction | null = null;
  reason = '';
  aviso = '';
  error = '';
  entityFilter = '';
  private readonly reviewExamples = [
    { id: 'rev-01', property: 'Refugio entre montañas', author: 'Huésped', text: 'Un lugar tranquilo, cómodo y muy bien ubicado.' },
  ];
  readonly reviews = this.reviewExamples;

  cerrarSesion(): void { this.session.signOut(); }

  get activeListings(): number { return this.listings.filter((item) => item.status === 'ACTIVA').length; }
  get listedUsers(): number { return this.users.length; }
  get filteredAuditEvents(): AuditEvent[] {
    const term = this.entityFilter.trim().toLocaleLowerCase();
    return this.auditEvents.filter((event) => !term || `${event.entity} ${event.action} ${event.actor}`.toLocaleLowerCase().includes(term));
  }
  get cityDemand(): { city: string; count: number }[] {
    const counts = new Map<string, number>();
    this.listings.filter((item) => item.status === 'ACTIVA').forEach((item) => counts.set(item.city, (counts.get(item.city) ?? 0) + 1));
    return Array.from(counts, ([city, count]) => ({ city, count }));
  }

  solicitarBloqueo(item: Accommodation): void { this.pending = { kind: 'listing', id: item.id, label: item.title }; this.reason = ''; this.error = ''; }
  solicitarRestriccion(user: MarketplaceUser): void { this.pending = { kind: 'user', id: user.id, label: user.name }; this.reason = ''; this.error = ''; }
  cerrarDialogo(): void { this.pending = null; this.error = ''; }

  confirmarAccion(): void {
    if (!this.pending || !this.reason.trim()) { this.error = 'Indica el motivo requerido para dejar trazabilidad.'; return; }
    if (this.pending.kind === 'listing') {
      const item = this.listings.find((entry) => entry.id === this.pending?.id);
      if (item) item.status = 'BLOQUEADA';
    } else {
      const user = this.users.find((entry) => entry.id === this.pending?.id);
      if (user) {
        user.status = 'RESTRINGIDO';
        if (user.role === 'ANFITRIÓN') this.listings.filter((item) => item.host.toLowerCase().includes('anfitrión') && item.status === 'ACTIVA').forEach((item) => item.status = 'PAUSADA');
      }
    }
    const action = this.pending.kind === 'listing' ? 'PUBLICACION_BLOQUEADA' : 'USUARIO_RESTRINGIDO';
    this.auditEvents.unshift({ actor: 'Administrador', action, entity: `${this.pending.label} · Motivo: ${this.reason.trim()}`, result: 'Completado', createdAt: new Date() });
    this.aviso = 'Cambios guardados correctamente.';
    this.pending = null;
    this.reason = '';
  }

  ocultarResena(id: string): void {
    const index = this.reviewExamples.findIndex((review) => review.id === id);
    if (index >= 0) this.reviewExamples.splice(index, 1);
    this.auditEvents.unshift({ actor: 'Administrador', action: 'RESENA_OCULTA', entity: id, result: 'Completado', createdAt: new Date() });
    this.aviso = 'Reseña ocultada.';
  }

  exportCsv(): void {
    const rows = [['Ciudad', 'Publicaciones activas'], ...this.cityDemand.map((item) => [item.city, String(item.count)])];
    const csv = rows.map((row) => row.map((cell) => `"${cell.replaceAll('"', '""')}"`).join(',')).join('\r\n');
    const blob = new Blob([`\uFEFF${csv}`], { type: 'text/csv;charset=utf-8' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = 'reporte-marketplace.csv';
    link.click();
    URL.revokeObjectURL(url);
  }

  imprimir(): void { window.print(); }
}
