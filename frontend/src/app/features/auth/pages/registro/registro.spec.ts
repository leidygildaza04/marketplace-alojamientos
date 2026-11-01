
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

  it('requires the fields accepted by the guest registration contract', () => {
    expect(component.form.invalid).toBeTrue();
    expect(Object.keys(component.form.controls)).toEqual([
      'nombres',
      'apellidos',
      'correo',
      'contrasenia',
      'confirmarContrasenia',
    ]);
  });

  it('enforces the password policy used by registration', () => {
    component.form.controls.contrasenia.setValue('password123');

    expect(component.form.controls.contrasenia.hasError('pattern')).toBeTrue();
  });

  it('requires the password confirmation to match', () => {
    component.form.controls.contrasenia.setValue('password123');
    component.form.controls.confirmarContrasenia.setValue('different123');

    expect(component.form.hasError('passwordMismatch')).toBeTrue();
  });

  it('rejects an invalid email address', () => {
    component.form.controls.correo.setValue('correo-invalido');

    expect(component.form.controls.correo.hasError('email')).toBeTrue();
  });

  it('requires names to contain at least two characters', () => {
    component.form.controls.nombres.setValue('A');

    expect(component.form.controls.nombres.hasError('minlength')).toBeTrue();
  });

  it('marks invalid fields as touched when submitting', () => {
    component.onSubmit();

    expect(component.form.controls.nombres.touched).toBeTrue();
    expect(component.form.controls.correo.touched).toBeTrue();
    expect(component.mensaje).toBe('');
  });
});
