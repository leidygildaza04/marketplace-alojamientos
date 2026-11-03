
import { CurrencyPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthSessionService } from '../../../../core/services/auth-session.service';
import { ProfilePanel } from '../../../../core/components/profile-panel/profile-panel';
import {
  Accommodation,
  MarketplaceDataService,
} from '../../../../core/services/marketplace-data.service';

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
  private readonly marketplace = inject(MarketplaceDataService);
  private readonly session = inject(AuthSessionService);

  readonly listings = this.marketplace.accommodations;

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

  selectedListingId = this.marketplace.accommodations[0]?.id ?? '';

  readonly periods: AvailabilityPeriod[] = [];

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

    // Validar imagen
    if (!this.draft.image.trim()) {
      this.error = 'La imagen del alojamiento es obligatoria.';
      return;
    }

    // Validar servicios
    const amenities = this.draft.amenities
      .split(',')
      .map((value) => value.trim())
      .filter(Boolean);

    if (amenities.length === 0) {
      this.error = 'Debes ingresar al menos un servicio.';
      return;
    }

    // Validar reglas
    const rules = this.draft.rules
      .split(',')
      .map((value) => value.trim())
      .filter(Boolean);

    if (rules.length === 0) {
      this.error = 'Debes ingresar al menos una regla.';
      return;
    }

    // Crear publicación como borrador
    const item: Accommodation = {
      id: `alojamiento-${Date.now()}`,
      title: this.draft.title.trim(),
      city: this.draft.city.trim(),
      country: 'Colombia',
      type: this.draft.type,
      capacity: this.draft.capacity,
      pricePerNight: this.draft.pricePerNight,
      cleaningFee: 0,
      rating: 0,
      image: this.draft.image.trim(),
      description: this.draft.description.trim(),
      amenities,
      rules,
      cancellationPolicy: 'Flexible',
      status: 'BORRADOR',
      host: 'Anfitrión Travesía',
      favorite: false,
    };

    this.marketplace.accommodations.push(item);

    // Limpiar formulario
    this.draft = {
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

    this.mostrarFormulario = false;
    this.aviso = 'Borrador guardado correctamente.';
  }

  cambiarEstado(item: Accommodation): void {
    if (item.status === 'ACTIVA') {
      item.status = 'PAUSADA';
    } else if (item.status === 'BORRADOR') {
      const missing = [
        !item.description.trim() && 'descripción',
        !item.image.trim() && 'imagen',
        !item.amenities.length && 'servicios',
        !item.rules.length && 'reglas',
      ].filter(Boolean);

      if (missing.length) {
        this.error =
          'Completa la información mínima antes de activar: ' +
          missing.join(', ') +
          '.';
        return;
      }

      item.status = 'ACTIVA';
    } else if (item.status === 'PAUSADA') {
      item.status = 'ACTIVA';
    }

    this.error = '';
    this.aviso = `Estado actualizado: ${item.title} · ${item.status}.`;
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

    this.periods.push({
      propertyId: listing.id,
      propertyTitle: listing.title,
      start: this.periodStart,
      end: this.periodEnd,
      status: 'DISPONIBLE',
    });

    this.periods.sort((a, b) => a.start.localeCompare(b.start));

    this.periodStart = '';
    this.periodEnd = '';

    this.aviso = 'Periodo agregado al calendario.';
  }
}
