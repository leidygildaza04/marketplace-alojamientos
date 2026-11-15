/** Modelos compartidos del marketplace. Los datos de negocio se consultan en los servicios backend. */
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
  total: number;
}

export interface GuestReservation {
  id?: string;
  code: string;
  property: string;
  city: string;
  arrival: string;
  departure: string;
  total: number | null;
  status: 'CONFIRMADA' | 'COMPLETADA' | 'CANCELADA' | 'PENDIENTE' | 'RECHAZADA' | 'REVISION_MANUAL' | 'CANCELACION_PENDIENTE';
  policy: 'Flexible' | 'Moderada';
}
