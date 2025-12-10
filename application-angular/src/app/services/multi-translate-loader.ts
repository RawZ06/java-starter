import { HttpClient } from '@angular/common/http';
import { TranslateLoader } from '@ngx-translate/core';
import { Observable, forkJoin } from 'rxjs';
import { map } from 'rxjs/operators';

interface TranslationResource {
  prefix: string;
  suffix: string;
  key?: string;
}

export class MultiTranslateHttpLoader implements TranslateLoader {
  constructor(
    private http: HttpClient,
    private resources: TranslationResource[]
  ) {}

  getTranslation(lang: string): Observable<any> {
    const requests = this.resources.map((resource) => {
      // Si le suffix contient {lang}, on le remplace par la langue
      const suffix = resource.suffix.replace('{lang}', lang);
      const url = `${resource.prefix}${suffix}`;

      return this.http.get(url).pipe(
        map((response: any) => {
          // Si une clé est définie, on encapsule la réponse sous cette clé
          if (resource.key) {
            return { [resource.key]: response };
          }
          return response;
        })
      );
    });

    return forkJoin(requests).pipe(
      map((responses) => {
        return responses.reduce((acc, response) => {
          return { ...acc, ...response };
        }, {});
      })
    );
  }
}

export function multiTranslateLoaderFactory(http: HttpClient): TranslateLoader {
  return new MultiTranslateHttpLoader(http, [
    { prefix: '/i18n/', suffix: '{lang}/header.json', key: 'header' },
    { prefix: '/i18n/', suffix: '{lang}/footer.json', key: 'footer' },
    { prefix: '/i18n/', suffix: '{lang}/home.json', key: 'home' },
    { prefix: '/i18n/', suffix: '{lang}/admin.json', key: 'admin' },
    { prefix: '/i18n/', suffix: '{lang}/dashboard.json', key: 'dashboard' },
    { prefix: '/i18n/', suffix: '{lang}/login.json', key: 'login' },
    { prefix: '/i18n/', suffix: '{lang}/users.json', key: 'users' },
    { prefix: '/i18n/', suffix: '{lang}/profile.json', key: 'profile' },
    { prefix: '/i18n/', suffix: '{lang}/privacy.json', key: 'privacy' },
    { prefix: '/i18n/', suffix: '{lang}/terms.json', key: 'terms' },
    { prefix: '/i18n/', suffix: '{lang}/theme.json', key: 'theme' },
    { prefix: '/i18n/', suffix: '{lang}/jobs.json', key: 'jobs' }
  ]);
}
