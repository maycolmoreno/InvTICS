# gestionactivosapi — Spring Boot REST API

## Arquitectura

Arquitectura **hexagonal (Clean Architecture)** con 4 capas:

```
dominio/           → Entidades, puertos (interfaces), DTOs, excepciones
aplicacion/        → Casos de uso (implementan lógica de negocio)
infraestructura/   → JPA, MapStruct mappers, seguridad, configuración
presentacion/      → Controladores REST, DTOs de request/response
```

**Regla de dependencia**: dominio ← aplicacion ← infraestructura/presentacion. El dominio NO importa clases de infraestructura ni de Spring.

### Convenciones de nombres

| Capa | Patrón | Ejemplo |
|------|--------|---------|
| Puertos repositorio | `{Entidad}RepositorioPuerto` | `EquipoRepositorioPuerto` |
| Adaptadores | `{Entidad}RepositorioImpl` | `EquiposRepositorioImpl` |
| Casos de uso | `{Entidad}UseCaseImpl` | `EquiposUseCaseImpl` |
| Entidades JPA | `{Entidad}Jpa` | `EquiposJpa` |
| Controladores | `{Entidad}Controlador` | `EquiposControlador` |
| Mappers | `{Entidad}Mapper` | `EquiposMapper` (MapStruct) |

### Patrón de implementación

- Los **use cases** inyectan **puertos** (interfaces del dominio), nunca implementaciones concretas
- Los **adaptadores** en `infraestructura/persistencia/repositorios/` implementan puertos usando JpaRepository
- **MapStruct** mapea entre entidades de dominio y entidades JPA
- Idioma del código: **español** (nombres de clases, métodos, variables)

## Build y Test

```powershell
# Compilar
./mvnw clean package -DskipTests

# Tests unitarios
./mvnw test

# Ejecutar en dev
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Script auxiliar
./tools/run-api-java17.ps1
```

## Stack técnico

- **Java 17** (enforced por maven-enforcer-plugin `[17,18)`)
- **Spring Boot 4.0.2** (webmvc, data-jpa, security, validation, cache, mail)
- **PostgreSQL** con **Flyway** (migraciones en `src/main/resources/db/migration/`)
- **MapStruct 1.6.3** para mapeo de objetos
- **Lombok** para boilerplate
- **OpenPDF 1.3.39** y **Apache POI 5.2.5** para reportes PDF/Excel
- **JUnit 5 + Mockito** para tests

## Seguridad

- Autenticación **HTTP Basic** con `BCryptPasswordEncoder`
- Autorización basada en roles: `ADMINISTRADOR`, `TECNICO`, `AUDITOR`
- `SecurityConfig` define permisos por endpoint (HttpMethod + path)
- `@PreAuthorize("hasRole('...')")` en controladores específicos
- Un usuario tiene **un solo rol** (relación ManyToOne)

## Base de datos

- **Flyway deshabilitado en dev** (`spring.flyway.enabled=false`), `ddl-auto=update`
- **Flyway habilitado en prod**, `ddl-auto=validate`
- BD por defecto: `cresio3` en `localhost:5432`
- Variables de entorno: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`

## Configuración

- Archivos de carga: max 5MB por archivo, 30MB total
- Almacenamiento de mantenimientos: `./data/mantenimientos/`
- Plantillas de actas: `./templates/acta-*.docx`
- Email SMTP: `MAIL_HOST`, `MAIL_USERNAME`, `MAIL_PASSWORD`

## Convenciones de código

- Endpoints REST bajo `/api/{recurso}`
- Paginación con `Pagina<T>` (modelo de dominio propio)
- Respuestas de error consistentes con excepciones de dominio
- Tests usan `@ExtendWith(MockitoExtension.class)`, `@Nested`, `@DisplayName`
