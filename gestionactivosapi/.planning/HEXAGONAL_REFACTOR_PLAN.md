# Hexagonal Refactoring Plan - GestionActivosAPI

## Executive Summary

This document is a **7-phase, 7-week executable plan** to refactor `gestionactivosapi` from 40% architectural compliance (with SOLID violations) to a **clean hexagonal architecture** fully compliant with SOLID principles.

**Current State:**
- Partial hexagonal structure (aplicacion, dominio, infraestructura, presentacion)
- Critical violations: Services inject concrete JPA repositories (DIP), mixed responsibilities (SRP), DTO leakage into domain
- Tech stack: Spring Boot 4.0.2, Java 17, Maven, MapStruct, Lombok, PostgreSQL

**Target State:**
- Pure hexagonal layers (Domain → Application → Infrastructure ← Adapters)
- DIP: All dependencies point inward; services depend on domain ports, not concrete repositories
- SRP: Single responsibility per class; clear separation Domain/Application/Infrastructure
- Scalable, testable, maintainable architecture
- Full SOLID compliance

**Timeline:** 7 weeks (2 weeks per phase, 2-3 tasks per phase)

---

## Phase 1: Architecture Design & Refactoring Strategy
**Week 1** | 3 tasks | Autonomous | No code changes yet

### Objectives
Establish the target architecture blueprint, identify all coupling points, and create refactoring roadmap.

### Critical Questions to Answer
1. **Port Segregation:** Which ports do we need? (e.g., `EquipoRepositorioPuerto`, `NotificacionPuerto`, `ArchivoGeneradorPuerto`)
2. **Entity Mapping:** How to separate Domain entities from JPA entities cleanly?
3. **Service Refactoring:** Which services implement use cases, and which are infrastructure utilities?
4. **Impact Analysis:** Which classes are affected by each refactoring step?

### Tasks

#### Task 1.1: Current Architecture Analysis & Coupling Map
- **Type:** auto
- **Duration:** 1 day
- **Objective:** Document precisely where DIP is violated and create a map of dependencies
- **Action:**
  - Scan all `aplicacion/servicios/*.java` files; identify every `@Autowired`/constructor injection
  - For each service, list: (1) Domain repositories injected, (2) Infrastructure repositories injected, (3) DTOs imported
  - Document patterns: How many services directly use `IXxxJpaRepositorio`? (should be 0 in hexagonal)
  - Create spreadsheet: Service | DomainPorts | Infra Repos | DTOs | DIP Risk (HIGH/MEDIUM/LOW)
  - Identify "pivot" entities: Equipos, Mantenimientos, Custodios (most central, refactor first)
- **Output:**
  - `ARCHITECTURE_ANALYSIS.md` with findings
  - `COUPLING_MAP.xlsx` with dependency matrix
  - `PIVOT_ENTITIES.md` (which to refactor first)
- **Verify:** 
  - Can list all services and their concrete dependencies
  - Spreadsheet covers 80%+ of services
  - Pivot entities identified with rationale

#### Task 1.2: Target Hexagonal Architecture Blueprint
- **Type:** auto
- **Duration:** 1.5 days
- **Objective:** Design the clean architecture with all layers defined
- **Action:**
  - Define port interfaces (no implementation details)
    - **Domain Ports:** `XxxRepositorioPuerto` (persistence), `NotificacionPuerto`, `ArchivoGeneradorPuerto`, etc.
    - **Application Service signature:** Each use case class with clear input/output contracts
    - Separate "coming in" ports (driven by presentation) vs "going out" ports (calling infrastructure)
  - Design entity mapping strategy:
    - Domain entities: `Equipo`, `Mantenimiento` (business logic, no JPA annotations)
    - JPA entities: `EquipoJpa`, `MantenimientoJpa` (persistence only, JPA annotations)
    - Mappers: MapStruct mappers between Domain ↔ JPA
  - Document package structure (final state):
    ```
    com.uisrael.gestionactivosapi
    ├── dominio
    │   ├── entidades
    │   ├── valoresobjeto
    │   ├── puertos  (NEW)
    │   └── servicios  (NEW domain services, no Spring)
    ├── aplicacion
    │   ├── casosuso
    │   │   ├── entradas (interfaces - keep)
    │   │   └── impl (use case implementations)
    │   └── servicios (application services only)
    ├── infraestructura
    │   ├── persistencia
    │   │   ├── adaptadores (NEW - adapt ports to JPA)
    │   │   ├── jpa
    │   │   └── mapeadores
    │   ├── repositorios (implementations of domain ports)
    │   └── [other adapters]
    └── presentacion
        ├── controladores
        └── dto
    ```
  - List all ports needed (minimum 12-15)
- **Output:** `ARCHITECTURE_BLUEPRINT.md` with visual diagrams (ASCII art) and detailed definitions
- **Verify:**
  - Blueprint defines all layers clearly
  - Port interfaces listed with methods
  - Package structure complete
  - Entity mapping strategy documented

#### Task 1.3: Refactoring Roadmap & Dependency Graph
- **Type:** auto
- **Duration:** 1 day
- **Objective:** Create execution order to avoid mid-refactoring broken code
- **Action:**
  - Analyze task dependencies:
    - CreatePorts depends on: Domain entity definitions (they exist, so no blocker)
    - RefactorServices depends on: Ports created
    - CreateAdapters depends on: Ports created + Domain entities updated
    - Tests depend on: All above
  - Determine refactoring order for entities/services:
    - **Wave 1 (Foundation):** Create ports + update base domain entities
    - **Wave 2 (Core Services):** Refactor 3-5 pivot services (Equipos, Mantenimientos, Custodios)
    - **Wave 3 (Adapters):** Create persistence adapters for Wave 2 services
    - **Wave 4 (Remaining):** Refactor remaining services and adapters
  - Estimate effort per task (hours of Claude execution, not human time)
  - Identify "safe refactor zones" (classes with few incoming dependencies) vs "dangerous zones" (widely used)
- **Output:** `ROADMAP.md` with phase breakdown, `DEPENDENCY_GRAPH.txt` with ASCII visualizations
- **Verify:**
  - Roadmap shows phases in correct order
  - No circular dependencies in graph
  - Refactoring approach documented

### Success Criteria for Phase 1
- [ ] All current services analyzed and documented (no surprises)
- [ ] Target architecture clear and visualized
- [ ] Ports defined and named (minimum list: 15+ ports)
- [ ] Entity mapping strategy decided (Domain vs JPA approach)
- [ ] Refactoring waves identified with clear sequence
- [ ] Risk areas flagged and mitigation strategy documented

---

## Phase 2: Port Interfaces & Domain Layer Enhancement
**Week 2** | 3 tasks | Autonomous | Core of architecture definition

### Objectives
Define all outgoing/incoming ports as clean interfaces, establish domain entities without infrastructure coupling.

### Key Principles
- **Pure Domain:** No Spring annotations, no JPA imports in domain layer
- **Port Segregation:** Separate interfaces for each concern (Repository, Notification, FileGeneration, etc.)
- **Interface Segregation:** Ports are minimal, focused (not god interfaces)

### Tasks

#### Task 2.1: Create Domain Port Interfaces
- **Type:** auto
- **Duration:** 2 days
- **Objective:** Define all repository and service ports that domain/application will depend on
- **Action:**
  Create new package: `dominio/puertos/`
  
  **Repository Ports** (one per aggregate):
  - `EquipoRepositorioPuerto` with methods: `guardar(Equipo)`, `obtenerPorId()`, `obtenerTodos()`, `actualizar()`, `eliminar()`, `existePorNombre()`
  - `MantenimientoRepositorioPuerto` with methods: `guardar()`, `obtenerPorId()`, `obtenerPorEquipo()`, `actualizar()`, etc.
  - `CustodioRepositorioPuerto`, `UsuarioRepositorioPuerto`, `UbicacionRepositorioPuerto`, `TicketRepositorioPuerto`, `RolRepositorioPuerto`, etc. (one per entity)
  
  **Specialized Service Ports** (for cross-cutting concerns):
  - `NotificacionPuerto`: `enviarEmail()`, `enviarSms()`
  - `ArchivoGeneradorPuerto`: `generarPdf()`, `generarExcel()`
  - `SchedulerPuerto`: `programarTarea()`
  - `AlmacenamientoPuerto`: `guardarArchivo()`, `obtenerArchivo()`, `eliminarArchivo()`
  - `LogAuditoriaPorto`: `registrarAccion()`
  
  **For each port interface:**
  - Use domain entities as parameters, NOT DTOs
  - No Spring imports (no @Autowired, @Qualifier)
  - Clear, small contracts (Interface Segregation Principle)
  - Document with JavaDoc what each method must guarantee
  
  **Example structure for one port:**
  ```java
  package com.uisrael.gestionactivosapi.dominio.puertos;
  
  import java.util.Optional;
  import java.util.List;
  import com.uisrael.gestionactivosapi.dominio.entidades.Equipo;
  
  public interface EquipoRepositorioPuerto {
      /**
       * Persists and returns the saved entity with ID assigned.
       * Throws IllegalArgumentException if entity violates domain rules.
       */
      Equipo guardar(Equipo equipo);
      
      Optional<Equipo> obtenerPorId(Integer id);
      List<Equipo> obtenerTodos();
      // ... etc
  }
  ```
  
- **Output:** 15+ port interface files in `dominio/puertos/`
- **Verify:**
  - No Spring imports in any port file
  - No DTO imports (use domain entities only)
  - All ports implement Interface Segregation (no god interfaces)
  - Method signatures use domain entities exclusively

#### Task 2.2: Update Domain Entities to Support Values Objects & Encapsulation
- **Type:** auto
- **Duration:** 1.5 days
- **Objective:** Strengthen domain entities to enforce business rules; prepare for separation from JPA
- **Action:**
  - Identify domain entities (Equipo, Mantenimiento, Custodio, Usuario, etc.)
  - For each entity:
    1. **Add value objects** where applicable (e.g., `Email`, `Nombre`, `EstadoEquipo` as enum)
    2. **Add domain logic methods** (e.g., `cambiarEstado()`, `validarAsignacion()`, `calcularProximaMantencion()`)
    3. **Remove JPA-specific annotations** from logic (but keep model structure intact for now; we'll create JPA twins in Phase 3)
    4. **Add constructors and validation** to enforce invariants
    5. Document **Aggregate roots** and bounded contexts
  - Example refactoring for `Equipo.java`:
    ```java
    public class Equipo {
        private Integer idEquipo;
        private Nombre nombre;  // Value object
        private Email emailResponsable; // Value object
        private EstadoEquipo estado; // Enum value object
        private List<Mantenimiento> mantenimientos = new ArrayList<>();
        
        // Remove getters that were only for JPA
        // Add domain methods
        public void cambiarEstado(EstadoEquipo nuevoEstado) {
            if (!puedeTransicionarA(nuevoEstado)) {
                throw new TransicionEstadoIlegalException(...);
            }
            this.estado = nuevoEstado;
        }
        
        public void asignarMantenimiento(Mantenimiento mtto) {
            if (!estoyCatalogoParaMtto()) {
                throw new EquipoNoAplica...();
            }
            this.mantenimientos.add(mtto);
        }
    }
    ```
  - **Preserve JPA annotations for now** if they're on fields (we're preparing, not breaking)
  - But move business validation OUT of persistence concerns
  
- **Output:** Updated domain entity files with enhanced encapsulation and business logic
- **Verify:**
  - [ ] Each entity has 2+ domain logic methods (not just getters/setters)
  - [ ] Value objects created for key concepts (Email, Nombre, Estado)
  - [ ] Validation enforced in constructors/setters (not in services)
  - [ ] Domain logic is testable without Spring/JPA

#### Task 2.3: Create Domain Exceptions & Events (Optional but Recommended)
- **Type:** auto
- **Duration:** 0.5 days
- **Objective:** Establish domain-level error handling and event mechanism
- **Action:**
  - Create package: `dominio/excepciones/`
  - Define base exception: `ExcepcionDominio` extends Exception (not RuntimeException, enforces handling)
  - For each domain concept, create specific exceptions:
    - `EquipoNoEncontradoException`
    - `MantenimientoYaExisteException`
    - `TransicionEstadoIlegalException`
    - `ValidacionNuegoException` (generic for validation)
  - (Optional) Create `dominio/eventos/` for domain events (useful for async operations later)
  
- **Output:** 10+ domain exception classes
- **Verify:**
  - All exceptions are domain-specific, not infrastructure
  - Clear error messages for debugging

### Success Criteria for Phase 2
- [ ] 15+ port interfaces created and clean (no Spring annotations)
- [ ] All repository ports follow same interface pattern
- [ ] Service/adapter ports segregated by concern
- [ ] Domain entities enhanced with business logic (not just data containers)
- [ ] Value objects created for key concepts
- [ ] Domain validation enforced (not in services)
- [ ] Domain exceptions established

---

## Phase 3: Refactor Core Services to Use Ports (DIP Compliance)
**Week 3** | 3 tasks | Autonomous | Implementing Dependency Inversion

### Objectives
Refactor services to depend on domain ports, not concrete repositories. Implement clean use case classes.

### Key Principle: **Dependency Inversion**
Services depend on abstractions (ports), not concrete implementations. Implementations depend on services to use the ports.

### Tasks

#### Task 3.1: Implement Use Case Classes (Application Layer)
- **Type:** auto + TDD
- **Duration:** 2 days
- **Objective:** Replace mixed service classes with clean, single-responsibility use cases
- **Action:**
  Pick **3 pivot use cases** to refactor (Equipos, Mantenimientos, Custodios):
  
  For each use case entity (e.g., "CrearEquipo"):
  
  1. **Create interface** (keep existing in `aplicacion/casosuso/entradas/`):
     ```java
     public interface ICrearEquipoUseCase {
         EquipoResponseDTO ejecutar(CrearEquipoRequestDTO request);
     }
     ```
  
  2. **Create implementation** in new package: `aplicacion/casosuso/impl/`:
     ```java
     @Component  // Spring component
     @RequiredArgsConstructor
     public class CrearEquipoUseCaseImpl implements ICrearEquipoUseCase {
         
         // DEPEND ON PORTS, NOT JPA REPOS
         private final EquipoRepositorioPuerto equipoRepo;  // Port, not JpaRepository
         private final NotificacionPuerto notificacion;
         private final CrearEquipoMapper mapper;  // MapStruct for DTO ↔ Domain
         
         @Override
         @Transactional
         public EquipoResponseDTO ejecutar(CrearEquipoRequestDTO request) {
             // 1. Validate request
             validarDatos(request);
             
             // 2. Create domain entity
             Equipo equipoNuevo = new Equipo(
                 request.getNombre(),
                 request.getDescripcion(),
                 // ... domain properties
             );
             
             // 3. Apply business rules
             if (equipoNuevo.requiereAprobacion()) {
                 equipoNuevo.marcarPendienteAprobacion();
             }
             
             // 4. Persist using port (abstraction)
             Equipo equipoGuardado = equipoRepo.guardar(equipoNuevo);
             
             // 5. Side effects (through ports, not direct services)
             notificacion.enviarEmailCreacionEquipo(equipoGuardado);
             
             // 6. Return DTO
             return mapper.toResponseDTO(equipoGuardado);
         }
     }
     ```
  
  3. **Key patterns:**
     - Constructor injection of ports (final, RequiredArgsConstructor)
     - Single @Transactional on use case method (not scattered)
     - Clear separation: Input validation → Domain logic → Persistence → Side effects → Output
     - All parameters are domain entities or primitives (no JPA entities)
     - Exception handling maps domain exceptions → HTTP response codes (in Controller, not here)
  
  4. **Pattern for all CRUD operations:**
     - **Create:** Validate → New entity → Check rules → Save → Notify → Return DTO
     - **Read:** Fetch via port → Map to DTO → Return
     - **Update:** Fetch → Apply changes via domain methods → Save → Notify → Return DTO
     - **Delete:** Fetch → Check rules (can delete?) → Delete via port → Notify → Return
  
  5. **Refactor 3 main services:**
     - `EquiposUseCase` family (CRUD + list operations)
     - `MantenimientosUseCase` family (Create, List, GetById)
     - `CustodiosUseCase` family (Associate, List)
  
  For each use case, create corresponding mapper (if not exists):
  - `EquipoMapper`: Equipo (domain) ↔ EquipoResponseDTO
  - `MantenimientoMapper`: Mantenimiento ↔ MantenimientoResponseDTO
  
- **Output:**
  - 15-20 new use case implementation classes in `aplicacion/casosuso/impl/`
  - MapStruct mappers (may reuse existing if compatible)
  - Old mixed services LEFT UNTOUCHED (will be removed in Phase 5)
  
- **Verify:**
  ```bash
  # After implementation, verify:
  # 1. No @Autowired of JpaRepository in new use cases
  # 2. Only inject ports (from dominio/puertos/)
  # 3. MapStruct compiles without errors
  grep -r "@Autowired.*JpaRepositorio" src/main/java/com/uisrael/gestionactivosapi/aplicacion/casosuso/impl/
  # Should return 0 matches
  ```

#### Task 3.2: Update Controllers to Use New Use Cases
- **Type:** auto
- **Duration:** 1.5 days
- **Objective:** Wire controllers to new use case implementations (instead of old mixed services)
- **Action:**
  - For each controller (e.g., `EquiposController`):
    1. Replace old service injection with new use case injection:
       ```java
       // OLD
       @Autowired
       private EquipoService equipoService;  // Mixed service
       
       // NEW
       @Autowired
       private ICrearEquipoUseCase crearEquipo;
       @Autowired
       private IObtenerEquiposUseCase obtenerEquipos;
       // ... one interface per use case
       ```
    2. Update method calls:
       ```java
       // OLD
       equipoService.crear(request);  // What does it do? Unclear
       
       // NEW
       crearEquipo.ejecutar(request);  // Clear intent
       ```
    3. Update exception handling in `@ExceptionHandler`:
       - Domain exceptions → HTTP 400/409/422 Bad Request
       - `RecursoNoEncontradoException` → 404
       - `ExcepcionDominio` → 500
  
  - Update 5-7 controller classes (those using refactored services)
  - Add exception handlers for domain exceptions
  
- **Output:** Controllers wired to use case interfaces instead of old services
- **Verify:**
  - [ ] Controllers inject use case interfaces (IXxxUseCase)
  - [ ] Old service injections removed
  - [ ] Method calls use `case.ejecutar(request)` pattern
  - [ ] Build succeeds: `mvn clean compile`

#### Task 3.3: Create Integration Tests for Refactored Use Cases
- **Type:** auto + TDD
- **Duration:** 1 day
- **Objective:** Verify use cases work end-to-end with mock ports
- **Action:**
  - Create test package: `src/test/java/.../aplicacion/casosuso/impl/`
  - For each refactored use case, write integration test:
  
  ```java
  @SpringBootTest
  @Transactional
  class CrearEquipoUseCaseTest {
      
      @MockBean
      private EquipoRepositorioPuerto equipoRepo;
      
      @MockBean
      private NotificacionPuerto notificacion;
      
      @Autowired
      private CrearEquipoUseCase useCase;
      
      @Test
      void testCrearEquipoValido() {
          // Arrange
          CrearEquipoRequestDTO request = new CrearEquipoRequestDTO(...);
          Equipo equipoEsperado = new Equipo(...);
          when(equipoRepo.guardar(any())).thenReturn(equipoEsperado);
          
          // Act
          EquipoResponseDTO resultado = useCase.ejecutar(request);
          
          // Assert
          assertThat(resultado.getId()).isEqualTo(equipoEsperado.getId());
          verify(equipoRepo).guardar(any());
          verify(notificacion).enviarEmailCreacionEquipo(equipoEsperado);
      }
      
      @Test
      void testCrearEquipoInvalido_LanzaExcepcion() {
          // Arrange
          CrearEquipoRequestDTO requestInvalido = new CrearEquipoRequestDTO(null, "");
          
          // Act & Assert
          assertThrows(ValidacionNuegoException.class, 
              () -> useCase.ejecutar(requestInvalido));
      }
  }
  ```
  
  - Write 3-5 test scenarios per use case:
    - Happy path (valid data)
    - Invalid input (validation)
    - Business rule violation
    - Side effect verification (mock notifications called)
  
- **Output:** Test classes with 10+ test cases per use case
- **Verify:**
  - [ ] All tests pass: `mvn test -Dtest=*UseCaseTest`
  - [ ] Mock port calls verified
  - [ ] Domain exceptions caught and handled

### Success Criteria for Phase 3
- [ ] 3+ pivot services completely refactored to use cases
- [ ] Use cases depend on ports (0 imports of JpaRepository)
- [ ] Controllers wired to new use cases
- [ ] 10+ test cases for new use cases
- [ ] Build succeeds: `mvn clean test`
- [ ] Old mixed services still work (no breaking changes yet)

---

## Phase 4: Create Persistence Adapters (Implement Ports)
**Week 4** | 3 tasks | Autonomous | Infrastructure Layer Implementation

### Objectives
Implement the ports defined in Phase 2 with Spring/JPA adapters. Decouple domain from infrastructure.

### Key Principle
Adapters implement ports, not the other way around. Services don't know adapters exist; they only know ports.

### Tasks

#### Task 4.1: Create Adapter Classes for Repository Ports
- **Type:** auto
- **Duration:** 2 days
- **Objective:** Implement domain repository ports using JPA
- **Action:**
  Create new package: `infraestructura/adaptadores/`
  
  For each domain repository port, create an adapter:
  ```java
  package com.uisrael.gestionactivosapi.infraestructura.adaptadores;
  
  import org.springframework.stereotype.Repository;
  import com.uisrael.gestionactivosapi.dominio.puertos.EquipoRepositorioPuerto;
  import com.uisrael.gestionactivosapi.dominio.entidades.Equipo;
  
  @Repository
  public class EquipoRepositorioAdapter implements EquipoRepositorioPuerto {
      
      private final IEquiposJpaRepositorio jpaRepo;
      private final EquipoMapperJpa mapper;  // Maps Equipo ↔ EquipoJpa
      
      public EquipoRepositorioAdapter(IEquiposJpaRepositorio jpaRepo, EquipoMapperJpa mapper) {
          this.jpaRepo = jpaRepo;
          this.mapper = mapper;
      }
      
      @Override
      public Equipo guardar(Equipo equipo) {
          EquipoJpa jpa = mapper.toJpa(equipo);
          EquipoJpa guardado = jpaRepo.save(jpa);
          return mapper.toDomain(guardado);
      }
      
      @Override
      public Optional<Equipo> obtenerPorId(Integer id) {
          return jpaRepo.findById(id)
              .map(mapper::toDomain);
      }
      
      @Override
      public List<Equipo> obtenerTodos() {
          return jpaRepo.findAll()
              .stream()
              .map(mapper::toDomain)
              .collect(Collectors.toList());
      }
      
      @Override
      public void eliminar(Integer id) {
          jpaRepo.deleteById(id);
      }
  }
  ```
  
  **Key patterns:**
  - Adapter is `@Repository` (Spring component)
  - Injects `JpaRepository` (infrastructure detail)
  - Implements domain port
  - Uses mapper to convert JpaEntity ↔ Domain entity
  - Domain business logic is NOT in adapter; adapter only handles persistence mechanics
  
  Create adapters for:
  - `EquipoRepositorioAdapter`
  - `MantenimientoRepositorioAdapter`
  - `CustodioRepositorioAdapter`
  - `UsuarioRepositorioAdapter`
  - `UbicacionRepositorioAdapter`
  - Plus others as needed (15-20 total)
  
- **Output:** 15+ adapter classes in `infraestructura/adaptadores/`
- **Verify:**
  - [ ] Each adapter implements exactly one port
  - [ ] No business logic in adapters (only mapping + JPA calls)
  - [ ] No circular dependencies (adapters depend on JPA + mappers, never on services)

#### Task 4.2: Create MapStruct Mappers (Equipo ↔ EquipoJpa)
- **Type:** auto
- **Duration:** 1 day
- **Objective:** Define mappings between Domain and JPA entities
- **Action:**
  Create new mappers in `infraestructura/persistencia/mapeadores/`:
  
  ```java
  @Mapper(componentModel = "spring")
  public interface EquipoMapperJpa {
      
      EquipoJpa toJpa(Equipo domain);
      
      Equipo toDomain(EquipoJpa jpa);
      
      // For nested objects
      @Mapping(target = "equipoJpa", source = "equipo")
      MantenimientoJpa toJpa(Mantenimiento domain);
      
      @Mapping(target = "equipo", source = "equipoJpa")
      Mantenimiento toDomain(MantenimientoJpa jpa);
  }
  ```
  
  Create mappers for all pivot entities:
  - `EquipoMapperJpa`
  - `MantenimientoMapperJpa`
  - `CustodioMapperJpa`
  - etc.
  
  **Key considerations:**
  - MapStruct generates Spring components automatically (componentModel = "spring")
  - Handle nested object mappings (e.g., Equipo has List<Mantenimiento>)
  - Use `@Mapping` for non-obvious conversions
  - Test mappers with unit tests
  
- **Output:** 10+ MapStruct mapper interfaces
- **Verify:**
  - [ ] Mappers compile: `mvn clean compile`
  - [ ] Generated mapper classes created in `target/generated-sources/`
  - [ ] No compilation warnings for mappers

#### Task 4.3: Wire Adapters to Spring Configuration
- **Type:** auto
- **Duration:** 1 day
- **Objective:** Register adapters in Spring so use cases receive implementations
- **Action:**
  - Create or update `infraestructura/configuracion/PersistenciaConfig.java`:
    ```java
    @Configuration
    public class PersistenciaConfig {
        
        @Bean
        public EquipoRepositorioPuerto equipoRepositorioPuerto(
            IEquiposJpaRepositorio jpaRepo, 
            EquipoMapperJpa mapper) {
            return new EquipoRepositorioAdapter(jpaRepo, mapper);
        }
        
        @Bean
        public MantenimientoRepositorioPuerto mantenimientoRepositorioPuerto(
            IMantenimientosJpaRepositorio jpaRepo, 
            MantenimientoMapperJpa mapper) {
            return new MantenimientoRepositorioAdapter(jpaRepo, mapper);
        }
        
        // ... etc for all ports
    }
    ```
  
  - Alternatively, use `@Component` on adapters (Spring auto-discovers them)
  - Verify Spring can inject ports into use cases (no multiple bean conflicts)
  
  **Testing the wiring:**
  - Create a simple integration test:
    ```java
    @SpringBootTest
    class AdapterWiringTest {
        @Autowired
        private EquipoRepositorioPuerto equipoRepo;  // Should resolve to adapter
        
        @Test
        void testPortCanBeInjected() {
            assertThat(equipoRepo).isNotNull();
            assertThat(equipoRepo).isInstanceOf(EquipoRepositorioAdapter.class);
        }
    }
    ```

- **Output:** Spring configuration that registers all adapters
- **Verify:**
  - [ ] `mvn clean test` passes for wiring test
  - [ ] `mvn spring-boot:run` starts without "No qualifying bean" errors
  - [ ] Application context loads all adapters

### Success Criteria for Phase 4
- [ ] 15+ port implementations created (adapters)
- [ ] All adapters in `infraestructura/adaptadores/`
- [ ] MapStruct mappers for all pivot entities
- [ ] Spring configuration wires adapters as port implementations
- [ ] Zero JPA imports in domain or application layers
- [ ] Build succeeds: `mvn clean test`

---

## Phase 5: Specialization Adapters (Non-Repository Ports)
**Week 5** | 2-3 tasks | Autonomous | Completing Infrastructure Layer

### Objectives
Implement non-repository ports (Notification, FileGeneration, Scheduler, etc.)

### Tasks

#### Task 5.1: Implement Notification Adapter
- **Type:** auto
- **Duration:** 1 day
- **Objective:** Create email/SMS notification adapter
- **Action:**
  - Implement `NotificacionPuerto` interface with Spring's `MailSender` or external service
  - Create `NotificacionAdapter` in `infraestructura/adaptadores/`
  - For each notification type, create method:
    ```java
    @Service
    public class NotificacionAdapter implements NotificacionPuerto {
        private final MailSender mailSender;
        private final SmsService smsService;  // (if applicable)
        
        public void enviarEmail(String destinatario, String asunto, String cuerpo) {
            // Use Spring's MailSender or external API
        }
    }
    ```
  - Create notification templates in `resources/templates/email/`
  - Handle errors gracefully (log but don't crash use cases)

- **Output:** Notification adapter with email support
- **Verify:** Adapter can be injected into use cases

#### Task 5.2: Implement File Generation Adapter
- **Type:** auto
- **Duration:** 1 day
- **Objective:** Create PDF/Excel generation adapter
- **Action:**
  - Implement `ArchivoGeneradorPuerto` with iText (PDF) and Apache POI (Excel)
  - Create `ArchivoGeneradorAdapter` in `infraestructura/adaptadores/`
  - Methods:
    ```java
    public byte[] generarPdf(MantenimientoData data) {
        // Use iText to generate PDF
    }
    
    public byte[] generarExcel(List<EquipoData> equipos) {
        // Use POI to generate Excel
    }
    ```
  - Create templates for reports
  - Handle file storage (if needed) via `AlmacenamientoPuerto`

- **Output:** File generation adapter with PDF/Excel support
- **Verify:** Adapter generates valid files

#### Task 5.3: Create Remaining Adapters (Scheduler, Audit Logging, etc.)
- **Type:** auto
- **Duration:** 0.5 days
- **Objective:** Implement lesser-priority ports
- **Action:**
  - `SchedulerAdapter` (using Spring's `@Scheduled`)
  - `LogAuditoriaAdapter` (logging user actions)
  - `AlmacenamientoAdapter` (file storage - local or S3)
  - Any other domain ports from Phase 2 not yet implemented

- **Output:** Skeleton adapters (may not be fully featured initially)
- **Verify:** All adapters can be injected

### Success Criteria for Phase 5
- [ ] All non-repository ports implemented
- [ ] Notification adapter sends emails
- [ ] File generation adapter creates PDF/Excel
- [ ] No "No qualifying bean" errors on startup
- [ ] Use cases can call all port methods

---

## Phase 6: JPA Entity Separation & Cleanup
**Week 6** | 2 tasks | Autonomous | Completing Infrastructure

### Objectives
Create separate JPA entities (EquipoJpa, MantenimientoJpa) parallel to domain entities; remove JPA coupling from domain classes.

### Tasks

#### Task 6.1: Create Twin JPA Entities (Equipo vs EquipoJpa)
- **Type:** auto
- **Duration:** 1.5 days
- **Objective:** Create persistence-specific entities separate from domain
- **Action:**
  - For each domain entity (Equipo, Mantenimiento, Custodio, etc.), create a JPA twin:
    ```java
    // Domain entity - NO JPA annotations
    public class Equipo {
        private Integer id;
        private String nombre;
        private List<Mantenimiento> mantenimientos;
    }
    
    // JPA entity - ONLY JPA annotations and database concerns
    @Entity
    @Table(name = "equipos")
    public class EquipoJpa {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer idEquipo;
        
        @Column(name = "nombre")
        private String nombre;
        
        @OneToMany(mappedBy = "equipoJpa")
        private List<MantenimientoJpa> mantenimientos;
    }
    ```
  
  - Move all JPA annotations from domain entities to JPA twins
  - Domain entities remain POJOs (Plain Old Java Objects)
  - JPA entities go in `infraestructura/persistencia/jpa/` (existing location)
  - Use MapStruct to bridge: Equipo ↔ EquipoJpa
  
  **Impact analysis:**
  - This is a refactoring with high risk (database queries depend on JPA entities)
  - Run full test suite after: `mvn clean test`
  - Verify no broken queries in `infraestructura/repositorios/`
  
- **Output:** Clean domain entities (no JPA annotations); parallel JPA entities
- **Verify:**
  - [ ] Domain entities have NO @Entity, @Table, @Column, @OneToMany, etc.
  - [ ] JPA entities have all persistence annotations
  - [ ] Mappers convert between both
  - [ ] Existing tests still pass

#### Task 6.2: Remove Old Service Classes (if safe)
- **Type:** auto
- **Duration:** 0.5 days
- **Objective:** Delete mixed service layer now that use cases are implemented
- **Action:**
  - Check which old services are replaced by refactored use cases
  - For those fully replaced: Delete `aplicacion/servicios/OldService.java`
  - For partially used (some methods still needed): Keep but mark as "deprecated" and create migration path
  - Update all references to point to new use cases
  
  **Caution:** Ensure no code still references old services before deleting
  
- **Output:** Cleaner `aplicacion/servicios/` directory (no dead code)
- **Verify:**
  - [ ] Build succeeds: `mvn clean compile`
  - [ ] No "cannot find symbol" errors

### Success Criteria for Phase 6
- [ ] Domain entities have NO JPA annotations
- [ ] Parallel JPA entities created for all main aggregates
- [ ] All JPA queries still work
- [ ] MapStruct mappers convert correctly
- [ ] Tests pass: `mvn clean test`
- [ ] Old services removed (or marked deprecated with clear migration path)

---

## Phase 7: Testing, Documentation & Full Integration
**Week 7** | 3 tasks | Autonomous + Checkpoint | Final Validation

### Objectives
Comprehensive testing of refactored architecture; validate SOLID compliance; document migration.

### Tasks

#### Task 7.1: Create Architecture Test Suite (ArchUnit)
- **Type:** auto + TDD
- **Duration:** 1.5 days
- **Objective:** Enforce architectural rules programmatically (no regression)
- **Action:**
  - Add ArchUnit dependency to pom.xml (check library)
  - Create test class: `src/test/java/.../ArchitectureComlianceTest.java`
  - Write rules to enforce:
    1. **No JPA imports in domain layer:**
       ```java
       @ArchTest
       public static final ArchRule noJpaInDomain =
           noClasses().that().resideInAPackage("..dominio..")
               .should().dependOnClassesThat()
               .resideInAPackage("..jpa..");
       ```
    2. **Services only depend on ports, not JPA repos:**
       ```java
       @ArchTest
       public static final ArchRule servicesUsePortsNotJpa =
           classes().that().resideInAPackage("..aplicacion.casosuso.impl..")
               .should().onlyDependOnClassesThat()
               .resideInAPackage("..dominio.puertos..");
       ```
    3. **Adapters depend on ports (implement them):**
       ```java
       @ArchTest
       public static final ArchRule adaptersImplementPorts =
           classes().that().resideInAPackage("..infraestructura.adaptadores..")
               .should().implement(classes().that()
                   .resideInAPackage("..dominio.puertos.."));
       ```
    4. **No cyclic dependencies:**
       ```java
       @ArchTest
       public static final ArchRule noCycles =
           SlicesRulesDefinition.slices().matching("..(*)..")
               .should().beFreeOfCycles();
       ```
  
  - Run: `mvn test -Dtest=ArchitectureComlianceTest`
  - If fails, it highlights violations (forcing fixes before commit)

- **Output:** ArchUnit test class with 5+ architectural rules
- **Verify:**
  - [ ] All architecture rules pass
  - [ ] Violations are caught by ArchUnit

#### Task 7.2: Integration Tests (End-to-End Flows)
- **Type:** auto + TDD
- **Duration:** 1 day
- **Objective:** Test complete use case flows from Controller → Domain → Adapter
- **Action:**
  - Create `src/test/java/.../integration/`
  - Write E2E tests for full flows:
    1. **Create Equipo flow:**
       ```
       Controller receives request
       → Use case creates domain entity
       → Adapter persists to DB
       → Notification sent
       → Response returned to client
       ```
    2. **Update Mantenimiento flow:**
       ```
       Fetch existing → Domain logic applies changes
       → Adapter updates DB → Events published
       → Response returned
       ```
  
  - Use `@SpringBootTest` with test database
  - Mock external systems (email, file storage) but test real domain/adapter logic
  - Cover happy paths + error scenarios
  
  - Example:
    ```java
    @SpringBootTest
    @Transactional
    class CrearEquipoIntegrationTest {
        @Autowired
        private MockMvc mockMvc;
        
        @Autowired
        private EquipoRepositorioPuerto equipoRepo;
        
        @Test
        void testCrearEquipoCompleto() throws Exception {
            // Request
            CrearEquipoRequest request = new CrearEquipoRequest("Laptop", "Nueva");
            
            // Execute via HTTP
            MvcResult result = mockMvc.perform(post("/api/equipos")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();
            
            // Verify
            String body = result.getResponse().getContentAsString();
            EquipoResponse response = objectMapper.readValue(body, EquipoResponse.class);
            assertThat(response.getId()).isNotNull();
            
            // Verify persisted
            Equipo persisted = equipoRepo.obtenerPorId(response.getId()).get();
            assertThat(persisted.getNombre()).isEqualTo("Laptop");
        }
    }
    ```

- **Output:** 10+ integration tests covering main use case flows
- **Verify:**
  - [ ] All integration tests pass: `mvn test -Dtest=*IntegrationTest`

#### Task 7.3: SOLID Compliance Audit & Documentation
- **Type:** auto + Checkpoint: human-verify
- **Duration:** 1 day
- **Objective:** Verify SOLID principles, create evidence document
- **Action:**
  Create document: `.planning/SOLID_COMPLIANCE_REPORT.md`
  
  For each principle, provide evidence:
  
  1. **Single Responsibility Principle (SRP):**
     - Each class has ONE reason to change
     - Evidence: Classes in `aplicacion/casosuso/impl/` have one use case each; adapters handle one port
     - Show file counts: Domain (~20 files), Application (~25 files), Infrastructure (~30 files)
  
  2. **Open/Closed Principle (OCP):**
     - Open for extension, closed for modification
     - Evidence: New adapter types can be added without modifying existing code
     - Example: Adding new `NotificacionSmsAdapter` doesn't touch `NotificacionPort` or use cases
  
  3. **Liskov Substitution Principle (LSP):**
     - Implementations can replace abstractions
     - Evidence: Any `EquipoRepositorioPuerto` implementation works in use cases
     - Test: Swap adapter with mock; tests still pass
  
  4. **Interface Segregation Principle (ISP):**
     - Clients depend on small, focused interfaces
     - Evidence: Ports are minimal (5-8 methods max)
     - Show each port and its methods
  
  5. **Dependency Inversion Principle (DIP):**
     - High-level modules (domain/application) depend on abstractions (ports)
     - Low-level modules (adapters) implement abstractions
     - Evidence: Zero imports of `@Repository` in domain; all services inject ports
     - Grep results:
       ```bash
       grep -r "IEquiposJpaRepositorio" src/main/java/com/uisrael/gestionactivosapi/aplicacion/
       # Should return 0 matches (no direct JPA repo imports in application)
       ```
  
  - Include metrics:
    - Cyclomatic complexity (optional, via SonarQube if available)
    - Test coverage (target: 70%+ for domain/application, 50%+ for infrastructure)
    - Number of layers (should be 4: Domain, Application, Infrastructure, Presentation)
  
- **Output:** `SOLID_COMPLIANCE_REPORT.md` with evidence
- **Verify:** (Checkpoint) Developer reviews report and confirms compliance

### Success Criteria for Phase 7
- [ ] ArchUnit tests enforce architecture (all pass)
- [ ] 10+ integration tests cover main flows
- [ ] SOLID compliance documented with evidence
- [ ] Test coverage: Domain 80%+, Application 75%+, Infrastructure 60%+
- [ ] Build succeeds: `mvn clean test`
- [ ] All refactored services work in production simulation
- [ ] Architecture is ready for team explanation/handoff

---

## Timeline & Phasing Summary

| Phase | Week | Focus | Effort | Risk |
|-------|------|-------|--------|------|
| 1 | 1 | Design, Analysis, Blueprint | 3 tasks, Low code | Low |
| 2 | 2 | Ports, Domain Enhancement | 3 tasks, 40 files | Low |
| 3 | 3 | **Use Cases + DIP** (Core) | 3 tasks, 20 files | **Medium** |
| 4 | 4 | Persistence Adapters | 3 tasks, 20 files | Medium |
| 5 | 5 | Specialization Adapters | 2-3 tasks, 5 files | Low |
| 6 | 6 | JPA Separation (BIG change) | 2 tasks, 50+ files | **High** |
| 7 | 7 | Testing + Validation | 3 tasks, 15 files | Low |

**Critical Success Factors:**
1. Phase 2 (ports) must be complete before Phase 3 (use cases) — **blocking dependency**
2. Phase 3 refactoring must NOT break existing functionality — use parallel approach (new use cases alongside old services)
3. Phase 6 JPA separation is high-risk; run full test suite immediately after

---

## Implementation Guidelines

### Code Patterns

#### Pattern 1: Domain Entity with Business Logic
```java
public class Equipo {
    private Integer id;
    private String nombre;
    private String descripcion;
    private EstadoEquipo estado;
    private LocalDateTime fechaAdquisicion;
    
    // Constructor enforces invariants
    public Equipo(String nombre, String descripcion) {
        if (nombre == null || nombre.isBlank()) {
            throw new ValidacionNuegoException("El nombre es requerido");
        }
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = EstadoEquipo.DISPONIBLE;
        this.fechaAdquisicion = LocalDateTime.now();
    }
    
    // Domain method (no infrastructure)
    public void cambiarEstado(EstadoEquipo nuevoEstado) {
        if (!puedeTransicionarA(nuevoEstado)) {
            throw new TransicionEstadoIlegalException(
                "No se puede pasar de " + estado + " a " + nuevoEstado);
        }
        this.estado = nuevoEstado;
    }
    
    private boolean puedeTransicionarA(EstadoEquipo nuevoEstado) {
        return switch (estado) {
            case DISPONIBLE -> nuevoEstado == EstadoEquipo.ASIGNADO || nuevoEstado == EstadoEquipo.MANTENIMIENTO ||  nuevoEstado == EstadoEquipo.DESCARTADO;
            case ASIGNADO -> nuevoEstado == EstadoEquipo.DISPONIBLE || nuevoEstado == EstadoEquipo.MANTENIMIENTO || nuevoEstado == EstadoEquipo.DESCARTADO;
            // ...
            default -> false;
        };
    }
}
```

#### Pattern 2: Domain Port (Interface)
```java
public interface EquipoRepositorioPuerto {
    Equipo guardar(Equipo equipo);
    Optional<Equipo> obtenerPorId(Integer id);
    List<Equipo> obtenerTodos();
    List<Equipo> filtrarPorEstado(EstadoEquipo estado);
    void eliminar(Integer id);
}
```

#### Pattern 3: Use Case (Application Service)
```java
@Component
@RequiredArgsConstructor
@Transactional
public class CrearEquipoUseCaseImpl implements ICrearEquipoUseCase {
    private final EquipoRepositorioPuerto equipoRepo;
    private final NotificacionPuerto notificacion;
    private final CrearEquipoMapper mapper;
    
    @Override
    public EquipoResponseDTO ejecutar(CrearEquipoRequestDTO request) {
        // Validate input
        validador.validar(request);  // Can be a separate injected validator
        
        // Create domain entity
        Equipo equipo = new Equipo(request.getNombre(), request.getDescripcion());
        
        // Apply business rules (domain)
        if (equipoRequiereAprobacion(equipo)) {
            equipo.marcarPendienteAprobacion();
        }
        
        // Persist (through port abstraction)
        Equipo guardado = equipoRepo.guardar(equipo);
        
        // Side effects
        notificacion.enviarCreacionEquipo(guardado);
        
        // Return DTO
        return mapper.toResponseDTO(guardado);
    }
}
```

#### Pattern 4: Adapter (Implements Port)
```java
@Repository
@RequiredArgsConstructor
public class EquipoRepositorioAdapter implements EquipoRepositorioPuerto {
    private final IEquiposJpaRepositorio jpaRepo;
    private final EquipoMapperJpa mapper;
    
    @Override
    public Equipo guardar(Equipo equipo) {
        EquipoJpa jpa = mapper.toJpa(equipo);
        EquipoJpa guardado = jpaRepo.save(jpa);
        return mapper.toDomain(guardado);
    }
    
    @Override
    public Optional<Equipo> obtenerPorId(Integer id) {
        return jpaRepo.findById(id).map(mapper::toDomain);
    }
}
```

#### Pattern 5: MapStruct Mapper
```java
@Mapper(componentModel = "spring")
public interface EquipoMapperJpa {
    EquipoJpa toJpa(Equipo domain);
    Equipo toDomain(EquipoJpa jpa);
    
    @Mapping(target = "equipoId", source = "id")
    MantenimientoJpa toJpa(Mantenimiento domain);
    
    @Mapping(target = "id", source = "equipoId")
    Mantenimiento toDomain(MantenimientoJpa jpa);
}
```

#### Pattern 6: Use Case Test
```java
@SpringBootTest
@Transactional
class CrearEquipoUseCaseTest {
    @MockBean
    private EquipoRepositorioPuerto equipoRepo;
    
    @MockBean
    private NotificacionPuerto notificacion;
    
    @Autowired
    private CrearEquipoUseCase useCase;
    
    @Test
    void testCrearEquipoValido() {
        CrearEquipoRequestDTO request = new CrearEquipoRequestDTO("Laptop", "HP");
        Equipo equipo = new Equipo("Laptop", "HP");
        equipo.setId(1);
        
        when(equipoRepo.guardar(any())).thenReturn(equipo);
        
        EquipoResponseDTO resultado = useCase.ejecutar(request);
        
        assertThat(resultado.getId()).isEqualTo(1);
        assertThat(resultado.getNombre()).isEqualTo("Laptop");
        verify(notificacion).enviarCreacionEquipo(equipo);
    }
}
```

### File Naming Conventions

- **Domain entities:** `Equipo.java`, `Mantenimiento.java` (no suffix)
- **JPA entities:** `EquipoJpa.java`, `MantenimientoJpa.java` (suffix + "Jpa")
- **Ports (interfaces):** `EquipoRepositorioPuerto.java`, `NotificacionPuerto.java` (suffix + "Puerto")
- **Use case interfaces:** `ICrearEquipoUseCase.java`, `IObtenerEquipoUseCase.java` (prefix "I")
- **Use case implementations:** `CrearEquipoUseCaseImpl.java` (suffix + "Impl")
- **Adapters:** `EquipoRepositorioAdapter.java`, `NotificacionAdapter.java` (suffix + "Adapter")
- **Mappers:** `EquipoMapper.java` (DTO), `EquipoMapperJpa.java` (Entity mapping)
- **Tests:** `CrearEquipoUseCaseTest.java`, `EquipoRepositorioAdapterTest.java` (suffix + "Test")

### Dependency Injection Flow

```
Presentation (Controller)
    ↓ injects
Application (IXxxUseCase interface)
    ↓ injects
Domain (XxxRepositorioPuerto interface)
    ↓ implemented by
Infrastructure (XxxRepositorioAdapter)
    ↓ uses
JPA (IXxxJpaRepositorio, XxxJpa entity)
```

### Maven Project Structure (Final)
```
src/main/java/com/uisrael/gestionactivosapi/
├── GestionactivosapiApplication.java
├── dominio/
│   ├── entidades/
│   │   ├── Equipo.java
│   │   ├── Mantenimiento.java
│   │   ├── ... (domain entities, NO JPA annotations)
│   ├── valoresobjeto/
│   │   ├── EstadoEquipo.java
│   │   ├── ... (enums, value objects)
│   ├── puertos/
│   │   ├── EquipoRepositorioPuerto.java
│   │   ├── MantenimientoRepositorioPuerto.java
│   │   ├── NotificacionPuerto.java
│   │   ├── ... (all port interfaces)
│   ├── excepciones/
│   │   ├── ExcepcionDominio.java
│   │   ├── ValidacionNuegoException.java
│   │   ├── ... (domain-specific exceptions)
│   └── servicios/  (optional: domain services for complex logic)
├── aplicacion/
│   ├── casosuso/
│   │   ├── entradas/
│   │   │   ├── ICrearEquipoUseCase.java
│   │   │   ├── ... (interface definitions - keep existing)
│   │   └── impl/
│   │       ├── CrearEquipoUseCaseImpl.java
│   │       ├── ObtenerEquiposUseCaseImpl.java
│   │       ├── ... (implementations)
│   └── servicios/
│       ├── ... (application services, may be deprecated)
├── infraestructura/
│   ├── adaptadores/
│   │   ├── EquipoRepositorioAdapter.java
│   │   ├── MantenimientoRepositorioAdapter.java
│   │   ├── NotificacionAdapter.java
│   │   ├── ... (all adapter implementations)
│   ├── persistencia/
│   │   ├── adaptadores/  (legacy location, may move to global adaptadores/)
│   │   ├── jpa/
│   │   │   ├── EquipoJpa.java
│   │   │   ├── MantenimientoJpa.java
│   │   │   ├── ... (JPA entities with annotations)
│   │   └── mapeadores/
│   │       ├── EquipoMapperJpa.java
│   │       ├── ... (MapStruct mappers)
│   ├── repositorios/
│   │   ├── IEquiposJpaRepositorio.java
│   │   ├── ... (Spring Data JpaRepository interfaces)
│   ├── seguridad/
│   ├── configuracion/
│   │   ├── PersistenciaConfig.java
│   │   └── ... (Spring configuration)
│   └── ... (other infrastructure layers)
└── presentacion/
    ├── controladores/
    │   ├── EquiposController.java
    │   ├── ... (HTTP endpoints)
    ├── dto/
    │   ├── request/
    │   │   ├── CrearEquipoRequestDTO.java
    │   │   └── ...
    │   └── response/
    │       ├── EquipoResponseDTO.java
    │       └── ...
    ├── mapeadores/
    │   ├── EquipoMapper.java (DTO mapping)
    │   └── ...
    └── validacion/
```

---

## Validation Checkpoints

### After Phase 1
- [ ] Current architecture documented (no surprises)
- [ ] Ports list complete (15+)
- [ ] Refactoring roadmap clear and agreed
- [ ] No architectural questions remaining

### After Phase 2
- [ ] All ports exist and compile
- [ ] Domain entities testable without Spring/JPA
- [ ] Domain layer has NO Spring imports

### After Phase 3
- [ ] Use cases depend ONLY on ports (zero JPA imports)
- [ ] Controllers wired to use cases (not old services)
- [ ] Integration tests for use cases pass
- [ ] Old services untouched (coexist)

### After Phase 4
- [ ] All adapters implement ports correctly
- [ ] MapStruct mappers compile and convert correctly
- [ ] Spring configuration injects ports as adapters
- [ ] No "No qualifying bean" errors

### After Phase 5
- [ ] All specialized adapters (email, files, scheduler) work
- [ ] No untested port interfaces remaining

### After Phase 6
- [ ] Domain entities have NO JPA annotations
- [ ] JPA entities in separate files (Equipo vs EquipoJpa)
- [ ] All existing queries still work
- [ ] Database operations transparent

### After Phase 7
- [ ] ArchUnit tests pass (enforce architecture)
- [ ] 10+ integration tests pass
- [ ] SOLID compliance documented
- [ ] Test coverage: Domain 80%+, Application 75%+
- [ ] Code ready for team review/handoff

---

## FAQ & Common Pitfalls

### Q: Should domain entities have JPA annotations?
**A:** No. Domain entities are pure POJOs. JPA annotations belong on `XxxJpa` twins. Mappers convert between them.

### Q: What if a domain entity has complex relationships (e.g., Equipo ↔ List<Mantenimiento>)?
**A:** Domain entity can have complex relationships; they are business logic. JPA entity mirrors the structure with JPA annotations. Mappers handle conversion, including nested object mapping.

### Q: How do I handle transactions?
**A:** `@Transactional` goes on use case methods (application layer), not on adapters. The adapter doesn't know it's transactional; the use case coordinates it.

### Q: What if the port interface doesn't match the JPA repository exactly?
**A:** That's fine! Adapter adapts (translates) between them. Port may have `filterByEstado(EstadoEquipo)`, while JPA repo has `findByEstadoJpa(Integer)`. Adapter bridges.

### Q: Can I have multiple adapters for the same port?
**A:** Yes! Example: `EquipoRepositorioAdapterJpa` (database), `EquipoRepositorioAdapterMongo` (MongoDB), or `EquipoRepositorioAdapterMemory` (testing). Spring chooses via `@Primary` or profiles.

### Q: How do I test use cases without a database?
**A:** Mock the ports using Mockito. Use cases depend on `EquipoRepositorioPuerto`, not `IEquiposJpaRepositorio`. Mock says "when guardar() is called, return this Equipo." Database is never touched.

### Q: What about circular dependencies?
**A:** Hexagonal architecture prevents them. Domain has no dependencies. Application depends on domain. Infrastructure depends on application + domain. Presentation depends on application. No layer depends "upward."

### Q: Can I start Phase 3 before Phase 2 is complete?
**A:** No. Phase 3 (use cases) depends on Phase 2 (ports). If ports don't exist, you can't inject them into use cases. Blocking dependency.

### Q: Can I refactor all services at once?
**A:** Not recommended. Use the "Wave" approach: refactor 3-5 core services first (Wave 1), test thoroughly, then Wave 2, etc. Reduces risk.

### Q: What about Lombok?
**A:** Use `@AllArgsConstructor`, `@RequiredArgsConstructor` for dependency injection. `@Data` and `@Getter`/`@Setter` are fine for entities. Avoid `@Builder` in domain entities (enforces invariant checking in constructor).

### Q: How do I handle validation?
**A:** Domain entities validate in constructors (invariants). Use cases may have a `validador` injected (custom validator bean) for input validation. Controllers have `@Valid` for basic structural validation.

### Q: What if I have "God Services" with 50+ methods?
**A:** Split into multiple use cases. One use case = one method. `CrearEquipoUseCase`, `ActualizarEquipoUseCase`, `ObtenerEquipoPorIdUseCase`, etc.

---

## Success Metrics

After full execution, measure:

1. **Architectural Compliance:**
   - 0 imports of JPA classes in domain/application layers ✓
   - 100% of services depend on ports (not concrete repos) ✓
   - 4 clear layers (Domain → Application → Infrastructure ← Presentation) ✓

2. **Code Quality:**
   - Cyclomatic complexity: avg < 10 per method
   - Test coverage: Domain 80%+, Application 75%+, Infrastructure 60%+
   - Zero "TODO" or tech debt comments related to architecture

3. **Team Readiness:**
   - New developers can understand architecture in < 2 hours
   - Adding new feature requires no architectural changes (open/closed)
   - Can swap storage (JPA → MongoDB) without changing use cases

4. **Testability:**
   - Use cases testable without Spring
   - Adapters testable with simple unit tests
   - Integration tests < 1 second each (fast feedback)

---

## Conclusion

This 7-week plan transforms `gestionactivosapi` into a **clean, SOLID-compliant hexagonal architecture**:
- Domain and application layers isolated from infrastructure
- Dependency Inversion Principle enforced (ports, not concrete deps)
- Single Responsibility: each class has one reason to change
- Interface Segregation: ports are small, focused
- Open/Closed: new adapters don't require existing code changes
- Testable, maintainable, scalable foundation for future growth

**Next Steps:**
1. Confirm this plan with architecture reviewer
2. Allocate 7 weeks of Claude executor time
3. Execute phases sequentially with validation checkpoints
4. Document decisions and lessons learned
5. Celebrate when Phase 7 completes! 🎉

—

**Plan created:** March 24, 2026
**Target timeline:** 7 weeks
**Effort:** ~150-200 hours Claude execution time
**Risk level:** Medium (High-impact refactoring, but structured approach mitigates)
