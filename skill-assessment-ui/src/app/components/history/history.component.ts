import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { QuizService } from '../../services/quiz.service';

// Import required Angular Material modules
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-history',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatCardModule
  ],
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.scss']
})
export class HistoryComponent implements OnInit {
  isLoading = true;
  historyData: any[] = [];
  displayedColumns: string[] = ['quizTitle', 'percentage', 'completedAt', 'actions'];

  constructor(
    private quizService: QuizService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadHistory();
  }

  loadHistory(): void {
    this.isLoading = true;
    this.quizService.getQuizHistory().subscribe({
      next: (data) => {
        this.historyData = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error("Failed to load quiz history", err);
        this.isLoading = false;
        // Optional: Show a snackbar error
      }
    });
  }

  viewReport(attemptId: number): void {
    // Navigate to the results page for the selected attempt
    this.router.navigate(['/results', attemptId]);
  }

  /**
   * Returns a CSS class based on the quiz score percentage.
   * @param percentage The user's score.
   * @returns A string representing the CSS class for coloring.
   */
  getScoreClass(percentage: number): string {
    if (percentage < 40) return 'score-low';
    if (percentage < 70) return 'score-medium';
    return 'score-high';
  }
}
