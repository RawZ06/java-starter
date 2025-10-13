import { Component, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { MenuModule } from 'primeng/menu';
import { ButtonModule } from 'primeng/button';
import { AuthService } from '../../services/auth.service';
import { LanguageService } from '../../services/language.service';
import { ThemeService } from '../../services/theme.service';

@Component({
  selector: 'app-header',
  imports: [RouterLink, TranslateModule, MenuModule, ButtonModule],
  templateUrl: './header.html'
})
export class Header {
  dropdownOpen = signal(false);
  langDropdownOpen = signal(false);
  themeDropdownOpen = signal(false);

  constructor(
    protected authService: AuthService,
    protected languageService: LanguageService,
    protected themeService: ThemeService
  ) {}

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
