import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Check if user is authenticated
  if (!authService.token()) {
    router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
    return false;
  }

  // Check if user is admin
  const currentUser = authService.currentUser();
  if (!currentUser || currentUser.role !== 'ADMIN') {
    // Redirect non-admin users to home
    router.navigate(['/']);
    return false;
  }

  return true;
};
