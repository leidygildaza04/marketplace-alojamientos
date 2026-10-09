import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { Registro } from './registro';

describe('Registro', () => {
  let component: Registro;
  let fixture: ComponentFixture<Registro>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Registro],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(Registro);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('requires the required personal and account details', () => {
    expect(component.form.invalid).toBeTrue();
    expect(Object.keys(component.form.controls)).toEqual([
      'nombres',
      'apellidos',
      'telefono',
      'correo',
      'fechaNacimiento',
      'contrasenia',
      'confirmarContrasenia',
    ]);
  });

  it('does not allow people under 18 to register', () => {
    const underageDate = new Date();
    underageDate.setFullYear(underageDate.getFullYear() - 17);
    const dateValue = [
      underageDate.getFullYear(),
      String(underageDate.getMonth() + 1).padStart(2, '0'),
      String(underageDate.getDate()).padStart(2, '0'),
    ].join('-');

    component.form.controls.fechaNacimiento.setValue(dateValue);

    expect(component.form.controls.fechaNacimiento.hasError('underage')).toBeTrue();
  });

  it('requires the password confirmation to match', () => {
    component.form.controls.contrasenia.setValue('password123');
    component.form.controls.confirmarContrasenia.setValue('different123');

    expect(component.form.hasError('passwordMismatch')).toBeTrue();
  });
});
