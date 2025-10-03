# Starter Java + Angular (Maven multi-module)

Ce README est **prêt à être donné à Claude** pour générer un starter propre et reproductible.  
Objectif : un **monorepo Maven** avec 4 modules : Angular front, logique commune (JPA/Postgres), API REST (Spring Web + Security), et Batch (Spring Batch + planification).  
Le front Angular a une page **Login** et une page **Hello World** qui appelle un endpoint **protégé** côté API.

---

## 0) Périmètre & versions imposées

- **Java** : 21 (Temurin/Adoptium recommandé)
- **Maven** : 3.9.x
- **Spring Boot** : 3.3.3
- **Spring Security** : fourni par le BOM Spring Boot
- **Base** : PostgreSQL 16
- **JPA** : Hibernate via `spring-boot-starter-data-jpa`
- **Batch** : `spring-boot-starter-batch` + planification via `spring-boot-starter`
- **Front** : Angular 18 (CLI 18), Node.js 20 LTS, npm (ou pnpm si précisé)
- **Build Angular dans Maven** : `com.github.eirslett:frontend-maven-plugin` + `maven-resources-plugin`
- **Reload dev** : Spring DevTools, Angular `ng serve`, Lombok pour code boilerplate

> Si une version n’est pas disponible le jour de la génération, **choisir la plus proche compatible** (mais **Java 21** et **Boot ≥ 3.2** sont obligatoires).

---

## 1) Arborescence attendue

```
/ (racine du repo, packaging=pom)
├─ pom.xml
├─ application-angular/          (Angular 18 + un pom pour intégration build)
│  ├─ pom.xml
│  ├─ package.json
│  └─ (src Angular…)
├─ application-common/           (JPA/Hibernate + Postgres + Lombok)
│  └─ pom.xml
├─ application-web/              (REST + Security + DevTools)
│  └─ pom.xml
└─ application-batch/            (Spring Batch + Scheduler + DevTools)
   └─ pom.xml
```

**Contraintes de dépendances** :
- `application-web` **dépend** de `application-common`
- `application-batch` **dépend** de `application-common`
- `application-angular` est autonome côté Node mais son **build** est déclenché par Maven et le **résultat** est **copié** dans `application-web/src/main/resources/static` (servi par Spring Boot en prod).

---

## 2) Root `pom.xml` (packaging=pom)

- Déclare **modules**, **Java 21**, **Spring Boot parent**, **plugin management** (frontend-maven-plugin), versions centralisées.
- Ce fichier **ne produit pas d’artefact**.

```xml
<!-- /pom.xml -->
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                             https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>com.example</groupId>
  <artifactId>application-parent</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  <packaging>pom</packaging>
  <name>application-parent</name>

  <properties>
    <java.version>21</java.version>
    <spring-boot.version>3.3.3</spring-boot.version>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.release>${java.version}</maven.compiler.release>
    <node.version>v20.14.0</node.version>
    <npm.version>10.7.0</npm.version>
    <angular.dist.dir>dist/application-angular</angular.dist.dir>
  </properties>

  <modules>
    <module>application-common</module>
    <module>application-web</module>
    <module>application-batch</module>
    <module>application-angular</module>
  </modules>

  <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-dependencies</artifactId>
        <version>${spring-boot.version}</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>

  <build>
    <pluginManagement>
      <plugins>
        <!-- Standard Spring Boot plugin -->
        <plugin>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-maven-plugin</artifactId>
          <version>${spring-boot.version}</version>
        </plugin>

        <!-- Frontend plugin to run Node/Angular within Maven phases -->
        <plugin>
          <groupId>com.github.eirslett</groupId>
          <artifactId>frontend-maven-plugin</artifactId>
          <version>1.15.0</version>
        </plugin>
      </plugins>
    </pluginManagement>
  </build>

</project>
```

---

## 3) Module `application-common`

**Objectif** : partager l’entité JPA, repositories, services communs.  
**Dépendances** : JPA, Postgres, Lombok, Validation.  
**Pas d’exécutable** (pas de `spring-boot-maven-plugin` ici).

```xml
<!-- application-common/pom.xml -->
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                             https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <parent>
    <groupId>com.example</groupId>
    <artifactId>application-parent</artifactId>
    <version>1.0.0-SNAPSHOT</version>
  </parent>

  <artifactId>application-common</artifactId>
  <name>application-common</name>
  <packaging>jar</packaging>

  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <dependency>
      <groupId>org.postgresql</groupId>
      <artifactId>postgresql</artifactId>
      <scope>runtime</scope>
    </dependency>

    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- Lombok (compileOnly + annotationProcessor) -->
    <dependency>
      <groupId>org.projectlombok</groupId>
      <artifactId>lombok</artifactId>
      <optional>true</optional>
    </dependency>

    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-devtools</artifactId>
      <scope>runtime</scope>
      <optional>true</optional>
    </dependency>

    <dependency>
      <groupId>org.mapstruct</groupId>
      <artifactId>mapstruct</artifactId>
      <version>1.6.0.Final</version>
      <optional>true</optional>
    </dependency>
    <dependency>
      <groupId>org.mapstruct</groupId>
      <artifactId>mapstruct-processor</artifactId>
      <version>1.6.0.Final</version>
      <scope>provided</scope>
    </dependency>
  </dependencies>
</project>
```

> Pour l’instant **aucune entité** requise ; placer un `README.md` ou une classe vide pour éviter un module vide.

---

## 4) Module `application-web`

**Objectif** : API REST + Security, sert les fichiers Angular **en prod** via `src/main/resources/static`.  
**Dépend de** `application-common`.

```xml
<!-- application-web/pom.xml -->
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                             https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <parent>
    <groupId>com.example</groupId>
    <artifactId>application-parent</artifactId>
    <version>1.0.0-SNAPSHOT</version>
  </parent>

  <artifactId>application-web</artifactId>
  <name>application-web</name>
  <packaging>jar</packaging>

  <dependencies>
    <dependency>
      <groupId>com.example</groupId>
      <artifactId>application-common</artifactId>
      <version>${project.version}</version>
    </dependency>

    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- JPA + DB, au cas où web fait aussi des accès -->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
      <groupId>org.postgresql</groupId>
      <artifactId>postgresql</artifactId>
      <scope>runtime</scope>
    </dependency>

    <!-- Dev -->
    <dependency>
      <groupId>org.projectlombok</groupId>
      <artifactId>lombok</artifactId>
      <optional>true</optional>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-devtools</artifactId>
      <scope>runtime</scope>
      <optional>true</optional>
    </dependency>
  </dependencies>

  <build>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
      </plugin>

      <!-- Copie des artefacts Angular vers /static à l’emballage -->
      <plugin>
        <artifactId>maven-resources-plugin</artifactId>
        <version>3.3.1</version>
        <executions>
          <execution>
            <id>copy-angular-to-static</id>
            <phase>prepare-package</phase>
            <goals><goal>copy-resources</goal></goals>
            <configuration>
              <outputDirectory>${project.basedir}/src/main/resources/static</outputDirectory>
              <resources>
                <resource>
                  <directory>${project.parent.basedir}/application-angular/${angular.dist.dir}</directory>
                  <filtering>false</filtering>
                </resource>
              </resources>
            </configuration>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>

</project>
```

### 4.1 Configuration Security (JWT simple) + CORS

- Endpoint d’authentification : `POST /api/auth/login` → retourne un JWT.
- Endpoints protégés par `Authorization: Bearer <token>`.
- CORS autorisé pour `http://localhost:4200` en dev.

**Classes à générer** (exemples minimaux) :

```java
// application-web/src/main/java/.../security/SecurityConfig.java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthFilter jwtAuthFilter;

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .cors(Customizer.withDefaults())
      .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/api/auth/**").permitAll()
        .requestMatchers(HttpMethod.GET, "/", "/index.html", "/assets/**", "/favicon.ico").permitAll()
        .anyRequest().authenticated()
      )
      .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("http://localhost:4200"));
    config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
    config.setAllowedHeaders(List.of("Authorization","Content-Type"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
```

```java
// application-web/src/main/java/.../security/JwtAuthFilter.java
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String auth = request.getHeader("Authorization");
    if (auth != null && auth.startsWith("Bearer ")) {
      String token = auth.substring(7);
      String username = jwtService.extractUsername(token);
      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails user = User.withUsername(username).password("N/A").roles("USER").build();
        UsernamePasswordAuthenticationToken a =
            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(a);
      }
    }
    filterChain.doFilter(request, response);
  }
}
```

```java
// application-web/src/main/java/.../security/JwtService.java
@Service
public class JwtService {
  private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // En prod, charger via config

  public String generateToken(String username) {
    return Jwts.builder().setSubject(username)
      .setIssuedAt(new Date())
      .setExpiration(Date.from(Instant.now().plus(Duration.ofHours(8))))
      .signWith(key).compact();
  }

  public String extractUsername(String token) {
    try {
      return Jwts.parserBuilder().setSigningKey(key).build()
        .parseClaimsJws(token).getBody().getSubject();
    } catch (JwtException e) {
      return null;
    }
  }
}
```

```java
// application-web/src/main/java/.../auth/AuthController.java
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final JwtService jwt;

  @PostMapping("/login")
  public Map<String, String> login(@RequestBody Map<String, String> payload) {
    // Demo: accepte n'importe quel user/pass non vides
    String username = payload.getOrDefault("username", "");
    String password = payload.getOrDefault("password", "");
    if (username.isBlank() || password.isBlank()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad credentials");
    }
    return Map.of("token", jwt.generateToken(username));
  }
}
```

```java
// application-web/src/main/java/.../api/HelloController.java
@RestController
@RequestMapping("/api")
public class HelloController {
  @GetMapping("/hello")
  public Map<String, String> hello(Authentication auth) {
    String user = auth != null ? auth.getName() : "anonymous";
    return Map.of("message", "hello-world", "user", user);
  }
}
```

**Dépendances JWT** à ajouter dans `application-web/pom.xml` :

```xml
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-api</artifactId>
  <version>0.12.6</version>
</dependency>
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-impl</artifactId>
  <version>0.12.6</version>
  <scope>runtime</scope>
</dependency>
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-jackson</artifactId>
  <version>0.12.6</version>
  <scope>runtime</scope>
</dependency>
```

### 4.2 Config `application.yml` (dev/prod)

```yaml
# application-web/src/main/resources/application.yml
spring:
  application:
    name: application-web
  datasource:
    url: jdbc:postgresql://localhost:5432/appdb
    username: app
    password: app
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate.format_sql: true
      hibernate.jdbc.time_zone: UTC
server:
  port: 8080
logging:
  level:
    org.springframework.security: INFO
```

---

## 5) Module `application-batch`

**Objectif** : jobs Spring Batch + planification via `@Scheduled`.  
**Dépend de** `application-common`.

```xml
<!-- application-batch/pom.xml -->
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                             https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <parent>
    <groupId>com.example</groupId>
    <artifactId>application-parent</artifactId>
    <version>1.0.0-SNAPSHOT</version>
  </parent>

  <artifactId>application-batch</artifactId>
  <name>application-batch</name>
  <packaging>jar</packaging>

  <dependencies>
    <dependency>
      <groupId>com.example</groupId>
      <artifactId>application-common</artifactId>
      <version>${project.version}</version>
    </dependency>

    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-batch</artifactId>
    </dependency>

    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter</artifactId>
    </dependency>

    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <dependency>
      <groupId>org.postgresql</groupId>
      <artifactId>postgresql</artifactId>
      <scope>runtime</scope>
    </dependency>

    <!-- Dev -->
    <dependency>
      <groupId>org.projectlombok</groupId>
      <artifactId>lombok</artifactId>
      <optional>true</optional>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-devtools</artifactId>
      <scope>runtime</scope>
      <optional>true</optional>
    </dependency>
  </dependencies>

  <build>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
      </plugin>
    </plugins>
  </build>
</project>
```

**Configuration de base** :

```java
// application-batch/src/main/java/.../BatchApplication.java
@SpringBootApplication
@EnableBatchProcessing
@EnableScheduling
public class BatchApplication {
  public static void main(String[] args) {
    SpringApplication.run(BatchApplication.class, args);
  }
}
```

```java
// application-batch/src/main/java/.../config/SampleJobConfig.java
@Configuration
@RequiredArgsConstructor
public class SampleJobConfig {
  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;

  @Bean
  public Job sampleJob() {
    return new JobBuilder("sampleJob", jobRepository)
      .start(sampleStep())
      .build();
  }

  @Bean
  public Step sampleStep() {
    return new StepBuilder("sampleStep", jobRepository)
      .tasklet((contribution, chunkContext) -> {
        System.out.println("Hello from Spring Batch");
        return RepeatStatus.FINISHED;
      }, transactionManager)
      .build();
  }

  @Scheduled(cron = "0 0/5 * * * *") // toutes les 5 min
  public void launch() throws Exception {
    JobParameters params = new JobParametersBuilder()
      .addLong("time", System.currentTimeMillis())
      .toJobParameters();
    // Autowire JobLauncher + Job bean
  }
}
```

`application.yml` similaire à `web`, avec `spring.batch.jdbc.initialize-schema=always` en dev :

```yaml
spring:
  application:
    name: application-batch
  datasource:
    url: jdbc:postgresql://localhost:5432/appdb
    username: app
    password: app
  jpa:
    hibernate:
      ddl-auto: update
  batch:
    jdbc:
      initialize-schema: always
```

---

## 6) Module `application-angular`

**Objectif** : Angular 18 avec page **Login** et **Hello**.  
**Build Maven** : installe Node/npm localement, `npm ci`, `ng build --configuration production`, copie vers `application-web/static`.

```xml
<!-- application-angular/pom.xml -->
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                             https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <parent>
    <groupId>com.example</groupId>
    <artifactId>application-parent</artifactId>
    <version>1.0.0-SNAPSHOT</version>
  </parent>

  <artifactId>application-angular</artifactId>
  <name>application-angular</name>
  <packaging>pom</packaging>

  <build>
    <plugins>
      <plugin>
        <groupId>com.github.eirslett</groupId>
        <artifactId>frontend-maven-plugin</artifactId>
        <executions>
          <execution>
            <id>install-node-and-npm</id>
            <goals><goal>install-node-and-npm</goal></goals>
            <configuration>
              <nodeVersion>${node.version}</nodeVersion>
              <npmVersion>${npm.version}</npmVersion>
            </configuration>
          </execution>
          <execution>
            <id>npm-ci</id>
            <goals><goal>npm</goal></goals>
            <configuration>
              <arguments>ci</arguments>
            </configuration>
          </execution>
          <execution>
            <id>ng-build</id>
            <goals><goal>npm</goal></goals>
            <phase>prepare-package</phase>
            <configuration>
              <arguments>run build</arguments>
            </configuration>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
</project>
```

**package.json minimal** :

```json
{
  "name": "application-angular",
  "version": "1.0.0",
  "private": true,
  "scripts": {
    "start": "ng serve",
    "build": "ng build --configuration production"
  },
  "dependencies": {
    "@angular/animations": "^18.0.0",
    "@angular/common": "^18.0.0",
    "@angular/compiler": "^18.0.0",
    "@angular/core": "^18.0.0",
    "@angular/forms": "^18.0.0",
    "@angular/platform-browser": "^18.0.0",
    "@angular/platform-browser-dynamic": "^18.0.0",
    "@angular/router": "^18.0.0",
    "rxjs": "^7.8.1",
    "tslib": "^2.6.2",
    "zone.js": "^0.14.3"
  },
  "devDependencies": {
    "@angular/cli": "^18.0.0",
    "@angular/compiler-cli": "^18.0.0",
    "typescript": "~5.5.0"
  }
}
```

**Service d’auth Angular** (JWT) + Interceptor :

```ts
// src/app/services/auth.service.ts
@Injectable({ providedIn: 'root' })
export class AuthService {
  private tokenKey = 'app_token';

  login(username: string, password: string) {
    return this.http.post<{token: string}>('/api/auth/login', { username, password })
      .pipe(tap(res => localStorage.setItem(this.tokenKey, res.token)));
  }
  logout() { localStorage.removeItem(this.tokenKey); }
  get token(): string | null { return localStorage.getItem(this.tokenKey); }

  constructor(private http: HttpClient) {}
}
```

```ts
// src/app/services/auth.interceptor.ts
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private auth: AuthService) {}
  intercept(req: HttpRequest<any>, next: HttpHandler) {
    const token = this.auth.token;
    if (token) {
      req = req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
    }
    return next.handle(req);
  }
}

export const authInterceptorProvider = {
  provide: HTTP_INTERCEPTORS,
  useClass: AuthInterceptor,
  multi: true
};
```

**Routes + Pages** :

```ts
// src/app/app.routes.ts
export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'hello', component: HelloComponent },
  { path: '', pathMatch: 'full', redirectTo: 'hello' }
];
```

```ts
// src/app/login/login.component.ts
@Component({
  selector: 'app-login',
  template: `
  <h1>Login</h1>
  <form (ngSubmit)="submit()">
    <input [(ngModel)]="username" name="username" placeholder="Username" required />
    <input [(ngModel)]="password" name="password" type="password" placeholder="Password" required />
    <button>Se connecter</button>
  </form>
  `
})
export class LoginComponent {
  username = ''; password = '';
  constructor(private auth: AuthService, private router: Router) {}
  submit() {
    this.auth.login(this.username, this.password).subscribe({
      next: () => this.router.navigate(['/hello'])
    });
  }
}
```

```ts
// src/app/hello/hello.component.ts
@Component({
  selector: 'app-hello',
  template: `
  <h1>Hello</h1>
  <button (click)="call()">Appeler l'API</button>
  <pre>{{ data | json }}</pre>
  `
})
export class HelloComponent {
  data: any;
  constructor(private http: HttpClient, private router: Router, private auth: AuthService) {}
  call() {
    if (!this.auth.token) { this.router.navigate(['/login']); return; }
    this.http.get('/api/hello').subscribe(res => this.data = res);
  }
}
```

**Proxy dev Angular** pour éviter CORS pendant `ng serve` (optionnel si CORS est déjà ouvert) :

`proxy.conf.json` :
```json
{ "/api": { "target": "http://localhost:8080", "secure": false, "changeOrigin": true } }
```
Puis `ng serve --proxy-config proxy.conf.json`.

---

## 7) Devtools & Reload

- **Backend** : inclure `spring-boot-devtools` (scope runtime, optional). Lancer via IDE avec **classpath reload** activé.
- **Frontend** : `ng serve` fournit le HMR.
- **Batch** : idem web (devtools) — attention aux jobs relancés à chaud.

---

## 8) Config Postgres (Docker Compose dev)

Fichier `docker-compose.yml` à la racine :

```yaml
version: "3.9"
services:
  db:
    image: postgres:16
    environment:
      POSTGRES_DB: appdb
      POSTGRES_USER: app
      POSTGRES_PASSWORD: app
    ports:
      - "5432:5432"
    volumes:
      - dbdata:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U app -d appdb"]
      interval: 5s
      timeout: 5s
      retries: 5
volumes:
  dbdata: {}
```

---

## 9) Commandes de build & exécution

### Dev (2 terminaux)
1. **DB** : `docker compose up -d`
2. **Backend web** : depuis `/application-web` → `mvn spring-boot:run`
3. **Frontend** : depuis `/application-angular` → `npm ci` puis `npm start`
   - Accès : `http://localhost:4200` (Angular), proxie `/api` vers `http://localhost:8080`

### Batch
- `cd application-batch && mvn spring-boot:run`

### Build full monorepo (prod)
- À la racine : `mvn -U -T 1C clean package`
  - Maven va : installer Node → build Angular → copier le `dist` dans `application-web/static` → packager les jars.

### Lancement prod minimal
- `java -jar application-web/target/application-web-1.0.0-SNAPSHOT.jar`
- Accéder à `http://localhost:8080` (l’index Angular est servi depuis `/static`)

---

## 10) Qualité & options

- **Tests** : ajouter `spring-boot-starter-test` + profils `test`.
- **Migrations** : optionnel `flyway-core` pour versionner le schéma.
- **OpenAPI** : optionnel `org.springdoc:springdoc-openapi-starter-webmvc-ui`.
- **CI** : GitHub Actions – cache Maven + Node, lancer `mvn -B -DskipTests=false verify`.

---

## 11) Checklist pour Claude (ne pas se tromper)

1. Créer la **racine** `pom.xml` avec packaging `pom`, modules listés dans l’ordre: common, web, batch, angular.
2. **Java 21**, **Boot 3.3.3**, **Node 20** via `frontend-maven-plugin`.
3. `application-common` : deps JPA + Postgres + Lombok + DevTools. **Aucun main**.
4. `application-web` : deps Web + Security + (JPA/PG si besoin) + DevTools + **jjwt**.  
   - Exposer `POST /api/auth/login` qui renvoie un **JWT**, configurer **JWT filter**.  
   - Exposer `GET /api/hello` **protégé**.  
   - **CORS** autorise `http://localhost:4200`.  
   - `maven-resources-plugin` copie le **dist Angular** vers `/static` au `prepare-package`.
5. `application-batch` : deps Batch + JPA + PG + DevTools.  
   - Un **Job** et un **Step** de démonstration + **@Scheduled** pour le lancer périodiquement.
6. `application-angular` : Angular 18, `package.json` avec scripts `start` et `build`.  
   - `frontend-maven-plugin` : `install-node-and-npm` → `npm ci` → `npm run build` au `prepare-package`.  
   - Pages **Login** et **Hello** + **AuthInterceptor**.  
   - Optionnel : `proxy.conf.json` en dev.
7. Ajouter `docker-compose.yml` Postgres (user/pass/db = app/app/appdb).  
8. Fichiers `application.yml` pour `web` et `batch` (voir exemples).  
9. Vérifier que `mvn clean package` à la racine **build tout** et que le jar web **sert** l’Angular en prod.  
10. Fournir **scripts README** pour run dev/prod + commandes.

---

## 12) Licence & auteurs

- Licence : MIT (par défaut, modifiable).
- Copyright : © 2025.

Bon build !
