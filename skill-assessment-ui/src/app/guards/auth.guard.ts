import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * Protects routes that require authentication.
 * If the user is not logged in, it redirects them to the /login page.
 */
export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isLoggedIn()) {
    return true; // User is logged in, allow access
  }

  // User is not logged in, redirect to login page
  router.navigate(['/login']);
  return false;
};

/**
 * Protects routes that should only be accessible to unauthenticated users (e.g., login, register).
 * If the user is already logged in, it redirects them to the /quizzes dashboard.
 */
export const publicGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isLoggedIn()) {
    return true; // User is not logged in, allow access
  }

  // User is already logged in, redirect to the main dashboard
  router.navigate(['/quizzes']);
  return false;
};
