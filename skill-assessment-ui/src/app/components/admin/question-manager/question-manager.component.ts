import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AdminQuizService } from '../../../services/admin-quiz.service';
import { QuestionEditDialogComponent } from '../dialogs/question-edit-dialog/question-edit-dialog.component';

// Import all required modules for this standalone component
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-question-manager',
  standalone: true,
  imports: [
    CommonModule, RouterLink, MatDialogModule, MatSnackBarModule, MatTableModule,
    MatButtonModule, MatIconModule, MatProgressSpinnerModule, MatTooltipModule,
    MatCardModule
  ],
  templateUrl: './question-manager.component.html',
  styleUrls: ['./question-manager.component.scss']
})
export class QuestionManagerComponent implements OnInit {
  quiz: any;
  quizId!: number;
  isLoading = true;
  displayedColumns: string[] = ['id', 'questionText', 'correctOption', 'actions'];
  dataSource: any[] = [];

  constructor(
    private route: ActivatedRoute,
    private adminQuizService: AdminQuizService,
    private snackBar: MatSnackBar,
    public dialog: MatDialog
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('quizId');
    if (idParam) {
      this.quizId = +idParam;
      this.loadQuizAndQuestions();
    } else {
      this.isLoading = false;
      console.error("Quiz ID not found in route parameters.");
    }
  }

  loadQuizAndQuestions(): void {
    this.isLoading = true;
    this.adminQuizService.getQuizById(this.quizId).subscribe({
      next: (data) => {
        this.quiz = data;
        this.dataSource = data.questions || []; // Ensure dataSource is always an array
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        console.error("Failed to load quiz data", err);
        this.snackBar.open('Could not load quiz data.', 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  openAddQuestionDialog(): void {
    const dialogRef = this.dialog.open(QuestionEditDialogComponent, {
      width: '650px',
      data: { quizId: this.quizId } // Pass the quizId to the dialog
    });

    dialogRef.afterClosed().subscribe(result => {
      // If the dialog returned 'true', it means a question was successfully added.
      if (result) {
        this.loadQuizAndQuestions(); // Reload the table to show the new question
      }
    });
  }

  editQuestion(question: any): void {
    const dialogRef = this.dialog.open(QuestionEditDialogComponent, {
      width: '650px',
      data: {
        quizId: this.quizId,
        questionData: question
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadQuizAndQuestions();
      }
    });
  }

  deleteQuestion(questionId: number): void {
    // Use a confirmation dialog for a better UX
    if (confirm('Are you sure you want to permanently delete this question?')) {
      this.adminQuizService.deleteQuestion(this.quizId, questionId).subscribe({
        next: () => {
          this.snackBar.open('Question deleted successfully.', 'OK', {
            duration: 3000,
            panelClass: ['success-snackbar']
          });
          this.loadQuizAndQuestions();
        },
        error: (err) => {
          console.error("Failed to delete question", err);
          this.snackBar.open('Could not delete the question.', 'Close', {
            duration: 5000,
            panelClass: ['error-snackbar']
          });
        }
      });
    }
  }
}
