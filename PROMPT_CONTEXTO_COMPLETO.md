# PROMPT DE CONTEXTO COMPLETO - SISTEMA CRESIO DE GESTIÓN DE ACTIVOS

Eres un asistente experto en desarrollo fullstack. Estoy trabajando en un **sistema de gestión de activos TI** llamado **CRESIO** compuesto por 3 proyectos interconectados. A continuación te doy el contexto completo de toda la arquitectura, entidades, endpoints, templates y app móvil para que puedas ayudarme con cualquier tarea.

---

## 🏗️ ARQUITECTURA GENERAL

```
┌──────────────────┐     HTTP/REST      ┌──────────────────────┐
│  crescio_mobile   │ ──────────────────►│                      │
│  (Flutter app)    │   Basic Auth       │   gestionactivosapi  │
│  Puerto: N/A      │                    │   (Spring Boot API)  │
└──────────────────┘                    │   Puerto: 8083       │
                                         │   PostgreSQL: cresio2│
┌──────────────────┐     HTTP/REST      │                      │
│ consumogestion    │ ──────────────────►│                      │
│ activosapi        │   Basic Auth       └──────────────────────┘
│ (Thymeleaf MVC)   │   + JWT session
│ Puerto: 8081      │
└──────────────────┘
```

- **gestionactivosapi** → Backend REST API (Spring Boot 4.0.2, Java 17, PostgreSQL, Spring Security con Basic Auth, Arquitectura Limpia por capas)
- **consumogestionactivosapi** → Frontend Web MVC (Spring Boot 4.0.2, Java 17, Thymeleaf + Bootstrap 5, consume la API con WebClient, sesión JWT)
- **crescio_mobile** → App móvil (Flutter 3.22+, Dart 3.3+, Provider, SQLite offline, consume la API con http package)

---

## 📦 PROYECTO 1: gestionactivosapi (Backend REST API)

### Tecnologías
- **Spring Boot 4.0.2** con Java 17
- **PostgreSQL** (base: `cresio2`, puerto 5432)
- **Spring Data JPA** + Hibernate (ddl-auto=update)
- **Spring Security** con HTTP Basic Auth + BCrypt
- **MapStruct 1.6.3** para mapeo DTO↔Entity
- **Lombok** para reducción de boilerplate
- **Apache POI 5.2.5** para Excel
- **OpenPDF 1.3.39** para PDF
- **Spring Mail** (Gmail SMTP) para notificaciones email
- **Flyway** configurado (migraciones no activas, se usa ddl-auto)

### Puerto: 8083
### Base URL: `http://localhost:8083/api`

### Estructura de Paquetes (Arquitectura Limpia)
```
com.uisrael.gestionactivosapi/
├── presentacion/           # Controllers REST + DTOs + Mappers + Validación
│   ├── controladores/      # 21 REST Controllers
│   ├── dto/request/        # 14 Request DTOs
│   ├── dto/response/       # 20+ Response DTOs
│   ├── mapeadores/         # MapStruct mappers (13 interfaces)
│   └── validacion/         # Custom validators (@CedulaEcuatoriana)
├── aplicacion/             # Casos de uso (46 implementaciones)
│   ├── casosuso/entradas/  # Interfaces de casos de uso
│   └── casosuso/impl/      # Implementaciones
├── dominio/                # Entidades de dominio, puertos, value objects
│   ├── entidades/          # Entidades de dominio
│   ├── puertos/            # Interfaces de repositorio
│   └── valoresobjeto/      # TipoEquipo, Email, Nombre, etc.
└── infraestructura/        # JPA entities, repos, config, seguridad
    ├── persistencia/jpa/   # 22 JPA Entities
    ├── repositorios/       # 21 JPA Repositories
    ├── configuracion/      # SecurityConfig, Scheduler
    ├── seguridad/          # ServicioDetallesUsuario
    └── servicios/          # Notificaciones, PDF, Email
```

### 📊 ENTIDADES JPA (22 entidades)

#### Entidades Principales

**EquiposJpa** (`equipos`)
- idEquipo (PK), codigoSap (unique), tipoEquipo, modelo, serial (unique), procesador, memoriaRamGb, capacidadAlmacenamientoGb, sistemaOperativo, licenciaWindowsActivada, etiquetaActivoFijo, tipoLicenciaOffice, versionOffice, unionDominio, ip (unique), mac (unique), fechaCompra, precioCompra, estadoEquipo, observacionEquipo, estado, deleted_at
- FK: fkMarcas → MarcasJpa, fkCategoria → CategoriaEquiposJpa, fkUbicacion → UbicacionesJpa

**UsuariosJpa** (`usuarios`)
- idUsuario (PK), nombre, cedula, correo (unique), contrasena (BCrypt), estado, deleted_at
- FK: fkDepartamento → DepartamentosJpa, fkRol → RolesJpa

**MantenimientosJpa** (`mantenimientos`)
- idMantenimiento (PK), equipoId, equipoSnapshot (embeddable: serieSnapshot, sineSnapshot, yearSnapshoted), idCliente, empresaId, fechaProgramada, fecCierre, frecuenciaDias, descripcion, tipoMantenimiento, idUsuario, estado, creadoEn, estadoInterno (enum), estadoGeneral, proximaFecha, fkProgramado, odooTicketId, tipoOrigen (enum), activo
- FK: fkEquipo → EquiposJpa, fkCliente → CustodiosJpa, fkUsuario → UsuariosJpa, programadoRel → MantenimientoProgramadoJpa

**CustodiasJpa** (`custodias`)
- idCustodiaEquipo (PK), fechaInicio, fechaFin, observacion, estado, tipoMovimiento
- FK: fkEquipo → EquiposJpa, fkCustodio → CustodiosJpa, fkUbicacion → UbicacionesJpa

**MantenimientoProgramadoJpa** (`mantenimientos_programados`)
- idProgramado (PK), equipoId, tecnicoId, frecuenciaDias, fechaUltimoMantenimiento, fechaProximoMantenimiento, estado, observaciones
- FK: fkEquipo → EquiposJpa, fkTecnicoAsignado → UsuariosJpa

#### Entidades Administrativas

**CustodiosJpa** (`custodios`)
- idCustodio (PK), nombre, cedula (unique), correo, telefono, fechaIngreso, estado, deleted_at
- FK: fkCargo → CargosJpa, fkUbicacion → UbicacionesJpa, fkUsuario → UsuariosJpa

**DepartamentosJpa** (`departamentos`)
- idDepartamento (PK), nombre, estado
- FK: fkUbicacion → UbicacionesJpa

**CargosJpa** (`cargos`)
- idCargo (PK), nombre, estado
- FK: fkDepartamento → DepartamentosJpa

**UbicacionesJpa** (`ubicaciones`)
- idUbicacion (PK), nombre, agencia, estado, latitud, longitud, direccion, ciudad, parroquia, provincia, linkCoordenada, deleted_at

**RolesJpa** (`roles`) → idRol, nombre, estado
**MarcasJpa** (`marcas`) → idMarca, nombre, estado
**CategoriaEquiposJpa** (`categorias_equipo`) → idCategoria, nombre, estado

#### Entidades de Soporte

**NotificacionJpa** (`notificaciones`) → idNotificacion, usuarioId, mensaje, url, leida, creadoEn, referenciaMantenimientoId; FK: fkUsuario, fkMantenimiento
**ImagenMantenimientoJpa** (`imagenes_mantenimiento`) → idImagen, idMantenimiento, nombreArchivo, rutaArchivo, tamanioBytes; FK: fkMantenimiento
**FirmaMantenimientoJpa** (`firmas_mantenimiento`) → id, idMantenimiento, tipoFirma (enum), firmaBase64, firmadoEn, ipOrigen; FK: mantenimiento
**ActividadChecklistJpa** (`actividades_checklist`) → idActividad, nombre, categoria, orden, estado
**ActividadRealizadaJpa** (`actividades_realizadas`) → idActividadRealizada, idMantenimiento, idActividad, realizada; FK: fkMantenimiento, fkActividad
**ChecklistCategoriaJpa** (`checklist_categoria`) → PK compuesta (idActividad, idCategoria)
**ActivoJpa** (`activos`) → Legacy, soft-deprecated
**ActualizacionActivoJpa** (`actualizacion_activos`) → Audit trail

### 🔐 Seguridad (SecurityConfig)

**Autenticación**: HTTP Basic Auth con BCryptPasswordEncoder
**CSRF**: Deshabilitado
**Roles**: ADMINISTRADOR, TECNICO, AUDITOR

| Recurso | GET | POST/PUT | DELETE |
|---------|-----|----------|--------|
| /api/equipos | ADMIN,TECNICO,AUDITOR | ADMIN,TECNICO | ADMIN,TECNICO |
| /api/mantenimiento* | ADMIN,TECNICO | ADMIN,TECNICO | ADMIN,TECNICO |
| /api/custodias, /api/custodios | ADMIN,TECNICO | ADMIN,TECNICO | ADMIN,TECNICO |
| /api/marcas, /api/categorias-equipo | ADMIN,TECNICO | ADMIN,TECNICO | ADMIN,TECNICO |
| /api/departamentos, /api/ubicaciones, /api/cargos | ADMIN,TECNICO | ADMIN | ADMIN |
| /api/usuarios, /api/roles | ADMIN | ADMIN | ADMIN |
| /api/notificaciones | ADMIN,TECNICO | ADMIN,TECNICO | - |
| /api/visita, /api/historial | ADMIN,TECNICO,AUDITOR | ADMIN,TECNICO | - |
| /api/setup/**, /api/auth/**, /api/autenticacion/** | Público | - | - |

### 🌐 Endpoints REST Completos

#### Autenticación
- `POST /api/autenticacion/login` → {correo, contrasena} → Usuarios (sin password)
- `GET /api/auth/yo` → Info del usuario autenticado

#### Equipos `/api/equipos`
- `POST /` → EquiposRequestDTO → EquiposResponseDTO (201)
- `GET /` → List<EquiposResponseDTO>
- `GET /{id}` → EquiposResponseDTO
- `PUT /{id}` → EquiposRequestDTO → EquiposResponseDTO
- `PUT /estado/{id}` → {estado: boolean} → EquiposResponseDTO
- `GET /existe-codigo?codigo=X&id=Y` → boolean (duplicado check)
- `GET /existe-serial?serial=X&id=Y` → boolean
- `GET /existe-ip?ip=X&id=Y` → boolean
- `GET /existe-mac?mac=X&id=Y` → boolean

#### Mantenimientos `/api/mantenimientos`
- `POST /` → MantenimientosRequestDTO → MantenimientosResponseDTO (201)
- `GET /` → List<MantenimientosResponseDTO>
- `GET /{id}` → MantenimientosResponseDTO

#### Mantenimiento Programado `/api/mantenimiento/programado`
- `GET /` → List<MantenimientoProgramadoResponseDTO>
- `GET /vencidos-proximos` → Lista vencidos y próximos
- `POST /` → MantenimientoProgramadoRequestDTO → (201)
- `POST /desactivar/{id}` → (204)

#### Custodias `/api/custodias`
- `POST /` → List<CustodiasRequestDTO> → List<CustodiasResponseDTO> (201)
- `GET /`, `GET /{id}`, `PUT /{id}`, `PUT /estado/{id}`
- `GET /conteo-tipo/{tipo}` → Conteo por tipo movimiento

#### Custodios `/api/custodios`
- CRUD completo + `PUT /{idCustodio}/vincular-usuario/{idUsuario}`
- `GET /existe-cedula?cedula=X&id=Y`, `GET /existe-correo?correo=X&id=Y`

#### Usuarios `/api/usuarios` - CRUD (solo ADMIN)
#### Roles `/api/roles` - CRUD (solo ADMIN)
#### Departamentos `/api/departamentos` - CRUD + existe-nombre + estado
#### Ubicaciones `/api/ubicaciones` - CRUD + existe-nombre + estado
#### Cargos `/api/cargos` - CRUD
#### Marcas `/api/marcas` - CRUD
#### Categorías `/api/categorias-equipo` - CRUD

#### Notificaciones `/api/notificaciones`
- `GET /` → Lista por usuario autenticado
- `GET /count` → Conteo no leídas
- `POST /{id}/leer` → Marcar como leída

#### Historial, Visita Técnica, Orden de Trabajo
- `/api/historial/{equipoId}` → Historial completo del equipo
- `/api/visita` → Gestión de visitas técnicas
- `/api/orden` → Órdenes de trabajo

---

## 🖥️ PROYECTO 2: consumogestionactivosapi (Frontend Web)

### Tecnologías
- **Spring Boot 4.0.2** con Java 17
- **Thymeleaf** + thymeleaf-layout-dialect
- **Bootstrap 5** + CSS custom CRESIO
- **WebClient** (reactivo) para consumir la API REST
- **Spring Security** + JWT (access token 15min, refresh 7 días)
- **POI + OpenPDF** para exportar Excel/PDF
- **Spring Mail** para emails

### Puerto: 8081
### API Backend: `http://localhost:8083/api`

### Estructura de Paquetes
```
com.uisrael.consumogestionactivosapi/
├── controlador/              # 17 MVC Controllers + 2 REST Controllers
│   ├── AuthControlador       # Login, setup, redirects
│   ├── ApiAuthControlador    # JWT login/refresh (JSON)
│   ├── ApiRelacionesControlador  # AJAX inline creation (JSON)
│   ├── InicioControlador     # Dashboard
│   ├── EquiposControlador    # Equipos CRUD views
│   ├── MantenimientoControlador  # Mantenimiento multi-equipo
│   ├── CustodiasControlador  # Custodias (actas)
│   ├── CustodiosControlador  # Custodios CRUD
│   ├── UsuariosControlador   # Usuarios (admin)
│   ├── UbicacionesControlador
│   ├── DepartamentosControlador
│   ├── RolesControlador
│   ├── MarcasControlador
│   ├── CategoriaEquiposControlador
│   ├── ActividadChecklistControlador
│   ├── ImportarControlador   # Excel import
│   ├── NotificacionesControlador
│   ├── VisitaTecnicaControlador
│   ├── OrdenTrabajoControlador
│   └── HistorialControlador
├── service/                  # 25+ service interfaces + implementaciones
│   ├── I*Servicio.java       # Interfaces
│   └── impl/*ServicioImpl.java  # WebClient implementations
├── dto/                      # 49 DTOs (Request + Response)
├── security/                 # JWT filter, SecurityConfig
└── config/                   # WebClient config, general config
```

### Controladores MVC y Vistas

| Controlador | Ruta | Vista Thymeleaf | Descripción |
|-------------|------|-----------------|-------------|
| InicioControlador | GET /inicio | inicio.html | Dashboard con stats |
| EquiposControlador | GET /equipos | Equipos/listarEquipos.html | Lista con filtros |
| | GET /equipos/nuevo-equipo | Equipos/nuevoEquipo.html | Formulario creación |
| | GET /equipos/editar-equipo/{id} | Equipos/editarEquipo.html | Formulario edición |
| | POST /equipos | - | Guardar/actualizar |
| MantenimientoControlador | GET /mantenimiento | mantenimiento/lista-mantenimientos.html | Lista mantenimientos |
| | GET /mantenimiento/nuevo | mantenimiento/registro-manual.html | Multi-equipo + checklist |
| | GET /mantenimiento/{id} | mantenimiento/detalle-mantenimiento.html | Detalle con imágenes |
| | GET /mantenimiento/{id}/pdf | - | Descarga PDF |
| | GET /mantenimiento/programado | mantenimiento/mantenimiento-programado.html | Programados |
| CustodiasControlador | GET /custodias | Custodias/listarCustodias.html | Lista agrupada por acta |
| | GET /custodias/nueva-custodia | Custodias/nuevoCustodia.html | Multi-equipo |
| CustodiosControlador | GET /custodios | Custodios/listarCustodios.html | Lista |
| | GET /custodios/nuevo-custodio | Custodios/nuevoCustodio.html | Con autocomplete |
| | GET /custodios/editar-custodio/{id} | Custodios/editarCustodio.html | Edición |
| UsuariosControlador | GET /usuarios | usuarios/listarUsuarios.html | Lista |
| | GET /usuarios/nuevo-usuario | usuarios/nuevoUsuario.html | Creación |
| | GET /usuarios/editar-usuario/{id} | usuarios/editarUsuario.html | Edición |
| UbicacionesControlador | CRUD /ubicaciones/* | ubicaciones/*.html | CRUD completo |
| DepartamentosControlador | CRUD /departamentos/* | departamentos/*.html | CRUD completo |
| RolesControlador | CRUD /roles/* | roles/*.html | CRUD completo |
| MarcasControlador | CRUD /marcas/* | marcas/*.html | CRUD completo |
| CategoriaEquiposControlador | CRUD /categorias-equipo/* | categorias_equipo/*.html | CRUD completo |
| ActividadChecklistControlador | CRUD /checklist/* | checklist/*.html | Agrupado por categoría |
| ImportarControlador | GET /importar, POST /importar/preview, POST /importar/confirmar | importar/importarEquipos.html | Excel upload |
| VisitaTecnicaControlador | GET /visita | Visita/visita-tecnica.html | Cascada AJAX |
| OrdenTrabajoControlador | GET /orden/{id} | Orden/orden-trabajo.html | Órdenes |
| HistorialControlador | GET /historial/{equipoId} | Historial/historial-equipo.html | Tabs: historial/info/stats |
| NotificacionesControlador | GET /notificaciones | mantenimiento/notificaciones.html | Lista notificaciones |

### ApiRelacionesControlador (AJAX inline creation)
Base: `/api/relaciones`
- GET/POST `/departamentos` - Listar/Crear departamentos
- GET/POST `/cargos` - Listar/Crear cargos
- GET/POST `/ubicaciones` - Listar/Crear ubicaciones
- GET/POST `/marcas` - Listar/Crear marcas
- GET/POST `/categorias` - Listar/Crear categorías equipo
- GET/POST `/roles` - Listar/Crear roles

### Template Layout (Thymeleaf)
- **Layout principal**: `plantilla/plantillaui.html`
- **Navegación lateral**: role-based con `th:if`
- **Decoración**: `layout:decorate="~{plantilla/plantillaui}"` + `layout:fragment="contenidopaginas"`
- **CSS**: Bootstrap 5 + CRESIO theme custom + Plus Jakarta Sans font
- **JS**: vendors.min.js (jQuery+Bootstrap bundle), DataTables, Select2, Quill, Tagify, ApexCharts
- **Modales inline**: Bootstrap 5 modals con `document.body.appendChild()` para evitar CSS containment

### Servicios (WebClient)
Todos los servicios usan WebClient con Basic Auth para consumir `/api/*` del backend:
- IEquiposServicio, IMarcasServicio, ICategoriaEquiposServicio
- IMantenimientoManualServicio, IMantenimientoProgramadoServicio
- ICustodiasServicio, ICustodiosServicio
- IUsuariosServicio, IRolesServicio
- IDepartamentosServicio, IUbicacionesServicio, ICargosServicio
- IActividadChecklistServicio, INotificacionServicio
- IVisitaTecnicaServicio, IOrdenTrabajoServicio, IHistorialEquipoServicio
- IImportarServicio
- CustodiasPdfService, CustodiasExcelService, CorreoServicio

---

## 📱 PROYECTO 3: crescio_mobile (Flutter App)

### Tecnologías
- **Flutter** ≥3.22.0, **Dart** ≥3.3.0
- **Provider** para state management
- **http** package para llamadas REST
- **flutter_secure_storage** para tokens
- **sqflite** para SQLite local (offline)
- **mobile_scanner** para QR
- **signature** para firmas digitales
- **image_picker** para fotos
- **connectivity_plus** para detectar offline
- **flutter_local_notifications** para notificaciones locales
- **get_it** como service locator

### applicationId: `com.cresio.mobile`

### Estructura del Proyecto
```
lib/
├── main.dart                    # Entry point, providers, routing
├── core/
│   ├── config/app_config.dart   # IP:puerto configurable (SharedPreferences)
│   ├── errors/exceptions.dart   # OfflineException, ServerException, AuthException, NotFoundException
│   ├── network/api_client.dart  # HTTP client con Basic Auth, auto-retry 401, timeout 15s
│   └── storage/
│       ├── local_database.dart  # SQLite: mantenimientos_pendientes, fotos_pendientes, firmas_pendientes, sync_queue
│       └── secure_storage_service.dart  # Tokens en keychain: auth_jwt, auth_user, auth_name, auth_role
├── features/
│   ├── auth/                    # Login + AuthProvider (ChangeNotifier)
│   ├── dashboard/               # DashboardShell (bottom nav) + DashboardProvider
│   ├── equipos/                 # Lista equipos + Detalle con historial
│   ├── mantenimientos/          # Lista + Detalle + Formulario (multi-equipo, checklist, firmas, fotos)
│   ├── ubicaciones/             # CRUD ubicaciones
│   ├── visitas/                 # Visitas técnicas con filtros en cascada
│   ├── notificaciones/          # Lista + marcar leída + badge
│   ├── configuracion/           # Config servidor (IP:puerto) + Settings
│   └── sync/                    # SyncService: cola offline → sincronización automática
└── shared/
    ├── theme/app_theme.dart     # Material 3: primary #185FA5, secondary #1D9E75
    └── widgets/                 # CresioScaffold, StatusBanner
```

### Autenticación
- **Método**: Basic Auth (base64 de correo:contraseña)
- **Login**: `GET /auth/yo` con header Authorization → recibe {correo, rol, nombreUsuario}
- **Persistencia**: flutter_secure_storage (auth_jwt, auth_user, auth_name, auth_role)
- **Auto-logout**: En respuesta 401

### Roles y Capacidades
```
ADMIN: ver/crear/cerrar mantenimientos, ver visitas/equipos/notificaciones, gestionar ubicaciones, configurar
TECNICO: ver/crear/cerrar mantenimientos, ver visitas/equipos/notificaciones
CONSULTA: solo lectura
```

### Pantallas y Navegación

**DashboardShell** (Bottom Navigation):
1. **Inicio** → Stats (mantenimientos abiertos, equipos activos, notificaciones), pendientes offline, actividad reciente
2. **Mantenimientos** → Lista filtrable por estado, formulario completo (multi-equipo + checklist + firmas + fotos)
3. **Visitas** → Filtros en cascada: Ubicación → Custodio → Equipos
4. **Equipos** → Lista con búsqueda + filtros (estado, tipo, custodio, ubicación), detalle con specs + custodia + estadísticas + historial
5. **Más** → Settings, Ubicaciones, Notificaciones

**FAB**: "+Mantenimiento" (solo si puede crear)

### Formulario de Mantenimiento (pantalla compleja)
1. Selección multi-equipo
2. Selección custodio (filtrado por equipos)
3. Tipo mantenimiento (PREVENTIVO, CORRECTIVO)
4. Fecha + Estado general
5. Detalle/observaciones
6. Checklist agrupado por categoría (100% obligatorio)
7. Imágenes (image_picker, compresión 80%)
8. Firma técnico + Firma custodio (signature pad, base64 PNG, ambas obligatorias)
9. Validación completa antes de enviar

### Sincronización Offline
```
App offline → crear/cerrar mantenimiento
  ↓ OfflineException → enqueueSyncOperation() → sync_queue (SQLite)
  ↓ SnackBar "Pendiente de sincronización"

App online → DashboardProvider.refresh()
  ↓ SyncService.syncPendingOperations() → procesa cola
  ↓ create_mantenimiento → POST /mantenimiento
  ↓ close_mantenimiento → POST /mantenimiento/cerrar/{id}
  ↓ Éxito: marcar sincronizado / Error: registrar e incrementar attempts
```

### Endpoints Consumidos por la App Móvil
```
GET  /auth/yo                          # Login
GET  /equipos                          # Lista equipos
GET  /historial/{equipoId}             # Detalle + historial equipo
GET  /mantenimiento                    # Lista mantenimientos
GET  /mantenimiento/{id}               # Detalle mantenimiento
POST /mantenimiento                    # Crear mantenimiento
POST /mantenimiento/cerrar/{id}        # Cerrar mantenimiento
POST /mantenimiento/{id}/imagenes      # Subir imágenes
GET  /custodios                        # Lista custodios
GET  /custodias                        # Relaciones equipo-custodio
GET  /visita/custodios?ubicacionId=X   # Custodios por ubicación
GET  /visita/equipos?ubicacionId=X&custodioId=Y  # Equipos filtrados
GET  /ubicaciones                      # Lista ubicaciones
POST /ubicaciones                      # Crear ubicación
PUT  /ubicaciones/{id}                 # Actualizar ubicación
PUT  /ubicaciones/estado/{id}          # Cambiar estado
GET  /actividades-checklist            # Checklist por categoría
GET  /notificaciones                   # Lista notificaciones
GET  /notificaciones/count             # Conteo no leídas
POST /notificaciones/{id}/leer         # Marcar leída
GET  /setup/necesario                  # Verificar servidor
```

---

## 🗄️ MODELO DE DATOS RELACIONAL

```
roles ←── usuarios ──→ departamentos ──→ ubicaciones
              ↑              ↑
              │              │
         custodios ──→ cargos ──→ departamentos
           ↑   ↑
           │   └── ubicaciones
           │
    custodias ──→ equipos ──→ marcas
                    ↑   ↗       categorias_equipo
                    │  /         ubicaciones
                    │ /
              mantenimientos ──→ usuarios (técnico)
                ↑   ↑   ↑
                │   │   └── firmas_mantenimiento
                │   └── imagenes_mantenimiento
                └── actividades_realizadas ──→ actividades_checklist
                
              mantenimientos_programados ──→ equipos + usuarios
              notificaciones ──→ usuarios + mantenimientos
```

---

## 🎨 CONVENCIONES DEL PROYECTO

### Backend (Java)
- Arquitectura limpia: presentacion → aplicacion → dominio → infraestructura
- Sufijos: `*Jpa` (entidades), `*RequestDTO`/`*ResponseDTO` (DTOs), `*UseCase` (casos de uso)
- Validación con Jakarta Validation + custom `@CedulaEcuatoriana`
- Soft delete con campo `deleted_at` y `estado` boolean

### Frontend (Thymeleaf)
- Layout: `layout:decorate="~{plantilla/plantillaui}"` + `layout:fragment="contenidopaginas"`
- Modales Bootstrap 5 movidos al `<body>` con JS para evitar CSS containment
- AJAX inline creation via `/api/relaciones/*`
- CSS: Bootstrap 5 + CRESIO custom theme + Plus Jakarta Sans
- Icons: Feather icons (`feather-*`)

### Mobile (Flutter)
- State management: Provider (ChangeNotifier)
- Repositorios en `features/*/data/`
- Screens en `features/*/presentation/`
- Offline-first: SQLite queue + sync automático
- Auth: Basic Auth persistido en secure storage
- Tema Material 3: Primary #185FA5, Secondary #1D9E75

---

## ⚙️ CONFIGURACIÓN DE DESARROLLO

### Backend API
```
Puerto: 8083
DB: postgresql://localhost:5432/cresio2 (user: postgres)
Run: mvn spring-boot:run (o ./tools/run-api-java17.ps1)
```

### Frontend Web
```
Puerto: 8081
API: http://localhost:8083/api
Run: mvn spring-boot:run
```

### Mobile
```
API configurable desde la app (Settings → IP:Puerto)
Default: 192.168.1.23:8082
Run: flutter run
```

---

Con este contexto completo puedes ayudarme con cualquier tarea de desarrollo, debugging, nuevas funcionalidades o refactoring en cualquiera de los 3 proyectos. Siempre que te pida algo, ya tienes el conocimiento de toda la arquitectura, entidades, endpoints, templates y flujos del sistema CRESIO.
