import { Component, Inject, OnInit } from '@angular/core'; // Import OnInit
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AdminQuizService, QuestionPayload } from '../../../../services/admin-quiz.service';
import { CommonModule } from '@angular/common';

// ... other Material module imports ...
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-question-edit-dialog',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, MatDialogModule, MatFormFieldModule,
    MatInputModule, MatSelectModule, MatButtonModule, MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  templateUrl: './question-edit-dialog.component.html',
  styleUrls: ['./question-edit-dialog.component.scss']
})
export class QuestionEditDialogComponent implements OnInit { // Implement OnInit
  questionForm: FormGroup;
  isSubmitting = false;
  isEditMode = false; // A flag to determine if we are editing or creating

  constructor(
    private fb: FormBuilder,
    private adminQuizService: AdminQuizService,
    public dialogRef: MatDialogRef<QuestionEditDialogComponent>,
    private snackBar: MatSnackBar,
    // Inject the data passed from the parent component
    @Inject(MAT_DIALOG_DATA) public data: { quizId: number, questionData?: any }
  ) {
    // Check if we received questionData, which means we are in edit mode
    this.isEditMode = !!this.data.questionData;

    // Initialize the form
    this.questionForm = this.fb.group({
      questionText: ['', [Validators.required, Validators.minLength(10)]],
      optionA: ['', Validators.required],
      optionB: ['', Validators.required],
      optionC: ['', Validators.required],
      optionD: ['', Validators.required],
      correctOption: ['', Validators.required],
      explanation: ['', Validators.maxLength(1000)]
    });
  }

  ngOnInit(): void {
    // If in edit mode, patch the form with the existing data
    if (this.isEditMode && this.data.questionData) {
      this.questionForm.patchValue(this.data.questionData);
    }
  }

  onSubmit(): void {
    if (this.questionForm.invalid || this.isSubmitting) {
      return;
    }
    this.isSubmitting = true;
    const payload: QuestionPayload = this.questionForm.value;

    if (this.isEditMode) {
      // If editing, call the update service method
      const questionId = this.data.questionData.id;
      this.adminQuizService.updateQuestion(this.data.quizId, questionId, payload).subscribe({
        next: () => this.handleSuccess('Question updated successfully!'),
        error: (err) => this.handleError(err, 'Failed to update question.')
      });
    } else {
      // If creating, call the add service method
      this.adminQuizService.addQuestionToQuiz(this.data.quizId, payload).subscribe({
        next: () => this.handleSuccess('Question added successfully!'),
        error: (err) => this.handleError(err, 'Failed to add question.')
      });
    }
  }

  private handleSuccess(message: string): void {
    this.snackBar.open(message, 'OK', {
      duration: 3000,
      panelClass: ['success-snackbar']
    });
    this.dialogRef.close(true); // Close dialog and signal success
  }

  private handleError(err: any, defaultMessage: string): void {
    this.isSubmitting = false;
    this.snackBar.open(defaultMessage, 'Close', {
      duration: 5000,
      panelClass: ['error-snackbar']
    });
    console.error(defaultMessage, err);
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
