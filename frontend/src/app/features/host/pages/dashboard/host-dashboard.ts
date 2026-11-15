
import { CurrencyPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthSessionService } from '../../../../core/services/auth-session.service';
import { ProfilePanel } from '../../../../core/components/profile-panel/profile-panel';
import { Accommodation } from '../../../../core/services/marketplace-data.service';
import { CatalogAccommodation, MarketplaceApiService } from '../../../../core/services/marketplace-api.service';

interface AvailabilityPeriod {
  propertyId: string;
  propertyTitle: string;
  start: string;
  end: string;
  status: 'DISPONIBLE' | 'RESERVADO';
}

@Component({
  selector: 'app-host-dashboard',
  standalone: true,
  imports: [CurrencyPipe, FormsModule, RouterLink, ProfilePanel],
  templateUrl: './host-dashboard.html',
  styleUrl: './host-dashboard.css',
})
export class HostDashboard {
  private readonly api = inject(MarketplaceApiService);
  private readonly session = inject(AuthSessionService);

  listings: Accommodation[] = [];

  mostrarFormulario = false;
  aviso = '';
  error = '';

  draft = {
    title: '',
    city: '',
    type: 'Apartamento',
    capacity: 1,
    pricePerNight: 0,
    description: '',
    image: '',
    amenities: '',
    rules: '',
  };

  periodStart = '';
  periodEnd = '';

  selectedListingId = '';

  readonly periods: AvailabilityPeriod[] = [];

  constructor() {
    this.api.getMyAccommodations().subscribe({
      next: (items) => {
        this.listings = items.map((item) => this.api.toAccommodation(item));
        this.selectedListingId = this.listings[0]?.id ?? '';
        this.listings.forEach((listing) => this.api.getAvailability(Number(listing.id)).subscribe({
          next: (periods) => {
            this.periods.push(...periods.map((period) => ({
              propertyId: String(period.alojamientoId), propertyTitle: listing.title,
              start: period.fechaInicio, end: period.fechaFin, status: period.estado === 'RESERVADO' ? 'RESERVADO' as const : 'DISPONIBLE' as const,
            })));
            this.periods.sort((a, b) => a.start.localeCompare(b.start));
          },
        }));
      },
      error: () => { this.error = 'No se pudieron cargar tus publicaciones.'; },
    });
  }

  cerrarSesion(): void {
    this.session.signOut();
  }

  get activeCount(): number {
    return this.listings.filter((item) => item.status === 'ACTIVA').length;
  }

  get pausedCount(): number {
    return this.listings.filter((item) => item.status === 'PAUSADA').length;
  }

  get draftCount(): number {
    return this.listings.filter((item) => item.status === 'BORRADOR').length;
  }

  crearBorrador(): void {
    this.error = '';
    this.aviso = '';

    // Validar título
    if (!this.draft.title.trim()) {
      this.error = 'El título es obligatorio.';
      return;
    }

    // Validar descripción
    if (!this.draft.description.trim()) {
      this.error = 'La descripción es obligatoria.';
      return;
    }

    // Validar ubicación
    if (!this.draft.city.trim()) {
      this.error = 'La ciudad o ubicación es obligatoria.';
      return;
    }

    // Validar tipo de alojamiento
    if (!this.draft.type.trim()) {
      this.error = 'Debes seleccionar el tipo de alojamiento.';
      return;
    }

    // Validar capacidad
    if (
      !Number.isFinite(this.draft.capacity) ||
      !Number.isInteger(this.draft.capacity) ||
      this.draft.capacity <= 0
    ) {
      this.error = 'La capacidad debe ser un número entero mayor que cero.';
      return;
    }

    // Validar precio
    if (
      !Number.isFinite(this.draft.pricePerNight) ||
      this.draft.pricePerNight <= 0
    ) {
      this.error = 'El precio por noche debe ser un número mayor que cero.';
      return;
    }

    const amenities = this.draft.amenities
      .split(',')
      .map((value) => value.trim())
      .filter(Boolean);

    const rules = this.draft.rules
      .split(',')
      .map((value) => value.trim())
      .filter(Boolean);

    this.api.createAccommodation({
      titulo: this.draft.title.trim(),
      descripcion: this.draft.description.trim(),
      direccion: this.draft.city.trim(),
      ciudad: this.draft.city.trim(),
      precioNoche: this.draft.pricePerNight,
      capacidad: this.draft.capacity,
      tipo: this.draft.type,
      imagen: this.draft.image.trim(),
      servicios: JSON.stringify(amenities),
      reglas: JSON.stringify(rules),
      cargoLimpieza: 0,
      politicaCancelacion: 'Flexible',
    }).subscribe({
      next: (created) => {
        const item = this.api.toAccommodation(created);
        this.listings = [...this.listings, item];
        this.selectedListingId = item.id;
        this.mostrarFormulario = false;
        this.aviso = 'Borrador guardado correctamente.';
        this.resetDraft();
      },
      error: () => { this.error = 'No se pudo guardar la publicación. Revisa la conexión y los datos.'; },
    });

  }

  cambiarEstado(item: Accommodation): void {
    if (item.status === 'BORRADOR') {
      const missing = [
        !item.description.trim() && 'descripción',
      ].filter(Boolean);

      if (missing.length) {
        this.error =
          'Completa la información mínima antes de activar: ' +
          missing.join(', ') +
          '.';
        return;
      }

    }
    this.error = '';
    const operation = item.status === 'ACTIVA'
      ? this.api.pauseAccommodation(Number(item.id))
      : this.api.activateAccommodation(Number(item.id));
    operation.subscribe({
      next: (updated: CatalogAccommodation) => {
        const mapped = this.api.toAccommodation(updated);
        this.listings = this.listings.map((current) => current.id === item.id ? { ...current, ...mapped } : current);
        this.aviso = `Estado actualizado: ${item.title} · ${mapped.status}.`;
      },
      error: () => { this.error = 'No se pudo cambiar el estado de la publicación.'; },
    });
  }

  private resetDraft(): void {
    this.draft = { title: '', city: '', type: 'Apartamento', capacity: 1, pricePerNight: 0, description: '', image: '', amenities: '', rules: '' };
  }

  agregarPeriodo(): void {
    this.error = '';

    if (
      !this.periodStart ||
      !this.periodEnd ||
      this.periodEnd <= this.periodStart
    ) {
      this.error = 'La salida debe ser posterior a la llegada.';
      return;
    }

    const listing = this.listings.find(
      (item) => item.id === this.selectedListingId,
    );

    if (!listing) {
      this.error = 'Selecciona un alojamiento para administrar su calendario.';
      return;
    }

    const overlap = this.periods.some(
      (period) =>
        period.propertyId === listing.id &&
        this.periodStart < period.end &&
        this.periodEnd > period.start,
    );

    if (overlap) {
      this.error = 'El periodo se superpone con otro periodo disponible.';
      return;
    }

    this.api.addAvailability(Number(listing.id), this.periodStart, this.periodEnd).subscribe({
      next: () => {
        this.periods.push({ propertyId: listing.id, propertyTitle: listing.title, start: this.periodStart, end: this.periodEnd, status: 'DISPONIBLE' });
        this.periods.sort((a, b) => a.start.localeCompare(b.start));
        this.periodStart = '';
        this.periodEnd = '';
        this.aviso = 'Periodo agregado al calendario.';
      },
      error: () => { this.error = 'No se pudo guardar el periodo. Verifica que no se superponga con otro.'; },
    });
  }
}
