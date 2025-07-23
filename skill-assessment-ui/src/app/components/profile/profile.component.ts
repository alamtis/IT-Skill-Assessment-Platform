import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

// Import Services
import { UserService } from '../../services/user.service';
import { AuthService } from '../../services/auth.service'; // Needed to get current user details

// Import all required Angular Material modules
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatIconModule,
    MatSnackBarModule
  ],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {
  profileForm: FormGroup;
  isSubmitting = false;

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private authService: AuthService, // Inject AuthService
    private snackBar: MatSnackBar
  ) {
    this.profileForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(20)]],
      email: ['', [Validators.required, Validators.email, Validators.maxLength(50)]],
      password: ['', [Validators.minLength(6), Validators.maxLength(40)]]
    });
  }

  ngOnInit(): void {
    this.authService.getCurrentUser().subscribe({
      next: (user) => {
        if (user) {
          this.profileForm.patchValue({
            username: user.username,
            email: user.email
          });
        }
      },
      error: (err) => {
        console.error("Could not fetch user details", err);
        this.snackBar.open("Could not load your profile data.", "Close", {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  // Strongly-typed getters for easy and safe access in the template
  get username(): AbstractControl | null {
    return this.profileForm.get('username');
  }

  get email(): AbstractControl | null {
    return this.profileForm.get('email');
  }

  get password(): AbstractControl | null {
    return this.profileForm.get('password');
  }

  onSubmit(): void {
    // Trigger validation on all fields before submitting
    this.profileForm.markAllAsTouched();
    if (this.profileForm.invalid || this.isSubmitting) {
      return;
    }

    this.isSubmitting = true;

    // Create a clean payload object from the form values
    const payload: { username: string; email: string; password?: string } = this.profileForm.value;
    if (!payload.password || payload.password.trim() === '') {
      delete payload.password;
    }


    this.userService.updateProfile(payload).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.snackBar.open('Profile updated successfully!', 'OK', {
          duration: 3000,
          panelClass: ['success-snackbar']
        });
        // Clear the password field after a successful update for security
        this.password?.reset();
        this.snackBar.open('If you changed your username, you may need to log in again to see all changes.', 'Info', {
          duration: 7000,
        });
      },
      error: (err) => {
        this.isSubmitting = false;
        const errorMessage = err.error?.message || 'Failed to update profile. Please try again.';
        this.snackBar.open(errorMessage, 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }
}
