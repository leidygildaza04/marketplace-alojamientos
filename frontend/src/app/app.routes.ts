
import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'login' },

  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/pages/login/login').then((m) => m.Login),
  },
  {
    path: 'registro',
    loadComponent: () =>
      import('./features/auth/pages/registro/registro').then((m) => m.Registro),
  },
  {
    path: 'registro-anfitrion',
    loadComponent: () =>
      import('./features/auth/pages/registro-anfitrion/registro-anfitrion').then(
        (m) => m.RegistroAnfitrion,
      ),
  },

 
{
  path: 'inicio',
  loadComponent: () =>
    import('./features/guest/pages/home/home').then(
      (m) => m.Home,
    ),
},

  { path: '**', redirectTo: 'login' },
];
