
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CurrencyPipe } from '@angular/common';

interface Alojamiento {
  id: string;
  nombre: string;
  ciudad: string;
  precio: number;
  calificacion: number;
  imagen: string;
  favorito: boolean;
  tipo: string;
  imagenNoDisponible: boolean;
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [FormsModule, RouterLink, CurrencyPipe],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  destino = '';
  llegada = '';
  salida = '';
  huespedes = 1;
  busquedaRealizada = false;
  errorFechas = '';
  soloFavoritos = false;
  imagenHeroNoDisponible = false;
  readonly fechaMinima = this.fechaLocal(new Date());

  alojamientos: Alojamiento[] = [
    {
      id: 'apartamento-con-encanto',
      nombre: 'Apartamento con encanto',
      ciudad: 'Bogotá, Colombia',
      precio: 180000,
      calificacion: 4.9,
      imagen:
        'https://images.unsplash.com/photo-1600210492486-724fe5c67fb0?auto=format&fit=crop&w=800&q=80',
      favorito: false,
      tipo: 'Apartamento',
      imagenNoDisponible: false,
    },
    {
      id: 'refugio-entre-montanas',
      nombre: 'Refugio entre montañas',
      ciudad: 'Villa de Leyva, Colombia',
      precio: 250000,
      calificacion: 4.8,
      imagen:
        'https://images.unsplash.com/photo-1449158743715-0a90ebb6d2d8?auto=format&fit=crop&w=800&q=80',
      favorito: false,
      tipo: 'Cabaña',
      imagenNoDisponible: false,
    },
    {
      id: 'casa-colonial-con-patio',
      nombre: 'Casa colonial con patio',
      ciudad: 'Cartagena, Colombia',
      precio: 320000,
      calificacion: 5.0,
      imagen:
        'https://images.unsplash.com/photo-1613490493576-7fde63acd811?auto=format&fit=crop&w=800&q=80',
      favorito: false,
      tipo: 'Casa',
      imagenNoDisponible: false,
    },
  ];

  get alojamientosFiltrados(): Alojamiento[] {
    const termino = this.destino.trim().toLocaleLowerCase();

    return this.alojamientos.filter((alojamiento) => {
      const coincideDestino = !termino ||
        `${alojamiento.nombre} ${alojamiento.ciudad} ${alojamiento.tipo}`
          .toLocaleLowerCase()
          .includes(termino);
      return coincideDestino && (!this.soloFavoritos || alojamiento.favorito);
    });
  }

  buscar(): void {
    this.errorFechas = '';
    const tieneLlegada = Boolean(this.llegada);
    const tieneSalida = Boolean(this.salida);

    if (tieneLlegada !== tieneSalida) {
      this.errorFechas = 'Selecciona tanto la fecha de llegada como la de salida.';
    } else if (this.llegada && this.llegada < this.fechaMinima) {
      this.errorFechas = 'La fecha de llegada no puede estar en el pasado.';
    } else if (this.llegada && this.salida && this.salida <= this.llegada) {
      this.errorFechas = 'La salida debe ser posterior a la llegada.';
    }

    if (this.errorFechas) return;

    this.busquedaRealizada = true;

    document.getElementById('alojamientos')?.scrollIntoView({
      behavior: 'smooth',
    });
  }

  alternarFavorito(alojamiento: Alojamiento): void {
    alojamiento.favorito = !alojamiento.favorito;
  }

  verFavoritos(): void {
    this.soloFavoritos = true;
    document.getElementById('alojamientos')?.scrollIntoView({ behavior: 'smooth' });
  }

  verTodos(): void {
    this.soloFavoritos = false;
    this.destino = '';
    this.busquedaRealizada = false;
  }

  marcarImagenNoDisponible(alojamiento: Alojamiento): void {
    alojamiento.imagenNoDisponible = true;
  }

  private fechaLocal(fecha: Date): string {
    const local = new Date(fecha.getTime() - fecha.getTimezoneOffset() * 60_000);
    return local.toISOString().slice(0, 10);
  }

  cerrarSesion(): void {
    // Por ahora, navegación de prueba.
    // Después se conectará al servicio real de autenticación.
    window.location.href = '/login';
  }
}
