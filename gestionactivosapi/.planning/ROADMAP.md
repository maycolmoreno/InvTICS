# Refactoring Roadmap: Execution Plan & Dependency Graph

**Status:** Execution Strategy | Phase 1, Task 1.3
**Date:** 2026-03-24
**Objective:** Define safe refactoring sequence without breaking functionality

---

## Executive Summary

Refactoring will be executed in **4 waves** to minimize risk and dependencies:

- **Wave 1 (Foundation):** Create all ports and adapters infrastructure
- **Wave 2 (Core Entities):** Refactor pivot entities (Equipo → Mantenimiento → Custodio)
- **Wave 3 (Services):** Refactor application services to use domain ports
- **Wave 4 (Integration):** Complete remaining services and validation

**Total Duration:** 5-6 days (2 weeks of committed development time)
**Risk Level:** LOW (architecture skeleton exists, parallel execution possible)
**Rollback Point:** Each wave is independently committed and testable

---

## Dependency Graph: Service Coupling

```
                    EXTERNAL WORLD
                         ↓
         ┌────────────────┼────────────────┐
         ↓                ↓                ↓
    [Email Server]  [File Storage]   [Database]
         ↓                ↓                ↓
    ┌────────────────────────────────────────────┐
    │         Infrastructure Adapters            │
    │  (Email + File + Repository implementations)│
    └────────────────────────────────────────────┘
         ↑                ↑                ↑
         └────────────────┼────────────────┘
                          │ (via ports)
    ┌────────────────────────────────────────────┐
    │          Application Services              │
    │  MantenimientoManualService                │
    │  MantenimientoProgramadoService            │
    │  NotificacionService                       │
    │  MantenimientoInformeService               │
    └────────────────────────────────────────────┘
                          ↑
    ┌────────────────────────────────────────────┐
    │           Domain Layer (Core)              │
    │  Equipo, Mantenimiento, Custodio           │
    │  Pure business logic - no dependencies     │
    └────────────────────────────────────────────┘

SERVICE DEPENDENCY CHAIN:
┌─────────────────────────────────────────────┐
│ MantenimientoManualService                  │
│ Depends on: 7 repos + 2 other services      │
│  ├─→ IMantenimientosJpaRepositorio          │
│  ├─→ IEquiposJpaRepositorio                 │
│  ├─→ ICustodiosJpaRepositorio               │
│  ├─→ IUsuariosJpaRepositorio                │
│  ├─→ MantenimientoProgramadoService (uses)  │
│  └─→ NotificacionService (uses)             │
└─────────────────────────────────────────────┘
         ↑                                    ↑
         │ (depends on)                    (orchestrates)
         │                                    │
    ┌────┴────────────────────────────────────┴─────┐
    │ MantenimientoProgramadoService                 │
    │ Depends on: 3 repos                            │
    └────────────────────────────────────────────────┘
         ↑
         │ (if mantenimiento closes)
         │
    ┌─────────────────────────────────────────┐
    │ NotificacionService                     │
    │ Depends on: 3 repos                     │
    └─────────────────────────────────────────┘
```

---

## Wave 1: Foundation (1.5 days)

### Objective
Create all port definitions and adapter infrastructure without touching existing services.

### Tasks

#### 1.1.1: Create Domain Repository Ports (3 hours)
**Dependencies:** None

**What to create:**
```
dominio/puertos/repositorios/
├── EquipoRepositorioPuerto.java
├── MantenimientoRepositorioPuerto.java
├── CustodioRepositorioPuerto.java
├── UsuarioRepositorioPuerto.java
├── NotificacionRepositorioPuerto.java
├── ActividadRealizadaRepositorioPuerto.java
├── ActividadChecklistRepositorioPuerto.java
├── ImagenMantenimientoRepositorioPuerto.java
├── MantenimientoProgramadoRepositorioPuerto.java
├── TicketRepositorioPuerto.java
├── VisitaTecnicaRepositorioPuerto.java
├── DepartamentoRepositorioPuerto.java
├── CategoriaRepositorioPuerto.java
├── MarcaRepositorioPuerto.java
└── RolRepositorioPuerto.java (15 ports)
```

**Signatures (standard CRUD + domain-specific queries):**
```java
public interface EquipoRepositorioPuerto {
    Equipo guardar(Equipo equipo);
    Optional<Equipo> obtenerPorId(Integer id);
    List<Equipo> obtenerTodos();
    void eliminar(Integer id);
    void actualizar(Equipo equipo);
    List<Equipo> obtenerPorEstado(boolean estado);
    Optional<Equipo> obtenerPorCodigoSap(String codigoSap);
    Optional<Equipo> obtenerPorNumeroSerie(String numeroSerie);
}
```

**Duration:** 3 hours (parallel creation possible)
**Validation:** All 15 interfaces compile without errors

---

#### 1.1.2: Create External Service Ports (1 hour)
**Dependencies:** None

**What to create:**
```
dominio/puertos/servicios/
├── GeneradorPdfPuerto.java
├── EnviadorCorreoPuerto.java
├── AlmacenadorArchivosPuerto.java
├── ServicioNotificacionPuerto.java
└── ServicioAuditoriaPuerto.java (5 ports)
```

**Signatures:**
```java
public interface GeneradorPdfPuerto {
    byte[] generarInforme(MantenimientoInfoDTO info);
}

public interface EnviadorCorreoPuerto {
    void enviarCorreo(String destinatario, String asunto, String cuerpo);
    void enviarCorreoConAdjunto(String dest, String asunto, String cuerpo, byte[] pdf);
}

public interface AlmacenadorArchivosPuerto {
    Path guardarArchivo(byte[] contenido, String nombre);
    byte[] leerArchivo(String ruta);
    boolean existeArchivo(String ruta);
}
```

**Duration:** 1 hour
**Validation:** All 5 interfaces compile

---

#### 1.1.3: Create MapStruct Mappers (4 hours)
**Dependencies:** 1.1.1 complete (need domain ports defined)

**What to create:** 14 MapStruct mappers
```
infraestructura/persistencia/mapeadores/
├── EquipoMapper.java
├── MantenimientoMapper.java
├── CustodioMapper.java
├── [... 11 more mappers]
```

**Mapper Pattern:**
```java
@Mapper(componentModel = "spring")
public interface EquipoMapper {
    EquipoJpa toPersistence(Equipo domainEntity);
    Equipo toDomain(EquipoJpa jpaEntity);
    List<Equipo> toDomainList(List<EquipoJpa> jpaList);
}
```

**Duration:** 4 hours (some can be parallel)
**Validation:** All mappers compile, Spring detects them

---

#### 1.1.4: Create Repository Adapters (6 hours)
**Dependencies:** 1.1.1 complete, 1.1.3 complete

**What to create:** 15 repository adapters
```
infraestructura/persistencia/adaptadores/
├── EquipoRepositorioAdapter.java
├── MantenimientoRepositorioAdapter.java
├── CustodioRepositorioAdapter.java
├── [... 12 more adapters]
```

**Adapter Pattern:**
```java
@Component
public class EquipoRepositorioAdapter implements EquipoRepositorioPuerto {
    private final EquipoSpringDataRepo springDataRepo;
    private final EquipoMapper mapper;
    
    @Override
    public Equipo guardar(Equipo equipo) {
        EquipoJpa jpa = mapper.toPersistence(equipo);
        EquipoJpa saved = springDataRepo.save(jpa);
        return mapper.toDomain(saved);
    }
    
    @Override
    public Optional<Equipo> obtenerPorId(Integer id) {
        return springDataRepo.findById(id).map(mapper::toDomain);
    }
    
    // ... other methods delegating to Spring Data + mapper
}
```

**Duration:** 6 hours (can be parallelized)
**Validation:** All adapters compile and wire correctly

---

#### 1.1.5: Create External Service Adapters (3 hours)
**Dependencies:** 1.1.2 complete

**What to create:** 5 service adapters
```
infraestructura/
├── pdf/PdfMantenimientoAdapter.java
├── correo/CorreoMantenimientoAdapter.java
├── archivos/AlmacenadorArchivosAdapter.java
├── notificaciones/ServicioNotificacionAdapter.java
└── auditoria/ServicioAuditoriaAdapter.java
```

**Example:**
```java
@Component
public class PdfMantenimientoAdapter implements GeneradorPdfPuerto {
    @Override
    public byte[] generarInforme(MantenimientoInfoDTO info) {
        // Use existing PdfMantenimientoService.generarInforme()
        // OR migrate logic here
    }
}
```

**Duration:** 3 hours
**Validation:** All adapters compile

---

#### 1.1.6: Spring Configuration (1 hour)
**Dependencies:** 1.1.4, 1.1.5 complete

**What to create:**
```
infraestructura/configuracion/
├── RepositorioConfig.java
└── ServiciosExternosConfig.java
```

**Config wiring:**
```java
@Configuration
public class RepositorioConfig {
    @Bean
    public EquipoRepositorioPuerto equipoRepositorioPuerto(
            EquipoSpringDataRepo repo, EquipoMapper mapper) {
        return new EquipoRepositorioAdapter(repo, mapper);
    }
    // ... 14 more beans
}
```

**Duration:** 1 hour
**Validation:** Spring context loads without errors, all beans wire correctly

---

### Wave 1 Summary

**Total Duration:** 1.5 days
**Commits:**
1. `chore(phase-1): Add 15 repository domain ports`
2. `chore(phase-1): Add 5 external service domain ports`
3. `chore(phase-1): Add 14 MapStruct mappers (JPA ↔ Domain)`
4. `chore(phase-1): Add 15 repository adapters`
5. `chore(phase-1): Add 5 external service adapters`
6. `chore(phase-1): Add Spring configuration for port wiring`

**Status:** Infrastructure complete, services unchanged
**Tests:** All existing tests still pass
**Risk:** NONE - no changes to existing services

---

## Wave 2: Pivot Entity Refactoring (1.5 days)

### Objective
Refactor the 3 most central entities (Equipo, Mantenimiento, Custodio) to use domain ports.

### Service Refactoring Order

**Priority 1: MantenimientoProgramadoService** (Simplest - refactor first for confidence)
- Current: 3 JPA repos
- New: 3 domain ports
- Duration: 2 hours
- Risk: LOW (focused service)

**Priority 2: MantenimientoInformeService** (Orchestrator - medium)
- Current: 3 JPA repos + service composition
- New: 3 domain ports
- Duration: 1.5 hours
- Risk: LOW (pure orchestration)

**Priority 3: NotificacionService** (Standalone - medium)
- Current: 3 JPA repos
- New: 3 domain ports
- Duration: 1.5 hours
- Risk: MEDIUM (many query operations)

**Priority 4: MantenimientoManualService** (Complex - largest)
- Current: 7 JPA repos
- New: 7 domain ports
- Duration: 3-4 hours
- Risk: MEDIUM (many operations)

### Wave 2 Sequence

```
TimeLineSequence:
Day 1 (4 hours):
├─ 0:00 - 2:00 → Refactor MantenimientoProgramadoService
├─ 2:00 - 3:30 → Refactor MantenimientoInformeService
└─ 3:30 - 4:00 → Integrate + test

Day 1.5 (3.5 hours):
├─ 0:00 - 1:30 → Refactor NotificacionService
├─ 1:30 - 2:00 → Refactor MantenimientoManualService (part 1)
├─ 2:00 - 3:00 → Refactor MantenimientoManualService (part 2)
└─ 3:00 - 3:30 → Integration testing

PARALLEL SAFETY:
- All 4 services are independent
- They can be refactored in parallel if needed
- No order dependency between them
```

### Example Refactoring: MantenimientoProgramadoService

**BEFORE:**
```java
@Service
public class MantenimientoProgramadoService {
    private final IMantenimientoProgramadoJpaRepositorio programadoRepo;
    private final IEquiposJpaRepositorio equiposRepo;
    private final IUsuariosJpaRepositorio usuariosRepo;
    
    public MantenimientoProgramadoResponseDTO programar(MantenimientoProgramadoRequestDTO request) {
        EquiposJpa equipo = equiposRepo.findById(request.getEquipoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Equipo no encontrado"));
        // ... JPA work
    }
}
```

**AFTER:**
```java
@Service
public class MantenimientoProgramadoApplicationService {
    
    // INJECT DOMAIN PORTS INSTEAD OF JPA REPOS
    private final MantenimientoProgramadoRepositorioPuerto programadoRepo;
    private final EquipoRepositorioPuerto equipoRepo;
    private final UsuarioRepositorioPuerto usuarioRepo;
    
    public MantenimientoProgramadoResultadoDTO programar(ProgramarMantenimientoDTO request) {
        // Work with domain entities, not JPA
        Equipo equipo = equipoRepo.obtenerPorId(request.getEquipoId())
                .orElseThrow(() -> new EquipoNoEncontradoException("Equipo no encontrado"));
        Usuario tecnico = usuarioRepo.obtenerPorId(request.getTecnicoId())
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
        
        MantenimientoProgramado programado = MantenimientoProgramado.crear(
                equipo, tecnico, request.getFrecuenciaDias(), request.getObservaciones());
        
        MantenimientoProgramado saved = programadoRepo.guardar(programado);
        return toResultadoDTO(saved);
    }
}
```

**Changes:**
- ✅ Replace `IXxxJpaRepositorio` → `XxxRepositorioPuerto`
- ✅ Replace `XxxJpa` entities → Domain `Xxx` entities
- ✅ Update method signatures (apply domain-specific exceptions)
- ✅ Update mappers (DTO ↔ domain entity)

**Validation:**
- All tests pass
- No JPA imports in this class
- Domain entities used throughout

---

## Wave 3: Complete Service Refactoring (2 days)

### Objective
Fully refactor all remaining services and create use case classes.

### Architecture Migration for Each Service

```
Before Refactoring:

aplicacion/servicios/
├── MantenimientoManualService.java (9 repos, mixed responsibilities)
├── PdfMantenimientoService.java (pure utility)
├── CorreoMantenimientoService.java (pure utility)
└── [others]

After Refactoring:

aplicacion/servicios/
├── MantenimientoManualApplicationService.java (refactored, slim)
├── utilities/
│   ├── PdfGenerator.java (moved)
│   ├── EmailComposer.java (moved)
│   └── FileManager.java (moved)
│
aplicacion/casosuso/impl/
├── RegistrarMantenimientoManualUC.java (NEW - business logic)
├── ProgramarMantenimientoUC.java (NEW)
├── CerrarMantenimientoUC.java (NEW)
├── EnviarNotificacionUC.java (NEW)
└── [others as needed]
```

### Tasks (Parallel - No Strict Order)

#### Wave 3.1: Extract Domain Services (2 hours)
- Create `dominio/servicios/CalculadorMantenimientoDominio.java`
- Create `dominio/servicios/ValidadorEquipoDominio.java`
- Move pure business logic from application services

#### Wave 3.2: Create Use Cases (4 hours)
- `RegistrarMantenimientoManualUC` - from MantenimientoManualService
- `ProgramarMantenimientoUC` - from MantenimientoProgramadoService
- `EnviarNotificacionUC` - from NotificacionService
- `GenerarInformeMantenimientoUC` - from MantenimientoInformeService
- `CerrarMantenimientoUC` - new

#### Wave 3.3: Refactor All Services (3 hours)
- Lean down MantenimientoManualApplicationService
- Lean down NotificacionApplicationService
- Move utilities to new packages

#### Wave 3.4: Update Controllers (2 hours)
- Change imports from services to use cases (if not already)
- Ensure DTO mapping chain: Presentation ↔ Application ↔ Domain

**Total Wave 3:** 2 days
**Risk:** MEDIUM (larger refactoring scope)
**Validation:** All integration tests pass

---

## Wave 4: Integration & Validation (1 day)

### Objective
Complete remaining services, run full integration tests, document.

### Tasks

#### 4.1: Remaining Adapters (2 hours)
- Verify all 20 adapters are in place
- Test each adapter in isolation

#### 4.2: Integration Testing (3 hours)
- Full stack test: Controller → Service → Port → Adapter → Database
- Verify data round-trip (Domain ↔ JPA)
- Test error scenarios

#### 4.3: Security & AOP (1 hour)
- Verify @Transactional still works
- Verify @PreAuthorize still works
- Check aspect binding

#### 4.4: Documentation (1 hour)
- Update README with new architecture
- Update ARCHITECTURE_BLUEPRINT.md with actual implementation notes

**Total Wave 4:** 1 day
**Risk:** LOW (validation only)

---

## Refactoring Risk Matrix

### Safe Zones (LOW RISK - Few Dependencies)

| Service | Why Safe | Duration |
|---------|----------|----------|
| PdfMantenimientoService | No repos, isolated | 0.5h |
| CorreoMantenimientoService | No repos, isolated | 0.5h |
| MantenimientoArchivoService | No repos, isolated | 0.5h |
| MantenimientoProgramadoService | 3 repos only, focused | 2h |

### Medium Zone (MEDIUM RISK)

| Service | Why Medium | Duration |
|---------|-----------|----------|
| NotificacionService | 3 repos but heavy queries | 2h |
| MantenimientoInformeService | Orchestrator, depends on 2 others | 2h |

### Dangerous Zone (HIGHER RISK)

| Service | Why Risky | Duration | Mitigation |
|---------|-----------|----------|------------|
| MantenimientoManualService | 7 repos, central to system, complex logic | 4h | Refactor in sub-stages, test heavily |

---

## Dependency Graph: Entity Relationships

```
ENTITY COUPLING MAP (Most to Least Central)

Tier 1 - CORE ENTITIES (Refactor Together)
┌──────────────────────────────────────────┐
│ Equipo (Equipment)                       │
│ ├─→ Mantenimiento (1:N)                  │
│ ├─→ Custodio (N:1)                       │
│ ├─→ Categoria (N:1)                      │
│ └─→ Ubicacion (N:1)                      │
└──────────────────────────────────────────┘

Tier 2 - TRANSACTIONAL ENTITIES
┌──────────────────────────────────────────┐
│ Mantenimiento (Maintenance Record)       │
│ ├─→ Equipo (N:1)                         │
│ ├─→ Usuario (N:1 - Technician)           │
│ ├─→ Custodio (N:1)                       │
│ ├─→ ActividadRealizada (1:N)             │
│ ├─→ ImagenMantenimiento (1:N)            │
│ ├─→ MantenimientoProgramado (1:1)        │
│ └─→ Notificacion (1:N)                   │
└──────────────────────────────────────────┘

Tier 3 - TRANSACTIONAL SUPPORT
┌──────────────────────────────────────────┐
│ ActividadRealizada (Activity Log)        │
│ ├─→ Mantenimiento (N:1)                  │
│ └─→ ActividadChecklist (N:1)             │
└──────────────────────────────────────────┘

REFACTORING DEPENDENCY FLOW:
1. Tier 1 first (Equipo must exist)
2. Tier 2 second (Depends on Tier 1)
3. Tier 3 third (Cleanup)

NO CIRCULAR DEPENDENCIES - Safe to refactor in order
```

---

## Testing Strategy

### Unit Tests (Domain Layer)
```
✓ Domain entities (no mocks needed)
✓ Domain value objects
✓ Domain services (mock ports)
✓ Use cases (mock all ports)
```

### Integration Tests (Adapter Layer)
```
✓ Repository adapters (test database)
✓ Service adapters (test external integrations if needed)
✓ Spring configuration wiring
```

### End-to-End Tests (Full Stack)
```
✓ Controller → Service → Port → Adapter → Database
✓ Data round-trip validation
✓ Error scenario handling
```

### Test Coverage Before vs After

| Layer | Before | After | Goal |
|-------|--------|-------|------|
| Domain | Low (mixed with infra) | High | 100% |
| Application | Medium | High | 95%+ |
| Adapter | Low (direct tests) | Medium | 85%+ |
| **TOTAL** | ~60% | **~90%** | 90% |

---

## Rollback Strategy

If issues arise at any wave:

**Wave 1 Rollback:** Delete all new files, revert to original
- Impact: ZERO - existing services unchanged
- Time: 5 minutes

**Wave 2 Rollback:** Keep adapters, revert service changes
- Impact: NONE - can re-branch and retry
- Time: 30 minutes

**Wave 3 Rollback:** Similar - keep adapters, revert services
- Impact: NONE
- Time: 30 minutes

**All Waves:** Git allows easy rollback at any point
- `git reset --hard <wave-X-commit>`

---

## Success Criteria Checklist

### Architecture Compliance
- [ ] Zero JPA imports in `dominio/` package
- [ ] Zero JPA imports in `aplicacion/` package (except adapters)
- [ ] All 20 ports have implementations
- [ ] All dependencies point inward (Domain ← App ← Infra)
- [ ] No circular dependencies

### Service Compliance
- [ ] All 8 services inject domain ports (0 JPA repos)
- [ ] All DTO imports are from `aplicacion/dto/` or `presentacion/dto/`
- [ ] No JPA entities in method signatures
- [ ] Controllers use presentation DTOs only

### Testing
- [ ] All existing tests pass
- [ ] New adapter tests added (15+ tests)
- [ ] New use case tests added (5+ tests)
- [ ] Integration tests updated
- [ ] Overall coverage ≥ 90%

### Documentation
- [ ] ARCHITECTURE_BLUEPRINT.md matches implementation
- [ ] Architecture ADR (Architecture Decision Record) created
- [ ] README updated with new structure
- [ ] Development guide for new services

---

## Timeline Summary

```
WAVE EXECUTION TIMELINE

Week 1 (Days 1-2.5):
├─ Day 1: Wave 1 (Foundation - all ports & adapters)
└─ Day 1.5: Wave 2 (Pivot entities - 4 services)

Week 2 (Days 3-4.5):
├─ Day 3: Wave 3 (Complete refactoring - 8 services)
└─ Day 4: Wave 4 (Integration & validation)

Week 2.5 (Day 5-5.5):
└─ Contingency / polish / documentation

PARALLEL POTENTIAL:
- Wave 1 tasks can run 80% in parallel (only last task depends on others)
- Wave 2 services have no refactoring dependencies - fully parallel
- Wave 3 services can mostly run parallel (after Wave 1)

ESTIMATED CALENDAR TIME: 2 weeks (5-6 development days)
ESTIMATED COMMITTED ENGINEER TIME: 6 days total
```

---

## Risk Mitigation Summary

| Risk | Probability | Impact | Mitigation |
|------|-------------|--------|-----------|
| Breaking existing tests | LOW | HIGH | Run tests after each wave |
| Adapter bugs | MEDIUM | MEDIUM | Unit test each adapter |
| Circular dependency | LOW | HIGH | Architecture review before coding |
| Database migration | NONE | N/A | No schema changes needed |
| Performance regression | LOW | MEDIUM | Load test adapters |
| Build failure | LOW | HIGH | CI/CD gates on each commit |

---

## Contingency Tasks

If major issues are discovered:

1. **Create abstraction layer for adapter debugging**
   - Duration: 2 hours
   - Adds: `infraestructura/debugging/AdapterLogInterceptor.java`

2. **Implement fallback to direct JPA (rollback path)**
   - Duration: 1 hour
   - Adds: Conditional bean selection per port

3. **Create adapter test suite**
   - Duration: 3 hours
   - Adds: 20 integration test classes

---

## Conclusion

The refactoring is **LOW RISK** because:
1. Architecture skeleton already exists
2. All dependencies are well-known (no hidden couplings)
3. Each wave can be independently rolled back
4. Tests validate each step
5. Parallel execution reduces calendar time
6. No database schema changes needed

**Confidence Level:** HIGH ✓

The refactoring is **HIGH PRIORITY** because:
1. Current DIP violations prevent scalability
2. Service layer is becoming a monolith
3. Testing is difficult due to infrastructure coupling
4. New features will amplify issues

**Execution Start:** Ready immediately after Phase 1 sign-off

---

**Roadmap Complete. All 3 Task 1 documents ready for execution.**
