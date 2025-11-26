# ===== BUILD BACKEND =====
FROM maven:3.9-eclipse-temurin-21 AS build-backend

WORKDIR /app

# Copy entire project (needed for Maven multi-module resolution)
COPY . .

# Build only backend modules
RUN mvn clean package -DskipTests -B -pl application-common,application-web,application-batch -am

# ===== BUILD FRONTEND =====
FROM maven:3.9-eclipse-temurin-21 AS build-frontend

WORKDIR /app

# Copy entire project (needed for Maven multi-module resolution)
COPY . .

# Build Angular files and application-front Spring Boot JAR
RUN mvn clean package -DskipTests -B -pl application-angular,application-front -am

# ===== WEB (BACKEND API) =====
FROM eclipse-temurin:21-jre-alpine AS web

WORKDIR /app

COPY --from=build-backend /app/application-web/target/*.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]

# ===== FRONT (ANGULAR served by Spring Boot/Tomcat) =====
FROM eclipse-temurin:21-jre-alpine AS front

WORKDIR /app

# Copy the Front Spring Boot JAR (contains Angular static files inside)
COPY --from=build-frontend /app/application-front/target/*.jar app.jar

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]

# ===== BATCH =====
FROM eclipse-temurin:21-jre-alpine AS batch

WORKDIR /app

COPY --from=build-backend /app/application-batch/target/*.jar app.jar

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD pgrep -f "java.*app.jar" || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
