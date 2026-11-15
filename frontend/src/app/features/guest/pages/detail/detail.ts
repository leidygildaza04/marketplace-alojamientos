import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Accommodation, Quote } from '../../../../core/services/marketplace-data.service';
import { AccommodationReviews, MarketplaceApiService } from '../../../../core/services/marketplace-api.service';
import { AuthSessionService } from '../../../../core/services/auth-session.service';

@Component({
  selector: 'app-accommodation-detail',
  standalone: true,
  imports: [CurrencyPipe, DatePipe, FormsModule, RouterLink],
  templateUrl: './detail.html',
  styleUrl: './detail.css',
})
export class AccommodationDetail {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly api = inject(MarketplaceApiService);
  private readonly session = inject(AuthSessionService);
  alojamiento: Accommodation | undefined;
  cargando = true;
  get role() { return this.session.role; }
  readonly fechaMinima = this.fechaLocal(new Date());
  imagenNoDisponible = false;
  llegada = '';
  salida = '';
  cotizacion: Quote | null = null;
  error = '';
  avisoReserva = '';
  titular = '';
  numeroTarjeta = '';
  vencimiento = '';
  cvv = '';
  procesandoPago = false;
  resenas: AccommodationReviews = { calificacionPromedio: 0, cantidad: 0, resenas: [] };

  constructor() {
    const id = this.route.snapshot.paramMap.get('id') ?? '';
    this.route.queryParamMap.subscribe((params) => {
      this.llegada = params.get('llegada') ?? '';
      this.salida = params.get('salida') ?? '';
    });
    this.api.getAccommodation(id).subscribe({
      next: (item) => { this.alojamiento = { ...item, rating: this.resenas.calificacionPromedio }; this.cargando = false; },
      error: () => { this.alojamiento = undefined; this.cargando = false; },
    });
    this.api.getAccommodationReviews(id).subscribe({ next: (summary) => {
      this.resenas = summary;
      if (this.alojamiento) this.alojamiento = { ...this.alojamiento, rating: summary.calificacionPromedio };
    } });
  }

  cerrarSesion(): void { this.session.signOut(); }

  formatearNumeroTarjeta(value: string): void {
    const digits = (value ?? '').replace(/\D/g, '').slice(0, 16);
    this.numeroTarjeta = (digits.match(/.{1,4}/g) ?? []).join(' ');
  }

  formatearVencimiento(value: string): void {
    const digits = (value ?? '').replace(/\D/g, '').slice(0, 4);
    this.vencimiento = digits.length > 2 ? `${digits.slice(0, 2)}/${digits.slice(2)}` : digits;
  }

  limitarCvv(value: string): void {
    this.cvv = (value ?? '').replace(/\D/g, '').slice(0, 3);
  }

  get datosTarjetaCompletos(): boolean {
    const numeroDigitos = this.numeroTarjeta.replace(/\D/g, '').length;
    return !!this.titular.trim() && numeroDigitos === 16
      && /^\d{2}\/\d{2}$/.test(this.vencimiento) && this.cvv.length === 3;
  }

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
    this.api.quoteReservation({ accommodationId: this.alojamiento.id, arrival: this.llegada, departure: this.salida }).subscribe({
      next: (quote) => { this.cotizacion = quote; },
      error: () => { this.cotizacion = null; this.error = 'No fue posible cotizar estas fechas. Verifica que el alojamiento siga disponible.'; },
    });
  }

  continuarReserva(): void {
    if (!this.alojamiento || !this.cotizacion || !this.llegada || !this.salida) {
      this.error = 'Calcula primero una cotización válida para continuar.';
      return;
    }
    this.error = '';
    if (this.procesandoPago) return;
    this.procesandoPago = true;
    this.api.checkout({
      accommodationId: this.alojamiento.id,
      arrival: this.llegada,
      departure: this.salida,
      cardholder: this.titular,
      cardNumber: this.numeroTarjeta,
      expiry: this.vencimiento,
      cvv: this.cvv,
    }).subscribe({
      next: (result) => {
        this.procesandoPago = false;
        this.numeroTarjeta = '';
        this.cvv = '';
        if (result.paymentStatus === 'APROBADO') {
          this.avisoReserva = `${result.message} Reserva ${result.reservationCode} confirmada.`;
          void this.router.navigateByUrl('/reservas');
        } else {
          this.error = `${result.message} Referencia: ${result.paymentReference}`;
        }
      },
      error: (error: { error?: { message?: string; detail?: string; title?: string } }) => {
        this.procesandoPago = false;
        this.error = error.error?.detail ?? error.error?.message ?? error.error?.title
          ?? 'No se pudo procesar el pago de prueba. Verifica las fechas e intenta nuevamente.';
      },
    });
  }

  private fechaLocal(date: Date): string {
    return new Date(date.getTime() - date.getTimezoneOffset() * 60_000).toISOString().slice(0, 10);
  }
}
