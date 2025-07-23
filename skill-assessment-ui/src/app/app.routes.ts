import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { QuizListComponent } from './components/quiz-list/quiz-list.component';
import { QuizAttemptComponent } from './components/quiz-attempt/quiz-attempt.component';
import {ResultsComponent} from './components/results/results.component';
import {authGuard, publicGuard} from './guards/auth.guard';
import { AdminDashboardComponent } from './components/admin/admin-dashboard/admin-dashboard.component';
import { adminGuard } from './guards/admin.guard';
import {QuestionManagerComponent} from './components/admin/question-manager/question-manager.component';
import {UserAnalyticsComponent} from './components/admin/user-analytics/user-analytics.component';
import {ProfileComponent} from './components/profile/profile.component';
import {HistoryComponent} from './components/history/history.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent, canActivate: [publicGuard] },
  { path: 'register', component: RegisterComponent, canActivate: [publicGuard] },
  { path: 'admin', component: AdminDashboardComponent, canActivate: [authGuard] },
  { path: 'quizzes', component: QuizListComponent, canActivate: [authGuard] },
  { path: 'quiz/:id', component: QuizAttemptComponent, canActivate: [authGuard] },
  { path: 'results/:attemptId', component: ResultsComponent, canActivate: [authGuard] },
  { path: 'history', component: HistoryComponent, canActivate: [authGuard] },
  { path: 'admin/quiz/:quizId/questions', component: QuestionManagerComponent, canActivate: [adminGuard] },
  { path: 'admin/users', component: UserAnalyticsComponent, canActivate: [adminGuard] },
  { path: 'profile', component: ProfileComponent, canActivate: [authGuard] },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: '**', redirectTo: '/login' }
];
