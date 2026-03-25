# Quick Reference Guide - Hexagonal Refactoring

## Plan Overview

**Project:** gestionactivosapi (Spring Boot 4.0.2, Java 17, PostgreSQL)
**Current State:** 40% hexagonal, DIP violations, mixed responsibilities
**Target State:** Clean hexagonal with SOLID compliance
**Duration:** 7 weeks
**Effort:** ~150-200 hours Claude execution

---

## Three Files You Need to Read (in order)

1. **[HEXAGONAL_REFACTOR_PLAN.md](HEXAGONAL_REFACTOR_PLAN.md)** ← START HERE
   - Complete 7-phase breakdown
   - Task descriptions
   - Implementation guidelines
   - Code patterns
   - FAQ

2. **[PHASE3_DETAILED_EXAMPLE.md](PHASE3_DETAILED_EXAMPLE.md)** ← TEMPLATE
   - Complete refactored code example
   - Shows before → after transformation
   - Copy this pattern for all entities
   - Layer-by-layer walkthrough

3. **[PHASE_CHECKLISTS.md](PHASE_CHECKLISTS.md)** ← VALIDATION
   - Phase-by-phase completion checklist
   - Validation commands
   - Testing procedures
   - Troubleshooting guide

---

## 7-Week Timeline

| Week | Phase | Focus | Tasks | Output |
|------|-------|-------|-------|--------|
| 1 | Phase 1 | Design & Analysis | 3 | Blueprint documents |
| 2 | Phase 2 | Ports & Domain | 3 | 15+ port interfaces, enhanced domain entities |
| 3 | Phase 3 | Use Cases | 3 | 15+ use case implementations, tests |
| 4 | Phase 4 | Adapters | 3 | 15+ persistence adapters, MapStruct mappers |
| 5 | Phase 5 | Specialization | 2-3 | Notification, file generation, scheduler adapters |
| 6 | Phase 6 | JPA Separation | 2 | Parallel JPA entities, cleanup |
| 7 | Phase 7 | Testing | 3 | ArchUnit tests, integration tests, documentation |

---

## Key Decisions (Locked)

✓ **Hexagonal architecture** (ports/adapters pattern)
✓ **SOLID principles** (especially DIP, SRP, ISP)
✓ **Spring Data JPA** for persistence (no changing this)
✓ **MapStruct** for DTO/entity mapping (already in pom.xml)
✓ **Separate domain entities** from JPA entities (Equipo vs EquipoJpa)
✓ **Ports at domain boundary** (domain defines contracts)
✓ **Adapters in infrastructure** (implement ports)

---

## Architecture Layers (Target)

```
Presentation Layer
    │ (Controllers, DTOs, Mappers)
    ↓
Application Layer
    │ (Use Cases, Application Services)
    ↓
Domain Layer
    │ (Entities, Business Logic, Ports/Interfaces)
    ↓ (depends on abstractions)
Infrastructure Layer
    │ (Adapters, JPA Entities, Repositories, External Services)
```

**Dependency flow:** HIGH-LEVEL depends on abstractions (ports) owned by DOMAIN.
LOW-LEVEL (adapters) implements the abstractions.

---

## File Structure (Final)

```
src/main/java/com/uisrael/gestionactivosapi/
├── dominio/
│   ├── entidades/           ← Domain entities (business logic)
│   ├── puertos/             ← Port interfaces (contracts) **NEW**
│   ├── valoresobjeto/       ← Enums, value objects
│   └── excepciones/         ← Domain-specific exceptions
├── aplicacion/
│   ├── casosuso/
│   │   ├── entradas/        ← Use case interfaces (keep)
│   │   └── impl/            ← Use case implementations **NEW**
│   └── servicios/           ← Application services
├── infraestructura/
│   ├── adaptadores/         ← Port implementations **NEW**
│   ├── persistencia/
│   │   ├── jpa/             ← JPA entities (separate from domain) **REFACTORED**
│   │   └── mapeadores/      ← MapStruct mappers
│   ├── repositorios/        ← Spring Data JPA interfaces
│   ├── configuracion/       ← Spring bean registration
│   └── [other layers]
└── presentacion/
    ├── controladores/       ← REST controllers (updated to use use cases)
    └── dto/
```

---

## Phase 1: Preparation (Week 1)

**Do NOT write code yet.** Design the architecture first.

### What to deliver:
- `ARCHITECTURE_ANALYSIS.md` — Mapping of current service dependencies
- `ARCHITECTURE_BLUEPRINT.md` — Target architecture with all layers defined
- `ROADMAP.md` — Phasing and dependency graph
- `REFACTORING_WAVES.md` — Order of implementation to avoid breaking changes

### Commands:
```bash
cd /path/to/gestionactivosapi

# Analyze current structure
find src -name "*.java" | wc -l  # Total Java files
find src -path "*/aplicacion/servicios/*" -name "*.java" | wc -l  # Count services

# Check current architecture
grep -r "IEquiposJpaRepositorio" src/main/java/com/uisrael/gestionactivosapi/aplicacion/
# Count how many services inject JPA repos (should be many before refactor)
```

---

## Phase 2: Foundation (Week 2)

**Create ports and enhance domain entities.**

### Primary tasks:

1. **Create all port interfaces** in `dominio/puertos/`
   - `EquipoRepositorioPuerto.java`
   - `MantenimientoRepositorioPuerto.java`
   - `NotificacionPuerto.java`
   - (15+ total)

   **Rule:** No Spring imports, no JPA imports, use domain entities only

2. **Enhance domain entities** in `dominio/entidades/`
   - Add business logic methods
   - Add value objects
   - Remove JPA annotations
   - Add domain validation

3. **Create domain exceptions** in `dominio/excepciones/`
   - `ValidacionNuegoException`
   - `EquipoYaExisteException`
   - (10+ total)

### Validation:
```bash
# Verify no Spring in ports
grep -r "@Autowired\|@Component" src/main/java/com/uisrael/gestionactivosapi/dominio/puertos/
# Should return 0

# Verify no JPA in domain
grep -r "@Entity\|@Column" src/main/java/com/uisrael/gestionactivosapi/dominio/entidades/
# Should return 0

# Test it compiles
mvn clean compile
```

---

## Phase 3: Use Cases (Week 3)

**Implement hexagonal workflows via use cases.**

### Primary task: Refactor services to use cases

Example (copy this pattern):
```java
// OLD (problematic)
@Service
public class EquiposService {
    @Autowired
    private IEquiposJpaRepositorio repo;  // ❌ Concrete infra
    
    public EquipoDTO crear(CrearEquipoRequestDTO request) {
        // Mixed logic, hard to test
    }
}

// NEW (clean)
@Component
@RequiredArgsConstructor
public class CrearEquipoUseCaseImpl implements ICrearEquipoUseCase {
    private final EquipoRepositorioPuerto repo;  // ✓ Port abstraction
    private final NotificacionPuerto notif;
    
    @Override
    @Transactional
    public EquipoResponseDTO ejecutar(CrearEquipoRequestDTO request) {
        // Clear workflow: validate → create → persist → notify
    }
}
```

### Validation:
```bash
# Verify use cases use ports, not JPA repos
grep -r "IEquiposJpaRepositorio" src/main/java/com/uisrael/gestionactivosapi/aplicacion/casosuso/impl/
# Should return 0

grep -r "RepositorioPuerto" src/main/java/com/uisrael/gestionactivosapi/aplicacion/casosuso/impl/
# Should return many (one per port injection)

# Test
mvn clean test
```

---

## Phase 4: Adapters (Week 4)

**Implement ports with concrete infrastructure.**

### Primary tasks:

1. **Create adapters** (in `infraestructura/adaptadores/`)
   ```java
   @Repository
   @RequiredArgsConstructor
   public class EquipoRepositorioAdapter implements EquipoRepositorioPuerto {
       private final IEquiposJpaRepositorio jpaRepo;
       private final EquipoMapperJpa mapper;
       
       @Override
       public Equipo guardar(Equipo equipo) {
           EquipoJpa jpa = mapper.toJpa(equipo);
           EquipoJpa saved = jpaRepo.save(jpa);
           return mapper.toDomain(saved);
       }
       // ... implement all port methods
   }
   ```

2. **Create MapStruct mappers** (entity conversion)
   ```java
   @Mapper(componentModel = "spring")
   public interface EquipoMapperJpa {
       EquipoJpa toJpa(Equipo domain);
       Equipo toDomain(EquipoJpa jpa);
   }
   ```

3. **Register in Spring configuration**
   ```java
   @Configuration
   public class PersistenciaConfig {
       @Bean
       public EquipoRepositorioPuerto equipoRepo(
           IEquiposJpaRepositorio jpa, EquipoMapperJpa mapper) {
           return new EquipoRepositorioAdapter(jpa, mapper);
       }
   }
   ```

### Validation:
```bash
# Verify adapters implement ports
grep -r "implements.*RepositorioPuerto" src/main/java/com/uisrael/gestionactivosapi/infraestructura/adaptadores/
# Should return 15+

# Test wiring
mvn test -Dtest=AdapterWiringTest

# Start application
mvn spring-boot:run
# Should start without "No qualifying bean" errors
```

---

## Phase 5: Specialization Adapters (Week 5)

**Implement non-repository ports (notifications, files, etc.)**

- `NotificacionAdapter` (email/SMS)
- `ArchivoGeneradorAdapter` (PDF/Excel)
- `SchedulerAdapter` (background tasks)
- `AuditoriaAdapter` (logging)

Each follows the same pattern: interface (port) in domain, implementation (adapter) in infrastructure.

---

## Phase 6: JPA Separation (Week 6)

**Create parallel JPA entities, remove from domain.**

### The big refactor:

```java
// DOMAIN entity (no JPA annotations)
public class Equipo {
    private Integer id;
    private String nombre;
    // ... no @Entity, @Column, @OneToMany
}

// JPA entity (all annotations here)
@Entity
@Table(name = "equipos")
public class EquipoJpa {
    @Id
    private Integer idEquipo;
    @Column
    private String nombre;
    // ... all JPA stuff
}

// Mapper converts between them
@Mapper(componentModel = "spring")
public interface EquipoMapperJpa {
    EquipoJpa toJpa(Equipo domain);
    Equipo toDomain(EquipoJpa jpa);
}
```

### Validation:
```bash
# Domain should have NO JPA annotations
grep -r "@Entity\|@Column\|@OneToMany" src/main/java/com/uisrael/gestionactivosapi/dominio/
# Should return 0

# JPA layer should have all annotations
grep -r "@Entity" src/main/java/com/uisrael/gestionactivosapi/infraestructura/persistencia/jpa/
# Should return many

# All tests still pass
mvn clean test
```

---

## Phase 7: Testing & Documentation (Week 7)

### ArchUnit Tests (enforce architecture)
```java
@ArchTest
public static final ArchRule noDomainDependsOnJpa =
    noClasses().that().resideInAPackage("..dominio..")
        .should().dependOnClassesThat()
        .resideInAPackage("..jpa..");
```

### Integration Tests (end-to-end flows)
```java
@SpringBootTest
public class CrearEquipoIntegrationTest {
    @Test
    void testCrearEquipoViaHttp() {
        // HTTP request → Controller → Use case → Domain → Adapter → Database
    }
}
```

### Documentation
- SOLID compliance report
- Architecture decision record (ADR)
- Team training materials

---

## Quick Command Reference

```bash
# Full build and test
mvn clean test

# Run specific test class
mvn test -Dtest=CrearEquipoUseCaseTest

# Start application
mvn spring-boot:run

# Generate test coverage report
mvn clean test jacoco:report
# Open target/site/jacoco/index.html

# Check for Spring imports in domain (should be 0)
grep -r "@Autowired\|@Component\|@Qualifier" src/main/java/*/dominio/

# Check for JPA imports in domain (should be 0)
grep -r "jakarta.persistence\|javax.persistence" src/main/java/*/dominio/

# Count architecture layers
echo "Domain:" && find src -path "*/dominio/*" -name "*.java" | wc -l
echo "Application:" && find src -path "*/aplicacion/*" -name "*.java" | wc -l
echo "Infrastructure:" && find src -path "*/infraestructura/*" -name "*.java" | wc -l
echo "Presentation:" && find src -path "*/presentacion/*" -name "*.java" | wc -l
```

---

## Success Indicators (After Phase 7)

✅ **Architecture:**
- [ ] Zero JPA imports in domain/application
- [ ] All services depend on ports
- [ ] 4 clear layers (Domain → Application → Infrastructure ← Presentation)
- [ ] All ports have implementations (adapters)

✅ **Testing:**
- [ ] All unit tests pass
- [ ] All integration tests pass
- [ ] ArchUnit tests enforce rules
- [ ] Coverage: Domain 80%+, App 75%+, Infra 60%+

✅ **Usability:**
- [ ] Application starts without errors
- [ ] New developer understands architecture in < 2 hours
- [ ] Adding new feature requires no architecture changes
- [ ] Can swap storage (JPA → MongoDB) without changing domain/app

✅ **Documentation:**
- [ ] Architecture blueprint documented
- [ ] SOLID compliance verified
- [ ] Code examples provided (this guide)
- [ ] Team trained

---

## Troubleshooting Quick Links

**"No qualifying bean" error?**
→ See Adapter wiring in Phase 4 or troubleshooting in PHASE_CHECKLISTS.md

**"Cannot resolve symbol" error?**
→ Check Phase 1 COUPLING_MAP for old service references that need updating

**Test fails with "Port is null"?**
→ Add `@MockBean` annotation to port in test class

**MapStruct mapper not generating?**
→ Verify `mapstruct-processor` in pom.xml build section

**Entity mapping broken?**
→ Check `@Mapping(target=..., source=...)` in mapper for field name mismatches

---

## Files in This Planning Directory

```
.planning/
├── HEXAGONAL_REFACTOR_PLAN.md       ← Main plan (read first)
├── PHASE3_DETAILED_EXAMPLE.md       ← Code template
├── PHASE_CHECKLISTS.md              ← Validation checkpoints
├── README.md                        ← This file
├── phases/
│   ├── phase-01/
│   │   └── SUMMARY.md               ← Created after Phase 1
│   ├── phase-02/
│   │   └── SUMMARY.md               ← Created after Phase 2
│   ├── ... (through Phase 7)
```

---

## Getting Started (Today)

1. **Read** `HEXAGONAL_REFACTOR_PLAN.md` (the main blueprint)
2. **Understand** Phase 1 tasks (design phase)
3. **Allocate** 1-2 weeks for Phase 1 work
4. **Create** the analysis documents listed in Phase 1
5. **Review** this plan with your team/architect
6. **Proceed** to Phase 2 once Phase 1 is approved

---

## Key Contacts & References

- **Hexagonal Architecture:** Alistair Cockburn (original author)
- **SOLID Principles:** Robert C. Martin ("Uncle Bob")
- **Spring Best Practices:** spring.io documentation
- **MapStruct:** mapstruct.org

---

## Final Words

This plan is **detailed and executable**. Each phase builds on the previous. Don't skip phases. Don't rush. Follow the code examples from `PHASE3_DETAILED_EXAMPLE.md` for consistency.

The refactoring is **high-impact but low-risk** because:
- ✓ New code is separate from old code (can coexist)
- ✓ Tests verify each phase independently
- ✓ Architecture is enforced (ArchUnit prevents regression)
- ✓ Gradual rollout (Wave 1 → Wave 2 → Wave 4)

You'll have a **clean, SOLID-compliant, hexagonal architecture** at the end.

---

**Questions? See:**
- HEXAGONAL_REFACTOR_PLAN.md → FAQ section
- PHASE_CHECKLISTS.md → Troubleshooting section
- PHASE3_DETAILED_EXAMPLE.md → Code explanations

**Ready to start Phase 1?** Create the three documents listed in Task 1.1, 1.2, 1.3.

**Good luck! 🚀**

