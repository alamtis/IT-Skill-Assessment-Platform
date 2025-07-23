import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';

export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const snackBar = inject(MatSnackBar);

  if (authService.isAdmin()) {
    return true;
  }

  snackBar.open('You do not have permission to access this page.', 'Close', {
    duration: 5000,
    panelClass: ['error-snackbar']
  });
  router.navigate(['/quizzes']);
  return false;
};
