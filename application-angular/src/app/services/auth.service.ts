import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly tokenKey = 'app_token';
  private readonly tokenSignal = signal<string | null>(this.getStoredToken());

  token = this.tokenSignal.asReadonly();

  constructor(private http: HttpClient) {}

  login(username: string, password: string) {
    return this.http.post<{ token: string }>('/api/auth/login', { username, password })
      .pipe(
        tap(res => {
          localStorage.setItem(this.tokenKey, res.token);
          this.tokenSignal.set(res.token);
        })
      );
  }

  logout() {
    localStorage.removeItem(this.tokenKey);
    this.tokenSignal.set(null);
  }

  private getStoredToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }
}
