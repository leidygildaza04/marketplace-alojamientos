import { CurrencyPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthSessionService } from '../../../../core/services/auth-session.service';
import { ProfilePanel } from '../../../../core/components/profile-panel/profile-panel';
import { Accommodation } from '../../../../core/services/marketplace-data.service';
import { MarketplaceApiService } from '../../../../core/services/marketplace-api.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [FormsModule, RouterLink, CurrencyPipe, ProfilePanel],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  private readonly api = inject(MarketplaceApiService);
  private readonly session = inject(AuthSessionService);
  alojamientos: Accommodation[] = [];
  cargandoAlojamientos = true;
  errorAlojamientos = '';

  constructor() {
    this.api.getAccommodations().subscribe({
      next: (items) => {
        this.alojamientos = items;
        this.cargandoAlojamientos = false;
        items.forEach((item) => this.api.getAccommodationReviews(item.id).subscribe({
          next: (summary) => {
            this.alojamientos = this.alojamientos.map((current) => current.id === item.id
              ? { ...current, rating: summary.calificacionPromedio } : current);
          },
        }));
      },
      error: () => { this.errorAlojamientos = 'No se pudieron cargar los alojamientos. Inténtalo nuevamente.'; this.cargandoAlojamientos = false; },
    });
  }

  get role() { return this.session.role; }

  cerrarSesion(): void { this.session.signOut(); }

  destino = '';
  llegada = '';
  salida = '';
  huespedes = 1;
  tipo = '';
  precioMinimo: number | null = null;
  precioMaximo: number | null = null;
  serviciosSeleccionados = { wifi: false, cocina: false, parqueadero: false, piscina: false };
  busquedaRealizada = false;
  errorFechas = '';
  errorComparacion = '';
  soloFavoritos = false;
  mostrarComparacion = false;
  imagenHeroNoDisponible = false;
  readonly fechaMinima = this.fechaLocal(new Date());
  readonly imagenesNoDisponibles = new Set<string>();
  readonly seleccionComparacion = new Set<string>();

  get alojamientosFiltrados(): Accommodation[] {
    const amenities = [this.serviciosSeleccionados.wifi && 'Wi-Fi', this.serviciosSeleccionados.cocina && 'Cocina equipada',
      this.serviciosSeleccionados.parqueadero && 'Parqueadero', this.serviciosSeleccionados.piscina && 'Piscina']
      .filter((amenity): amenity is string => Boolean(amenity));
    const destination = this.destino.trim().toLocaleLowerCase();
    const resultados = this.alojamientos.filter((item) => item.status === 'ACTIVA'
      && (!destination || `${item.title} ${item.city}`.toLocaleLowerCase().includes(destination))
      && (!this.tipo || item.type === this.tipo)
      && item.capacity >= this.huespedes
      && (this.precioMinimo == null || item.pricePerNight >= this.precioMinimo)
      && (this.precioMaximo == null || item.pricePerNight <= this.precioMaximo)
      && amenities.every((amenity) => item.amenities.includes(amenity)));
    return this.soloFavoritos ? resultados.filter((item) => item.favorite) : resultados;
  }

  get alojamientosComparados(): Accommodation[] {
    return this.alojamientos.filter(
      (item) => item.status === 'ACTIVA' && this.seleccionComparacion.has(item.id),
    );
  }

  buscar(): void {
    this.errorFechas = '';
    if (Boolean(this.llegada) !== Boolean(this.salida)) {
      this.errorFechas = 'Selecciona tanto la fecha de llegada como la de salida.';
    } else if (this.llegada && this.llegada < this.fechaMinima) {
      this.errorFechas = 'La fecha de llegada no puede estar en el pasado.';
    } else if (this.llegada && this.salida && this.salida <= this.llegada) {
      this.errorFechas = 'La salida debe ser posterior a la llegada.';
    }
    if (this.precioMinimo != null && this.precioMaximo != null && this.precioMinimo > this.precioMaximo) {
      this.errorFechas = 'El precio mínimo no puede superar el precio máximo.';
    }
    if (this.errorFechas) return;

    this.busquedaRealizada = true;
    document.getElementById('alojamientos')?.scrollIntoView({ behavior: 'smooth' });
  }

  alternarFavorito(alojamiento: Accommodation): void {
    alojamiento.favorite = !alojamiento.favorite;
  }

  alternarComparacion(alojamiento: Accommodation): void {
    this.errorComparacion = '';
    if (this.seleccionComparacion.has(alojamiento.id)) {
      this.seleccionComparacion.delete(alojamiento.id);
    } else if (this.seleccionComparacion.size < 3) {
      this.seleccionComparacion.add(alojamiento.id);
    } else {
      this.errorComparacion = 'Puedes comparar hasta 3 alojamientos a la vez.';
    }
  }

  verComparacion(): void {
    this.mostrarComparacion = true;
  }

  cerrarComparacion(): void {
    this.mostrarComparacion = false;
  }

  verFavoritos(): void {
    this.soloFavoritos = true;
    document.getElementById('alojamientos')?.scrollIntoView({ behavior: 'smooth' });
  }

  verTodos(): void {
    this.soloFavoritos = false;
    this.destino = '';
    this.tipo = '';
    this.precioMinimo = null;
    this.precioMaximo = null;
    this.serviciosSeleccionados = { wifi: false, cocina: false, parqueadero: false, piscina: false };
    this.busquedaRealizada = false;
  }

  marcarImagenNoDisponible(id: string): void {
    this.imagenesNoDisponibles.add(id);
  }

  private fechaLocal(fecha: Date): string {
    const local = new Date(fecha.getTime() - fecha.getTimezoneOffset() * 60_000);
    return local.toISOString().slice(0, 10);
  }
}
