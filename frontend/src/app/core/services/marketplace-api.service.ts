import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import type { Accommodation, AccommodationFilters } from './marketplace-data.service';
import { environment } from '../../../environments/environment';

export interface CreateReservationRequest {
  accommodationId: string;
  arrival: string;
  departure: string;
  guests: number;
}

export interface ReservationRecord {
  id: string;
  code: string;
  accommodationId: string;
  arrival: string;
  departure: string;
  total: number;
  status: 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'COMPLETED';
}

/** Cliente HTTP tipado para alojamientos y reservas. */
@Injectable({ providedIn: 'root' })
export class MarketplaceApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiBaseUrl.replace(/\/$/, '');

  getAccommodations(filters: AccommodationFilters = {}): Observable<Accommodation[]> {
    let params = new HttpParams();
    if (filters.destination) params = params.set('destination', filters.destination);
    if (filters.type) params = params.set('type', filters.type);
    if (filters.guests) params = params.set('guests', filters.guests);
    if (filters.minPrice != null) params = params.set('minPrice', filters.minPrice);
    if (filters.maxPrice != null) params = params.set('maxPrice', filters.maxPrice);
    for (const amenity of filters.amenities ?? []) params = params.append('amenities', amenity);
    return this.http.get<Accommodation[]>(`${this.baseUrl}/accommodations`, { params });
  }

  getAccommodation(id: string): Observable<Accommodation> {
    return this.http.get<Accommodation>(`${this.baseUrl}/accommodations/${encodeURIComponent(id)}`);
  }

  createReservation(request: CreateReservationRequest): Observable<ReservationRecord> {
    return this.http.post<ReservationRecord>(`${this.baseUrl}/reservations`, request);
  }

  getReservations(): Observable<ReservationRecord[]> {
    return this.http.get<ReservationRecord[]>(`${this.baseUrl}/reservations`);
  }
}
