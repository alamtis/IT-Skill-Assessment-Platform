import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AdminQuizService, GenerateAiQuizPayload } from '../../../../services/admin-quiz.service';
import { CommonModule } from '@angular/common';

// Import all required Material modules
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-generate-ai-quiz-dialog',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, MatDialogModule, MatFormFieldModule,
    MatInputModule, MatSelectModule, MatButtonModule, MatProgressSpinnerModule
  ],
  templateUrl: './generate-ai-quiz-dialog.component.html',
  styleUrls: ['./generate-ai-quiz-dialog.component.scss']
})
export class GenerateAiQuizDialogComponent {
  generateForm: FormGroup;
  isSubmitting = false;

  difficultyLevels = ['BEGINNER', 'INTERMEDIATE', 'ADVANCED'];
  itProfiles = ['JAVA_DEVELOPER', 'PYTHON_DEVELOPER', 'DEVOPS_ENGINEER', 'FRONTEND_ENGINEER'];

  constructor(
    private fb: FormBuilder,
    private adminQuizService: AdminQuizService,
    public dialogRef: MatDialogRef<GenerateAiQuizDialogComponent>,
    private snackBar: MatSnackBar
  ) {
    this.generateForm = this.fb.group({
      itProfile: ['', Validators.required],
      difficultyLevel: ['', Validators.required],
      numberOfQuestions: [5, [Validators.required, Validators.min(5), Validators.max(50)]]
    });
  }

  onSubmit(): void {
    if (this.generateForm.invalid || this.isSubmitting) {
      return;
    }
    this.isSubmitting = true;

    const payload: GenerateAiQuizPayload = this.generateForm.value;

    this.adminQuizService.generateQuizWithAi(payload).subscribe({
      next: (response) => {
        this.snackBar.open(`AI quiz "${response.title}" generated successfully!`, 'OK', {
          duration: 3000,
          panelClass: ['success-snackbar']
        });
        this.dialogRef.close(true);
      },
      error: (err) => {
        this.isSubmitting = false;
        this.snackBar.open('Failed to generate AI quiz. Please try again.', 'Close', {
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
