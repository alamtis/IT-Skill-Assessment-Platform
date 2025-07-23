import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {Observable, BehaviorSubject, of, tap} from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';
import { jwtDecode } from 'jwt-decode';

interface DecodedToken {
  sub: string;
  iat: number;
  exp: number;
  roles?: string[];
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  private userApiUrl = '/api/users';
  private readonly TOKEN_KEY = 'auth_token';
  private decodedToken: DecodedToken | null = null;

  constructor(private http: HttpClient, private router: Router) {
    this.decodeAndStoreToken();
  }

  register(user: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, user, { responseType: 'text' });
  }

  login(credentials: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        if (response && response.accessToken) {
          localStorage.setItem(this.TOKEN_KEY, response.accessToken);
          this.decodeAndStoreToken();
        }
      })
    );
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    this.router.navigate(['/login']);
    this.decodedToken = null;
  }
  private decodeAndStoreToken(): void {
    const token = this.getToken();
    if (token) {
      try {
        this.decodedToken = jwtDecode<DecodedToken>(token);
      } catch (error) {
        console.error("Failed to decode token", error);
        this.decodedToken = null;
      }
    }
  }

  isLoggedIn(): boolean {
    if (!this.decodedToken) return false;
    const expiry = this.decodedToken.exp * 1000;
    return (Date.now() < expiry);
  }

  isAdmin(): boolean {
    if (!this.isLoggedIn()) {
      return false;
    }

    const roles = this.decodedToken?.roles;
    return Array.isArray(roles) && roles.includes('ROLE_ADMIN');
  }
  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  /**
   * Fetches the details of the currently authenticated user.
   * @returns An observable of the user object.
   */
  getCurrentUser(): Observable<any> {
    if (!this.isLoggedIn()) {
      return of(null); // Return an observable of null if not logged in
    }
    return this.http.get<any>(`${this.userApiUrl}/me`);
  }
}
