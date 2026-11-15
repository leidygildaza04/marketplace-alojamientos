import { DatePipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthSessionService } from '../../../../core/services/auth-session.service';
import { ProfilePanel } from '../../../../core/components/profile-panel/profile-panel';
import { Accommodation } from '../../../../core/services/marketplace-data.service';
import { AuditEventRecord, MarketplaceApiService, ReviewRecord } from '../../../../core/services/marketplace-api.service';
import { IdentityApiService, IdentityUser } from '../../../../core/services/identity-api.service';

type AdminSection = 'resumen' | 'publicaciones' | 'usuarios' | 'auditoria';
interface MarketplaceUser { id: string; name: string; email: string; role: 'HUÉSPED' | 'ANFITRIÓN' | 'ADMIN'; status: 'ACTIVO' | 'RESTRINGIDO'; }
interface AuditEvent { id: number; actor: string; action: string; entity: string; result: string; createdAt: string; }
interface PendingAction { kind: 'listing' | 'user'; id: string; label: string; active?: boolean; }

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [DatePipe, FormsModule, RouterLink, ProfilePanel],
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.css',
})
export class AdminDashboard {
  private readonly api = inject(MarketplaceApiService);
  private readonly identityApi = inject(IdentityApiService);
  private readonly session = inject(AuthSessionService);

  listings: Accommodation[] = [];
  users: MarketplaceUser[] = [];
  auditEvents: AuditEvent[] = [];
  reviews: ReviewRecord[] = [];
  metrics = {
    totalReservas: 0, reservasConfirmadas: 0, estanciasCompletadas: 0,
    reservasCanceladas: 0, reservasPendientes: 0, resenasVisibles: 0,
    calificacionPromedio: 0, ingresosRegistrados: 0,
  };
  section: AdminSection = 'resumen';
  pending: PendingAction | null = null;
  reason = '';
  aviso = '';
  error = '';
  entityFilter = '';
  mostrarResenasOcultas = false;

  get reviewsToShow(): ReviewRecord[] {
    return this.reviews.filter((review) => review.visible !== this.mostrarResenasOcultas);
  }

  constructor() {
    this.cargarPublicaciones();
    this.cargarUsuarios();
    this.cargarMetricas();
    this.cargarResenas();
    this.cargarAuditoria();
  }

  cerrarSesion(): void { this.session.signOut(); }

  get activeListings(): number { return this.listings.filter((item) => item.status === 'ACTIVA').length; }
  get listedUsers(): number { return this.users.length; }
  get currentUserId(): string { return String(this.session.userId ?? ''); }
  get filteredAuditEvents(): AuditEvent[] {
    const term = this.entityFilter.trim().toLocaleLowerCase();
    return this.auditEvents.filter((event) => !term || `${event.entity} ${event.action} ${event.actor}`.toLocaleLowerCase().includes(term));
  }
  get cityDemand(): { city: string; count: number }[] {
    const counts = new Map<string, number>();
    this.listings.filter((item) => item.status === 'ACTIVA').forEach((item) => {
      const city = item.city || 'Sin ciudad';
      counts.set(city, (counts.get(city) ?? 0) + 1);
    });
    return Array.from(counts, ([city, count]) => ({ city, count }));
  }

  nombreAlojamiento(review: ReviewRecord): string {
    return this.listings.find((item) => item.id === String(review.alojamientoId))?.title ?? `Alojamiento ${review.alojamientoId}`;
  }

  solicitarBloqueo(item: Accommodation): void {
    this.pending = { kind: 'listing', id: item.id, label: item.title };
    this.reason = ''; this.error = '';
  }

  solicitarRestriccion(user: MarketplaceUser): void {
    this.pending = { kind: 'user', id: user.id, label: user.name, active: user.status !== 'ACTIVO' };
    this.reason = ''; this.error = '';
  }

  cerrarDialogo(): void { this.pending = null; this.error = ''; }

  confirmarAccion(): void {
    if (!this.pending || !this.reason.trim()) { this.error = 'Indica el motivo requerido para dejar trazabilidad.'; return; }
    const action = this.pending;
    const reason = this.reason.trim();
    if (action.kind === 'listing') {
      this.api.blockAccommodation(Number(action.id), reason).subscribe({
        next: (response) => {
          this.listings = this.listings.map((item) => item.id === action.id
            ? { ...item, ...this.api.toAccommodation(response) } : item);
          this.cerrarDialogo();
          this.aviso = 'Publicación bloqueada.';
          this.cargarAuditoria();
        },
        error: () => { this.error = 'No se pudo bloquear la publicación. Inténtalo nuevamente.'; },
      });
      return;
    }

    this.identityApi.setAdminUserActive(action.id, Boolean(action.active), reason).subscribe({
      next: (user) => {
        this.actualizarUsuario(user);
        this.cerrarDialogo();
        this.aviso = action.active ? 'Cuenta reactivada.' : 'Cuenta restringida y publicaciones del anfitrión pausadas.';
        this.guardarEvento(action.active ? 'CUENTA_REACTIVADA' : 'CUENTA_RESTRINGIDA', 'USUARIO', user.id, reason);
      },
      error: () => { this.error = 'No se pudo cambiar el estado. Puede tener reservas futuras pendientes.'; },
    });
  }

  cambiarVisibilidadResena(review: ReviewRecord): void {
    const visible = !review.visible;
    const detail = visible ? 'Reseña restaurada por administración' : 'Reseña ocultada por administración';
    this.api.setReviewVisible(review.id, visible).subscribe({
      next: (updated) => {
        this.reviews = this.reviews.map((item) => item.id === updated.id ? updated : item);
        this.aviso = visible ? 'Reseña restaurada.' : 'Reseña ocultada.';
        this.cargarMetricas();
        this.guardarEvento(visible ? 'RESENA_RESTAURADA' : 'RESENA_OCULTA', 'RESENA', updated.id, detail);
      },
      error: () => { this.error = 'No se pudo actualizar la reseña.'; },
    });
  }

  exportCsv(): void {
    const rows = [['Ciudad', 'Publicaciones activas'], ...this.cityDemand.map((item) => [item.city, String(item.count)])];
    const csv = rows.map((row) => row.map((cell) => `"${cell.replaceAll('"', '""')}"`).join(',')).join('\r\n');
    const blob = new Blob([`\uFEFF${csv}`], { type: 'text/csv;charset=utf-8' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url; link.download = 'reporte-marketplace.csv'; link.click(); URL.revokeObjectURL(url);
  }

  imprimir(): void { window.print(); }

  private cargarPublicaciones(): void {
    this.api.getAllAccommodationsForAdmin().subscribe({
      next: (items) => { this.listings = items.map((item) => this.api.toAccommodation(item)); },
      error: () => { this.error = 'No se pudieron cargar las publicaciones para moderación.'; },
    });
  }

  private cargarUsuarios(): void {
    this.identityApi.getAdminUsers().subscribe({
      next: (items) => { this.users = items.map((item) => this.mapUser(item)); },
      error: () => { this.error = 'No se pudieron cargar los usuarios reales desde Identity.'; },
    });
  }

  private cargarMetricas(): void {
    this.api.getAdminMetrics().subscribe({
      next: (metrics) => { this.metrics = metrics; },
      error: () => { this.error = 'No se pudieron cargar las métricas de reservas.'; },
    });
  }

  private cargarResenas(): void {
    this.api.getAdminReviews().subscribe({
      next: (items) => { this.reviews = items; },
      error: () => { this.error = 'No se pudieron cargar las reseñas reales.'; },
    });
  }

  private cargarAuditoria(): void {
    this.api.getAdminAuditEvents().subscribe({
      next: (items) => { this.auditEvents = items.map((item) => this.mapAudit(item)); },
      error: () => { this.error = 'No se pudo consultar la auditoría real.'; },
    });
  }

  private mapUser(user: IdentityUser): MarketplaceUser {
    const role = user.role === 'GUEST' ? 'HUÉSPED' : user.role === 'HOST' ? 'ANFITRIÓN' : 'ADMIN';
    return { id: String(user.id), name: user.fullName, email: user.email, role, status: user.active ? 'ACTIVO' : 'RESTRINGIDO' };
  }

  private actualizarUsuario(user: IdentityUser): void {
    const mapped = this.mapUser(user);
    this.users = this.users.map((item) => item.id === mapped.id ? mapped : item);
  }

  private mapAudit(event: AuditEventRecord): AuditEvent {
    const detail = event.detalle ? ` · ${event.detalle}` : '';
    return {
      id: event.idEvento,
      actor: event.idUsuario == null ? 'Sistema' : `Usuario #${event.idUsuario}`,
      action: event.accion,
      entity: `${event.entidad} #${event.idEntidad}${detail}`,
      result: event.resultado,
      createdAt: event.fechaEvento,
    };
  }

  private guardarEvento(action: string, entity: string, entityId: number, detail: string): void {
    this.api.recordAdminAuditEvent({ accion: action, entidad: entity, idEntidad: entityId, detalle: detail, resultado: 'EXITOSO' })
      .subscribe({ next: () => this.cargarAuditoria(), error: () => { this.error = 'La acción se guardó, pero no se pudo registrar en auditoría.'; } });
  }
}
