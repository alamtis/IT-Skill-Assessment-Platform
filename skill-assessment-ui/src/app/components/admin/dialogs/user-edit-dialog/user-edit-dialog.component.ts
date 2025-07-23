import { Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AdminUserService } from '../../../../services/admin-user.service';
import { CommonModule } from '@angular/common';

// Import all required Material modules
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-user-edit-dialog',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, MatDialogModule, MatFormFieldModule,
    MatInputModule, MatSelectModule, MatButtonModule, MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  templateUrl: './user-edit-dialog.component.html',
  styleUrls: ['./user-edit-dialog.component.scss']
})
export class UserEditDialogComponent {
  userForm: FormGroup;
  isSubmitting = false;
  isLoading = false;
  isEditMode: boolean;
  roles = ['ROLE_USER', 'ROLE_ADMIN'];
  title: string;
  private userId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private adminUserService: AdminUserService,
    public dialogRef: MatDialogRef<UserEditDialogComponent>,
    private snackBar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) public data: { userId?: number }
  ) {

    // 1. Determine the mode and set the title.
    this.isEditMode = !!this.data?.userId;
    this.title = this.isEditMode ? 'Edit User' : 'Create New User';
    this.userId = this.data?.userId || null;

    // 2. Initialize the form structure.
    this.userForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      // Password is only required when NOT in edit mode.
      password: ['', this.isEditMode ? [Validators.minLength(6)] : [Validators.required, Validators.minLength(6)]],
      roles: [['ROLE_USER'], Validators.required]
    });
  }

  ngOnInit(): void {
    // If we are in edit mode, fetch the full user data
    if (this.isEditMode && this.userId) {
      this.isLoading = true;
      this.adminUserService.getUserById(this.userId).subscribe({
        next: (userData) => {
          // Now we have the full user object with roles
          this.userForm.patchValue({
            username: userData.username,
            email: userData.email,
            roles: userData.roles
          });
          this.isLoading = false;
        },
        error: (err) => {
          this.isLoading = false;
          console.error("Failed to load user data", err);
          this.snackBar.open("Could not load user data.", "Close", { duration: 3000 });
          this.dialogRef.close();
        }
      });
    }
  }
  onSubmit(): void {
    if (this.userForm.invalid || this.isSubmitting) return;

    this.isSubmitting = true;
    const payload = this.userForm.getRawValue();

    if (this.isEditMode && (!payload.password || payload.password.trim() === '')) {
      delete payload.password;
    }

    const action = this.isEditMode && this.userId
      ? this.adminUserService.adminUpdateUser(this.userId, payload)
      : this.adminUserService.adminCreateUser(payload);


    action.subscribe({
      next: () => {
        this.snackBar.open(`User ${this.isEditMode ? 'updated' : 'created'} successfully!`, 'OK', {
          duration: 3000, panelClass: ['success-snackbar']
        });
        this.dialogRef.close(true);
      },
      error: (err) => {
        this.isSubmitting = false;
        this.snackBar.open(err.error?.message || `Failed to ${this.isEditMode ? 'update' : 'create'} user.`, 'Close', {
          duration: 5000, panelClass: ['error-snackbar']
        });
      }
    });
  }
}
