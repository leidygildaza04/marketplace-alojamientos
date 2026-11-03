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

  it('requires the fields accepted by the host registration contract', () => {
    expect(component.form.invalid).toBeTrue();
    expect(Object.keys(component.form.controls)).toEqual([
      'nombres',
      'apellidos',
      'correo',
      'contrasenia',
      'confirmarContrasenia',
    ]);
  });

  it('enforces the password policy for the host account', () => {
    component.form.controls.contrasenia.setValue('password123');

    expect(component.form.controls.contrasenia.hasError('pattern')).toBeTrue();
  });
});
