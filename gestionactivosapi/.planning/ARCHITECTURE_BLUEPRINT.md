# Architecture Blueprint: Clean Hexagonal Design

**Status:** Target State Design | Phase 1, Task 1.2
**Date:** 2026-03-24
**Objective:** Define the clean hexagonal architecture with all layers and ports

---

## Executive Summary

Target architecture is a **pure hexagonal (ports & adapters) pattern** with strict layer separation, all dependencies pointing inward, and zero infrastructure coupling in the domain/application layers.

**Architecture Principle:** Business logic flows inward, infrastructure adapts from outside.

```
                    USER REQUESTS
                         ↓
        ┌─────────────────────────────────┐
        │      PRESENTATION LAYER         │ ← Controllers · DTOs
        │  (HTTP endpoints, REST API)     │
        └─────────────────────────────────┘
                         ↓
                  APPLICATION LAYER
                (incoming ports, use cases)
                         ↓
        ┌─────────────────────────────────┐
        │        DOMAIN LAYER             │ ← Pure business logic
        │  (Entities, Value Objects,      │
        │   Domain Services, Exceptions)  │
        └─────────────────────────────────┘
                         ↓
                ADAPTER LAYER (LEFT & RIGHT)
        ┌──────────────────┬──────────────────┐
        │  Data Adapter    │  Service Adapters│
        │  (JPA, MapStruct)│  (Email, PDF)    │
        └──────────────────┴──────────────────┘
                         ↓
         EXTERNAL SYSTEMS (Database, Mail, Files)
```

---

## Package Structure (Final State)

```
com.uisrael.gestionactivosapi/
│
├── dominio/                          (CORE - Zero infrastructure)
│   ├── entidades/                    (Domain classes, no JPA)
│   │   ├── Equipo.java              (pure domain entity)
│   │   ├── Mantenimiento.java
│   │   ├── Custodio.java
│   │   ├── Usuario.java
│   │   ├── ActividadRealizada.java
│   │   ├── ImagenMantenimiento.java
│   │   ├── Notificacion.java
│   │   ├── MantenimientoProgramado.java
│   │   ├── ActividadChecklist.java
│   │   ├── Ticket.java
│   │   ├── VisitaTecnica.java
│   │   ├── FirmaMantenimiento.java
│   │   └── [28 entities total]
│   │
│   ├── puertos/                      (Domain ports/interfaces - NEW)
│   │   ├── repositorios/
│   │   │   ├── EquipoRepositorioPuerto.java
│   │   │   ├── MantenimientoRepositorioPuerto.java
│   │   │   ├── CustodioRepositorioPuerto.java
│   │   │   ├── UsuarioRepositorioPuerto.java
│   │   │   ├── NotificacionRepositorioPuerto.java
│   │   │   ├── ActividadRealizadaRepositorioPuerto.java
│   │   │   ├── ActividadChecklistRepositorioPuerto.java
│   │   │   ├── ImagenMantenimientoRepositorioPuerto.java
│   │   │   ├── MantenimientoProgramadoRepositorioPuerto.java
│   │   │   ├── TicketRepositorioPuerto.java
│   │   │   ├── VisitaTecnicaRepositorioPuerto.java
│   │   │   ├── DepartamentoRepositorioPuerto.java
│   │   │   ├── CategoriaRepositorioPuerto.java
│   │   │   ├── MarcaRepositorioPuerto.java
│   │   │   └── RolRepositorioPuerto.java (15 ports total)
│   │   │
│   │   └── servicios/               (External service ports - NEW)
│   │       ├── GeneradorPdfPuerto.java
│   │       ├── EnviadorCorreoPuerto.java
│   │       ├── AlmacenadorArchivosPuerto.java
│   │       ├── ServicioNotificacionPuerto.java
│   │       └── ServicioAuditoriaPuerto.java (5 service ports)
│   │
│   ├── servicios/                    (Domain services - pure logic - NEW)
│   │   ├── CalculadorMantenimientoDominio.java
│   │   ├── ValidadorEquipoDominio.java
│   │   └── [domain-only calculation services]
│   │
│   ├── valoresobjeto/                (Value objects - no JPA - NEW)
│   │   ├── CodigoEquipo.java
│   │   ├── NumeroSerieEquipo.java
│   │   ├── CedulaCustodio.java
│   │   └── [domain value objects]
│   │
│   ├── excepciones/
│   │   ├── EntidadNoEncontradaException.java
│   │   ├── EquipoNoActivoException.java
│   │   ├── CustodioNoActivoException.java
│   │   └── [domain exceptions]
│   │
│   └── repositorios/                (Legacy: will be replaced by puertos/repositorios/)
│       └── [14 existing interfaces - DEPRECATED IN REFACTOR]
│
├── aplicacion/                       (USE CASES & APPLICATION LAYER)
│   ├── casosuso/                    (Application execution ports)
│   │   ├── entradas/
│   │   │   └── [Keep existing DTOs/Interfaces]
│   │   │
│   │   └── impl/                    (Use case implementations - MOVE FROM servicios)
│   │       ├── RegistrarMantenimientoManualUC.java
│   │       ├── ProgramarMantenimientoUC.java
│   │       ├── GenerarInformeMantenimientoUC.java
│   │       ├── EnviarNotificacionUC.java
│   │       ├── CerrarMantenimientoUC.java
│   │       └── [7-8 core use cases]
│   │
│   ├── servicios/                   (Application services - slim orchestration)
│   │   ├── MantenimientoManualApplicationService.java (refactored)
│   │   ├── MantenimientoProgramadoApplicationService.java (refactored)
│   │   ├── NotificacionApplicationService.java (refactored)
│   │   ├── MantenimientoInformeOrchestrator.java
│   │   │
│   │   └── utilities/               (Helper services - NEW)
│   │       ├── PdfGenerator.java  (moved from utility)
│   │       ├── EmailComposer.java (moved from utility)
│   │       └── FileManager.java   (moved from utility)
│   │
│   ├── dto/                         (Application DTOs - NEW)
│   │   ├── request/
│   │   │   ├── RegistrarMantenimientoDTO.java
│   │   │   ├── ProgramarMantenimientoDTO.java
│   │   │   └── [Internal communication only]
│   │   │
│   │   └── response/
│   │       ├── MantenimientoResultadoDTO.java
│   │       └── [Internal output]
│   │
│   └── excepciones/
│       └── [Application-level exceptions]
│
├── infraestructura/                 (ADAPTERS & INFRASTRUCTURE)
│   ├── persistencia/
│   │   ├── jpa/                     (JPA Entities only - no business logic)
│   │   │   ├── EquipoJpa.java      (persistence only, full JPA)
│   │   │   ├── MantenimientoJpa.java
│   │   │   ├── base/
│   │   │   │   ├── BaseEntityJpa.java
│   │   │   │   └── TimestampedJpa.java
│   │   │   │
│   │   │   ├── base/
│   │   │   └── repositories/       (Spring Data interfaces)
│   │   │       ├── EquipoSpringDataRepo.java
│   │   │       ├── MantenimientoSpringDataRepo.java
│   │   │       └── [14 Spring Data repositories]
│   │   │
│   │   ├── mapeadores/              (Entity Mapping - MapStruct)
│   │   │   ├── EquipoMapper.java
│   │   │   │   └── Domain (Equipo) ↔ JPA (EquipoJpa)
│   │   │   ├── MantenimientoMapper.java
│   │   │   ├── CustodioMapper.java
│   │   │   ├── UsuarioMapper.java
│   │   │   ├── NotificacionMapper.java
│   │   │   ├── ActividadRealizadaMapper.java
│   │   │   ├── ActividadChecklistMapper.java
│   │   │   ├── ImagenMantenimientoMapper.java
│   │   │   ├── MantenimientoProgramadoMapper.java
│   │   │   ├── TicketMapper.java
│   │   │   ├── VisitaTecnicaMapper.java
│   │   │   ├── DepartamentoMapper.java
│   │   │   ├── CategoriaMapper.java
│   │   │   └── MarcaMapper.java (14 mappers total)
│   │   │
│   │   └── adaptadores/             (Port Implementations - ADAPTERS)
│   │       ├── EquipoRepositorioAdapter.java
│   │       │   └── Implements: EquipoRepositorioPuerto
│   │       │   └── Uses: EquipoSpringDataRepo, EquipoMapper
│   │       │
│   │       ├── MantenimientoRepositorioAdapter.java
│   │       │   └── Implements: MantenimientoRepositorioPuerto
│   │       │
│   │       ├── CustodioRepositorioAdapter.java
│   │       ├── UsuarioRepositorioAdapter.java
│   │       ├── NotificacionRepositorioAdapter.java
│   │       ├── ActividadRealizadaRepositorioAdapter.java
│   │       ├── ActividadChecklistRepositorioAdapter.java
│   │       ├── ImagenMantenimientoRepositorioAdapter.java
│   │       ├── MantenimientoProgramadoRepositorioAdapter.java
│   │       ├── TicketRepositorioAdapter.java
│   │       ├── VisitaTecnicaRepositorioAdapter.java
│   │       ├── DepartamentoRepositorioAdapter.java
│   │       ├── CategoriaRepositorioAdapter.java
│   │       ├── MarcaRepositorioAdapter.java
│   │       └── RolRepositorioAdapter.java (15 adapters total)
│   │
│   ├── pdf/                         (PDF Service Adapter)
│   │   └── PdfMantenimientoAdapter.java
│   │       └── Implements: GeneradorPdfPuerto
│   │       └── Uses: iText library
│   │
│   ├── correo/                      (Email Service Adapter)
│   │   └── CorreoMantenimientoAdapter.java
│   │       └── Implements: EnviadorCorreoPuerto
│   │       └── Uses: JavaMailSender
│   │
│   ├── archivos/                    (File Storage Adapter)
│   │   └── AlmacenadorArchivosAdapter.java
│   │       └── Implements: AlmacenadorArchivosPuerto
│   │       └── Uses: java.nio.file
│   │
│   ├── seguridad/                   (Security Adapter)
│   │   └── ServicioDetallesUsuario.java
│   │
│   └── configuracion/               (Spring Configuration)
│       ├── PersistenciaConfig.java
│       ├── RepositorioConfig.java  (NEW - Wiring adapters to ports)
│       ├── ServiciosExternosConfig.java (NEW - Wiring external service adapters)
│       └── TransactionConfig.java
│
└── presentacion/                    (HTTP LAYER - External Interface)
    ├── controladores/              (Spring REST Controllers)
    │   ├── EquiposControlador.java
    │   ├── MantenimientosControlador.java
    │   ├── CustodiosControlador.java
    │   ├── NotificacionControlador.java
    │   └── [20 REST controllers]
    │
    ├── dto/                       (Presentation DTOs - Request/Response)
    │   ├── request/
    │   │   ├── RegistrarMantenimientoRequestDTO.java
    │   │   └── [Controller input DTOs]
    │   │
    │   └── response/
    │       ├── MantenimientoResponseDTO.java
    │       └── [Controller output DTOs]
    │
    ├── mapeadores/               (Presentation layer mappers)
    │   ├── PresentacionDtoMapper.java
    │   └── [Presentation ↔ Application mapping]
    │
    └── validacion/
        └── [Input validation]
```

---

## Port Definitions (20 Total)

### Repository Ports (15 - "Going OUT" ports)

These interfaces abstract persistence operations away from the application layer.

```java
// dominio/puertos/repositorios/EquipoRepositorioPuerto.java
public interface EquipoRepositorioPuerto {
    // Persistence operations (signatures only, no JPA)
    Equipo guardar(Equipo equipo);
    Optional<Equipo> obtenerPorId(Integer id);
    List<Equipo> obtenerTodos();
    void eliminar(Integer id);
    void actualizar(Equipo equipo);
    List<Equipo> obtenerPorEstado(boolean estado);
    // ... domain-specific queries
}

// IMPLEMENTED BY:
// infraestructura/persistencia/adaptadores/EquipoRepositorioAdapter.java
@Component
public class EquipoRepositorioAdapter implements EquipoRepositorioPuerto {
    private final EquipoSpringDataRepo springRepo;        // JPA
    private final EquipoMapper mapper;                    // MapStruct
    
    @Override
    public Equipo guardar(Equipo equipo) {
        EquipoJpa jpaEntity = mapper.toPersistence(equipo);
        EquipoJpa saved = springRepo.save(jpaEntity);
        return mapper.toDomain(saved);
    }
    // ... implementation details
}
```

**List of 15 Repository Ports:**
1. `EquipoRepositorioPuerto` - Equipment/asset records
2. `MantenimientoRepositorioPuerto` - Maintenance records
3. `CustodioRepositorioPuerto` - Custodian/personnel
4. `UsuarioRepositorioPuerto` - System users
5. `NotificacionRepositorioPuerto` - User notifications
6. `ActividadRealizadaRepositorioPuerto` - Completed activities
7. `ActividadChecklistRepositorioPuerto` - Activity templates
8. `ImagenMantenimientoRepositorioPuerto` - Maintenance images
9. `MantenimientoProgramadoRepositorioPuerto` - Scheduled maintenance
10. `TicketRepositorioPuerto` - Service tickets
11. `VisitaTecnicaRepositorioPuerto` - Technical visits
12. `DepartamentoRepositorioPuerto` - Organization dept
13. `CategoriaRepositorioPuerto` - Equipment categories
14. `MarcaRepositorioPuerto` - Equipment brands
15. `RolRepositorioPuerto` - User roles

### External Service Ports (5 - "Going OUT" ports)

These interfaces abstract external system operations.

```java
// dominio/puertos/servicios/GeneradorPdfPuerto.java
public interface GeneradorPdfPuerto {
    byte[] generarInforme(MantenimientoInfoDTO info);
    byte[] generarReporte(List<Mantenimiento> mantenimientos);
}

// IMPLEMENTED BY:
// infraestructura/pdf/PdfMantenimientoAdapter.java
@Component
public class PdfMantenimientoAdapter implements GeneradorPdfPuerto {
    // Uses iText library internally
}
```

**List of 5 Service Ports:**
1. `GeneradorPdfPuerto` - PDF generation (replaces PdfMantenimientoService)
2. `EnviadorCorreoPuerto` - Email dispatch (wraps JavaMailSender)
3. `AlmacenadorArchivosPuerto` - File storage (replaces MantenimientoArchivoService)
4. `ServicioNotificacionPuerto` - Notification logic (replaces NotificacionService)
5. `ServicioAuditoriaPuerto` - Audit trail (NEW - for future use)

---

## Entity Mapping Strategy (MapStruct)

### Domain Entity vs JPA Entity Pattern

```
DOMAIN LAYER                          PERSISTENCE LAYER
(Zero JPA annotations)                (Full JPA annotations)

Equipo.java                           EquipoJpa.java
├── id: Integer                       ├── idEquipo: Integer
├── codigoSap: String                 ├── codigoSap: String
├── numeroSerie: String               ├── numeroSerie: String
├── estado: boolean                   ├── estado: Boolean (JPA)
├── custodioActual: Custodio ◄────────┤── fkCustodio: CustodioJpa
├── ubicacion: String                 ├── ubicacion: String
├── [domain methods]                  ├── @Entity, @Table JPA stuff
└── isActivityPermitted()             ├── @Version, @CreatedDate
                                      ├── @Column, @JoinColumn
                                      └── [JPA-specific]
```

### Mapper Pattern

```java
// infraestructura/persistencia/mapeadores/EquipoMapper.java

@Mapper(componentModel = "spring")
public interface EquipoMapper {
    
    // Domain → JPA
    EquipoJpa toPersistence(Equipo domainEntity);
    
    // JPA → Domain
    Equipo toDomain(EquipoJpa jpaEntity);
    
    List<Equipo> toDomainList(List<EquipoJpa> jpaEntities);
    
    // Custom mappings for nested objects
    @Mapping(source = "fkCustodio", target = "custodioActual")
    Equipo toDomainWithCustodio(EquipoJpa jpaEntity);
}
```

### Benefits of Separation
- Domain layer is completely technology-agnostic
- JPA can be swapped without touching business logic
- Testing domain logic doesn't require database/ORM setup
- Clear separation of concerns

---

## Application Service Refactoring Pattern

### BEFORE (Current - DIP Violation)
```java
@Service
public class MantenimientoManualService {
    private final IMantenimientosJpaRepositorio mantenimientosRepo;  // INFRA
    private final IEquiposJpaRepositorio equiposRepo;                // INFRA
    private final ICustodiosJpaRepositorio custodiosRepo;            // INFRA
    
    public void crear(MantenimientoManualRequestDTO request) {
        // Directly manipulates JPA entities
        MantenimientosJpa entity = new MantenimientosJpa();
        mantenimientosRepo.save(entity);  // Direct to JPA
    }
}
```

### AFTER (Refactored - DIP Compliant)
```java
@Service
public class MantenimientoManualApplicationService {
    
    // Inject domain ports, not JPA repos
    private final MantenimientoRepositorioPuerto mantenimientoRepo;
    private final EquipoRepositorioPuerto equipoRepo;
    private final CustodioRepositorioPuerto custodioRepo;
    private final GeneradorPdfPuerto generadorPdf;
    
    // Delegate to use case
    private final RegistrarMantenimientoManualUC registrarUC;
    
    public MantenimientoResultadoDTO crear(RegistrarMantenimientoDTO request) {
        // Calls use case, which works with domain entities
        Mantenimiento mantenimiento = registrarUC.ejecutar(request);
        
        // Save via domain port adapter
        Mantenimiento saved = mantenimientoRepo.guardar(mantenimiento);
        
        // Consume other ports
        byte[] pdf = generadorPdf.generarInforme(saved);
        
        return toResponseDTO(saved);
    }
}
```

---

## Dependency Injection Wiring

### Spring Configuration (NEW)

```java
// infraestructura/configuracion/RepositorioConfig.java
@Configuration
public class RepositorioConfig {
    
    // Wire repository adapters to domain ports
    @Bean
    public EquipoRepositorioPuerto equipoRepositorioPuerto(
            EquipoSpringDataRepo springDataRepo,
            EquipoMapper mapper) {
        return new EquipoRepositorioAdapter(springDataRepo, mapper);
    }
    
    @Bean
    public MantenimientoRepositorioPuerto mantenimientoRepositorioPuerto(
            MantenimientoSpringDataRepo springDataRepo,
            MantenimientoMapper mapper) {
        return new MantenimientoRepositorioAdapter(springDataRepo, mapper);
    }
    
    // ... 13 more repository adapters
}

// infraestructura/configuracion/ServiciosExternosConfig.java
@Configuration
public class ServiciosExternosConfig {
    
    @Bean
    public GeneradorPdfPuerto generadorPdfPuerto() {
        return new PdfMantenimientoAdapter();
    }
    
    @Bean
    public EnviadorCorreoPuerto enviadorCorreoPuerto(JavaMailSender mailSender) {
        return new CorreoMantenimientoAdapter(mailSender);
    }
    
    // ... other service port adapters
}
```

---

## Data Flow Example: Create Manual Maintenance

```
┌─────────────────────────────────────────────────────┐
│ HTTP POST /api/mantenimiento/manual                 │
│ Body: RegistrarMantenimientoRequestDTO              │
└──────────────────────────┬──────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────┐
│ MantenimientosControlador                           │
│ - Receives DTO from HTTP                            │
│ - Validates input                                   │
│ - Maps to application DTO                           │
└──────────────────────────┬──────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────┐
│ MantenimientoManualApplicationService               │
│ - Receives application DTO                          │
│ - Calls use case: RegistrarMantenimientoManualUC    │
└──────────────────────────┬──────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────┐
│ RegistrarMantenimientoManualUC (Use Case)           │
│ 1. Validate via EquipoRepositorioPuerto → adapter  │
│    - Load Equipo domain entity                      │
│ 2. Validate via CustodioRepositorioPuerto → adapter│
│    - Load Custodio domain entity                    │
│ 3. Call domain service: CalculadorMantenimiento    │
│    - Pure business logic                           │
│ 4. Create domain entity: new Mantenimiento(...)    │
│ 5. Save via MantenimientoRepositorioPuerto → adapter│
│    - Adapter converts to JPA                        │
│    - Saves via Spring Data                          │
│ 6. Return domain entity                             │
└──────────────────────────┬──────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────┐
│ Application Service (continued orchestration)       │
│ 1. Call GeneradorPdfPuerto → adapter               │
│    - Generates PDF from domain entity               │
│ 2. Call AlmacenadorArchivosPuerto → adapter         │
│    - Stores PDF to filesystem                       │
│ 3. Call EnviadorCorreoPuerto → adapter              │
│    - Sends email notification                       │
│ 4. Return service result                            │
└──────────────────────────┬──────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────┐
│ MantenimientosControlador                           │
│ - Maps domain entity to response DTO                │
│ - Returns HTTP 200 + JSON response                  │
└──────────────────────────┬──────────────────────────┘
                           ↓
                  HTTP 200 OK + JSON

KEY POINTS:
- Controllers only see presentation DTOs
- Services only see domain entities and ports
- Adapters handle all infrastructure details
- JPA is ONLY in the adapter layer
- Domain layer has ZERO knowledge of persistence
```

---

## Key Architectural Principles Implemented

### 1. Dependency Inversion (DIP) ✓
- High-level (application) depends on abstractions (domain ports)
- Low-level (infrastructure) implements abstractions
- NO concrete infrastructure in business logic

### 2. Single Responsibility (SRP) ✓
- Services handle orchestration only
- Use cases handle business logic
- Adapters handle infrastructure only
- Controllers handle HTTP only

### 3. Open/Closed Principle (OCP) ✓
- New storage type? Add new adapter without modifying domain
- New email provider? Add new adapter without modifying domain
- New external service? Add new service port + adapter

### 4. Layer Isolation ✓
- Domain imports nothing from infrastructure
- Application imports only domain + ports
- Infrastructure imports domain + external libraries
- Presentation is completely separated

### 5. Testability ✓
- Domain logic tested without database (mock ports)
- Application tested with mock ports
- Infrastructure tested with real/test database
- Complete test isolation possible

---

## Migration Summary

| Component | Before | After | Impact |
|-----------|--------|-------|--------|
| Services | 8 mixed | 3 lean + 2 utility + 7 use cases | Cleaner separation |
| Ports | 14 unused | 14 repos + 5 services = 20 implemented | Full DIP compliance |
| Adapters | 0 | 15 repository + 5 service | Infrastructure isolated |
| Mappers | 0 | 14 MapStruct mappers | Strong typing |
| Dependencies | Infra ← App ← Domain | App → Domain ← Adapters | Correct direction |

---

## Technology Stack (No Changes)

- **JPA/Hibernate:** Only in adapters (EquipoJpa, etc.)
- **MapStruct:** New - for entity mapping
- **Spring Data:** Only via adapters (Spring repos)
- **Spring Framework:** Core - dependency wiring unchanged
- **Java 17:** No changes
- **Maven:** No changes
- **PostgreSQL:** No changes

---

## Success Criteria

- [ ] All 15 repository ports implemented as adapters
- [ ] All 5 service ports implemented as adapters
- [ ] No JPA imports in domain/aplicacion layers
- [ ] All services inject domain ports, not JPA repos
- [ ] 100% SOLID compliance in domain layer
- [ ] No circular dependencies
- [ ] All tests passing with new architecture
- [ ] Controller ↔ Application ↔ Domain data flow intact

---

**Blueprint Complete. Ready for Task 1.3: Refactoring Roadmap**
