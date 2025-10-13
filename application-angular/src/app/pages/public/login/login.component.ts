import { Component, signal } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { AuthService } from '../../../services/auth.service';
import { LanguageService } from '../../../services/language.service';
import { ThemeService } from '../../../services/theme.service';
import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { ButtonModule } from 'primeng/button';
import { MessageModule } from 'primeng/message';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    FormsModule,
    TranslateModule,
    CardModule,
    InputTextModule,
    PasswordModule,
    ButtonModule,
    MessageModule
  ],
  templateUrl: './login.component.html'
})
export class LoginComponent {
  username = signal('');
  password = signal('');
  error = signal('');
  langDropdownOpen = signal(false);
  themeDropdownOpen = signal(false);

  constructor(
    private authService: AuthService,
    private router: Router,
    private translate: TranslateService,
    protected languageService: LanguageService,
    protected themeService: ThemeService
  ) {}

  toggleLangDropdown() {
    this.langDropdownOpen.update(v => !v);
  }

  toggleThemeDropdown() {
    this.themeDropdownOpen.update(v => !v);
  }

  selectLanguage(lang: string) {
    this.languageService.setLanguage(lang);
    this.langDropdownOpen.set(false);
  }

  selectTheme(mode: 'light' | 'dark' | 'system') {
    this.themeService.setTheme(mode);
    this.themeDropdownOpen.set(false);
  }

  submit() {
    this.error.set('');
    this.authService.login(this.username(), this.password()).subscribe({
      next: () => {
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.translate.get('login.loginError').subscribe((text: string) => {
          this.error.set(text);
        });
        console.error('Login error:', err);
      }
    });
  }
}
