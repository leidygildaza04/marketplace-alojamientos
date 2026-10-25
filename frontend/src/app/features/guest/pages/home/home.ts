import { CurrencyPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthSessionService } from '../../../../core/services/auth-session.service';
import { Accommodation, MarketplaceDataService } from '../../../../core/services/marketplace-data.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [FormsModule, RouterLink, CurrencyPipe],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  private readonly marketplace = inject(MarketplaceDataService);
  private readonly session = inject(AuthSessionService);

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
    const resultados = this.marketplace.getActiveAccommodations({
      destination: this.destino,
      type: this.tipo || undefined,
      guests: this.huespedes,
      minPrice: this.precioMinimo,
      maxPrice: this.precioMaximo,
      amenities: [
        this.serviciosSeleccionados.wifi && 'Wi-Fi',
        this.serviciosSeleccionados.cocina && 'Cocina equipada',
        this.serviciosSeleccionados.parqueadero && 'Parqueadero',
        this.serviciosSeleccionados.piscina && 'Piscina',
      ].filter((amenity): amenity is string => Boolean(amenity)),
    });
    return this.soloFavoritos ? resultados.filter((item) => item.favorite) : resultados;
  }

  get alojamientosComparados(): Accommodation[] {
    return this.marketplace.accommodations.filter(
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
