import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

export interface UserProfile {
  id: number;
  login: string;
  email?: string;
  firstName?: string;
  lastName?: string;
  role: string;
  active: boolean;
  createdAt?: string;
}

export interface UpdateProfileRequest {
  email?: string;
  firstName?: string;
  lastName?: string;
  currentPassword?: string;
  newPassword?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private profileSignal = signal<UserProfile | null>(null);
  readonly profile = this.profileSignal.asReadonly();

  constructor(private http: HttpClient) {}

  getProfile(): Observable<UserProfile> {
    return this.http.get<UserProfile>('/api/user/profile').pipe(
      tap(profile => this.profileSignal.set(profile))
    );
  }

  updateProfile(request: UpdateProfileRequest): Observable<UserProfile> {
    return this.http.put<UserProfile>('/api/user/profile', request).pipe(
      tap(profile => this.profileSignal.set(profile))
    );
  }

  clearProfile(): void {
    this.profileSignal.set(null);
  }
}
