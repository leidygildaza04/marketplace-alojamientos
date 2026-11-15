import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthSessionService, UserRole } from './auth-session.service';

export function requireRole(...roles: UserRole[]): CanActivateFn {
  return () => {
    const session = inject(AuthSessionService);
    const router = inject(Router);
    if (!session.isAuthenticated()) return router.createUrlTree(['/login']);
    return roles.length === 0 || (session.role && roles.includes(session.role))
      ? true
      : router.createUrlTree(['/inicio']);
  };
}
