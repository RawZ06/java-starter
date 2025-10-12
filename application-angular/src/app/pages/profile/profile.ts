import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { ProfileService, UserProfile } from '../../services/profile.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, TranslateModule],
  templateUrl: './profile.html',
  styleUrl: './profile.css'
})
export class ProfileComponent implements OnInit {
  profile = signal<UserProfile | null>(null);
  loading = signal(true);
  saving = signal(false);
  error = signal<string | null>(null);
  successMessage = signal<string | null>(null);

  // Form fields
  email = signal('');
  firstName = signal('');
  lastName = signal('');

  // Password change
  showPasswordForm = signal(false);
  currentPassword = signal('');
  newPassword = signal('');
  confirmPassword = signal('');
  passwordError = signal<string | null>(null);

  constructor(
    private profileService: ProfileService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadProfile();
  }

  loadProfile() {
    this.loading.set(true);
    this.error.set(null);

    this.profileService.getProfile().subscribe({
      next: (profile) => {
        this.profile.set(profile);
        this.email.set(profile.email || '');
        this.firstName.set(profile.firstName || '');
        this.lastName.set(profile.lastName || '');
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set('profile.errorLoadingProfile');
        this.loading.set(false);
        console.error('Error loading profile:', err);
      }
    });
  }

  updateProfile() {
    this.saving.set(true);
    this.error.set(null);
    this.successMessage.set(null);

    this.profileService.updateProfile({
      email: this.email(),
      firstName: this.firstName(),
      lastName: this.lastName()
    }).subscribe({
      next: (profile) => {
        this.profile.set(profile);
        this.saving.set(false);
        this.successMessage.set('profile.profileUpdatedSuccess');
        setTimeout(() => this.successMessage.set(null), 3000);
      },
      error: (err) => {
        this.error.set('profile.errorUpdatingProfile');
        this.saving.set(false);
        console.error('Error updating profile:', err);
      }
    });
  }

  togglePasswordForm() {
    this.showPasswordForm.update(v => !v);
    this.currentPassword.set('');
    this.newPassword.set('');
    this.confirmPassword.set('');
    this.passwordError.set(null);
  }

  updatePassword() {
    if (!this.currentPassword() || !this.newPassword() || !this.confirmPassword()) {
      this.passwordError.set('profile.fillAllPasswordFields');
      return;
    }

    if (this.newPassword() !== this.confirmPassword()) {
      this.passwordError.set('profile.passwordsDontMatch');
      return;
    }

    this.saving.set(true);
    this.passwordError.set(null);
    this.successMessage.set(null);

    this.profileService.updateProfile({
      currentPassword: this.currentPassword(),
      newPassword: this.newPassword()
    }).subscribe({
      next: () => {
        this.saving.set(false);
        this.successMessage.set('profile.passwordUpdatedSuccess');
        this.showPasswordForm.set(false);
        this.currentPassword.set('');
        this.newPassword.set('');
        this.confirmPassword.set('');
        setTimeout(() => this.successMessage.set(null), 3000);
      },
      error: (err) => {
        this.passwordError.set(err.error?.error || 'profile.errorUpdatingProfile');
        this.saving.set(false);
        console.error('Error updating password:', err);
      }
    });
  }

  goBack() {
    this.router.navigate(['/']);
  }
}
