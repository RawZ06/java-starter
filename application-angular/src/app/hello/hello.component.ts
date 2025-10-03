import { Component, signal } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-hello',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './hello.component.html',
  styleUrl: './hello.component.css'
})
export class HelloComponent {
  data = signal<any>(null);
  error = signal('');

  constructor(
    private http: HttpClient,
    private router: Router,
    public authService: AuthService
  ) {}

  callApi() {
    if (!this.authService.token()) {
      this.router.navigate(['/login']);
      return;
    }

    this.error.set('');
    this.http.get('/api/hello').subscribe({
      next: (res) => {
        this.data.set(res);
      },
      error: (err) => {
        this.error.set('Failed to call API. Please login again.');
        console.error('API call error:', err);
      }
    });
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
