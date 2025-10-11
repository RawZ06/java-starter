import { Injectable, signal } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Injectable({
  providedIn: 'root'
})
export class LanguageService {
  private readonly STORAGE_KEY = 'app_language';
  private currentLangSignal = signal<string>('fr');

  currentLang = this.currentLangSignal.asReadonly();

  constructor(private translate: TranslateService) {
    // Récupérer la langue sauvegardée ou utiliser FR par défaut
    const savedLang = localStorage.getItem(this.STORAGE_KEY) || 'fr';
    this.setLanguage(savedLang);
  }

  setLanguage(lang: string) {
    this.translate.use(lang);
    localStorage.setItem(this.STORAGE_KEY, lang);
    this.currentLangSignal.set(lang);
  }

  toggleLanguage() {
    const newLang = this.currentLangSignal() === 'fr' ? 'en' : 'fr';
    this.setLanguage(newLang);
  }

  getAvailableLanguages() {
    return [
      { code: 'fr', label: 'Français', flag: '🇫🇷' },
      { code: 'en', label: 'English', flag: '🇬🇧' }
    ];
  }
}
