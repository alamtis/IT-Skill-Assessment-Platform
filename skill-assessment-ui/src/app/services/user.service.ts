import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private userApiUrl = '/api/users';

  constructor(private http: HttpClient) { }

  updateProfile(payload: { email?: string; password?: string }): Observable<any> {
    return this.http.put(`${this.userApiUrl}/me`, payload);
  }
}
