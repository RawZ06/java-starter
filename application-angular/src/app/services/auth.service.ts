import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap, catchError, of, firstValueFrom } from 'rxjs';

export interface CurrentUser {
  id: number;
  login: string;
  email: string;
  firstName?: string;
  lastName?: string;
  role: string;
  active: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly tokenKey = 'app_token';
  private readonly tokenSignal = signal<string | null>(this.getStoredToken());
  private readonly currentUserSignal = signal<CurrentUser | null>(null);

  token = this.tokenSignal.asReadonly();
  currentUser = this.currentUserSignal.asReadonly();

  constructor(private http: HttpClient, private router: Router) {}

  login(username: string, password: string) {
    return this.http.post<{ token: string }>('/api/public/auth/login', { username, password })
      .pipe(
        tap(res => {
          localStorage.setItem(this.tokenKey, res.token);
          this.tokenSignal.set(res.token);
          this.loadCurrentUser();
        })
      );
  }

  logout() {
    localStorage.removeItem(this.tokenKey);
    this.tokenSignal.set(null);
    this.currentUserSignal.set(null);
    this.router.navigate(['/']);
  }

  async loadCurrentUser() {
    if (!this.token()) {
      return;
    }

    try {
      const user = await firstValueFrom(
        this.http.get<CurrentUser>('/api/auth/me').pipe(
          catchError(() => {
            // If /me fails (401, 403, etc.), logout silently
            this.tokenSignal.set(null);
            this.currentUserSignal.set(null);
            localStorage.removeItem(this.tokenKey);
            return of(null);
          })
        )
      );

      if (user) {
        this.currentUserSignal.set(user);
      }
    } catch (error) {
      console.error('Failed to load current user', error);
    }
  }

  private getStoredToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }
}
