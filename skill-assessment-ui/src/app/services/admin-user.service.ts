import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AdminUserService {
  private adminApiUrl = `/api/admin`;
  private userApiUrl = `/api/users`;

  constructor(private http: HttpClient) {
  }

  getUserAnalytics(): Observable<any[]> {
    return this.http.get<any[]>(`${this.adminApiUrl}/analytics/users`);
  }

  getUserById(userId: number): Observable<any> {
    return this.http.get<any>(`${this.userApiUrl}/${userId}`);
  }
  adminCreateUser(payload: any): Observable<any> {
    return this.http.post(`${this.adminApiUrl}/users`, payload);
  }

  adminUpdateUser(userId: number, payload: any): Observable<any> {
    return this.http.put(`${this.adminApiUrl}/users/${userId}`, payload);
  }

  /**
   * (Admin only) Deletes a specific quiz attempt.
   * @param attemptId The ID of the quiz attempt to delete.
   */
  deleteQuizAttempt(attemptId: number): Observable<Object> {
    return this.http.delete(`${this.adminApiUrl}/attempts/${attemptId}`);
  }

}
