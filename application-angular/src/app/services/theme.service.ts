import { Injectable, signal, effect } from '@angular/core';

export type ThemeMode = 'light' | 'dark' | 'system';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private readonly THEME_KEY = 'theme-preference';

  // Signal for current theme mode
  private themeModeSignal = signal<ThemeMode>(this.getStoredTheme());

  // Readonly signal for components to use
  themeMode = this.themeModeSignal.asReadonly();

  // Signal to track if currently in dark mode (computed from system or user preference)
  isDark = signal<boolean>(false);

  constructor() {
    // Initialize theme on service creation
    this.applyTheme(this.themeModeSignal());

    // Listen to system theme changes
    if (window.matchMedia) {
      window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', (e) => {
        if (this.themeModeSignal() === 'system') {
          this.applyTheme('system');
        }
      });
    }

    // Effect to apply theme when it changes
    effect(() => {
      const mode = this.themeModeSignal();
      this.applyTheme(mode);
      localStorage.setItem(this.THEME_KEY, mode);
    });
  }

  /**
   * Set the theme mode
   */
  setTheme(mode: ThemeMode): void {
    this.themeModeSignal.set(mode);
  }

  /**
   * Toggle between light and dark (ignoring system)
   */
  toggleTheme(): void {
    const current = this.themeModeSignal();
    if (current === 'light') {
      this.setTheme('dark');
    } else {
      this.setTheme('light');
    }
  }

  /**
   * Get stored theme from localStorage or default to system
   */
  private getStoredTheme(): ThemeMode {
    const stored = localStorage.getItem(this.THEME_KEY);
    if (stored === 'light' || stored === 'dark' || stored === 'system') {
      return stored;
    }
    return 'system';
  }

  /**
   * Apply theme to document
   */
  private applyTheme(mode: ThemeMode): void {
    const htmlElement = document.documentElement;
    let shouldBeDark = false;

    if (mode === 'system') {
      // Use system preference
      shouldBeDark = window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches;
    } else {
      shouldBeDark = mode === 'dark';
    }

    // Update class on html element for both PrimeNG and Tailwind
    if (shouldBeDark) {
      htmlElement.classList.add('dark');
    } else {
      htmlElement.classList.remove('dark');
    }

    // Update isDark signal
    this.isDark.set(shouldBeDark);
  }

  /**
   * Get theme icon for display
   */
  getThemeIcon(mode: ThemeMode): string {
    switch (mode) {
      case 'light':
        return 'pi pi-sun';
      case 'dark':
        return 'pi pi-moon';
      case 'system':
        return 'pi pi-desktop';
      default:
        return 'pi pi-desktop';
    }
  }

  /**
   * Get available theme modes
   */
  getAvailableThemes(): Array<{ mode: ThemeMode; label: string; icon: string }> {
    return [
      { mode: 'light', label: 'theme.light', icon: 'pi pi-sun' },
      { mode: 'dark', label: 'theme.dark', icon: 'pi pi-moon' },
      { mode: 'system', label: 'theme.system', icon: 'pi pi-desktop' }
    ];
  }
}
