import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AdminQuizService } from '../../../services/admin-quiz.service';
import { CreateQuizDialogComponent } from '../dialogs/create-quiz-dialog/create-quiz-dialog.component';
import { GenerateAiQuizDialogComponent } from '../dialogs/generate-ai-quiz-dialog/generate-ai-quiz-dialog.component';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import {MatCard, MatCardContent} from '@angular/material/card';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, MatTableModule, MatButtonModule, MatIconModule, MatProgressSpinnerModule, MatCard, MatCardContent],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.scss']
})
export class AdminDashboardComponent implements OnInit {
  displayedColumns: string[] = ['id', 'title', 'itProfile', 'difficultyLevel', 'numberOfQuestions', 'actions'];
  dataSource: any[] = [];
  isLoading = true;

  constructor(public dialog: MatDialog, private adminQuizService: AdminQuizService, private router: Router, private snackBar: MatSnackBar) {}

  ngOnInit(): void {
    this.loadQuizzes();
  }

  loadQuizzes(): void {
    this.isLoading = true;
    this.adminQuizService.getAllQuizzes().subscribe(data => {
      this.dataSource = data;
      this.isLoading = false;
    });
  }

  openCreateQuizDialog(): void {
    const dialogRef = this.dialog.open(CreateQuizDialogComponent, {
      width: '500px',
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadQuizzes();
      }
    });
  }

  openGenerateAiDialog(): void {
    const dialogRef = this.dialog.open(GenerateAiQuizDialogComponent, {
      width: '500px',
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadQuizzes();
      }
    });
  }

  manageQuestions(quizId: number): void {
    this.router.navigate(['/admin/quiz', quizId, 'questions']);
  }

  deleteQuiz(quizId: number, quizTitle: string): void {
    // Use a simple browser confirm dialog for safety
    if (confirm(`Are you sure you want to permanently delete the quiz "${quizTitle}"? This action cannot be undone.`)) {
      this.adminQuizService.deleteQuiz(quizId).subscribe({
        next: () => {
          this.snackBar.open(`Quiz "${quizTitle}" deleted successfully.`, 'OK', {
            duration: 3000,
            panelClass: ['success-snackbar']
          });
          // Refresh the data in the table
          this.loadQuizzes();
        },
        error: (err) => {
          console.error("Failed to delete quiz", err);
          this.snackBar.open('Could not delete the quiz. Please try again.', 'Close', {
            duration: 5000,
            panelClass: ['error-snackbar']
          });
        }
      });
    }
  }
}
