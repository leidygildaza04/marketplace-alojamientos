import { Injectable } from '@angular/core';

export interface Accommodation {
  id: string;
  title: string;
  city: string;
  country: string;
  type: string;
  capacity: number;
  pricePerNight: number;
  cleaningFee: number;
  rating: number;
  image: string;
  description: string;
  amenities: string[];
  rules: string[];
  cancellationPolicy: string;
  status: 'ACTIVA' | 'PAUSADA' | 'BORRADOR' | 'BLOQUEADA';
  host: string;
  favorite: boolean;
}

export interface AccommodationFilters {
  destination?: string;
  type?: string;
  guests?: number;
  minPrice?: number | null;
  maxPrice?: number | null;
  amenities?: string[];
}

export interface Quote {
  nights: number;
  nightlySubtotal: number;
  cleaningFee: number;
  serviceFee: number;
  longStayDiscount: number;
  extras: number;
  total: number;
}

export interface MarketplaceReview {
  id: string;
  property: string;
  author: string;
  text: string;
}

export interface GuestReservation {
  code: string;
  property: string;
  city: string;
  arrival: string;
  departure: string;
  total: number;
  status: 'CONFIRMADA' | 'COMPLETADA' | 'CANCELADA';
  policy: 'Flexible' | 'Moderada';
}

@Injectable({ providedIn: 'root' })
export class MarketplaceDataService {
  private readonly hiddenReviewsKey = 'estancia-hidden-reviews';
  private readonly reservationsKey = 'estancia-reservations';
  private readonly initialReservations: GuestReservation[] = [
    { code: 'R-1024', property: 'Refugio entre montañas', city: 'Villa de Leyva', arrival: '2026-12-12', departure: '2026-12-15', total: 664000, status: 'CONFIRMADA', policy: 'Flexible' },
    { code: 'R-0987', property: 'Apartamento con encanto', city: 'Bogotá', arrival: '2026-08-03', departure: '2026-08-05', total: 310000, status: 'COMPLETADA', policy: 'Moderada' },
    { code: 'R-0950', property: 'Casa colonial con patio', city: 'Cartagena', arrival: '2026-10-10', departure: '2026-10-12', total: 720000, status: 'CANCELADA', policy: 'Flexible' },
  ];
  readonly reviews: MarketplaceReview[] = [
    { id: 'rev-01', property: 'Refugio entre montañas', author: 'Huésped', text: 'Un lugar tranquilo, cómodo y muy bien ubicado.' },
  ];
  readonly serviceFeeRate = 0.1;
  readonly longStayDiscountRate = 0.1;
  readonly longStayMinimumNights = 7;
  // Semillas ficticias para la interfaz. La API reemplazará esta fuente cuando esté disponible.
  readonly accommodations: Accommodation[] = [
    {
      id: 'apartamento-bogota',
      title: 'Apartamento con encanto',
      city: 'Bogotá',
      country: 'Colombia',
      type: 'Apartamento',
      capacity: 3,
      pricePerNight: 180000,
      cleaningFee: 40000,
      rating: 4.9,
      image: 'https://images.unsplash.com/photo-1600210492486-724fe5c67fb0?auto=format&fit=crop&w=900&q=80',
      description: 'Un espacio cálido y luminoso para descubrir la ciudad con calma.',
      amenities: ['Wi-Fi', 'Cocina equipada', 'Lavadora'],
      rules: ['No fumar', 'No se permiten fiestas'],
      cancellationPolicy: 'Flexible',
      status: 'ACTIVA',
      host: 'Equipo Travesía',
      favorite: false,
    },
    {
      id: 'cabana-villa-leyva',
      title: 'Refugio entre montañas',
      city: 'Villa de Leyva',
      country: 'Colombia',
      type: 'Cabaña',
      capacity: 4,
      pricePerNight: 180000,
      cleaningFee: 40000,
      rating: 4.8,
      image: 'https://images.unsplash.com/photo-1449158743715-0a90ebb6d2d8?auto=format&fit=crop&w=900&q=80',
      description: 'Una cabaña tranquila rodeada de naturaleza, a poca distancia del centro histórico.',
      amenities: ['Wi-Fi', 'Parqueadero', 'Cocina equipada'],
      rules: ['No fumar', 'Mascotas con autorización'],
      cancellationPolicy: 'Flexible',
      status: 'ACTIVA',
      host: 'Equipo Travesía',
      favorite: false,
    },
    {
      id: 'casa-cartagena',
      title: 'Casa colonial con patio',
      city: 'Cartagena',
      country: 'Colombia',
      type: 'Casa',
      capacity: 8,
      pricePerNight: 320000,
      cleaningFee: 55000,
      rating: 5,
      image: 'https://images.unsplash.com/photo-1613490493576-7fde63acd811?auto=format&fit=crop&w=900&q=80',
      description: 'Casa amplia con patio interior para compartir una estancia especial.',
      amenities: ['Wi-Fi', 'Piscina', 'Cocina equipada', 'Parqueadero'],
      rules: ['No fumar', 'Respetar el horario de descanso'],
      cancellationPolicy: 'Moderada',
      status: 'ACTIVA',
      host: 'Equipo Travesía',
      favorite: false,
    },
    {
      id: 'casa-campestre',
      title: 'Casa campestre',
      city: 'Villa de Leyva',
      country: 'Colombia',
      type: 'Casa',
      capacity: 8,
      pricePerNight: 320000,
      cleaningFee: 50000,
      rating: 4.7,
      image: 'https://images.unsplash.com/photo-1449158743715-0a90ebb6d2d8?auto=format&fit=crop&w=900&q=80',
      description: 'Una casa amplia y cómoda para disfrutar una estancia en Villa de Leyva.',
      amenities: ['Parqueadero', 'Cocina equipada'],
      rules: ['No fumar'],
      cancellationPolicy: 'Moderada',
      status: 'ACTIVA',
      host: 'Equipo Travesía',
      favorite: false,
    },
  ];

  getActiveAccommodations(filters: AccommodationFilters = {}): Accommodation[] {
    const destination = filters.destination?.trim().toLocaleLowerCase() ?? '';
    return this.accommodations.filter((item) => {
      const searchable = `${item.title} ${item.city} ${item.country}`.toLocaleLowerCase();
      return item.status === 'ACTIVA'
        && (!destination || searchable.includes(destination))
        && (!filters.type || item.type === filters.type)
        && (!filters.guests || item.capacity >= filters.guests)
        && (filters.minPrice == null || item.pricePerNight >= filters.minPrice)
        && (filters.maxPrice == null || item.pricePerNight <= filters.maxPrice)
        && (filters.amenities ?? []).every((amenity) => item.amenities.includes(amenity));
    });
  }

  getById(id: string): Accommodation | undefined {
    return this.accommodations.find((item) => item.id === id && item.status === 'ACTIVA');
  }

  getVisibleReviews(): MarketplaceReview[] {
    const hidden = this.getHiddenReviewIds();
    return this.reviews.filter((review) => !hidden.has(review.id));
  }

  getHiddenReviews(): MarketplaceReview[] {
    const hidden = this.getHiddenReviewIds();
    return this.reviews.filter((review) => hidden.has(review.id));
  }

  hideReview(id: string): void {
    const hidden = this.getHiddenReviewIds();
    hidden.add(id);
    this.saveHiddenReviewIds(hidden);
  }

  restoreReview(id: string): void {
    const hidden = this.getHiddenReviewIds();
    hidden.delete(id);
    this.saveHiddenReviewIds(hidden);
  }

  getReservations(): GuestReservation[] {
    if (typeof localStorage === 'undefined') return [...this.initialReservations];
    try {
      const stored: unknown = JSON.parse(localStorage.getItem(this.reservationsKey) ?? 'null');
      if (Array.isArray(stored)) return stored as GuestReservation[];
    } catch { /* Recupera los datos iniciales si el almacenamiento local no es válido. */ }
    this.saveReservations(this.initialReservations);
    return [...this.initialReservations];
  }

  createReservation(reservation: Omit<GuestReservation, 'code' | 'status'>): GuestReservation {
    const created: GuestReservation = { ...reservation, code: `R-${Date.now().toString().slice(-6)}`, status: 'CONFIRMADA' };
    const reservations = this.getReservations();
    reservations.unshift(created);
    this.saveReservations(reservations);
    return created;
  }

  cancelReservation(code: string): void {
    const reservations = this.getReservations().map((reservation) => reservation.code === code ? { ...reservation, status: 'CANCELADA' as const } : reservation);
    this.saveReservations(reservations);
  }

  private saveReservations(reservations: GuestReservation[]): void {
    if (typeof localStorage !== 'undefined') localStorage.setItem(this.reservationsKey, JSON.stringify(reservations));
  }

  private getHiddenReviewIds(): Set<string> {
    if (typeof localStorage === 'undefined') return new Set();
    try {
      const stored: unknown = JSON.parse(localStorage.getItem(this.hiddenReviewsKey) ?? '[]');
      return new Set(Array.isArray(stored) ? stored.filter((id): id is string => typeof id === 'string') : []);
    } catch {
      return new Set();
    }
  }

  private saveHiddenReviewIds(ids: Set<string>): void {
    if (typeof localStorage !== 'undefined') localStorage.setItem(this.hiddenReviewsKey, JSON.stringify([...ids]));
  }

  calculateQuote(item: Accommodation, arrival: string, departure: string, breakfast = false): Quote | null {
    const nights = Math.round((Date.parse(`${departure}T00:00:00Z`) - Date.parse(`${arrival}T00:00:00Z`)) / 86_400_000);
    if (!Number.isFinite(nights) || nights <= 0) return null;
    const nightlySubtotal = item.pricePerNight * nights;
    const serviceFee = Math.round(nightlySubtotal * this.serviceFeeRate);
    const longStayDiscount = nights >= this.longStayMinimumNights
      ? Math.round(nightlySubtotal * this.longStayDiscountRate)
      : 0;
    const extras = breakfast ? 30000 : 0;
    return {
      nights,
      nightlySubtotal,
      cleaningFee: item.cleaningFee,
      serviceFee,
      longStayDiscount,
      extras,
      total: nightlySubtotal + item.cleaningFee + serviceFee + extras - longStayDiscount,
    };
  }
}
