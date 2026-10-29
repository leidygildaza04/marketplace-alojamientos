import { CurrencyPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MarketplaceDataService, Quote } from '../../../../core/services/marketplace-data.service';
import { AuthSessionService } from '../../../../core/services/auth-session.service';

@Component({
  selector: 'app-accommodation-detail',
  standalone: true,
  imports: [CurrencyPipe, FormsModule, RouterLink],
  templateUrl: './detail.html',
  styleUrl: './detail.css',
})
export class AccommodationDetail {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly marketplace = inject(MarketplaceDataService);
  private readonly session = inject(AuthSessionService);
  readonly alojamiento = this.marketplace.getById(this.route.snapshot.paramMap.get('id') ?? '');
  get role() { return this.session.role; }
  readonly fechaMinima = this.fechaLocal(new Date());
  imagenNoDisponible = false;
  llegada = '';
  salida = '';
  desayuno = false;
  cotizacion: Quote | null = null;
  error = '';
  avisoReserva = '';

  cerrarSesion(): void { this.session.signOut(); }

  cotizar(): void {
    this.error = '';
    this.avisoReserva = '';
    if (!this.alojamiento || !this.llegada || !this.salida) {
      this.error = 'Selecciona las fechas de llegada y salida para ver el desglose.';
      return;
    }
    if (this.salida <= this.llegada) {
      this.error = 'La salida debe ser posterior a la llegada.';
      return;
    }
    this.cotizacion = this.marketplace.calculateQuote(this.alojamiento, this.llegada, this.salida, this.desayuno);
  }

  continuarReserva(): void {
    if (!this.alojamiento || !this.cotizacion || !this.llegada || !this.salida) {
      this.error = 'Calcula primero una cotización válida para continuar.';
      return;
    }
    this.marketplace.createReservation({
      property: this.alojamiento.title,
      city: this.alojamiento.city,
      arrival: this.llegada,
      departure: this.salida,
      total: this.cotizacion.total,
      policy: this.alojamiento.cancellationPolicy === 'Flexible' ? 'Flexible' : 'Moderada',
    });
    void this.router.navigateByUrl('/reservas');
  }

  private fechaLocal(date: Date): string {
    return new Date(date.getTime() - date.getTimezoneOffset() * 60_000).toISOString().slice(0, 10);
  }
}
