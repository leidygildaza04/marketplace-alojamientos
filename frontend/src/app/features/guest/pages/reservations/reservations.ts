import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { GuestReservation } from '../../../../core/services/marketplace-data.service';
import { MarketplaceApiService, ReviewRecord } from '../../../../core/services/marketplace-api.service';

@Component({
  selector: 'app-guest-reservations',
  standalone: true,
  imports: [CurrencyPipe, DatePipe, FormsModule, RouterLink],
  templateUrl: './reservations.html',
  styleUrl: './reservations.css',
})
export class GuestReservations {
  private readonly api = inject(MarketplaceApiService);

  reservas: GuestReservation[] = [];
  cargando = true;
  error = '';
  reservaParaCancelar: GuestReservation | null = null;
  motivo = '';
  aviso = '';
  readonly resenasEnviadas = new Set<string>();
  reservaParaCalificar: GuestReservation | null = null;
  calificacion = 5;
  comentarioResena = '';
  enviandoResena = false;

  constructor() {
    this.api.getReservations().subscribe({
      next: (reservations) => {
        const visibles = reservations.filter((reservation) => reservation.status !== 'RECHAZADA');
        this.reservas = visibles.map((reservation) => ({
          id: reservation.id, code: reservation.code, property: `Alojamiento ${reservation.accommodationId}`,
          city: '', arrival: reservation.arrival, departure: reservation.departure, total: reservation.total,
          status: reservation.status, policy: 'Flexible',
        }));
        this.api.getMyReviews().subscribe({
          next: (reviews) => reviews.forEach((review) => this.resenasEnviadas.add(String(review.reservaId))),
        });
        visibles.forEach((reservation) => this.api.getAccommodation(reservation.accommodationId).subscribe({
          next: (property) => {
            this.reservas = this.reservas.map((item) => item.id === reservation.id
              ? { ...item, property: property.title, city: property.city,
                policy: property.cancellationPolicy === 'Moderada' ? 'Moderada' : 'Flexible' } : item);
          },
        }));
        this.cargando = false;
      },
      error: () => { this.error = 'No se pudieron cargar tus reservas.'; this.cargando = false; },
    });
  }

  abrirCalificacion(reserva: GuestReservation): void {
    this.reservaParaCalificar = reserva;
    this.calificacion = 5;
    this.comentarioResena = '';
    this.error = '';
  }

  enviarCalificacion(): void {
    const reserva = this.reservaParaCalificar;
    if (!reserva?.id || this.enviandoResena || this.calificacion < 1 || this.calificacion > 5) return;
    this.enviandoResena = true;
    this.api.createReview(reserva.id, this.calificacion, this.comentarioResena.trim()).subscribe({
      next: (review: ReviewRecord) => {
        this.enviandoResena = false;
        this.resenasEnviadas.add(String(review.reservaId));
        this.reservaParaCalificar = null;
        this.aviso = '¡Gracias! Tu calificación se guardó y ya aparece en el alojamiento.';
      },
      error: () => {
        this.enviandoResena = false;
        this.error = 'No se pudo guardar la calificación. Confirma que la estancia terminó y vuelve a intentar.';
      },
    });
  }

  abrirCancelacion(reserva: GuestReservation): void {
    this.reservaParaCancelar = reserva;
    this.motivo = '';
    this.aviso = '';
  }

  cancelarReserva(): void {
    const reservationId = this.reservaParaCancelar?.id;
    if (!this.reservaParaCancelar || !reservationId || !this.motivo.trim()) return;
    const selected = this.reservaParaCancelar;
    this.api.cancelReservation(reservationId).subscribe({
      next: () => {
        this.reservas = this.reservas.map((item) => item.id === selected.id ? { ...item, status: 'CANCELADA' } : item);
        this.aviso = `La reserva ${selected.code} fue cancelada y las fechas quedaron liberadas. La simulación de pago no genera cobros ni reembolsos reales.`;
        this.reservaParaCancelar = null;
      },
      error: () => { this.error = 'No se pudo cancelar la reserva. Inténtalo nuevamente.'; },
    });
  }

}
