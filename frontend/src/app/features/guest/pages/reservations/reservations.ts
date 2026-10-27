import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthSessionService } from '../../../../core/services/auth-session.service';

interface Reservation {
  code: string;
  property: string;
  city: string;
  arrival: string;
  departure: string;
  total: number;
  status: 'CONFIRMADA' | 'COMPLETADA' | 'CANCELADA';
  policy: 'Flexible' | 'Moderada';
}

@Component({
  selector: 'app-guest-reservations',
  standalone: true,
  imports: [CurrencyPipe, DatePipe, FormsModule, RouterLink],
  templateUrl: './reservations.html',
  styleUrl: './reservations.css',
})
export class GuestReservations {
  private readonly session = inject(AuthSessionService);

  cerrarSesion(): void { this.session.signOut(); }

  readonly reservas: Reservation[] = [
    { code: 'R-1024', property: 'Refugio entre montañas', city: 'Villa de Leyva', arrival: '2026-12-12', departure: '2026-12-15', total: 664000, status: 'CONFIRMADA', policy: 'Flexible' },
    { code: 'R-0987', property: 'Apartamento con encanto', city: 'Bogotá', arrival: '2026-08-03', departure: '2026-08-05', total: 310000, status: 'COMPLETADA', policy: 'Moderada' },
    { code: 'R-0950', property: 'Casa colonial con patio', city: 'Cartagena', arrival: '2026-10-10', departure: '2026-10-12', total: 720000, status: 'CANCELADA', policy: 'Flexible' },
  ];
  reservaParaCancelar: Reservation | null = null;
  motivo = '';
  devolucion = 0;
  aviso = '';

  abrirCancelacion(reserva: Reservation): void {
    this.reservaParaCancelar = reserva;
    this.motivo = '';
    this.aviso = '';
    this.devolucion = this.calcularDevolucion(reserva);
  }

  cancelarReserva(): void {
    if (!this.reservaParaCancelar || !this.motivo.trim()) return;
    this.aviso = `Se solicitó la cancelación de ${this.reservaParaCancelar.code}. Devolución estimada: ${this.devolucion.toLocaleString('es-CO')} COP.`;
    this.reservaParaCancelar = null;
  }

  private calcularDevolucion(reserva: Reservation): number {
    const hoursUntilArrival = (Date.parse(`${reserva.arrival}T12:00:00`) - Date.now()) / 3_600_000;
    const ratio = reserva.policy === 'Flexible'
      ? hoursUntilArrival >= 48 ? 1 : hoursUntilArrival >= 24 ? 0.5 : 0
      : hoursUntilArrival >= 24 * 7 ? 1 : hoursUntilArrival >= 48 ? 0.5 : 0;
    return Math.round(reserva.total * ratio);
  }
}
