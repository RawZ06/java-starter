import { Component, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { AuthService } from '../../services/auth.service';
import { LanguageService } from '../../services/language.service';

@Component({
  selector: 'app-header',
  imports: [RouterLink, TranslateModule],
  templateUrl: './header.html'
})
export class Header {
  dropdownOpen = signal(false);
  langDropdownOpen = signal(false);

  constructor(
    protected authService: AuthService,
    protected languageService: LanguageService
  ) {}

  toggleDropdown() {
    this.dropdownOpen.update(v => !v);
  }

  toggleLangDropdown() {
    this.langDropdownOpen.update(v => !v);
  }

  selectLanguage(lang: string) {
    this.languageService.setLanguage(lang);
    this.langDropdownOpen.set(false);
  }

  logout() {
    this.authService.logout();
    this.dropdownOpen.set(false);
  }
}
