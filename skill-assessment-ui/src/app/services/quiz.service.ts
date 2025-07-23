import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class QuizService {
  // Use a relative path, as configured with the Nginx proxy
  private apiUrl = `/api`;

  constructor(private http: HttpClient) {}

  getAvailableQuizzes(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/quizzes`);
  }

  // Get the full quiz with questions for the user to take.
  // We use the admin endpoint here for simplicity.
  getQuizWithQuestions(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/admin/quizzes/${id}`);
  }

  submitQuiz(quizId: number, submission: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/quizzes/${quizId}/submit`, submission);
  }

  getQuizHistory(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/users/me/quiz-history`);
  }

  getReport(attemptId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/reports/${attemptId}`);
  }
}
