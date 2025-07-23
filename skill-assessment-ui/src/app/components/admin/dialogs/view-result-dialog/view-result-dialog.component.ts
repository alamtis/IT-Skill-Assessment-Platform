import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { QuizService } from '../../../../services/quiz.service';
import { ResultsComponent } from '../../../results/results.component';
import { CommonModule } from '@angular/common';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-view-result-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    ResultsComponent,
    MatProgressSpinnerModule,
    MatButtonModule
  ],
  templateUrl: './view-result-dialog.component.html',
  styleUrls: ['./view-result-dialog.component.scss']
})
export class ViewResultDialogComponent implements OnInit {
  isLoading = true;
  fullResultData: any;

  constructor(
    private quizService: QuizService,
    @Inject(MAT_DIALOG_DATA) public data: { attemptId: number }
  ) {}

  ngOnInit(): void {
    this.quizService.getReport(this.data.attemptId).subscribe(result => {
      this.fullResultData = result;
      this.isLoading = false;
    });
  }
}
