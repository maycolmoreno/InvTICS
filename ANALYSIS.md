# Análisis Exhaustivo de Proyectos: Arquitectura Hexagonal y Principios SOLID

**Fecha de Análisis:** 24 de marzo de 2026

**Proyectos Evaluados:**
- `gestionactivosapi` (Java - Spring Boot 4.0.2)
- `consumogestionactivosapi` (Java - Spring Boot 4.0.2)
- `crescio_mobile` (Flutter 3.22.0+)

---

## ÍNDICE

1. [Resumen Ejecutivo](#resumen-ejecutivo)
2. [Análisis por Proyecto](#análisis-por-proyecto)
3. [Evaluación de Arquitectura Hexagonal](#evaluación-de-arquitectura-hexagonal)
4. [Análisis de Principios SOLID](#análisis-de-principios-solid)
5. [Problemas Identificados](#problemas-identificados)
6. [Recomendaciones Prioritarias](#recomendaciones-prioritarias)
7. [Matriz de Impacto y Esfuerzo](#matriz-de-impacto-y-esfuerzo)

---

## Resumen Ejecutivo

### Estado General

| Aspecto | gestionactivosapi | consumogestionactivosapi | crescio_mobile |
|---------|-------------------|--------------------------|----------------|
| **Arquitectura Hexagonal** | Parcialmente Implementada (40%) | No Aplicable (Cliente Simple) | Parcialmente Implementada (60%) |
| **Cumplimiento SOLID** | Crítico (2.5/5) | No Aplicable | Moderado (3/5) |
| **Organizacion** | Estructurada | Minimal | Clean Structure |
| **Prioritario** | REFACTORING URGENTE | Mantenimiento Básico | Mejoras Incrementales |

### Hallazgos Críticos

1. **gestionactivosapi**: Violación severa de Dependency Inversion en capa de aplicación
2. **crescio_mobile**: Acoplamiento entre Provider y Repository que dificulta testabilidad
3. **consumogestionactivosapi**: Utilidad básica sin estructura - riesgo bajo

---

## Análisis por Proyecto

### 1. GESTIONACTIVOSAPI (Java - API Principal)

#### 1.1 Estructura Actual

```
src/main/java/com/uisrael/gestionactivosapi/
├── presentacion/                    # ADAPTADOR: HTTP
│   ├── controladores/               # 20 controladores REST
│   ├── dto/                         # Request/Response DTOs
│   ├── mapeadores/                  # IUsuariosDtoMapper, IEquiposDtoMapper, etc.
│   └── validacion/                  # Validadores personalizados
├── aplicacion/                      # CAPA DE APLICACIÓN
│   ├── casosuso/
│   │   ├── entradas/                # 19+ interfaces de puertos
│   │   └── impl/                    # 19+ implementaciones de casos de uso
│   ├── servicios/                   # PROBLEMA: 8 servicios adicionales
│   └── excepciones/                 # RulesException, RecursoNoEncontradoException, etc.
├── dominio/                         # NÚCLEO DE NEGOCIO
│   ├── entidades/                   # 28 entidades del dominio
│   ├── repositorios/                # 18 interfaces de puertos (repositorios)
│   ├── validacion/                  # Lógica de validacion empresarial
│   └── dto/                         # DTOs de dominio
└── infraestructura/                 # ADAPTADORES: BASE DE DATOS
    ├── persistencia/
    │   └── jpa/                     # Entidades JPA (EquiposJpa, UsuariosJpa, etc.)
    ├── repositorios/                # 20 IxxxJpaRepositorio (extends JpaRepository)
    ├── seguridad/                   # Configuración de seguridad
    └── configuracion/               # Beans de configuración
```

#### 1.2 Capas Definidas

**BIEN DEFINIDAS:**
- Separación clara entre capas con paquetes independientes
- Interfaces de puertos tanto en controladores como en repositorios
- Entidades de dominio sin anotaciones JPA (EquIpos.java es POJO)

**PROBLEMAS CRÍTICOS:**
- Entidades JPA paralelas en infraestructura (EquiposJpa.java con @Entity)
- Servicios de aplicación que mezclan responsabilidades
- Falta de mapeo entre entidades de dominio y JPA en repositorios

#### 1.3 Flujo de Dependencias Actual

```
REQUEST
  ↓
Controlador (presenta)
  ├─→ inyecta IEquiposUseCase (PUERTO) ✓
  ├─→ inyecta IEquiposDtoMapper
  ↓
EquiposUseCaseImpl (aplicacion)
  ├─→ inyecta IEquiposRepositorio (PUERTO) ✓
  ├─→ valida en dominio ✓
  ↓
IEquiposRepositorio (Interfaz de Dominio)
  ↑
  PROBLEMA: Los servicios inyectan
  IEquiposJpaRepositorio (infraestructura)
  en lugar de IEquiposRepositorio
```

**Ejemplo del Problema - MantenimientoProgramadoService.java (línea 10-12):**

```java
@Service
@RequiredArgsConstructor
public class MantenimientoProgramadoService {
    private final IMantenimientoProgramadoJpaRepositorio programadoRepo;  // ❌ INFRAESTRUCTURA
    private final IEquiposJpaRepositorio equiposRepo;                    // ❌ INFRAESTRUCTURA
    private final IUsuariosJpaRepositorio usuariosRepo;                  // ❌ INFRAESTRUCTURA
    
    // Debería ser:
    // private final IMantenimientoProgramadoRepositorio programadoRepo;  // ✓ DOMINIO
    // private final IEquiposRepositorio equiposRepo;                     // ✓ DOMINIO
    // private final IUsuariosRepositorio usuariosRepo;                   // ✓ DOMINIO
}
```

#### 1.4 Casos de Uso vs Servicios (Confusión Conceptual)

**19 Casos de Uso Implementados:**
- `equiposUseCaseImpl` → valida reglas de negocio ✓
- `mantenimientosUseCaseImpl` → lógica empresarial ✓
- Algunos cases of use están bien separados ✓

**8 Servicios Adicionales (ANTI-PATRÓN):**
- `MantenimientoProgramadoService` → Mezcla aplicación + repositorios JPA
- `CorreoSchedulerService` → Scheduler que inyecta repositorios JPA
- `PdfMantenimientoService` → Generación de reportes con acceso a repos
- Estos servicios actúan como "atajos" evitando casos de uso

**Impacto:** 
- Lógica de negocio duplicada en casos de uso y servicios
- Testabilidad reducida (dependencias concretas a JPA)
- Violación del principio de inversión de dependencias

---

### 2. CONSUMOGESTIONACTIVOSAPI (Java - Cliente)

#### 2.1 Estructura Actual

```
src/main/java/com/uisrael/consumogestionactivosapi/
└── util/
    ├── WebClientHelper.java         # Helper para manejo de errores HTTP
    └── CedulaEcuatorianaUtils.java # Validación de cédulas
```

#### 2.2 Características

**Simple pero Funcional:**
- Propósito: Consumidor/Cliente de `gestionactivosapi`
- Dependencias: Spring Boot WebFlux, Jackson
- Enfoque: Utilidades de helper para consumo de APIs

```java
// WebClientHelper.java (líneas 1-45)
public final class WebClientHelper {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    private WebClientHelper() {} // Clase utilidad (patrón correcto)
    
    public static String extraerMensajeError(WebClientResponseException ex) {
        // Extrae y normaliza mensajes de error de respuestas HTTP
    }
}
```

**Evaluación:**
- ✓ No viola principios SOLID (funciones puras)
- ✓ Bajo acoplamiento
- ✗ Sin arquitectura clara (no es necesaria para una utilidad)
- ✓ Fácil de mantener

#### 2.3 Conclusión

Este proyecto es un cliente simple y **NO REQUIERE** arquitectura hexagonal. Su propósito es proporcionar helpers para consumo de APIs. No es candidato para refactoring arquitectónico.

---

### 3. CRESCIO_MOBILE (Flutter - App Móvil)

#### 3.1 Estructura Actual

```
lib/
├── core/                            # NÚCLEO DE LA APLICACIÓN
│   ├── config/                      # Configuración global
│   ├── errors/                      # Excepciones personalizadas
│   ├── network/                     # API Client, HTTP logic
│   └── storage/                     # Almacenamiento local
├── features/                        # MÓDULOS DE NEGOCIO (Clean Architecture)
│   ├── auth/
│   │   ├── data/
│   │   │   ├── auth_repository.dart
│   │   │   └── auth_models.dart
│   │   └── presentation/
│   │       ├── login_screen.dart
│   │       └── auth_provider.dart
│   ├── equipos/
│   │   ├── data/
│   │   │   ├── equipos_repository.dart
│   │   │   └── equipo_models.dart
│   │   └── presentation/
│   │       ├── equipos_screen.dart
│   │       └── equipo_detail_screen.dart
│   ├── visitas/
│   ├── tickets/
│   ├── notificaciones/
│   ├── sync/
│   └── configuracion/
├── shared/                          # COMPONENTES COMPARTIDOS
│   ├── theme/                       # Tema de la aplicación
│   └── widgets/                     # Widgets reutilizables
└── main.dart                        # Punto de entrada (MultiProvider)
```

#### 3.2 Patrón de Arquitectura

**Implementación:** Clean Architecture + Provider State Management

**Flujo de Datos:**
```
Screen (Presentation)
  ├─→ inyecta Provider (State Management)
  ├─→ Provider inyecta Repository
  ↓
Repository (Data)
  ├─→ inyecta ApiClient
  ↓
ApiClient (Network)
  └─→ HTTP calls + error handling
```

**Ejemplo - auth_provider.dart:**

```dart
class AuthProvider extends ChangeNotifier {
  AuthProvider({required AuthRepository repository}) : _repository = repository;
  
  final AuthRepository _repository;
  
  Future<bool> login(String username, String password) async {
    _status = AuthStatus.loading;
    notifyListeners();
    
    try {
      final session = await _repository.login(
        LoginRequest(username: username.trim(), password: password),
      );
      _session = session;
      _status = AuthStatus.authenticated;
      notifyListeners();
    } catch (e) {
      _status = AuthStatus.unauthenticated;
      _errorMessage = e.message;
      notifyListeners();
    }
  }
}
```

#### 3.3 Calidad de Implementación

**FORTALEZAS:**
- ✓ Separación clara presentación/lógica
- ✓ Provider injection en main.dart (MultiProvider)
- ✓ Manejo robusto de excepciones en ApiClient
- ✓ Modularidad por features

**DEBILIDADES:**
- ✗ acoplamiento Provider-UI (auth_provider directamente en screen)
- ✗ Lógica de negocio en Providers (debería estar en Use Cases)
- ✗ No hay capa de Casos de Uso (dominio limpio)
- ✗ Repository devuelve modelos de datos (sin abstracciones)
- ✗ Validaciones dispersas en múltiples lugares

#### 3.4 Comparación con Clean Architecture Ideal

| Capa | Flutter Ideal | crescio_mobile | ¿Mejora? |
|------|---------------|----------------|----------|
| Presentation | StatefulWidgets | Screens + Providers | Parcial |
| Domain | Use Cases, Entities | ❌ MISSING | Crítica |
| Data | Repositories | EquiposRepository | ✓ |
| Infrastructure | Data Sources | ApiClient (network) | ✓ |

---

## Evaluación de Arquitectura Hexagonal

### Principios de Arquitectura Hexagonal

**3 Elementos Clave:**
1. **Núcleo de Negocio** (Casos de Uso, Entidades)
2. **Puertos** (Interfaces de entrada/salida)
3. **Adaptadores** (Implementaciones concretas)

### Evaluación por Proyecto

#### gestionactivosapi: 40% Implementada ⚠️

**Implementado:**
- ✓ Puertos de entrada: `IEquiposUseCase`, `IMantenimientosUseCase` (casos de uso)
- ✓ Puertos de salida: `IEquiposRepositorio`, `IUsuariosRepositorio` (interfaces)
- ✓ Adaptadores de entrada: `EquiposControlador`, `MantenimientosControlador`
- ✓ Adaptadores de salida: Repositorios JPA

**NO Implementado:**
- ❌ Inversión de dependencias rota: Servicios inyectan `IxxxJpaRepositorio` en lugar de `IxxxRepositorio`
- ❌ Cadena de inversión quebrada: Aplicación → Infraestructura (debería ser Aplicación ← Infraestructura)
- ❌ Entidades duplicadas: `Equipos` (dominio) vs `EquiposJpa` (infraestructura)
- ❌ No hay caso de uso para `MantenimientoProgramadoService` (¿lógica perdida?)

**Diagrama de Flujo ACTUAL (incorrecto):**

```
┌─────────────────────────────────────────────────────┐
│              GESTIONACTIVOSAPI                       │
├──────────────── PRESENTACIÓN ────────────────────┐  │
│  EquiposControlador                              │  │
│  └─→ inyecta IEquiposUseCase               ✓    │  │
├─────────────── APLICACIÓN ─────────────────────┐│  │
│  EquiposUseCaseImpl                         │  ││  │
│  ├─→ inyecta IEquiposRepositorio (BUENO)  ✓│  ││  │
│                                             │  ││  │
│  MantenimientoProgramadoService            │  ││  │
│  ├─→ inyecta IEquiposJpaRepositorio ❌    │  ││  │
│  └─→ mezcla DTOs de presentación       ❌ │  ││  │
├──────────────── DOMINIO ────────────────────────┤│
│  - Entidades: Equipos (limpia)            ✓ │  ││  │
│  - Repositorios: IEquiposRepositorio      ✓ │  ││  │
│  └─→ BUG: No es implementado en app     ❌ │  ││  │
├──────────────── INFRAESTRUCTURA ──────────┐    │  │
│  IEquiposJpaRepositorio ← EquiposJpa    │    │  │
│  IEquiposJpaRepositorio extends JpaRepo │    │  │
│  └─→ Inyectado directamente en aplicación ❌ │
└────────────────────────────────────────┘    │  │
                    ↑                          │  │
                (AQUÍ ESTÁ ROTO)              │  │
                                           └──┘  │
└─────────────────────────────────────────────────┘
```

**Diagrama de Flujo CORRECTO (objetivo):**

```
┌─────────────────────────────────────────────────────┐
│              GESTIONACTIVOSAPI                       │
├──────────────── PRESENTACIÓN ────────────────────┐  │
│  EquiposControlador                              │  │
│  └─→ inyecta IEquiposUseCase               ✓    │  │
├─────────────── APLICACIÓN ─────────────────────┐│  │
│  EquiposUseCaseImpl                         │  ││  │
│  ├─→ inyecta IEquiposRepositorio           ✓│  ││  │
│  └─→ no depende de Jpa               ✓    │  ││  │
├──────────────── DOMINIO ────────────────────────┤│
│  - Entidades: Equipos (limpia)            ✓ │  ││  │
│  - Repositorios: IEquiposRepositorio      ✓ │  ││  │
│  - Casos de Uso: IMantenimientoProgramado ✓ │  ││  │
├──────────────── INFRAESTRUCTURA ──────────┐    │  │
│  EquiposJpaRepositorioImpl                │    │  │
│  └─→ implements IEquiposRepositorio  ✓  │    │  │
│      + extends JpaRepository         ✓  │    │  │
│      + mapea EquiposJpa ↔ Equipos  ✓  │    │  │
└────────────────────────────────────────┘    │  │
       ↑                                    │  │
       └─ Inyectado como IEquiposRepositorio │  │
                                          └──┘  │
└─────────────────────────────────────────────────┘
```

#### consumogestionactivosapi: N/A

No aplica - es un cliente/utilidad simple sin requerimientos de arquitectura.

#### crescio_mobile: 60% Implementada ⚠️

**Implementado:**
- ✓ Separación Presentación/Data
- ✓ Repositorios como adaptadores
- ✓ ApiClient como adaptador de red
- ✓ Inyección de dependencias (MultiProvider)

**FALTA - Capa de Dominio:**
- ❌ No hay casos de uso (Use Cases/Interactors)
- ❌ Lógica de negocio en Providers (debería estar en domain)
- ❌ Entidades de dominio ausentes
- ❌ Sin abstracciones firmes entre capas

**Ejemplo de Problema:**

```dart
// Actual (acoplado):
EquiposRepository.listarConHistorial() {
  // Lógica de negocio: enriquecimiento + sorting
  for (final equipo in equipos) {
    final historial = await obtenerDetalle(equipoId);
    enriched.add(
      EquipoListItem(
        ...
        estadoMantenimiento: historial.estadoMantenimiento,
        diasSinMantenimiento: historial.estadisticas.diasSinMantenimiento,
      ),
    );
  }
  enriched.sort(_priorityCompare);  // Lógica de negocio aquí
}

// Ideal (usar caso de uso):
class ObtenerEquiposConHistorialUseCase implements UseCase<List<Equipo>, NoParams> {
  final EquiposRepository equiposRepo;
  
  Future<Either<Failure, List<Equipo>>> call(NoParams params) async {
    final equipos = await equiposRepo.listar();
    
    final enriquecidos = <Equipo>[];
    for (final equipo in equipos) {
      final historial = await equiposRepo.obtenerHistorial(equipo.id);
      enriquecidos.add(equipo.enriquecerConHistorial(historial));
    }
    
    return Right(enriquecidos..sort((a, b) => a.prioridad.compareTo(b.prioridad)));
  }
}
```

---

## Análisis de Principios SOLID

### S - Single Responsibility Principle (Responsabilidad Única)

#### gestionactivosapi

**Violaciones Críticas:**

| Clase | Responsabilidades | Problema |
|-------|-------------------|----------|
| `MantenimientoProgramadoService` | 1) Lógica de cálculo, 2) Persistencia, 3) Conversión de DTOs | ❌ 3 responsabilidades |
| `EquiposControlador` | 1) HTTP, 2) Validación de parámetros, 3) Mapeo de DTOs | ⚠️ Aceptable (típico en REST) |
| `EquiposUseCaseImpl` | 1) Lógica de validación, 2) Orquestación | ✓ Bien definido |

**Servicios Problemáticos:**

```java
// MantenimientoProgramadoService.java
public class MantenimientoProgramadoService {
    // RESPONSABILIDAD 1: Cálculo de fechas de mantenimiento
    public void recalcularProximaFecha(Integer equipoId) { 
        LocalDate hoy = LocalDate.now();
        programado.setFechaProximoMantenimiento(hoy.plusDays(...));
    }
    
    // RESPONSABILIDAD 2: Acceso a repositorio
    private final IMantenimientoProgramadoJpaRepositorio programadoRepo;
    
    // RESPONSABILIDAD 3: Conversión de DTOs
    public MantenimientoProgramadoResponseDTO obtenerPorEquipo(Integer equipoId) {
        return programadoRepo.findByEquipoId(equipoId).map(this::toDto).orElse(null);
    }
}
```

**Recomendación:**
- Extraer lógica a `IMantenimientoProgramadoUseCase`
- Inyectar `IMantenimientoProgramadoRepositorio` (no JPA)
- Separar mapeo en mapper específico

#### crescio_mobile

**Violaciones:**

| Clase | Responsabilidades | Problema |
|-------|-------------------|----------|
| `auth_provider.dart` | 1) Estado de sesión, 2) Lógica de login, 3) Persistencia | ⚠️ 3 responsabilidades |
| `equipos_repository.dart` | 1) Obtener datos, 2) Enriquecer datos, 3) Sorting | ⚠️ 2+ responsabilidades |
| `equipos_screen.dart` | 1) UI rendering, 2) Business logic (priorización) | ❌ Acoplado |

**Código Problemático:**

```dart
// equipos_repository.dart - Mezcla de responsabilidades
Future<List<EquipoListItem>> listarConHistorial() async {
  final equipos = await listar();
  final enriched = <EquipoListItem>[];
  
  // RESPONSABILIDAD 1: Obtener datos
  for (final equipo in equipos) {
    
    // RESPONSABILIDAD 2: Enriquecer con lógica
    try {
      final historial = await obtenerDetalle(equipoId);
      enriched.add(
        EquipoListItem(
          ...
          estadoMantenimiento: historial.estadoMantenimiento,
          diasSinMantenimiento: historial.estadisticas.diasSinMantenimiento,
        ),
      );
    }
  }
  
  // RESPONSABILIDAD 3: Ordenar según reglas
  enriched.sort(_priorityCompare);
  return enriched;
}
```

### O - Open/Closed Principle (Abierto para Extensión, Cerrado para Modificación)

#### gestionactivosapi

**Estado: PARCIALMENTE CUMPLIDO ⚠️**

**Bien:**
- ✓ Interfaces de casos de uso permiten nuevas implementaciones
- ✓ Mapeadores pueden extenderse sin modificar controladores

**Problemas:**
- ❌ Servicios hardcodeados (no hay forma de cambiar implementación)
- ❌ Al agregar nuevo servicio, hay que modificar controladores para inyectarlo
- ❌ Las validaciones están acopladas a cada caso de uso (no reutilizable)

**Ejemplo de Incumplimiento:**

```java
// EquiposControlador - Cerrado para extensión
@RestController
@RequestMapping("/api/equipos")
public class EquiposControlador {
    private final IEquiposUseCase equiposUseCase;
    
    // Si necesitamos comportamiento diferente, NO podemos:
    // - Cambiar a otra implementación sin recompilación
    // - Agregar comportamiento sin modificar este controlador
    // ❌ Cerrado para extensión, pero no abierto para comportamientos nuevos
}
```

#### crescio_mobile

**Estado: NO CUMPLIDO ❌**

Sin abstracciones formales (no hay interfaces de Repository), es difícil extender:

```dart
// Actual - acoplado a implementación específica
class AuthProvider {
  AuthProvider({required AuthRepository repository});
  
  // Problema: AuthRepository es la implementación concreta
  // No hay: abstract class IAuthRepository
  // Si queremos mock para testing: forzados a crear AuthRepositoryMock
}
```

### L - Liskov Substitution Principle (Sustitución de Liskov)

#### gestionactivosapi

**Estado: VIOLADO EN INFRAESTRUCTURA ❌**

**Problema Fundamental:**

```java
// INCORRECTO: Las implementaciones JPA extienden de manera informal
public interface IEquiposJpaRepositorio extends JpaRepository<EquiposJpa, Integer> {
    // Métodos adicionales específicos de JPA
    List<EquiposJpa> findByEstadoEquals(boolean estado);
}

// En aplicación se inyecta:
private final IEquiposJpaRepositorio repo; // ❌ Rompe LSP

// Debería ser:
public interface IEquiposRepositorio {
    Equipos guardar(Equipos equipo);
    List<Equipos> listarActivos();
}

// E inyectarse así:
private final IEquiposRepositorio repo; // ✓ Cumple LSP
```

**Impacto:**
- Las capas superiores no pueden cambiar de implementación
- No se puede testear con un mock que implemente diferente
- Acoplamiento directo a JPA

#### crescio_mobile

**Estado: N/A (sin abstracciones)**

No hay interfaces definidas, así que no aplica LSP.

### I - Interface Segregation Principle (Segregación de Interfaces)

#### gestionactivosapi

**Estado: PARCIALMENTE CUMPLIDO ⚠️**

**Bien:**
- ✓ Interfaces de casos de uso son específicas:
  ```java
  public interface IEquiposUseCase {
      Equipos crear(Equipos equipo);
      Equipos obtenerPorId(int id);
      List<Equipos> listar();
      // Solo métodos relevantes
  }
  ```

**Problemas:**
- ❌ Repositorios JPA usan JpaRepository que expone 40+ métodos innecesarios
  ```java
  public interface IEquiposJpaRepositorio extends JpaRepository<EquiposJpa, Integer> {
      // Hereda: save(), delete(), flush(), saveAndFlush(), deleteInBatch(), etc.
      // Pero necesita solo: guardar, buscar, listar
  }
  ```

#### crescio_mobile

**Estado: NO APLICABLE (sin interfaces)**

---

### D - Dependency Inversion Principle (Inversión de Dependencias)

#### gestionactivosapi

**Estado: CRITICAMENTE VIOLADO ❌❌❌**

Este es el problema más grave del proyecto.

**Análisis Detallado:**

```java
// NIVEL 1: Controlador → Caso de Uso (✓ CORRECTO)
public class EquiposControlador {
    private final IEquiposUseCase equiposUseCase; // Depende de abstracción
}

// NIVEL 2: Caso de Uso → Repositorio (✓ CORRECTO pero...)
public class EquiposUseCaseImpl implements IEquiposUseCase {
    private final IEquiposRepositorio repositorio; // Depende de abstracción
}

// NIVEL 3: AQUÍ ESTÁ EL PROBLEMA
// En lugar del flujo anterior, muchos servicios hacen:
public class MantenimientoProgramadoService {
    private final IMantenimientoProgramadoJpaRepositorio repo; // ❌ CONCRETO JPA
    private final IEquiposJpaRepositorio equiposRepo;          // ❌ CONCRETO JPA
    private final IUsuariosJpaRepositorio usuariosRepo;        // ❌ CONCRETO JPA
}

// Esto crea una cadena rota:
// Aplicación → Infraestructura (INCORRECTO)
// 
// Debería ser:
// Aplicación ↑ ← Infraestructura (CORRECTO - DIP)
```

**Diagrama de Flujo de Dependencias:**

```
INCORRECTO (actual):
┌─────────────┐
│ Dominio     │ (Entidades, interfaces)
└────────┬────┘
         │← depends on
         │
    ┌────▼──────────┐
    │ Aplicación    │
    ├───────────────┤
    │ ServiciosJpa  │←──────┐
    │ inyecta Jpa   │       │
    └───────────────┘       │ depends on
         │                  │ (INCORRECTO)
         │                  │
    ┌────▼──────────────────┤───────┐
    │ Infraestructura (JPA) │       │
    └───────────────────────┘       │
                                    │
                        aplicación usa
                     implementaciones JPA
                             ↓
                      ACOPLAMIENTO FUERTE
                      (Violación DIP)

CORRECTO (objetivo):
┌─────────────┐
│ Dominio     │
│ - Entidades │
│ - Puertos   │
└────────┬────┘
         │implements
         │
    ┌────▼──────────┐
    │ Aplicación    │
    │ - Casos Uso   │
    │ - inyecta     │
    │   IRepos      │
    └──────┬────────┘
           │depends on abstracciones
           │
         ▲─┐
         │ │
    ┌────┘ │
    │      │
┌───┴──────▼──────────┐
│ Infraestructura     │
│ - JPA Entities      │
│ - Repositorio Impl  │
│ - Springs Beans     │
└─────────────────────┘
       implements
       IRepositorio
```

**Impacto de la Violación:**

1. **Testing**: No se puede mockear repositorios sin JPA
   ```java
   // No se puede hacer:
   MantenimientoProgramadoService service = 
       new MantenimientoProgramadoService(mockRepo); // ❌ Mock debe extender JpaRepo
   
   // Se fuerza a usar:
   @ExtendWith(SpringExtension.class)
   class MantenimientoProgramadoServiceTest { // Test integración, no unitario
       @MockBean
       private IMantenimientoProgramadoJpaRepositorio repo;
   }
   ```

2. **Reutilización**: No se puede cambiar persistencia (SQL → NoSQL)
   - Tendría que reescribir servicios

3. **Arquitectura**: Infraestructura controla aplicación (invertido)

#### crescio_mobile

**Estado: PARCIALMENTE VIOLADO ⚠️**

```dart
class EquiposScreen {
  build() {
    return Consumer<AuthProvider>(
      builder: (context, authProvider, _) => // ACOPLADO A PROVIDER CONCRETO
        ListView(
          children: [
            // UI rendering de datos del provider
          ],
        ),
    );
  }
}

// Ideal sería usar:
class EquiposScreen {
  final ObtenerEquiposUseCase useCase;
  
  build() {
    return Consumer<ObtenerEquiposProvider>(
      builder: (context, provider, _) =>
        // Aquí está ok: useCase está abstraído detrás del provider
    );
  }
}
```

---

## Problemas Identificados

### CRÍTICOS (Deben Arreglarse de Inmediato)

#### 1. **Violación de Dependency Inversion en gestionactivosapi**

**Ubicación:** 
- `src/main/java/.../aplicacion/servicios/` (todos los servicios)
- Ej: `MantenimientoProgramadoService.java`, `CorreoSchedulerService.java`

**Problema:**
```
Servicios inyectan IxxxJpaRepositorio (infraestructura)
en lugar de IxxxRepositorio (dominio)
```

**Severidad:** CRÍTICA 🔴  
**Impacto:** Acoplamiento a JPA, imposible testear sin BD, imposible cambiar BD

**Líneas Aproximadas:**
- `MantenimientoProgramadoService.java:10-12` - inyecciones JPA
- `CorreoSchedulerService.java` - mismo patrón  
- `PdfMantenimientoService.java` - mismo patrón

**Solución:**
```java
// ANTES (INCORRECTO):
@Service
public class MantenimientoProgramadoService {
    @Autowired
    private IMantenimientoProgramadoJpaRepositorio repo; // ❌
}

// DESPUÉS (CORRECTO):
@Service
public class MantenimientoProgramadoService {
    @Autowired
    private IMantenimientoProgramadoRepositorio repo; // ✓ interfaz de dominio
}

// + crear implementación en infraestructura:
@Repository
public class MantenimientoProgramadoRepositorioImpl 
    implements IMantenimientoProgramadoRepositorio {
    
    @Autowired
    private IMantenimientoProgramadoJpaRepositorio jpaRepo;
    
    @Override
    public List<MantenimientoProgramado> obtenerPendientes() {
        return jpaRepo.findByEstadoTrue()
            .stream()
            .map(this::mapToDomain)
            .collect(toList());
    }
}
```

---

#### 2. **Mezcla de Responsabilidades en Servicios de Aplicación**

**Ubicación:**
- `MantenimientoProgramadoService.java`
- `CorreoMantenimientoService.java`
- `NotificacionService.java`

**Problema:**
```
Cada servicio combina:
1) Lógica de validación
2) Cálculos comerciales
3) Conversión de DTOs
4) Orquestación de repositorios
```

**Ejemplo - MantenimientoProgramadoService:**
```java
public class MantenimientoProgramadoService {
    
    // RESPONSABILIDAD 1: Programar mantenimiento
    public MantenimientoProgramadoResponseDTO programar(MantenimientoProgramadoRequestDTO request) {
        // Valida equipo existe (1)
        // Crea entidad (2)
        // Convierte a DTO (3)
    }
    
    // RESPONSABILIDAD 2: Recalcular fechas
    public void recalcularProximaFecha(Integer equipoId) {
        // Lógica de cálculo
    }
    
    // RESPONSABILIDAD 3: Obtener pendientes
    public List<MantenimientoProgramadoJpa> obtenerPendientesParaNotificar() {
        // Filtrado y consultas
    }
}
```

**Severidad:** ALTA 🟠  
**Impacto:** Difícil de testear, difícil de reutilizar, difícil de mantener

**Solución:** Crear casos de uso específicos:
```java
// Caso de Uso 1
public interface IProgramarMantenimientoUseCase {
    MantenimientoProgramado programar(ProgramarMantenimientoRequest request);
}

// Caso de Uso 2
public interface IRecalcularFechaMantenimientoUseCase {
    void recalcular(int equipoId);
}

// Caso de Uso 3
public interface IObtenerMantenimientosVencidosUseCase {
    List<MantenimientoProgramado> obtener();
}
```

---

#### 3. **Lógica de Negocio en Presentation Layer (crescio_mobile)**

**Ubicación:**
- `lib/features/equipos/data/equipos_repository.dart` línea 20-60
- `lib/features/equipos/presentation/equipos_screen.dart`

**Problema:**
```dart
// INCORRECTO: Lógica de negocio en Repository
Future<List<EquipoListItem>> listarConHistorial() async {
  final equipos = await listar();
  final enriched = <EquipoListItem>[];
  
  for (final equipo in equipos) {
    // 1. Obtiene historial
    // 2. Enriquece datos (LÓGICA DE NEGOCIO)
    // 3. Calcula prioridades (LÓGICA DE NEGOCIO)
    // 4. Ordena por reglas complejas (LÓGICA DE NEGOCIO)
  }
  
  enriched.sort(_priorityCompare); // ← Regla de negocio aquí
  return enriched;
}

_priorityRank() {
  // Reglas de priorización basadas en estado de mantenimiento
  // ¿Qué pasa si cambia la regla? Habría que tocar Repository
}
```

**Severidad:** MEDIA 🟡  
**Impacto:** Lógica no testeable en aislamiento, difícil de cambiar reglas

**Solución:** Crear Use Case:
```dart
abstract class GetEquiposConHistorialUseCase {
  Future<List<Equipo>> call();
}

class GetEquiposConHistorialUseCaseImpl implements GetEquiposConHistorialUseCase {
  final EquiposRepository repository;
  final EquipoEnrichmentService enrichmentService;
  final EquipoPriorityService priorityService;
  
  @override
  Future<List<Equipo>> call() async {
    final equipos = await repository.listar();
    
    final enriquecidos = await Future.wait(
      equipos.map((e) => enrichmentService.enriquecer(e))
    );
    
    return enriquecidos..sort(priorityService.compare);
  }
}
```

---

### ALTOS (Impactan Mantenibilidad)

#### 4. **Dualidad de Entidades (Domain vs JPA)**

**Ubicación:** `gestionactivosapi`

**Problema:**
```
Existen dos versiones de cada entidad:
1) Equipos (dominio limpio)
2) EquiposJpa (con anotaciones JPA @Entity, @Column, etc)

Cambio en BD = cambio en EquiposJpa + mapeo + actualizar Equipos
```

**Archivo Afectado:**
- `src/main/java/.../dominio/entidades/Equipos.java`
- `src/main/java/.../infraestructura/persistencia/jpa/EquiposJpa.java`

**Severidad:** ALTA 🟠  
**Impacto:** Código duplicado, difícil de mantener, inconsistencias

**Solución Parcial:** Usar mappers genéricos:
```java
public interface EntityMapper<Domain, Database> {
    Domain toDomain(Database db);
    Database toDatabase(Domain domain);
}

public class EquiposMapper implements EntityMapper<Equipos, EquiposJpa> {
    @Override
    public Equipos toDomain(EquiposJpa jpa) {
        return new Equipos(
            jpa.getIdEquipo(),
            jpa.getCodigoSap(),
            // ... mapeo automático
        );
    }
}
```

---

#### 5. **Sin Capa de Dominio (crescio_mobile)**

**Ubicación:** `lib/features/`

**Problema:**
```
No hay:
- Entities de dominio
- Value Objects
- Use Cases formales
- Repository Interfaces
```

**Severidad:** MEDIA 🟡  
**Impacto:** Lógica a nivel de Presenter, difícil de testear

---

### MODERADOS (Mejorables)

#### 6. **Manejo Inconsistente de Excepciones**

**gestionactivosapi:**
- ✓ `RecursoNoEncontradoException` bien definida
- ✗ Algunos servicios lanzan `IllegalArgumentException` (genérica)
- ✗ Inconsistencia en manejo en controladores

```java
// MEJOR SERÍA:
public class UsuarioInvalidoException extends DomainException { }
public class EquipoNoEncontradoException extends DomainException { }
public class MantenimientoDuplicadoException extends DomainException { }
```

#### 7. **Validaciones Dispersas**

**gestionactivosapi:**
- Validaciones en controladores (`@Valid`)
- Validaciones en casos de uso
- Validaciones en servicios

**crescio_mobile:**
- Validaciones en Providers
- Validaciones en repositories
- Validaciones en screens

**Mejor:** Usar Value Objects o Specification Pattern

---

## Recomendaciones Prioritarias

### Matriz de Priorización

| # | Problema | Proyectos | Prioridad | Esfuerzo | Impacto | Recomendación |
|---|----------|-----------|-----------|----------|---------|---------------|
| 1 | Inversión DIP quebrada | gestionactivosapi | **CRÍTICA** | 4 semanas | Mayor | Refactor servicios → casos de uso |
| 2 | Mezcla SRP en servicios | gestionactivosapi | **CRÍTICA** | 3 semanas | Mayor | Separar respons. → nuevos Use Cases |
| 3 | Lógica en Repository | crescio_mobile | ALTA | 2 semanas | Medio | Extraer a Use Cases |
| 4 | Sin capa Domain | crescio_mobile | ALTA | 3 semanas | Medio | Implementar entities + use cases |
| 5 | Dualidad Entidades | gestionactivosapi | ALTA | 2 semanas | Medio | Mappers automáticos |
| 6 | Excepciones inconsistentes | gestionactivosapi | MEDIA | 1 semana | Bajo | Crear jerarquía de excepciones |
| 7 | Validaciones dispersas | Ambos | MEDIA | 2 semanas | Bajo | Centralizar validaciones |

### Plan de Acción por Proyecto

#### GESTIONACTIVOSAPI - Refactoring Crítico

**Fase 1 (Semanas 1-2): Crear Infraestructura de Repositorios**
```
TAREA: Crear implementaciones de repositorio que respeten DIP
ARCHIVOS:
├── infraestructura/repositorios/EquiposRepositorioImpl.java
│   implements IEquiposRepositorio
│   + inyecta IEquiposJpaRepositorio
│   + mapea EquiposJpa ↔ Equipos
├── infraestructura/repositorios/MantenimientoProgramadoRepositorioImpl.java
├── infraestructura/repositorios/UsuariosRepositorioImpl.java
└── ... (uno por cada interfaz de dominio)

TESTING: Tests unitarios con mocks
```

**Fase 2 (Semanas 3-4): Migrar Servicios → Casos de Uso**
```
TAREA: Convertir servicios en casos de uso válidos
CAMBIOS:
1. MantenimientoProgramadoService
   → IProgramarMantenimientoUseCase + IRecalcularFechaMantenimientoUseCase
   
2. CorreoSchedulerService    
   → IObtenerMantenimientosVencidosUseCase + INotificarMantenimientosUseCase
   
3. PdfMantenimientoService
   → IGenerarReporteMantenimientoUseCase

4. NotificacionService
   → cada método → caso de uso separado
```

**Fase 3 (Semana 5): Pruebas y Validación**
```
Ejecutar suite de tests
Validar que DIP se respeta en toda la cadena
```

---

#### CRESCIO_MOBILE - Implementar Clean Architecture

**Fase 1 (Semana 1): Crear Capa Domain**
```
lib/features/{feature}/domain/
├── entities/
│   └── equipo.dart (valor objects puro, sin UI)
├── repositories/
│   └── i_equipos_repository.dart (interfaz abstracta)
└── usecases/
    ├── get_equipos_usecase.dart
    ├── get_equipo_detalle_usecase.dart
    └── get_equipos_con_historial_usecase.dart

EJEMPLO:
abstract class IEquiposRepository {
  Future<List<Equipo>> listar();
  Future<Equipo> obtenerDetalle(int id);
}

class GetEquiposConHistorialUseCase {
  final IEquiposRepository repository;
  
  Future<List<Equipo>> call() async {
    // Lógica de enriquecimiento aquí
  }
}
```

**Fase 2 (Semana 2): Refactor Data Layer**
```
Actualizar repositories para implementar interfaces de dominio:

class EquiposRepositoryImpl implements IEquiposRepository {
  final ApiClient apiClient;
  
  @override
  Future<List<Equipo>> listar() async {
    // mapeo de API a Equipo
  }
}
```

**Fase 3 (Semana 3): Actualizar Presentation Layer**
```
Inyectar Use Cases en Providers:

class EquiposProvider extends ChangeNotifier {
  final GetEquiposConHistorialUseCase getEquiposUseCase;
  
  Future<void> cargarEquipos() async {
    _equipos = await getEquiposUseCase();
    notifyListeners();
  }
}
```

---

#### CONSUMOGESTIONACTIVOSAPI - Sin Cambios Críticos

**Recomendación:** Mantener como utilidad simple. Considerar:
- Agregar tests unitarios para WebClientHelper
- Documentación de uso
- Logs mejorados

---

### Checklist de Implementación

**gestionactivosapi - DIP Fix:**
- [ ] Crear `EquiposRepositorioImpl implements IEquiposRepositorio`
- [ ] Crear `MantenimientoProgramadoRepositorioImpl`
- [ ] Crear `UsuariosRepositorioImpl`
- [ ] Actualizar `MantenimientoProgramadoService` → `ProgramarMantenimientoUseCaseImpl`
- [ ] Actualizar `CorreoSchedulerService` → `NotificarMantenimientosUseCaseImpl`
- [ ] Tests unitarios para cada repositorio impl
- [ ] Tests unitarios para casos de uso
- [ ] Validar inyección de dependencias

**crescio_mobile - Domain Layer:**
- [ ] Crear `lib/features/equipos/domain/entities/`
- [ ] Crear `lib/features/equipos/domain/repositories/`
- [ ] Crear `lib/features/equipos/domain/usecases/`
- [ ] Actualizar `EquiposRepositoryImpl`
- [ ] Actualizar `EquiposProvider`
- [ ] Tests unitarios para use cases
- [ ] Tests de integración con blocs

---

## Matriz de Impacto y Esfuerzo

### Gráfico de Priorización

```
IMPACTO
   ↑
   │  CRÍTICOS (hacer primero)
   │  ┌─ #1: DIP quebrada (4W, Alto impacto)
   │  │ └─ #2: SRP en servicios (3W, Alto impacto) 
   │  │
   │  │  ALTOS (hacer después)
   │  │ ┌─ #5: Dualidad de entidades (2W, Medio)
   │  │ │ └─ #4: Sin domain (3W, Medio)
   │  │ │
   │  │ │  MEDIOS (optimizar último)
   │  │ │ ┌─ #6: Excepciones (1W, Bajo)
   │  │ │ │ └─ #7: Validaciones (2W, Bajo)
   │  │ │ │
   │  └─┴─┴──────────────→
   │           ESFUERZO

TIMEBOXING PROPUESTO:
- Mes 1: Tareas críticas (#1, #2) = 7 semanas
- Mes 2: Tareas altas (#4, #5) = 5 semanas  
- Mes 3: Tareas medias (#6, #7) = 3 semanas
```

---

## Conclusiones

### Síntesis por Proyecto

#### gestionactivosapi

| Aspecto | Estado | Calificación |
|---------|--------|--------------|
| Arquitectura Hexagonal | 40% (incompleta) | 2/5 |
| SOLID Compliance | Crítica | 2/5 |
| Testing | Muy Difícil | 1/5 |
| Mantenibilidad | Comprometida | 2/5 |
| **RECOMENDACIÓN** | **REFACTOR URGENTE** | **⚠️⚠️⚠️** |

**Acciones Inmediatas:**
1. Crear implementaciones de repositorio (semana 1)
2. Migrar servicios a casos de uso (semanas 2-4)
3. Implementar tests unitarios
4. Validar arquitectura hexagonal correcta

#### crescio_mobile

| Aspecto | Estado | Calificación |
|---------|--------|--------------|
| Arquitectura Hexagonal | 60% (falta domain) | 3/5 |
| SOLID Compliance | Parcial | 3/5 |
| Testing | Moderado | 2.5/5 |
| Mantenibilidad | Aceptable | 3/5 |
| **RECOMENDACIÓN** | **MEJORAR ARQUITECTURA** | **⚠️** |

**Acciones Inmediatas:**
1. Crear capa de Domain (entities, use cases)
2. Refactor repositories para implementar interfaces
3. Agregar tests unitarios de use cases

#### consumogestionactivosapi

| Aspecto | Estado | Calificación |
|---------|--------|--------------|
| Arquitectura | N/A (cliente simple) | 4/5 |
| SOLID Compliance | N/A | 4/5 |
| Testing | Posible | 3/5 |
| Mantenibilidad | Buena | 4/5 |
| **RECOMENDACIÓN** | **MANTENER** | **✓** |

**Acciones Inmediatas:**
1. Agregar tests unitarios
2. Documentación de API

---

## Referencias y Patrones a Implementar

### Pattern: Dependency Inversion Correcta

```java
// 1. DOMINIO define el puerto (interfaz)
package dominio.repositorios;
public interface IEquiposRepositorio {
    Equipos guardar(Equipos equipo);
    Optional<Equipos> buscarPorId(int id);
}

// 2. APLICACIÓN depende del puerto
package aplicacion.casosuso;
public class CrearEquipoUseCaseImpl implements ICrearEquipoUseCase {
    private final IEquiposRepositorio repositorio; // Depende de INTERFAZ
    
    public Equipos crear(Equipos equipo) {
        // validar en dominio
        return repositorio.guardar(equipo);
    }
}

// 3. INFRAESTRUCTURA implementa el puerto
package infraestructura.repositorios;
@Repository
public class EquiposRepositorioImpl implements IEquiposRepositorio {
    @Autowired
    private IEquiposJpaRepositorio jpaRepositorio; // Depende DE ARRIBA (aplicación)
    
    @Override
    public Equipos guardar(Equipos equipo) {
        EquiposJpa jpa = mapToDB(equipo);
        EquiposJpa saved = jpaRepositorio.save(jpa);
        return mapToDomain(saved);
    }
}
```

### Pattern: Clean Architecture (Flutter)

```dart
// feature/equipos/domain/entities/equipo.dart
class Equipo {
  final int id;
  final String codigo;
  
  const Equipo({required this.id, required this.codigo});
  
  int calcularPrioridad() {
    // Lógica de dominio PURA
  }
}

// feature/equipos/domain/usecases/get_equipos_usecase.dart
abstract class GetEquiposUseCase {
  Future<List<Equipo>> call();
}

// feature/equipos/data/repositories/equipos_repository_impl.dart
@immutable
class EquiposRepositoryImpl implements IEquiposRepository {
  final ApiClient _apiClient;
  
  @override
  Future<List<Equipo>> listar() async {
    final json = await _apiClient.get('/equipos');
    return (json as List).map((j) => Equipo.fromJson(j)).toList();
  }
}
```

---

**Documento Finalizado: 24 de marzo de 2026**

**Próximos Pasos:**
1. Revisar este análisis con equipo de desarrollo
2. Priorizar tareas según matriz
3. Crear tickets en sistema de control de versiones
4. Asignar recursos
5. Ejecutar refactoring en iteraciones

**Última Actualización:** 2026-03-24
