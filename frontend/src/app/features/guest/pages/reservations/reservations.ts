import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { GuestReservation, MarketplaceDataService } from '../../../../core/services/marketplace-data.service';

@Component({
  selector: 'app-guest-reservations',
  standalone: true,
  imports: [CurrencyPipe, DatePipe, FormsModule, RouterLink],
  templateUrl: './reservations.html',
  styleUrl: './reservations.css',
})
export class GuestReservations {
  private readonly marketplace = inject(MarketplaceDataService);

  get reservas(): GuestReservation[] { return this.marketplace.getReservations(); }
  reservaParaCancelar: GuestReservation | null = null;
  motivo = '';
  devolucion = 0;
  aviso = '';

  abrirCancelacion(reserva: GuestReservation): void {
    this.reservaParaCancelar = reserva;
    this.motivo = '';
    this.aviso = '';
    this.devolucion = this.calcularDevolucion(reserva);
  }

  cancelarReserva(): void {
    if (!this.reservaParaCancelar || !this.motivo.trim()) return;
    this.marketplace.cancelReservation(this.reservaParaCancelar.code);
    this.aviso = `La reserva ${this.reservaParaCancelar.code} fue cancelada. Devolución estimada: ${this.devolucion.toLocaleString('es-CO')} COP.`;
    this.reservaParaCancelar = null;
  }

  private calcularDevolucion(reserva: GuestReservation): number {
    const hoursUntilArrival = (Date.parse(`${reserva.arrival}T12:00:00`) - Date.now()) / 3_600_000;
    const ratio = reserva.policy === 'Flexible'
      ? hoursUntilArrival >= 48 ? 1 : hoursUntilArrival >= 24 ? 0.5 : 0
      : hoursUntilArrival >= 24 * 7 ? 1 : hoursUntilArrival >= 48 ? 0.5 : 0;
    return Math.round(reserva.total * ratio);
  }
}
