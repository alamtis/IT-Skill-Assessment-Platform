import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AdminQuizService, CreateQuizPayload } from '../../../../services/admin-quiz.service';
import { CommonModule } from '@angular/common';

// Import all required Material modules
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-create-quiz-dialog',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, MatDialogModule, MatFormFieldModule,
    MatInputModule, MatSelectModule, MatButtonModule, MatProgressSpinnerModule
  ],
  templateUrl: './create-quiz-dialog.component.html',
  styleUrls: ['./create-quiz-dialog.component.scss']
})
export class CreateQuizDialogComponent {
  createForm: FormGroup;
  isSubmitting = false;

  // Enums to populate select dropdowns
  difficultyLevels = ['BEGINNER', 'INTERMEDIATE', 'ADVANCED'];
  itProfiles = ['JAVA_DEVELOPER', 'PYTHON_DEVELOPER', 'DEVOPS_ENGINEER', 'FRONTEND_ENGINEER'];

  constructor(
    private fb: FormBuilder,
    private adminQuizService: AdminQuizService,
    public dialogRef: MatDialogRef<CreateQuizDialogComponent>,
    private snackBar: MatSnackBar
  ) {
    this.createForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(5)]],
      description: ['', Validators.maxLength(500)],
      difficultyLevel: ['', Validators.required],
      itProfile: ['', Validators.required]
    });
  }

  onSubmit(): void {
    if (this.createForm.invalid || this.isSubmitting) {
      return;
    }
    this.isSubmitting = true;

    const payload: CreateQuizPayload = this.createForm.value;

    this.adminQuizService.createQuiz(payload).subscribe({
      next: (response) => {
        this.snackBar.open(`Quiz "${response.title}" created successfully!`, 'OK', {
          duration: 3000,
          panelClass: ['success-snackbar']
        });
        this.dialogRef.close(true); // Close dialog and signal success
      },
      error: (err) => {
        this.isSubmitting = false;
        this.snackBar.open('Failed to create quiz. Please try again.', 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
