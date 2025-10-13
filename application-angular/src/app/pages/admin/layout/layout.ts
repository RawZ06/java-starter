import { Component, signal } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { AuthService } from '../../../services/auth.service';
import { LanguageService } from '../../../services/language.service';
import { ThemeService } from '../../../services/theme.service';

@Component({
  selector: 'app-layout',
  imports: [RouterLink, RouterOutlet, TranslateModule],
  templateUrl: './layout.html'
})
export class Layout {
  sidebarOpen = signal(true);
  dropdownOpen = signal(false);
  langDropdownOpen = signal(false);
  themeDropdownOpen = signal(false);
  currentYear = new Date().getFullYear();

  constructor(
    protected authService: AuthService,
    protected languageService: LanguageService,
    protected themeService: ThemeService
  ) {}

  toggleSidebar() {
    this.sidebarOpen.update(v => !v);
  }

  toggleDropdown() {
    this.dropdownOpen.update(v => !v);
  }

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

  logout() {
    this.authService.logout();
    this.dropdownOpen.set(false);
  }
}
