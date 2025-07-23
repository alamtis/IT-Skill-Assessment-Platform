import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { QuizService } from '../../services/quiz.service';
import { MatStepperModule } from '@angular/material/stepper';
import { MatRadioModule } from '@angular/material/radio';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-quiz-attempt',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, MatSnackBarModule, MatStepperModule,
    MatRadioModule, MatButtonModule, MatProgressSpinnerModule, MatCardModule,
    MatIconModule
  ],
  templateUrl: './quiz-attempt.component.html',
  styleUrls: ['./quiz-attempt.component.scss']
})
export class QuizAttemptComponent implements OnInit {
  quiz: any;
  quizForm!: FormGroup;
  quizAttemptId: number | null = null;
  isLoading = true;
  isSubmitting = false;
  private quizStartTime: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private quizService: QuizService,
    private fb: FormBuilder,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    const quizIdParam = this.route.snapshot.paramMap.get('id');
    if (quizIdParam) {
      const quizId = +quizIdParam;
      this.loadQuiz(quizId);
    }
  }

  loadQuiz(id: number): void {
    this.quizService.getQuizWithQuestions(id).subscribe({
      next: (data) => {
        this.quiz = data;
        this.buildForm();
        this.isLoading = false;
        this.quizStartTime = new Date().toISOString();
      },
      error: () => {
        this.isLoading = false;
        this.snackBar.open('Failed to load the quiz. Please try again.', 'Close', {
          duration: 3000, panelClass: ['error-snackbar']
        });
        this.router.navigate(['/quizzes']);
      }
    });
  }

  buildForm(): void {
    const formControls = this.quiz.questions.reduce((acc: any, question: any) => {
      acc[question.id] = this.fb.group({
        questionId: [question.id],
        submittedOption: ['', Validators.required]
      });
      return acc;
    }, {});
    this.quizForm = this.fb.group(formControls);
  }

  submitQuiz(): void {
    if (this.quizForm.invalid || this.isSubmitting)  {
      this.snackBar.open('Please answer all questions before submitting.', 'Close', { duration: 3000 });
      return;
    }
    if (!this.quizStartTime) {
      this.snackBar.open('Quiz session is invalid. Please restart the quiz.', 'Close', { duration: 3000, panelClass: ['error-snackbar'] });
      return;
    }

    this.isSubmitting = true;

    const answers = Object.values(this.quizForm.value);

    const submissionPayload = {
      startedAt: this.quizStartTime,
      answers: answers
    };

    this.quizService.submitQuiz(this.quiz.id, submissionPayload).subscribe({
      next: (result) => {
        this.isSubmitting = false;
        if (result && result.attemptId) {
          // The backend now gives us the ID directly. No more fragile string parsing!
          this.router.navigate(['/results', result.attemptId]);
        } else {
          // This is a fallback for a malformed backend response.
          console.error("Submission was successful, but the response did not contain the new attemptId.", result);
          this.snackBar.open('Quiz submitted, but failed to redirect to results.', 'Close', {
            duration: 5000, panelClass: ['error-snackbar']
          });
          // Navigate the user somewhere safe.
          this.router.navigate(['/history']);
        }
      },
      error: (err) => {
        this.isSubmitting = false;
        this.snackBar.open('There was an error submitting your quiz. Please try again.', 'Close', {
          duration: 5000, panelClass: ['error-snackbar']
        });
        console.error('Submission Error:', err);
      }
    });
  }
}
