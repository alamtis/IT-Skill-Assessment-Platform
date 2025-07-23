import {Component, Input, OnInit, Optional} from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { QuizService } from '../../services/quiz.service';

// Import all required Angular Material modules
import { MatCardModule } from '@angular/material/card';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTabsModule } from '@angular/material/tabs';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatListModule } from '@angular/material/list';
import { MatDividerModule } from '@angular/material/divider';
import { MatDialogRef } from '@angular/material/dialog';


@Component({
  selector: 'app-results',
  standalone: true,
  imports: [
    CommonModule, MatCardModule, MatProgressBarModule, MatTabsModule,
    MatIconModule, MatButtonModule, MatProgressSpinnerModule, MatListModule,
    MatDividerModule
  ],
  templateUrl: './results.component.html',
  styleUrls: ['./results.component.scss']
})
export class ResultsComponent implements OnInit {
  @Input() resultDataInput: any;

  isLoading = true;
  resultData: any;
  report: any;
  studyPlan: any;

  constructor(
    private route: ActivatedRoute,
    private quizService: QuizService,
    private router: Router,
  @Optional() private dialogRef: MatDialogRef<any>
) {}

  ngOnInit(): void {
    if (this.resultDataInput) {
      this.processResultData(this.resultDataInput);
      this.isLoading = false;
    } else {
      const attemptIdParam = this.route.snapshot.paramMap.get('attemptId');
      if (attemptIdParam) {
        const attemptId = +attemptIdParam;
        this.loadReport(attemptId);
      } else {
        console.error("Attempt ID not found in route.");
        this.router.navigate(['/quizzes']);
      }
    }
  }

  loadReport(attemptId: number): void {
    this.isLoading = true;
    this.quizService.getReport(attemptId).subscribe({
      next: (data) => {
        this.resultData = data;

        // Safely parse the JSON strings from the backend
        try {
          this.report = JSON.parse(data.detailedReportJson);
        } catch (e) {
          console.error("Failed to parse report JSON", e);
          this.report = { title: "Report Unavailable", summary: "The AI-generated report could not be loaded." };
        }

        try {
          this.studyPlan = JSON.parse(data.studyPlanJson);
        } catch (e) {
          console.error("Failed to parse study plan JSON", e);
          this.studyPlan = { title: "Study Plan Unavailable", recommendations: ["The AI-generated study plan could not be loaded."] };
        }
        this.processResultData(data);
        this.isLoading = false;
      },
      error: (err) => {
        console.error("Failed to load report", err);
        this.isLoading = false;
        // Optional: Redirect or show an error message
        this.router.navigate(['/quizzes']);
      }
    });
  }

  processResultData(data: any): void {
    this.resultData = data;
    try {
      this.report = JSON.parse(data.detailedReportJson);
    } catch (e) {
      this.report = { title: "Rapport Indisponible" };
    }
    try {
      this.studyPlan = JSON.parse(data.studyPlanJson);
    } catch (e) {
      this.studyPlan = { title: "Plan d'étude Indisponible" };
    }
  }

  objectKeys(obj: any): string[] {
    return Object.keys(obj);
  }

  getScoreColor(): 'primary' | 'accent' | 'warn' {
    if (!this.resultData) return 'primary';
    const percentage = this.resultData.percentage;
    if (percentage < 40) return 'warn';
    if (percentage < 70) return 'accent';
    return 'primary';
  }

  /**
   * A helper method to find URLs in a string and wrap them in an anchor tag.
   * @param text The plain text to process.
   * @returns A string with HTML anchor tags for any found URLs.
   */
  linkify(text: string): string {
    if (!text) {
      return '';
    }
    const urlRegex = /(\b(https?|ftp|file):\/\/[-A-Z0-9+&@#\/%?=~_|!:,.;]*[-A-Z0-9+&@#\/%=~_|])/ig;
    return text.replace(urlRegex, (url) => {
      return `<a href="${url}" target="_blank" rel="noopener noreferrer">${url}</a>`;
    });
  }

  takeAnotherQuiz(): void {
    // If this component is inside a dialog, close the dialog first.
    if (this.dialogRef) {
      this.dialogRef.close();
    }
    // Then, navigate to the quizzes page.
    this.router.navigate(['/quizzes']);
  }
}
