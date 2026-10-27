import { CurrencyPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthSessionService } from '../../../../core/services/auth-session.service';
import { Accommodation, MarketplaceDataService } from '../../../../core/services/marketplace-data.service';

interface AvailabilityPeriod { propertyId: string; propertyTitle: string; start: string; end: string; status: 'DISPONIBLE' | 'RESERVADO'; }

@Component({
  selector: 'app-host-dashboard',
  standalone: true,
  imports: [CurrencyPipe, FormsModule, RouterLink],
  templateUrl: './host-dashboard.html',
  styleUrl: './host-dashboard.css',
})
export class HostDashboard {
  private readonly marketplace = inject(MarketplaceDataService);
  private readonly session = inject(AuthSessionService);
  readonly listings = this.marketplace.accommodations;
  mostrarFormulario = false;
  aviso = '';
  error = '';
  draft = { title: '', city: '', type: 'Apartamento', capacity: 1, pricePerNight: 0, description: '', image: '', amenities: '', rules: '' };
  periodStart = '';
  periodEnd = '';
  selectedListingId = this.marketplace.accommodations[0]?.id ?? '';
  readonly periods: AvailabilityPeriod[] = [];

  cerrarSesion(): void { this.session.signOut(); }

  get activeCount(): number { return this.listings.filter((item) => item.status === 'ACTIVA').length; }
  get pausedCount(): number { return this.listings.filter((item) => item.status === 'PAUSADA').length; }
  get draftCount(): number { return this.listings.filter((item) => item.status === 'BORRADOR').length; }

  crearBorrador(): void {
    this.error = '';
    if (!this.draft.title.trim() || !this.draft.city.trim() || this.draft.capacity < 1 || this.draft.pricePerNight <= 0) {
      this.error = 'Completa título, ciudad, capacidad mayor que cero y precio positivo.';
      return;
    }
    const item: Accommodation = {
      id: `alojamiento-${Date.now()}`,
      title: this.draft.title.trim(), city: this.draft.city.trim(), country: 'Colombia', type: this.draft.type,
      capacity: this.draft.capacity, pricePerNight: this.draft.pricePerNight, cleaningFee: 0, rating: 0,
      image: this.draft.image.trim(),
      description: this.draft.description.trim(),
      amenities: this.draft.amenities.split(',').map((value) => value.trim()).filter(Boolean),
      rules: this.draft.rules.split(',').map((value) => value.trim()).filter(Boolean),
      cancellationPolicy: 'Flexible', status: 'BORRADOR', host: 'Anfitrión Estancia', favorite: false,
    };
    this.marketplace.accommodations.push(item);
    this.draft = { title: '', city: '', type: 'Apartamento', capacity: 1, pricePerNight: 0, description: '', image: '', amenities: '', rules: '' };
    this.mostrarFormulario = false;
    this.aviso = 'Borrador guardado correctamente.';
  }

  cambiarEstado(item: Accommodation): void {
    if (item.status === 'ACTIVA') item.status = 'PAUSADA';
    else if (item.status === 'BORRADOR') {
      const missing = [
        !item.description.trim() && 'descripción',
        !item.image.trim() && 'imagen',
        !item.amenities.length && 'servicios',
        !item.rules.length && 'reglas',
      ].filter(Boolean);
      if (missing.length) {
        this.error = 'Completa la información mínima antes de activar: ' + missing.join(', ') + '.';
        return;
      }
      item.status = 'ACTIVA';
    } else if (item.status === 'PAUSADA') item.status = 'ACTIVA';
    this.error = '';
    this.aviso = `Estado actualizado: ${item.title} · ${item.status}.`;
  }

  agregarPeriodo(): void {
    this.error = '';
    if (!this.periodStart || !this.periodEnd || this.periodEnd <= this.periodStart) {
      this.error = 'La salida debe ser posterior a la llegada.';
      return;
    }
    const listing = this.listings.find((item) => item.id === this.selectedListingId);
    if (!listing) {
      this.error = 'Selecciona un alojamiento para administrar su calendario.';
      return;
    }
    const overlap = this.periods.some((period) =>
      period.propertyId === listing.id && this.periodStart < period.end && this.periodEnd > period.start,
    );
    if (overlap) {
      this.error = 'El periodo se superpone con otro periodo disponible.';
      return;
    }
    this.periods.push({ propertyId: listing.id, propertyTitle: listing.title, start: this.periodStart, end: this.periodEnd, status: 'DISPONIBLE' });
    this.periods.sort((a, b) => a.start.localeCompare(b.start));
    this.periodStart = '';
    this.periodEnd = '';
    this.aviso = 'Periodo agregado al calendario.';
  }
}
