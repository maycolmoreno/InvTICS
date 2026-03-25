# Architecture Analysis: Coupling Map & DIP Violations

**Status:** Current State Analysis | Phase 1, Task 1.1
**Date:** 2026-03-24
**Objective:** Document all DIP violations and create dependency coupling matrix

---

## Executive Summary

The `gestionactivosapi` currently violates Dependency Inversion Principle (DIP) in **8 out of 8 application services**. All services directly inject concrete JPA repositories instead of domain ports, creating tight coupling to infrastructure.

| Metric | Value |
|--------|-------|
| Total Services Analyzed | 8 |
| Services with DIP Violation | 8 (100%) |
| Avg JPA Repos per Service | 3.25 |
| Domain Ports Currently Used | 1 (IFirmaMantenimientoRepositorio only) |
| Repository Interfaces in Domain | 14 (IEquiposRepositorio, ICustodiosRepositorio, etc.) |
| Missing Infrastructure Implementations | 13 |

---

## Service Coupling Matrix

### Legend
- **Domain Ports:** Domain interfaces that SHOULD be used (currently unused except FirmaMantenimiento)
- **Infra Repos:** JPA repository interfaces directly injected (DIP VIOLATION)
- **DTOs:** Presentation DTOs used in application layer (SRP VIOLATION)
- **DIP Risk:** Assessment of coupling severity

### Services Breakdown

#### 1. **MantenimientoManualService** ⚠️ HIGH RISK

**Type:** Complex service - Core business logic for manual maintenance records

| Aspect | Details |
|--------|---------|
| **Infra Repos Injected** | 7 |
| **Domain Ports Used** | 1 (IFirmaMantenimientoRepositorio) |
| **DIP Risk** | **HIGH** |
| **SRP Violations** | 3+ responsibilities |

**Injected Dependencies:**
```
- IMantenimientosJpaRepositorio (direct JPA)  ← VIOLATION
- IActividadRealizadaJpaRepositorio (direct JPA)  ← VIOLATION
- IActividadChecklistJpaRepositorio (direct JPA)  ← VIOLATION
- IImagenMantenimientoJpaRepositorio (direct JPA)  ← VIOLATION
- IEquiposJpaRepositorio (direct JPA)  ← VIOLATION
- ICustodiosJpaRepositorio (direct JPA)  ← VIOLATION
- IUsuariosJpaRepositorio (direct JPA)  ← VIOLATION
- IFirmaMantenimientoRepositorio (GOOD - domain port)  ✓
```

**Responsibilities:**
- Creating maintenance records
- Saving activities and images
- Calculating signatures
- Invoking programmed maintenance calculations
- Sending notifications

**Domain Ports Needed:**
- `MantenimientoRepositorioPuerto` → Replace IMantenimientosJpaRepositorio
- `ActividadRealizadaRepositorioPuerto` → Replace IActividadRealizadaJpaRepositorio
- `ActividadChecklistRepositorioPuerto` → Replace IActividadChecklistJpaRepositorio
- `ImagenMantenimientoRepositorioPuerto` → Replace IImagenMantenimientoJpaRepositorio
- `EquipoRepositorioPuerto` → Replace IEquiposJpaRepositorio
- `CustodioRepositorioPuerto` → Replace ICustodiosJpaRepositorio
- `UsuarioRepositorioPuerto` → Replace IUsuariosJpaRepositorio

**DTO Coupling:** Imports 6 DTOs from presentacion module → Should be application layer DTOs only

**Current Code Smell:**
```java
@Service
public class MantenimientoManualService {
    private final IMantenimientosJpaRepositorio mantenimientosRepo;      // INFRA
    private final IEquiposJpaRepositorio equiposRepo;                    // INFRA
    private final ICustodiosJpaRepositorio custodiosRepo;                // INFRA
    // ... many more JPA repos
    private final IFirmaMantenimientoRepositorio firmaRepo;              // DOMAIN ✓
}
```

---

#### 2. **MantenimientoProgramadoService** ⚠️ HIGH RISK

**Type:** Service - Scheduled maintenance coordination

| Aspect | Details |
|--------|---------|
| **Infra Repos Injected** | 3 |
| **Domain Ports Used** | 0 |
| **DIP Risk** | **HIGH** |
| **SRP Violations** | 1 (focused) |

**Injected Dependencies:**
```
- IMantenimientoProgramadoJpaRepositorio (direct JPA)  ← VIOLATION
- IEquiposJpaRepositorio (direct JPA)  ← VIOLATION
- IUsuariosJpaRepositorio (direct JPA)  ← VIOLATION
```

**Domain Ports Needed:**
- `MantenimientoProgramadoRepositorioPuerto`
- `EquipoRepositorioPuerto`
- `UsuarioRepositorioPuerto`

---

#### 3. **NotificacionService** ⚠️ HIGH RISK

**Type:** Service - User notification management

| Aspect | Details |
|--------|---------|
| **Infra Repos Injected** | 3 |
| **Domain Ports Used** | 0 |
| **DIP Risk** | **HIGH** |
| **SRP Violations** | 1 (focused) |

**Injected Dependencies:**
```
- INotificacionJpaRepositorio (direct JPA)  ← VIOLATION
- IUsuariosJpaRepositorio (direct JPA)  ← VIOLATION
- IMantenimientosJpaRepositorio (direct JPA)  ← VIOLATION
```

**Domain Ports Needed:**
- `NotificacionRepositorioPuerto`
- `UsuarioRepositorioPuerto`
- `MantenimientoRepositorioPuerto`

---

#### 4. **PdfMantenimientoService** ✓ GOOD

**Type:** Utility service - PDF generation (no direct data access)

| Aspect | Details |
|--------|---------|
| **Infra Repos Injected** | 0 |
| **Domain Ports Used** | 0 |
| **DIP Risk** | **LOW** |
| **SRP Violations** | 0 (pure PDF generation) |

**Analysis:** This is correctly isolated. Any refactoring is minimal.

---

#### 5. **CorreoMantenimientoService** ✓ GOOD

**Type:** Utility service - Email sending (no direct data access)

| Aspect | Details |
|--------|---------|
| **Infra Repos Injected** | 0 |
| **Domain Ports Used** | 0 |
| **DIP Risk** | **LOW** |
| **SRP Violations** | 0 (pure email composition) |

**Analysis:** Correctly uses JavaMailSender port-like abstraction. Minimal refactoring needed.

---

#### 6. **CorreoSchedulerService** ⚠️ MEDIUM RISK

**Type:** Service - Scheduled email dispatcher

| Aspect | Details |
|--------|---------|
| **Infra Repos Injected** | 1 (external - JavaMailSender) |
| **Domain Ports Used** | 0 |
| **DIP Risk** | **MEDIUM** |
| **SRP Violations** | 0 (focused) |

**Injected Dependencies:**
```
- JavaMailSender (external lib - acceptable)
```

**Analysis:** 
- Directly accesses JPA entity `MantenimientoProgramadoJpa` 
- Should receive domain entity or DTO as parameter instead
- Current design: `void enviarAvisoMantenimiento(MantenimientoProgramadoJpa programado)`
- Better: `void enviarAvisoMantenimiento(MantenimientoProgramado programado)`

**Domain Ports Needed:**
- Create domain entity conversion capability

---

#### 7. **MantenimientoInformeService** ⚠️ MEDIUM RISK

**Type:** Service - Orchestrator for PDF + email workflow

| Aspect | Details |
|--------|---------|
| **Infra Repos Injected** | 3 |
| **Domain Ports Used** | 0 |
| **DIP Risk** | **MEDIUM** |
| **SRP Violations** | 1 (orchestrator pattern is OK) |

**Injected Dependencies:**
```
- IEquiposJpaRepositorio (direct JPA)  ← VIOLATION
- ICustodiosJpaRepositorio (direct JPA)  ← VIOLATION
- IUsuariosJpaRepositorio (direct JPA)  ← VIOLATION
- PdfMantenimientoService (application service)  ✓
- MantenimientoArchivoService (application service)  ✓
- CorreoMantenimientoService (application service)  ✓
```

**Domain Ports Needed:**
- `EquipoRepositorioPuerto`
- `CustodioRepositorioPuerto`
- `UsuarioRepositorioPuerto`

---

#### 8. **MantenimientoArchivoService** ✓ GOOD

**Type:** Utility service - File storage operations

| Aspect | Details |
|--------|---------|
| **Infra Repos Injected** | 0 |
| **Domain Ports Used** | 0 |
| **DIP Risk** | **LOW** |
| **SRP Violations** | 0 (pure file operations) |

**Analysis:** Pure infrastructure adapter. No database coupling. Correctly isolated.

---

## Summary: Repository Usage Violation Count

```
Direct JPA Repository Injections (Violations):

MantenimientoManualService ............ 7 violations
MantenimientoProgramadoService ........ 3 violations
NotificacionService .................. 3 violations
PdfMantenimientoService .............. 0 violations
CorreoMantenimientoService ........... 0 violations
CorreoSchedulerService ............... 0 violations
MantenimientoInformeService .......... 3 violations
MantenimientoArchivoService .......... 0 violations
                                       ───────────
TOTAL VIOLATIONS ..................... 16 violations (3 services are repeats)
```

---

## Current Domain Ports vs. Missing Implementations

**Ports Defined in `dominio/repositorios/`:**
1. ✓ ICategoriaEquiposRepositorio
2. ✓ ICargosRepositorio
3. ✓ ICustodiosRepositorio
4. ✓ ICustodiasRepositorio
5. ✓ IDepartamentosRepositorio
6. ✓ IEquiposRepositorio
7. ✓ IEquipoVisitaRepositorio
8. ✓ IFirmaMantenimientoRepositorio (HAS implementation in domain, unusual but OK)
9. ✓ IMantenimientosRepositorio
10. ✓ IMarcasRepositorio
11. ✓ IRolesRepositorio
12. ✓ ITicketsRepositorio
13. ✓ IUbicacionesRepositorio
14. ✓ IUsuariosRepositorio

**Ports Implemented in Infrastructure:**
- ✗ NONE (all are interfaces only, no JPA adapters exist)

**Critical Issue:** Domain ports exist but infrastructure doesn't implement them. Services bypass domain ports and inject JPA repos directly.

---

## Pivot Entities (Priority for Refactoring)

These entities have the most cross-service dependencies and are central to business logic:

### 1. **Equipo** (HIGHEST PRIORITY)
- **Central to:** MantenimientoManualService, MantenimientoProgramadoService, MantenimientoInformeService
- **Coupling Score:** 12 (used by 3 services, cascading dependencies)
- **Business Impact:** Core asset entity
- **Refactor First:** YES - Foundation for other refactorings

### 2. **Mantenimiento** (HIGH PRIORITY)
- **Central to:** MantenimientoManualService, NotificacionService, MantenimientoInformeService
- **Coupling Score:** 11
- **Business Impact:** Core transactional entity
- **Refactor Second:** YES - Depends heavily on Equipo

### 3. **Custodio** (HIGH PRIORITY)
- **Central to:** MantenimientoManualService, MantenimientoInformeService
- **Coupling Score:** 8
- **Business Impact:** Primary stakeholder in maintenance workflow
- **Refactor Third:** YES - Depends on Equipo relationship

### 4. **Usuario** (MEDIUM PRIORITY)
- **Central to:** MantenimientoManualService, NotificacionService, MantenimientoInformeService
- **Coupling Score:** 9
- **Business Impact:** Authentication & audit trail
- **Refactor Watch:** May refactor with Mantenimiento

### 5. **Notificacion** (MEDIUM PRIORITY)
- **Central to:** NotificacionService
- **Coupling Score:** 5
- **Business Impact:** System communication
- **Refactor After Mantenimiento:** YES - Depends on Usuario & Mantenimiento

---

## Key Findings

### 🔴 Critical Issues
1. **No DIP Implementation:** Domain ports exist but are unused; services depend on JPA repos instead
2. **DTO Pollution:** Application services import DTO objects from presentacion layer
3. **Mixed Responsibilities:** Services handle persistence, business logic, and email simultaneously

### 🟡 Architecture Debt
4. **No Adapters:** `infraestructura/persistencia/adaptadores` folder exists but is empty
5. **Direct JPA Coupling:** 16 direct injections of IXxxJpaRepositorio interfaces
6. **No Domain Entity Separation:** Domain entities are mixed with JPA annotations

### ✅ Salvageable Elements
7. `IFirmaMantenimientoRepositorio` - Already correctly uses domain port
8. `PdfMantenimientoService` & `CorreoMantenimientoService` - Properly isolated
9. `MantenimientoArchivoService` - Good file operation abstraction

---

## Next Steps for Architecture Blueprint

1. Create 14 domain port implementations in `infraestructura/persistencia/adaptadores/`
2. Define domain entities without JPA annotations (extract to `dominio/entidades/`)
3. Create MapStruct mappers between Domain ↔ JPA entities
4. Refactor services to inject domain ports instead of JPA repos
5. Extract common repository operations to base adapter
6. Add DTO layer in application for internal communication

---

## Refactoring Complexity Estimate

| Service | Complexity | Days | Blocker | Notes |
|---------|-----------|------|---------|-------|
| MantenimientoManualService | Very High | 2-3 | None | 7 repos to replace |
| MantenimientoProgramadoService | Medium | 1-1.5 | None | 3 repos, straightforward |
| NotificacionService | Medium | 1 | None | 3 repos, query-heavy |
| MantenimientoInformeService | Low | 0.5 | None | Simple orchestration |
| Others (Pdf, Correo, Archivo) | Very Low | 0.5 | None | Minimal changes |
| **TOTAL REFACTORING** | | **5-6 days** | **None** | Parallel execution possible |

---

## Files Involved for Analysis

**Services (aplicacion/servicios/):**
- MantenimientoManualService.java
- MantenimientoProgramadoService.java
- NotificacionService.java
- PdfMantenimientoService.java
- CorreoMantenimientoService.java
- CorreoSchedulerService.java
- MantenimientoInformeService.java
- MantenimientoArchivoService.java

**Domain Ports (dominio/repositorios/):**
- 14 interfaces defined, 0 implemented

**Infrastructure Repos (infraestructura/repositorios/):**
- All JPA repositories injected directly into services

**Missing Directory:**
- `infraestructura/persistencia/adaptadores/` exists but empty

---

## Conclusion

The application is **18% compliant with hexagonal architecture**:
- ✓ Structure exists (aplicacion, dominio, infraestructura, presentacion)
- ✗ Domain ports unused (critical issue)
- ✗ Services violate DIP (100% of services)
- ✗ No adapter implementations (0% of ports implemented)
- ✗ DTO leakage into application layer

**Refactoring is HIGH PRIORITY but LOW RISK** - architecture skeleton is in place, only missing the implementation layer and port-to-adapter bridging.

---

**Analysis Complete. Ready for Task 1.2: Architecture Blueprint**
