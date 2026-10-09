import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { RegistroAnfitrion } from './registro-anfitrion';

describe('RegistroAnfitrion', () => {
  let component: RegistroAnfitrion;
  let fixture: ComponentFixture<RegistroAnfitrion>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegistroAnfitrion],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(RegistroAnfitrion);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('requires host identity and contact details', () => {
    expect(component.form.invalid).toBeTrue();
    expect(Object.keys(component.form.controls)).toEqual([
      'nombres',
      'apellidos',
      'cedula',
      'direccion',
      'telefono',
      'correo',
      'fechaNacimiento',
      'contrasenia',
      'confirmarContrasenia',
    ]);
  });

  it('requires a numeric identity document', () => {
    component.form.controls.cedula.setValue('ABC123');

    expect(component.form.controls.cedula.hasError('pattern')).toBeTrue();
  });
});
