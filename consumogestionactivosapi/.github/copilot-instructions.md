# consumogestionactivosapi — Spring Boot MVC (Frontend Web)

## Arquitectura

Aplicación **MVC tradicional** con Thymeleaf que consume la API REST del backend (`gestionactivosapi`).

```
controlador/       → 23 controladores MVC (renderizan vistas)
service/           → Servicios que llaman al backend via RestClient
modelo/dto/        → request/ y response/ DTOs para la API
config/            → SecurityConfig, WebClientConfig, WebMvcConfig
security/          → SesionUsuario (session-scope), JWT, filtros
util/              → Helpers (WebClientHelper, CedulaEcuatorianaUtils)
```

### Flujo de datos

```
Usuario → Controlador MVC → Servicio → RestClient → Backend API (:8083)
                ↓
          Vista Thymeleaf (templates/)
```

### Convenciones de nombres

| Componente | Patrón | Ejemplo |
|------------|--------|---------|
| Controladores | `{Entidad}Controlador` | `EquiposControlador` |
| Servicios (interfaz) | `I{Entidad}Servicio` | `IEquiposServicio` |
| Servicios (impl) | `{Entidad}ServicioImpl` | `EquiposServicioImpl` |
| Request DTOs | `{Entidad}Request` | `EquipoRequest` |
| Response DTOs | `{Entidad}Response` | `EquipoResponse` |

## Build y Test

```powershell
# Compilar
./mvnw clean package -DskipTests

# Tests
./mvnw test

# Ejecutar en dev
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

## Stack técnico

- **Java 17**, **Spring Boot 4.0.2**
- **Thymeleaf** con layout dialect (`plantilla/plantillaui` como layout maestro)
- **Spring Security** + JWT (jjwt 0.12.3)
- **RestClient** (Spring 6) para comunicación con backend
- **OpenPDF** y **Apache POI** para exportación PDF/Excel
- **Spring Mail** para envío de correos
- **Lombok**

## Conexión con el backend

- URL base: `${API_BASE_URL:http://localhost:8083/api}`
- RestClient con interceptor que agrega `Authorization` header desde `SesionUsuario`
- Errores extraídos con `WebClientHelper`
- El frontend corre en **puerto 8081**, el backend en **puerto 8083**

## Sesión y roles

- `SesionUsuario` es `@SessionScope` — almacena: correo, nombre, rol, JWT token, idUsuario
- Roles: `ADMINISTRADOR`, `TECNICO`, `AUDITOR`
- Verificación en templates: `th:if="${sesionUsuario.rol == 'ADMINISTRADOR'}"`
- Métodos helper: `tieneRol(String)`, `tieneAlgunRol(String...)`
- `iniciarSesion()` / `cerrarSesion()` para gestión de sesión

## Templates Thymeleaf

- Layout maestro: `templates/plantilla/plantillaui.html`
- Patrón de decoración: `layout:decorate="~{plantilla/plantillaui}"`
- Fragment de contenido: `layout:fragment="content"`
- Menú lateral dinámico por rol (th:if)
- CSS: Bootstrap + tema corporativo (`assets/css/`)

## Configuración

- Thymeleaf cache: **off** en dev, **on** en prod
- Log level: **DEBUG** en dev, **WARN** en prod
- Stacktraces ocultos en prod (`server.error.include-stacktrace=never`)
- Uploads: `./uploads/` (mantenimientos, actas, PDFs)

## Convenciones de código

- Idioma: **español** (nombres de clases, métodos, templates)
- Validación de cédula ecuatoriana con `CedulaEcuatorianaUtils`
- Las rutas MVC siguen el patrón: `/{recurso}`, `/{recurso}/nuevo-{recurso}`, `/{recurso}/editar-{recurso}/{id}`
