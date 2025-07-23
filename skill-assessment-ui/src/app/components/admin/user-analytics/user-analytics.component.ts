import { Component, OnInit } from '@angular/core';
import { animate, state, style, transition, trigger } from '@angular/animations';
import { AdminUserService } from '../../../services/admin-user.service';

// Import all required modules
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialog, MatDialogModule } from "@angular/material/dialog";
import { ViewResultDialogComponent } from '../dialogs/view-result-dialog/view-result-dialog.component';
import {UserEditDialogComponent} from '../dialogs/user-edit-dialog/user-edit-dialog.component';
import {MatSnackBar} from '@angular/material/snack-bar';
import {MatTooltip} from '@angular/material/tooltip';

@Component({
  selector: 'app-user-analytics',
  standalone: true,
  imports: [CommonModule, MatTableModule, MatIconModule, MatButtonModule, MatProgressSpinnerModule, MatDialogModule, MatTooltip],
  templateUrl: './user-analytics.component.html',
  styleUrls: ['./user-analytics.component.scss'],
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({ height: '0px', minHeight: '0', paddingTop: '0', paddingBottom: '0', opacity: 0 })),
      state('expanded', style({ height: '*' })),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ],
})
export class UserAnalyticsComponent implements OnInit {
  isLoading = true;
  dataSource: any[] = [];
  columnsToDisplay = ['userId', 'username', 'email', 'attemptCount', 'actions'];
  expandedElement: any | null;

  constructor(private adminUserService: AdminUserService, private dialog: MatDialog, private snackBar: MatSnackBar ) {}

  ngOnInit(): void {
    this.loadUserAnalytics();
  }

  loadUserAnalytics(): void {
    this.isLoading = true;
    this.adminUserService.getUserAnalytics().subscribe(data => {
      this.dataSource = data;
      this.isLoading = false;
    });
  }

  trackByUser(index: number, user: any): number {
    return user.userId;
  }

  viewAttemptDetails(attemptId: number): void {
    this.dialog.open(ViewResultDialogComponent, {
      width: '90vw',
      maxWidth: '1000px',
      data: { attemptId: attemptId }
    });
  }

  openAddUserDialog(): void {
    const dialogRef = this.dialog.open(UserEditDialogComponent, {
      width: '500px'
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadUserAnalytics();
      }
    });
  }

  openEditUserDialog(user: any): void {
    const dialogRef = this.dialog.open(UserEditDialogComponent, {
      width: '500px',
      data: { userId: user.userId }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadUserAnalytics(); // Reload on success
      }
    });
  }
  /**
   * Deletes a specific quiz attempt after confirmation.
   * @param attemptId The ID of the attempt to delete.
   * @param event The mouse click event, to prevent the row from collapsing.
   */
  deleteAttempt(attemptId: number, event: MouseEvent): void {
    event.stopPropagation(); // Prevent the click from bubbling up to the row

    if (confirm('Are you sure you want to permanently delete this quiz attempt record?')) {
      this.adminUserService.deleteQuizAttempt(attemptId).subscribe({
        next: () => {
          this.snackBar.open('Quiz attempt deleted successfully.', 'OK', {
            duration: 3000,
            panelClass: ['success-snackbar']
          });
          // Reload the data to reflect the change
          this.loadUserAnalytics();
        },
        error: (err) => {
          console.error("Failed to delete attempt", err);
          this.snackBar.open('Could not delete the quiz attempt.', 'Close', {
            duration: 5000,
            panelClass: ['error-snackbar']
          });
        }
      });
    }
  }
}
