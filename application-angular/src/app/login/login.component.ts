import { Component, signal } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  username = signal('');
  password = signal('');
  error = signal('');

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  submit() {
    this.error.set('');
    this.authService.login(this.username(), this.password()).subscribe({
      next: () => {
        this.router.navigate(['/hello']);
      },
      error: (err) => {
        this.error.set('Login failed. Please check your credentials.');
        console.error('Login error:', err);
      }
    });
  }
}
