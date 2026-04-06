# ═══════════════════════════════════════════
# Dockerfile — gestionactivosapi (Backend API)
# Multi-stage build: Maven build + JRE runtime
# ═══════════════════════════════════════════

# --- Stage 1: Build ---
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

COPY src src
RUN ./mvnw package -DskipTests -B

# --- Stage 2: Runtime ---
FROM eclipse-temurin:17-jre
WORKDIR /app

RUN groupadd --system appuser && useradd --system --gid appuser appuser

COPY --from=build /app/target/*.jar app.jar

RUN mkdir -p /app/data/mantenimientos /app/data/actas /app/templates \
    && chown -R appuser:appuser /app

USER appuser

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
