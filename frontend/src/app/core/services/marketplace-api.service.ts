import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { map, Observable, throwError } from 'rxjs';
import type { Accommodation, AccommodationFilters } from './marketplace-data.service';
import { environment } from '../../../environments/environment';
import { AuthSessionService } from './auth-session.service';

export interface QuoteRequest { accommodationId: string; arrival: string; departure: string; }
export interface CheckoutRequest extends QuoteRequest { cardNumber: string; expiry: string; cvv: string; cardholder: string; }
export interface CheckoutRecord {
  reservationId: number; reservationCode: string; reservationStatus: ReservationRecord['status'];
  total: number; paymentStatus: 'APROBADO' | 'RECHAZADO'; paymentReference: string; message: string;
}
export interface QuoteRecord {
  nights: number; nightlySubtotal: number; cleaningFee: number; serviceFee: number;
  longStayDiscount: number; total: number; currency: string;
}

export interface ReservationRecord {
  id: string;
  code: string;
  accommodationId: string;
  arrival: string;
  departure: string;
  total: number | null;
  status: 'PENDIENTE' | 'CONFIRMADA' | 'COMPLETADA' | 'CANCELADA' | 'RECHAZADA' | 'REVISION_MANUAL' | 'CANCELACION_PENDIENTE';
}

export interface ReviewRecord {
  id: number;
  reservaId: number;
  alojamientoId: number;
  calificacion: number;
  comentario: string | null;
  fechaCreacion: string;
  visible: boolean;
}

export interface AccommodationReviews {
  calificacionPromedio: number;
  cantidad: number;
  resenas: ReviewRecord[];
}

export interface AdminMetrics {
  totalReservas: number;
  reservasConfirmadas: number;
  estanciasCompletadas: number;
  reservasCanceladas: number;
  reservasPendientes: number;
  resenasVisibles: number;
  calificacionPromedio: number;
  ingresosRegistrados: number;
}

export interface AuditEventRecord {
  idEvento: number;
  idUsuario: number | null;
  accion: string;
  entidad: string;
  idEntidad: number;
  detalle: string | null;
  resultado: string;
  fechaEvento: string;
}

export interface CatalogAccommodation {
  id: number;
  hostId?: number;
  titulo: string;
  descripcion: string | null;
  direccion: string | null;
  ciudad: string | null;
  precioNoche: number;
  capacidad: number;
  estado: 'BORRADOR' | 'ACTIVA' | 'PAUSADA' | 'BLOQUEADA' | 'PENDIENTE_REVISION';
  tipoAlojamiento: string | null;
  imagenes: string[] | null;
  servicios: string[] | null;
  reglas: string | null;
  tarifaLimpieza: number | null;
  politicaCancelacion: string | null;
  serviciosAdicionales?: { nombre: string; precio: number }[];
}

export interface SaveAccommodationRequest {
  titulo: string;
  descripcion: string;
  direccion: string;
  ciudad: string;
  precioNoche: number;
  capacidad: number;
  tipoAlojamiento?: string;
  imagenes?: string[];
  servicios?: string[];
  reglas?: string;
  tarifaLimpieza?: number;
  politicaCancelacion?: string;
  serviciosAdicionales?: { nombre: string; precio: number }[];
}

export interface AvailabilityPeriodRecord {
  id: number;
  alojamientoId: number;
  fechaInicio: string;
  fechaFin: string;
  estado: 'DISPONIBLE' | 'RESERVADO';
}

/** Cliente HTTP tipado para alojamientos y reservas. */
@Injectable({ providedIn: 'root' })
export class MarketplaceApiService {
  private readonly http = inject(HttpClient);
  private readonly catalogUrl = environment.catalogApiProxyBaseUrl.replace(/\/$/, '');
  private readonly bookingUrl = environment.bookingApiProxyBaseUrl.replace(/\/$/, '');
  private readonly backofficeUrl = environment.backofficeApiProxyBaseUrl.replace(/\/$/, '');
  private readonly session = inject(AuthSessionService);

  getAccommodations(filters: AccommodationFilters = {}): Observable<Accommodation[]> {
    return this.http.get<CatalogAccommodation[]>(`${this.catalogUrl}/api/alojamientos`).pipe(
      map((items) => items.map((item) => this.toAccommodation(item)).filter((item) => this.matches(item, filters))),
    );
  }

  getAccommodation(id: string): Observable<Accommodation> {
    return this.http.get<CatalogAccommodation>(`${this.catalogUrl}/api/alojamientos/${encodeURIComponent(id)}`).pipe(
      map((item) => this.toAccommodation(item)),
    );
  }

  getMyAccommodations(): Observable<CatalogAccommodation[]> {
    return this.http.get<CatalogAccommodation[]>(`${this.catalogUrl}/api/alojamientos/mios`);
  }

  createAccommodation(request: SaveAccommodationRequest): Observable<CatalogAccommodation> {
    return this.http.post<CatalogAccommodation>(`${this.catalogUrl}/api/alojamientos`, request);
  }

  updateAccommodation(id: number, request: SaveAccommodationRequest): Observable<CatalogAccommodation> {
    return this.http.put<CatalogAccommodation>(`${this.catalogUrl}/api/alojamientos/${id}`, request);
  }

  activateAccommodation(id: number): Observable<CatalogAccommodation> {
    return this.http.post<CatalogAccommodation>(`${this.catalogUrl}/api/alojamientos/${id}/activar`, {});
  }

  pauseAccommodation(id: number): Observable<CatalogAccommodation> {
    return this.http.patch<CatalogAccommodation>(`${this.catalogUrl}/api/alojamientos/${id}/pausar`, {});
  }

  getAllAccommodationsForAdmin(): Observable<CatalogAccommodation[]> {
    return this.http.get<CatalogAccommodation[]>(`${this.catalogUrl}/api/alojamientos/administracion`);
  }

  blockAccommodation(id: number, reason: string): Observable<CatalogAccommodation> {
    return this.http.patch<CatalogAccommodation>(`${this.catalogUrl}/api/alojamientos/${id}/bloquear`, { motivo: reason });
  }

  getAvailability(id: number): Observable<AvailabilityPeriodRecord[]> {
    return this.http.get<AvailabilityPeriodRecord[]>(`${this.catalogUrl}/api/v1/alojamientos/${id}/periodos-disponibilidad`);
  }

  addAvailability(id: number, fechaInicio: string, fechaFin: string): Observable<AvailabilityPeriodRecord> {
    return this.http.post<AvailabilityPeriodRecord>(`${this.catalogUrl}/api/v1/alojamientos/${id}/periodos-disponibilidad`, { fechaInicio, fechaFin });
  }

  quoteReservation(request: QuoteRequest): Observable<QuoteRecord> {
    return this.http.post<{
      noches: number;
      subtotalNoches: number;
      cargoLimpieza: number;
      cargoServicio: number;
      descuentoEstancia: number;
      total: number;
      moneda: string;
    }>(`${this.bookingUrl}/api/v1/reservas/cotizar`, {
      alojamientoId: Number(request.accommodationId), checkIn: request.arrival, checkOut: request.departure,
    }).pipe(map((quote) => ({
      nights: quote.noches,
      nightlySubtotal: quote.subtotalNoches,
      cleaningFee: quote.cargoLimpieza,
      serviceFee: quote.cargoServicio,
      longStayDiscount: quote.descuentoEstancia,
      total: quote.total,
      currency: quote.moneda,
    })));
  }

  checkout(request: CheckoutRequest): Observable<CheckoutRecord> {
    if (!this.session.userId) {
      return throwError(() => new Error('Inicia sesión nuevamente para crear una reserva.'));
    }
    return this.http.post<{
      reservaId: number;
      codigoReserva: string;
      estadoReserva: ReservationRecord['status'];
      total: number;
      estadoPago: 'APROBADO' | 'RECHAZADO';
      referenciaPago: string;
      mensaje: string;
    }>(`${this.bookingUrl}/api/v1/reservas/checkout`, {
      alojamientoId: Number(request.accommodationId), checkIn: request.arrival, checkOut: request.departure,
      numeroTarjeta: request.cardNumber, vencimiento: request.expiry, cvv: request.cvv, titular: request.cardholder,
    }).pipe(map((result) => ({
      reservationId: result.reservaId,
      reservationCode: result.codigoReserva,
      reservationStatus: result.estadoReserva,
      total: result.total,
      paymentStatus: result.estadoPago,
      paymentReference: result.referenciaPago,
      message: result.mensaje,
    })));
  }

  getReservations(): Observable<ReservationRecord[]> {
    return this.http.get<Array<{ id: number; userId: number; alojamientoId: number; checkIn: string; checkOut: string; estado: ReservationRecord['status']; total: number | null }>>(
      `${this.bookingUrl}/api/v1/reservas/mias`,
    ).pipe(map((items) => items.map((item) => ({
      id: String(item.id), code: `R-${item.id}`, accommodationId: String(item.alojamientoId),
      arrival: item.checkIn, departure: item.checkOut, total: item.total, status: item.estado,
    }))));
  }

  cancelReservation(id: string): Observable<ReservationRecord> {
    return this.http.post<{ id: number; userId: number; alojamientoId: number; checkIn: string; checkOut: string; estado: ReservationRecord['status']; total: number | null }>(
      `${this.bookingUrl}/api/v1/reservas/${encodeURIComponent(id)}/cancelar`, {},
    ).pipe(map((item) => ({
      id: String(item.id), code: `R-${item.id}`, accommodationId: String(item.alojamientoId),
      arrival: item.checkIn, departure: item.checkOut, total: item.total, status: item.estado,
    })));
  }

  createReview(reservationId: string, calificacion: number, comentario: string): Observable<ReviewRecord> {
    return this.http.post<ReviewRecord>(
      `${this.bookingUrl}/api/v1/reservas/${encodeURIComponent(reservationId)}/resena`,
      { calificacion, comentario },
    );
  }

  getMyReviews(): Observable<ReviewRecord[]> {
    return this.http.get<ReviewRecord[]>(`${this.bookingUrl}/api/v1/reservas/mias/resenas`);
  }

  getAccommodationReviews(id: string): Observable<AccommodationReviews> {
    return this.http.get<AccommodationReviews>(
      `${this.bookingUrl}/api/v1/alojamientos/${encodeURIComponent(id)}/resenas`,
    );
  }

  getAdminMetrics(): Observable<AdminMetrics> {
    return this.http.get<AdminMetrics>(`${this.bookingUrl}/api/admin/metricas`);
  }

  getAdminReviews(): Observable<ReviewRecord[]> {
    return this.http.get<ReviewRecord[]>(`${this.bookingUrl}/api/admin/resenas`);
  }

  setReviewVisible(id: number, visible: boolean): Observable<ReviewRecord> {
    return this.http.patch<ReviewRecord>(`${this.bookingUrl}/api/admin/resenas/${id}/visibilidad`, { visible });
  }

  getAdminAuditEvents(): Observable<AuditEventRecord[]> {
    return this.http.get<AuditEventRecord[]>(`${this.backofficeUrl}/api/admin/auditoria`);
  }

  recordAdminAuditEvent(event: Omit<AuditEventRecord, 'idEvento' | 'idUsuario' | 'fechaEvento'>): Observable<AuditEventRecord> {
    return this.http.post<AuditEventRecord>(`${this.backofficeUrl}/api/admin/auditoria`, event);
  }

  toAccommodation(item: CatalogAccommodation): Accommodation {
    return {
      id: String(item.id), title: item.titulo, city: item.ciudad ?? '', country: 'Colombia', type: item.tipoAlojamiento ?? 'Alojamiento',
      capacity: item.capacidad, pricePerNight: Number(item.precioNoche), cleaningFee: Number(item.tarifaLimpieza ?? 0), rating: 0,
      image: item.imagenes?.[0] ?? 'https://images.unsplash.com/photo-1600210492486-724fe5c67fb0?auto=format&fit=crop&w=900&q=80',
      description: item.descripcion ?? '', amenities: this.parseList(item.servicios), rules: this.parseList(item.reglas), cancellationPolicy: item.politicaCancelacion ?? 'Flexible',
      status: item.estado === 'PENDIENTE_REVISION' ? 'BORRADOR' : item.estado, host: `Anfitrión ${item.hostId ?? ''}`.trim(), favorite: false,
    };
  }

  private parseList(value: string[] | string | null): string[] {
    if (!value) return [];
    if (Array.isArray(value)) return value;
    try {
      const parsed: unknown = JSON.parse(value);
      if (Array.isArray(parsed)) return parsed.filter((item): item is string => typeof item === 'string');
    } catch { /* Acepta datos antiguos en texto separado por comas. */ }
    return value.split(',').map((item) => item.trim()).filter(Boolean);
  }

  private matches(item: Accommodation, filters: AccommodationFilters): boolean {
    const destination = filters.destination?.trim().toLocaleLowerCase() ?? '';
    return (!destination || `${item.title} ${item.city}`.toLocaleLowerCase().includes(destination))
      && (!filters.guests || item.capacity >= filters.guests)
      && (filters.minPrice == null || item.pricePerNight >= filters.minPrice)
      && (filters.maxPrice == null || item.pricePerNight <= filters.maxPrice);
  }
}
