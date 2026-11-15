
import { registerLocaleData } from '@angular/common';
import localeEs from '@angular/common/locales/es';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { Home } from './home';
import { MarketplaceDataService } from '../../../../core/services/marketplace-data.service';

registerLocaleData(localeEs, 'es-CO');

describe('Home - Comparación de alojamientos SCRUM-28', () => {
  let fixture: ComponentFixture<Home>;
  let component: Home;
  let marketplace: MarketplaceDataService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Home],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(Home);
    component = fixture.componentInstance;
    marketplace = TestBed.inject(MarketplaceDataService);
  });

  it('CA-15.1: permite comparar entre dos y tres alojamientos mostrando sus características', () => {
    const alojamientos = marketplace.accommodations;

    component.alternarComparacion(alojamientos[0]);
    component.alternarComparacion(alojamientos[1]);
    component.alternarComparacion(alojamientos[2]);

    component.verComparacion();
    fixture.detectChanges();

    expect(component.seleccionComparacion.size).toBe(3);
    expect(component.alojamientosComparados.length).toBe(3);

    const panel = fixture.nativeElement.querySelector(
      '.comparison-panel',
    ) as HTMLElement;

    expect(panel).not.toBeNull();

    const tabla = panel.textContent ?? '';

    expect(tabla).toContain('Apartamento con encanto');
    expect(tabla).toContain('Casa colonial con patio');
    expect(tabla).toContain('Precio por noche');
    expect(tabla).toContain('Capacidad');
    expect(tabla).toContain('Tipo');
    expect(tabla).toContain('Calificación');
    expect(tabla).toContain('Servicios');
    expect(tabla).toContain('4.9');
    expect(tabla).toContain('Wi-Fi');
  });

  it('CA-15.2: impide seleccionar un cuarto alojamiento e informa el límite', () => {
    const alojamientos = marketplace.accommodations;

    component.alternarComparacion(alojamientos[0]);
    component.alternarComparacion(alojamientos[1]);
    component.alternarComparacion(alojamientos[2]);
    component.alternarComparacion(alojamientos[3]);

    fixture.detectChanges();

    expect(component.seleccionComparacion.size).toBe(3);
    expect(
      component.seleccionComparacion.has(alojamientos[3].id),
    ).toBeFalse();

    expect(component.errorComparacion).toBe(
      'Puedes comparar hasta 3 alojamientos a la vez.',
    );

    expect(fixture.nativeElement.textContent).toContain(
      'Puedes comparar hasta 3 alojamientos a la vez.',
    );
  });

  it('CA-15.3: muestra como no disponible un alojamiento pausado', () => {
    const alojamientos = marketplace.accommodations;

    component.alternarComparacion(alojamientos[0]);
    component.alternarComparacion(alojamientos[1]);

    alojamientos[1].status = 'PAUSADA';

    component.verComparacion();
    fixture.detectChanges();

    expect(component.alojamientosComparados.length).toBe(2);

    const panel = fixture.nativeElement.querySelector(
      '.comparison-panel',
    ) as HTMLElement;

    expect(panel).not.toBeNull();

    const tabla = panel.textContent ?? '';

    expect(tabla).toContain('Apartamento con encanto');
    expect(tabla).toContain('Refugio entre');
    expect(tabla).toContain('(No disponible)');
    expect(tabla).toContain('No disponible');

    // Los datos del alojamiento pausado no deben mostrarse.
    expect(tabla).not.toContain('4 huéspedes');
    expect(tabla).not.toContain('Parqueadero');
  });
});
