import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

// Define interfaces for the request payloads to ensure type safety
export interface CreateQuizPayload {
  title: string;
  description: string;
  difficultyLevel: string;
  itProfile: string;
}

export interface GenerateAiQuizPayload {
  itProfile: string;
  difficultyLevel: string;
  numberOfQuestions: number;
}

export interface QuestionPayload {
  questionText: string;
  optionA: string;
  optionB: string;
  optionC: string;
  optionD: string;
  correctOption: 'A' | 'B' | 'C' | 'D';
  explanation?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AdminQuizService {
  // Use a relative path, as configured with the Nginx proxy
  private adminApiUrl = `/api/admin/quizzes`;

  constructor(private http: HttpClient) { }

  /**
   * Fetches all quizzes from the admin endpoint.
   * @returns An observable array of quizzes.
   */
  getAllQuizzes(): Observable<any[]> {
    return this.http.get<any[]>(this.adminApiUrl);
  }

  /**
   * Creates a new quiz manually.
   * @param payload The data for the new quiz.
   * @returns An observable of the newly created quiz object.
   */
  createQuiz(payload: CreateQuizPayload): Observable<any> {
    return this.http.post(this.adminApiUrl, payload);
  }

  /**
   * Triggers the backend to generate a new quiz using the AI service.
   * @param payload The parameters for the AI generation.
   * @returns An observable of the newly generated quiz object.
   */
  generateQuizWithAi(payload: GenerateAiQuizPayload): Observable<any> {
    return this.http.post(`${this.adminApiUrl}/generate-ai`, payload);
  }

  // Method to get a single quiz with its questions
  getQuizById(quizId: number): Observable<any> {
    return this.http.get<any>(`${this.adminApiUrl}/${quizId}`);
  }

  // Method to add a question to a quiz
  addQuestionToQuiz(quizId: number, payload: QuestionPayload): Observable<any> {
    return this.http.post(`${this.adminApiUrl}/${quizId}/questions`, payload);
  }

  /**
   * Updates an existing question for a specific quiz.
   * @param quizId The ID of the parent quiz.
   * @param questionId The ID of the question to update.
   * @param payload The new data for the question.
   */
  updateQuestion(quizId: number, questionId: number, payload: QuestionPayload): Observable<any> {
    return this.http.put(`${this.adminApiUrl}/${quizId}/questions/${questionId}`, payload);
  }

  /**
   * Deletes a question from a specific quiz.
   * @param quizId The ID of the parent quiz.
   * @param questionId The ID of the question to delete.
   */
  deleteQuestion(quizId: number, questionId: number): Observable<Object> {
    return this.http.delete(`${this.adminApiUrl}/${quizId}/questions/${questionId}`);
  }

  /**
   * Deletes a quiz by its ID.
   * @param quizId The ID of the quiz to delete.
   */
  deleteQuiz(quizId: number): Observable<Object> {
    return this.http.delete(`${this.adminApiUrl}/${quizId}`);
  }
}
