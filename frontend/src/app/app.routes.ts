import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'login' },
  {
    path: 'login',
    loadComponent: () => import('./features/auth/pages/login/login').then((module) => module.Login),
  },
  {
    path: 'registro',
    loadComponent: () =>
      import('./features/auth/pages/registro/registro').then((module) => module.Registro),
  },
  {
    path: 'registro-anfitrion',
    loadComponent: () =>
      import('./features/auth/pages/registro-anfitrion/registro-anfitrion').then(
        (module) => module.RegistroAnfitrion,
      ),
  },
  {
    path: 'inicio',
    loadComponent: () =>
      import('./features/guest/pages/home/home').then((module) => module.Home),
  },
  {
    path: 'alojamiento/:id',
    loadComponent: () =>
      import('./features/guest/pages/detail/detail').then(
        (module) => module.AccommodationDetail,
      ),
  },
  {
    path: 'reservas',
    loadComponent: () =>
      import('./features/guest/pages/reservations/reservations').then(
        (module) => module.GuestReservations,
      ),
  },
  {
    path: 'anfitrion',
    loadComponent: () =>
      import('./features/host/pages/dashboard/host-dashboard').then(
        (module) => module.HostDashboard,
      ),
  },
  {
    path: 'administracion',
    loadComponent: () =>
      import('./features/admin/pages/dashboard/admin-dashboard').then(
        (module) => module.AdminDashboard,
      ),
  },
  // Un enlace de sección no válido no debe devolver al usuario al inicio de sesión.
  { path: '**', redirectTo: 'inicio' },
];
