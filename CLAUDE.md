# CLAUDE.md - Projet Starter Java + Angular généré

## 📋 Résumé de la génération

Ce projet a été généré avec succès selon les spécifications du README.md.

### Structure créée

```
starter-java/
├── pom.xml                         (Parent Maven)
├── docker-compose.yml              (PostgreSQL 16)
├── .gitignore                      (Complet Maven + Node + IDE)
├── application-common/             (Module partagé)
│   ├── pom.xml
│   └── src/main/java/fr/rawz06/starter/common/
├── application-web/                (API REST + Security)
│   ├── pom.xml
│   └── src/main/java/fr/rawz06/starter/web/
│       ├── WebApplication.java
│       ├── security/
│       │   ├── SecurityConfig.java
│       │   ├── JwtService.java
│       │   └── JwtAuthFilter.java
│       ├── auth/
│       │   └── AuthController.java (POST /api/auth/login)
│       └── api/
│           └── HelloController.java (GET /api/hello)
├── application-batch/              (Spring Batch + Scheduler)
│   ├── pom.xml
│   └── src/main/java/fr/rawz06/starter/batch/
│       ├── BatchApplication.java
│       └── config/
│           └── SampleJobConfig.java (Job planifié toutes les 5 min)
└── application-angular/            (Angular 20)
    ├── pom.xml (frontend-maven-plugin)
    ├── package.json
    ├── proxy.conf.json
    └── src/app/
        ├── services/
        │   └── auth.service.ts (avec signals)
        ├── interceptors/
        │   └── auth.interceptor.ts (functional interceptor)
        ├── login/
        │   ├── login.component.ts (avec signals)
        │   ├── login.component.html
        │   └── login.component.css
        ├── hello/
        │   ├── hello.component.ts (avec signals)
        │   ├── hello.component.html
        │   └── hello.component.css
        ├── app.routes.ts (routing standalone)
        └── app.config.ts (HTTP client + interceptor)
```

## 🎯 Caractéristiques implémentées

### Backend (Java 21 + Spring Boot 3.3.3)

#### Module `application-common`
- ✅ JPA/Hibernate configuré
- ✅ PostgreSQL driver
- ✅ Lombok inclus
- ✅ MapStruct inclus (prêt à l'emploi)
- ✅ DevTools activé

#### Module `application-web`
- ✅ Spring Security configuré avec JWT (jjwt 0.12.6)
- ✅ Authentification stateless
- ✅ CORS configuré pour `http://localhost:4200`
- ✅ Endpoints publics: `/api/auth/login`
- ✅ Endpoints protégés: `/api/hello`
- ✅ Fichiers statiques Angular servis depuis `/static`
- ✅ DevTools activé
- ✅ JWT avec expiration 8h

#### Module `application-batch`
- ✅ Spring Batch configuré
- ✅ Job de démonstration "Hello World"
- ✅ Scheduler activé (cron: toutes les 5 minutes)
- ✅ Initialisation auto du schéma batch
- ✅ DevTools activé

### Frontend (Angular 20)

#### Moderne & Signals
- ✅ **Angular 20.3.0** (dernière version)
- ✅ **Signals** utilisés partout (username, password, data, error, token)
- ✅ **Standalone components** (pas de modules)
- ✅ **Functional interceptor** (HttpInterceptorFn)
- ✅ **Control flow syntax** moderne (@if, @for)
- ✅ Routing standalone

#### Fonctionnalités
- ✅ Service AuthService avec signal pour le token
- ✅ Interceptor JWT fonctionnel
- ✅ Page Login avec formulaire
- ✅ Page Hello avec appel API protégé
- ✅ Gestion d'erreurs
- ✅ Proxy dev configuré (`proxy.conf.json`)
- ✅ Build production via Maven

## 🔧 Configuration

### Base de données (PostgreSQL)
- Database: `appdb`
- User: `app`
- Password: `app`
- Port: `5432`

### Ports
- Backend Web: `8080`
- Frontend Dev: `4200` (via `npm start`)
- PostgreSQL: `5432`

## 🚀 Commandes de démarrage

### Mode développement (2-3 terminaux)

#### 1. Base de données
```bash
docker compose up -d
```

#### 2. Backend Web
```bash
cd application-web
mvn spring-boot:run
```

#### 3. Frontend Angular (dev avec HMR)
```bash
cd application-angular
npm install  # première fois uniquement
npm start
```
Accès: http://localhost:4200

#### 4. Backend Batch (optionnel)
```bash
cd application-batch
mvn spring-boot:run
```

### Build production complet

À la racine du projet:
```bash
mvn clean package
```

Cette commande va:
1. Installer Node.js localement (via frontend-maven-plugin)
2. Faire `npm ci` dans application-angular
3. Builder Angular en mode production
4. Copier le dist Angular dans `application-web/src/main/resources/static`
5. Packager tous les JARs

### Lancement production

```bash
java -jar application-web/target/application-web-1.0.0-SNAPSHOT.jar
```

Accès: http://localhost:8080 (sert l'Angular depuis `/static`)

## 🔑 Test de l'authentification

1. Aller sur http://localhost:4200
2. Page Login s'affiche
3. Entrer n'importe quel username/password non vide (c'est une démo)
4. Cliquer sur "Se connecter"
5. Redirection vers `/hello`
6. Cliquer sur "Appeler l'API"
7. Voir la réponse JSON avec `{"message": "hello-world", "user": "votreUsername"}`

## 📦 Packages utilisés

### Backend
- **groupId**: `fr.rawz06.starter`
- **Modules**:
  - `application-parent` (pom parent)
  - `application-common` (commun)
  - `application-web` (web)
  - `application-batch` (batch)
  - `application-angular` (angular)

### Versions principales
- Java: **21**
- Spring Boot: **3.3.3**
- Angular: **20.3.0**
- Node: **20.14.0**
- PostgreSQL: **16**
- JJWT: **0.12.6**
- MapStruct: **1.6.0.Final**

## 🎨 Particularités Angular 20

L'implémentation utilise toutes les fonctionnalités modernes d'Angular:

### Signals partout
```typescript
username = signal('');
password = signal('');
token = this.tokenSignal.asReadonly();
data = signal<any>(null);
```

### Functional Interceptor
```typescript
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  // ...
};
```

### Control Flow moderne
```html
@if (error()) {
  <div class="error-message">{{ error() }}</div>
}
```

### Standalone components
```typescript
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  // ...
})
```

## 🔍 Points d'attention

### JWT Secret
La clé JWT est générée aléatoirement au démarrage dans `JwtService.java`. En production, il faut:
1. Générer une clé stable
2. La stocker dans `application.yml`
3. L'injecter dans `JwtService`

### Authentification
L'authentification actuelle accepte n'importe quel username/password non vide. C'est une démo. Pour production:
1. Créer une entité User dans `application-common`
2. Ajouter un repository
3. Implémenter un UserDetailsService
4. Vérifier les credentials dans `AuthController`

### Batch Job
Le job s'exécute toutes les 5 minutes. Pour modifier:
- Changer le cron dans `SampleJobConfig.java` ligne 51
- Pour désactiver: commenter l'annotation `@Scheduled`

## ✅ Checklist de vérification

- ✅ Structure multi-module Maven
- ✅ Java 21
- ✅ Spring Boot 3.3.3
- ✅ Angular 20 avec signals
- ✅ JWT fonctionnel
- ✅ CORS configuré
- ✅ Spring Batch avec scheduler
- ✅ PostgreSQL 16 via Docker
- ✅ DevTools partout
- ✅ Build Maven complet
- ✅ Proxy Angular configuré
- ✅ .gitignore complet
- ✅ Frontend-maven-plugin configuré

## 🎉 Conclusion

Le projet est **100% fonctionnel** et prêt à être utilisé comme starter. Tous les modules communiquent correctement et les technologies modernes sont utilisées (signals, functional interceptor, standalone components).

Pour démarrer rapidement:
```bash
docker compose up -d
cd application-angular && npm install && npm start
# Dans un autre terminal:
cd application-web && mvn spring-boot:run
```

Puis aller sur http://localhost:4200 et tester le login/dashboard !

---

## 🆕 Mise à jour : Architecture Front Public / Backoffice Admin

### Changements apportés

Le starter a été restructuré pour séparer clairement les pages publiques et le backoffice administrateur, inspiré de laravel-boilerplate.

### Backend - Nouvelle structure des controllers

```
application-web/
└── src/main/java/fr/rawz06/starter/web/
    ├── auth/
    │   └── AuthController.java (POST /api/public/auth/login)
    └── controller/
        ├── pub/
        │   └── PublicController.java (GET /api/public/info)
        └── admin/
            └── AdminDashboardController.java
                ├── GET /api/admin/dashboard
                └── GET /api/admin/profile
```

**Configuration de sécurité mise à jour** (`SecurityConfig.java`):
- `/api/public/**` : Routes publiques (non protégées)
- `/api/admin/**` : Routes administrateur (protégées par JWT)

### Frontend - Nouvelle architecture

#### 1. Page d'accueil publique (`/`)
- **Composant**: `Home`
- **Features**:
  - Header avec logo "Java Starter Kit"
  - Bouton "Login" en haut à droite (si non connecté)
  - Menu dropdown utilisateur (si connecté) avec :
    - Accéder au backoffice
    - Paramètres du compte
    - Se déconnecter
  - Hero section avec titre "Java Starter Kit" et "Powered by Angular"

#### 2. Layout Admin réutilisable
- **Composant**: `Layout` (inspiré de laravel-boilerplate)
- **Features**:
  - Sidebar collapsible avec navigation :
    - Dashboard
    - Users
    - Settings
    - Back to site
  - Header admin avec :
    - Bouton toggle sidebar
    - Menu dropdown utilisateur
  - Zone de contenu principale avec `<router-outlet>`
- **Style**: Sidebar gris foncé (#1f2937), design moderne et épuré

#### 3. Page Dashboard (`/dashboard`)
- **Composant**: `Dashboard`
- **Features**:
  - Utilise le layout admin
  - Affiche des statistiques (Total Users, Active Users, Total Posts)
  - Cards avec icônes colorées
  - Appel API sécurisé vers `/api/admin/dashboard`
  - Message de bienvenue personnalisé

#### 4. Routing et Guards
```typescript
routes: Routes = [
  { path: '', component: Home },                    // Public home
  { path: 'login', component: LoginComponent },     // Public login
  {
    path: 'dashboard',
    component: Layout,                              // Admin layout
    canActivate: [authGuard],                       // Protected
    children: [
      { path: '', component: Dashboard }
    ]
  },
  {
    path: 'admin',
    component: Layout,                              // Admin layout
    canActivate: [authGuard],                       // Protected
    children: [
      { path: 'users', component: Dashboard },
      { path: 'settings', component: Dashboard }
    ]
  }
]
```

**Guard fonctionnel** (`authGuard`):
- Vérifie la présence du token JWT
- Redirige vers `/login` si non authentifié
- Conserve l'URL de retour dans `returnUrl`

### Flux utilisateur

1. **Visiteur non connecté**:
   - Arrive sur `/` → Voit la page d'accueil avec bouton "Login"
   - Clique sur "Login" → Page de connexion
   - Se connecte → Redirigé vers `/dashboard`

2. **Utilisateur connecté**:
   - Arrive sur `/` → Voit la page d'accueil avec menu utilisateur
   - Menu utilisateur propose :
     - "Accéder au backoffice" → `/dashboard`
     - "Paramètres du compte" → `/profile`
     - "Se déconnecter" → Déconnexion et retour à `/`
   - Accède au dashboard → Layout admin avec sidebar
   - Navigation dans sidebar → Users, Settings, etc.
   - "Back to site" → Retour à `/`

### Composants créés

```
src/app/
├── home/                           # Page d'accueil publique
│   ├── home.ts
│   ├── home.html
│   └── home.css
├── shared/
│   └── header/                     # Header public avec menu
│       ├── header.ts
│       ├── header.html
│       └── header.css
├── admin/
│   ├── layout/                     # Layout admin réutilisable
│   │   ├── layout.ts
│   │   ├── layout.html
│   │   └── layout.css
│   └── dashboard/                  # Page dashboard
│       ├── dashboard.ts
│       ├── dashboard.html
│       └── dashboard.css
└── guards/
    └── auth-guard.ts              # Guard fonctionnel
```

### Styles et design

- **Front public**: Design moderne avec gradient violet (#667eea → #764ba2)
- **Admin**: Sidebar gris foncé (#1f2937), fond clair (#f9fafb)
- **Icons**: Lucide icons (SVG inline)
- **Responsive**: Sidebar collapsible sur mobile
- **Animations**: Transitions douces (0.2s-0.3s)

### Points techniques

- **Signals partout**: `sidebarOpen`, `dropdownOpen`, `stats`, `loading`
- **Routing enfants**: Layout admin avec children routes
- **Guard fonctionnel**: `CanActivateFn` avec inject()
- **AuthService mis à jour**: Endpoint `/api/public/auth/login`, redirect après logout
- **Dropdown management**: Click handlers pour ouvrir/fermer les menus

### Prochaines étapes suggérées

1. Créer les composants manquants :
   - `UsersComponent` pour `/admin/users`
   - `SettingsComponent` pour `/admin/settings`
   - `ProfileComponent` pour `/profile`
2. Ajouter des endpoints backend correspondants
3. Implémenter la vraie gestion des utilisateurs (CRUD)
4. Ajouter des breadcrumbs dans le layout admin
5. Implémenter les filtres et la pagination

### Test rapide

```bash
# 1. Démarrer PostgreSQL
docker compose up -d

# 2. Démarrer le backend
cd application-web && mvn spring-boot:run

# 3. Démarrer le frontend (dans un autre terminal)
cd application-angular && npm start

# 4. Tester
# - Aller sur http://localhost:4200 → Page d'accueil
# - Cliquer sur "Login"
# - Se connecter avec n'importe quel username/password
# - Voir le dashboard avec stats
# - Tester la navigation dans la sidebar
# - Tester le menu utilisateur
```
