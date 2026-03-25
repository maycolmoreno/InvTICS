# Phase Execution Checklists
## Hexagonal Refactoring Validation & Progress Tracking

---

## PHASE 1: Architecture Design & Refactoring Strategy

### Task 1.1 Completion Checklist

- [ ] All services analyzed for concrete repository dependencies
- [ ] Spreadsheet created: Service | Domain Ports | Infra Repos | DTOs | Risk Level
- [ ] Covers 80%+ of services in `aplicacion/servicios/`
- [ ] Pivot entities identified (Equipos, Mantenimientos, Custodios prioritized)
- [ ] Document created: `ARCHITECTURE_ANALYSIS.md`
- [ ] Spreadsheet created: `COUPLING_MAP.xlsx`
- [ ] Document created: `PIVOT_ENTITIES.md`

**Validation Commands:**
```bash
# Count services
find src/main/java -path "*/aplicacion/servicios/*.java" -type f | wc -l

# Verify documents exist
ls -la .planning/ARCHITECTURE_ANALYSIS.md
ls -la .planning/COUPLING_MAP.xlsx
ls -la .planning/PIVOT_ENTITIES.md
```

---

### Task 1.2 Completion Checklist

- [ ] Target architecture documented in `ARCHITECTURE_BLUEPRINT.md`
- [ ] All 4 layers clearly defined (Domain, Application, Infrastructure, Presentation)
- [ ] Port interfaces listed with method signatures (min. 15 ports)
- [ ] Entity mapping strategy documented (Domain vs JPA approach)
- [ ] Final package structure defined (ASCII tree)
- [ ] Repository ports defined
- [ ] Specialized service ports defined (Notification, FileGeneration, etc.)
- [ ] Visual diagram included (ASCII art acceptable)

**Visual Check:**
- [ ] Blueprint shows flow: Presentation → Application → Domain ← Infrastructure
- [ ] Ports are at domain boundary (Domain level, not infrastructure)
- [ ] All ports implement Interface Segregation (no god interfaces)

---

### Task 1.3 Completion Checklist

- [ ] Refactoring roadmap with phases documented
- [ ] Phase sequence clearly defined
- [ ] Dependency graph created (no circular dependencies)
- [ ] Refactoring waves identified:
  - [ ] Wave 1: Foundation (ports + base domain entities)
  - [ ] Wave 2: Core services (Equipos, Mantenimientos, Custodios)
  - [ ] Wave 3: Adapters for Wave 2
  - [ ] Wave 4: Remaining services
- [ ] Safe vs dangerous refactor zones identified
- [ ] Effort estimation provided per phase
- [ ] Risk mitigation strategies documented

**Validation:**
```bash
# Verify files exist
ls -la .planning/ROADMAP.md
ls -la .planning/DEPENDENCY_GRAPH.txt
ls -la .planning/REFACTORING_WAVES.md
```

---

## PHASE 2: Port Interfaces & Domain Layer Enhancement

### Task 2.1 Completion Checklist

- [ ] Package created: `dominio/puertos/`
- [ ] 15+ repository port interfaces created
- [ ] 5+ specialized port interfaces created (services)
- [ ] **All port files:** No Spring imports (@Autowired, @Component)
- [ ] **All port files:** No JPA imports
- [ ] **All port files:** Use domain entities (not DTOs)
- [ ] Interface Segregation enforced (max 8 methods per port)
- [ ] JavaDoc documentation on all methods
- [ ] Ports compile without errors: `mvn clean compile`

**Validation Commands:**
```bash
# Verify no Spring imports in ports
grep -r "@Autowired\|@Component\|@Qualifier" src/main/java/com/uisrael/gestionactivosapi/dominio/puertos/
# Should return 0 matches

# Verify no JPA imports
grep -r "jakarta.persistence\|javax.persistence\|JpaRepository" src/main/java/com/uisrael/gestionactivosapi/dominio/puertos/
# Should return 0 matches

# Count port files
find src/main/java -path "*/dominio/puertos/*" -name "*.java" -type f | wc -l
# Should be 15+

# Verify compilation
mvn clean compile
```

---

### Task 2.2 Completion Checklist

- [ ] Domain entities updated with business logic methods (2+ per entity)
- [ ] Value objects created (Email, Nombre, Estado enum)
- [ ] Constructors enforce invariants (validation)
- [ ] Domain methods testable without Spring/JPA
- [ ] **Domain entities:** No JPA annotations (keep structure, remove @Entity, @Column, @OneToMany)
- [ ] Encapsulation enforced (setters are minimal/private)
- [ ] Business logic moved OUT of services into entities
- [ ] Aggregate roots identified
- [ ] Entity files compile: `mvn clean compile`

**Validation Commands:**
```bash
# Verify no @Entity in domain layer
grep -r "@Entity\|@Column\|@OneToMany\|@ManyToOne" src/main/java/com/uisrael/gestionactivosapi/dominio/entidades/
# Should return 0 matches

# Verify domain methods exist
grep -r "public void\|public boolean" src/main/java/com/uisrael/gestionactivosapi/dominio/entidades/*.java | head -20
# Should show domain business logic

# Compile
mvn clean compile
```

---

### Task 2.3 Completion Checklist

- [ ] Package created: `dominio/excepciones/`
- [ ] Base exception created: `ExcepcionDominio`
- [ ] 10+ specific domain exceptions created
- [ ] All exceptions are domain-specific (not infrastructure)
- [ ] Clear, actionable error messages
- [ ] All exceptions compile: `mvn clean compile`

**Domain Exception List:**
- [ ] `ValidacionNuegoException`
- [ ] `EquipoNoEncontradoException`
- [ ] `EquipoYaExisteException`
- [ ] `TransicionEstadoIlegalException`
- [ ] `EquipoNoDisponibleException`
- [ ] (7+ more, entity-specific)

---

## PHASE 3: Refactor Core Services to Use Ports

### Task 3.1 Completion Checklist

- [ ] Package created: `aplicacion/casosuso/impl/`
- [ ] 15-20 use case implementations created (one per use case interface)
- [ ] 3 pivot use cases fully implemented:
  - [ ] CrearEquipoUseCaseImpl (follows example pattern)
  - [ ] ObtenerEquiposUseCaseImpl
  - [ ] ObtenerEquipoPorIdUseCaseImpl
  - [ ] Equivalentes para Mantenimientos
  - [ ] Equivalentes para Custodios
- [ ] **All use cases:** Depend on ports (EquipoRepositorioPuerto), NOT JPA repos
- [ ] **All use cases:** Have single @Transactional on execute method
- [ ] **All use cases:** Use MapStruct mappers (not manual conversion)
- [ ] **All use cases:** Have clear workflow (validate → create → apply rules → persist → notify → return)
- [ ] MapStruct mappers created/updated for DTO conversion

**Validation Commands:**
```bash
# NO JPA repository imports in use cases
grep -r "IEquiposJpaRepositorio\|IMantenimientosJpaRepositorio" src/main/java/com/uisrael/gestionactivosapi/aplicacion/casosuso/impl/
# Should return 0 matches

# Verify port imports instead
grep -r "RepositorioPuerto" src/main/java/com/uisrael/gestionactivosapi/aplicacion/casosuso/impl/ | wc -l
# Should be 15+

# Compile
mvn clean compile
```

---

### Task 3.2 Completion Checklist

- [ ] Controllers updated to inject use cases (not services)
- [ ] Old service injections removed
- [ ] Exception handlers added for domain exceptions
- [ ] Method calls use `useCase.ejecutar(request)` pattern
- [ ] 5-7 controller classes updated
- [ ] Build succeeds: `mvn clean compile`
- [ ] Controllers inject:
  - [ ] ICrearEquipoUseCase
  - [ ] IObtenerEquiposUseCase
  - [ ] IObtenerEquipoPorIdUseCase
  - [ ] (other refactored use cases)

**Validation Commands:**
```bash
# Verify use case interface injection (should see many)
grep -r "private final I.*UseCase\|@Autowired.*I.*UseCase" src/main/java/com/uisrael/gestionactivosapi/presentacion/controladores/
# Should see multiple

# Verify NO old service injection
grep -r "private final.*Service\|@Autowired.*Service" src/main/java/com/uisrael/gestionactivosapi/presentacion/controladores/ | grep -v "UnitOfWorkService\|MailService"\
# Should return minimal (if any, document why they're needed)

# Compile
mvn clean compile
```

---

### Task 3.3 Completion Checklist

- [ ] Test package created: `src/test/java/.../aplicacion/casosuso/impl/`
- [ ] 3-5 test classes created (one per refactored use case)
- [ ] Each test class has 5+ test cases (happy path + errors)
- [ ] Test cases include:
  - [ ] Happy path (valid input)
  - [ ] Invalid input (validation fails)
  - [ ] Business rule violation
  - [ ] Side effect verification (mocks called)
  - [ ] Exception handling
- [ ] All tests use Mockito to mock ports
- [ ] All tests pass: `mvn test -Dtest=*UseCaseTest`
- [ ] Mock port calls verified with `verify()`
- [ ] Test coverage: Application layer 75%+

**Validation Commands:**
```bash
# Run tests
mvn test -Dtest=*UseCaseTest

# Verify test count
find src/test/java -path "*casosuso/impl/*Test.java" -type f | wc -l
# Should be 3+

# Check coverage (if using JaCoCo)
mvn clean test jacoco:report
# Check target/site/jacoco/ for coverage report
```

---

## PHASE 4: Create Persistence Adapters

### Task 4.1 Completion Checklist

- [ ] Package created: `infraestructura/adaptadores/`
- [ ] 15+ adapter classes created (one per port)
- [ ] Adapters implement repository ports:
  - [ ] EquipoRepositorioAdapter (implements EquipoRepositorioPuerto)
  - [ ] MantenimientoRepositorioAdapter
  - [ ] CustodioRepositorioAdapter
  - [ ] UsuarioRepositorioAdapter
  - [ ] (10+ more)
- [ ] **All adapters:** Marked with `@Repository`
- [ ] **All adapters:** Inject JpaRepository (NOT domain ports)
- [ ] **All adapters:** Use mapper to convert JpaEntity ↔ Domain entity
- [ ] **All adapters:** No business logic (only mapping + JPA calls)
- [ ] **All adapters:** Implement port interface exactly
- [ ] Compilation succeeds: `mvn clean compile`

**Validation Commands:**
```bash
# Verify adapters implement ports
grep -r "implements.*RepositorioPuerto" src/main/java/com/uisrael/gestionactivosapi/infraestructura/adaptadores/
# Should show 15+ matches

# Verify no business logic (no complex methods, only persistence)
wc -l src/main/java/com/uisrael/gestionactivosapi/infraestructura/adaptadores/*.java
# Each adapter should be 50-100 lines (simple converters)

# Compile
mvn clean compile
```

---

### Task 4.2 Completion Checklist

- [ ] Package: `infraestructura/persistencia/mapeadores/`
- [ ] 10+ MapStruct mappers created (Entity ↔ JPA)
  - [ ] EquipoMapperJpa
  - [ ] MantenimientoMapperJpa
  - [ ] CustodioMapperJpa
  - [ ] (7+ more)
- [ ] Each mapper has:
  - [ ] `toJpa(Domain)` method
  - [ ] `toDomain(Jpa)` method
  - [ ] Proper `@Mapping` annotations for complex conversions
  - [ ] Enum conversion handling (if applicable)
- [ ] Mappers compile: `mvn clean compile`
- [ ] No manual mapping in adapters (all via MapStruct)

**Validation Commands:**
```bash
# Verify mappers exist
find src/main/java -path "*/persistencia/mapeadores/*MapperJpa.java" -type f | wc -l
# Should be 10+

# Verify MapStruct generates implementation
find target/generated-sources -name "*MapperJpaImpl.java" -type f | wc -l
# Should match number of mappers

# Compile
mvn clean compile
```

---

### Task 4.3 Completion Checklist

- [ ] Spring configuration file created: `infraestructura/configuracion/PersistenciaConfig.java`
- [ ] All adapters registered as Spring beans (either @Bean method or @Component)
- [ ] Each adapter registered as implementation of its port:
  ```
  EquipoRepositorioPuerto → EquipoRepositorioAdapter
  MantenimientoRepositorioPuerto → MantenimientoRepositorioAdapter
  (etc.)
  ```
- [ ] No "No qualifying bean" errors on startup
- [ ] Application context loads successfully: `mvn spring-boot:run`
- [ ] Integration test verifies wiring (ports can be injected)

**Validation Test:**
```java
@SpringBootTest
class AdapterWiringTest {
    @Autowired
    private EquipoRepositorioPuerto equipoRepo;
    
    @Test
    void testPortInjectsAsAdapter() {
        assertThat(equipoRepo)
            .isInstanceOf(EquipoRepositorioAdapter.class);
    }
}
```

**Validation Commands:**
```bash
# Run wiring test
mvn test -Dtest=AdapterWiringTest

# Try starting application (should not error)
mvn spring-boot:run -DskipTests

# Check logs for "No qualifying bean" errors (should be 0)
```

---

## PHASE 5: Specialization Adapters

### Task 5.1 Completion Checklist

- [ ] `NotificacionAdapter` created in `infraestructura/adaptadores/`
- [ ] Implements `NotificacionPuerto` interface
- [ ] Uses Spring's `JavaMailSender` or similar
- [ ] Methods implemented:
  - [ ] `enviarEmail()`
  - [ ] `enviarSms()` (if applicable)
  - [ ] `notificarEquipoCreado()`
  - [ ] (other notification methods)
- [ ] Error handling graceful (logs, doesn't crash)
- [ ] Email templates created in `resources/templates/email/`
- [ ] Adapter can be injected into use cases

---

### Task 5.2 Completion Checklist

- [ ] `ArchivoGeneradorAdapter` created
- [ ] Implements `ArchivoGeneradorPuerto`
- [ ] Dependencies: iText (PDF), Apache POI (Excel)
- [ ] Methods implemented:
  - [ ] `generarPdf()`
  - [ ] `generarExcel()`
- [ ] Report templates created (if separate from code)
- [ ] Can be injected into use cases

---

### Task 5.3 Completion Checklist

- [ ] Additional adapters created/stubbed:
  - [ ] SchedulerAdapter (using @Scheduled)
  - [ ] LogAuditoriaAdapter
  - [ ] AlmacenamientoAdapter (file storage)
  - [ ] (others as needed)
- [ ] All adapters can be injected (no "No qualifying bean" errors)
- [ ] Application still starts with all adapters

---

## PHASE 6: JPA Entity Separation & Cleanup

### Task 6.1 Completion Checklist

- [ ] For each domain entity, create parallel JPA entity:
  - [ ] Equipo → EquipoJpa (separate file)
  - [ ] Mantenimiento → MantenimientoJpa
  - [ ] Custodio → CustodioJpa
  - [ ] (all major entities)
- [ ] **Domain entities:** NO JPA annotations (@Entity, @Column, @OneToMany)
- [ ] **JPA entities:** ALL JPA annotations present
- [ ] **JPA entities:** Located in `infraestructura/persistencia/jpa/`
- [ ] MapStruct mappers convert between both
- [ ] All existing database queries still work
- [ ] Full test suite passes: `mvn clean test`

**Validation Commands:**
```bash
# Verify no @Entity in domain
grep -r "@Entity\|@Column\|@OneToMany" src/main/java/com/uisrael/gestionactivosapi/dominio/entidades/
# Should return 0 matches

# Verify @Entity only in JPA layer
grep -r "@Entity" src/main/java/com/uisrael/gestionactivosapi/infraestructura/persistencia/jpa/
# Should return many matches

# Verify mappers handle conversion
grep -r "toJpa\|toDomain" src/main/java/com/uisrael/gestionactivosapi/infraestructura/persistencia/mapeadores/
# Should see mapper calls in adapters

# Test
mvn clean test
```

---

### Task 6.2 Completion Checklist

- [ ] Identify which old services have been fully replaced by use cases
- [ ] Delete old service files (if fully replaced):
  - [ ] `aplicacion/servicios/OldService.java` (only if 100% replaced)
- [ ] OR mark deprecated if partially used:
  - [ ] Add `@Deprecated` annotation
  - [ ] Document migration path
  - [ ] Add JavaDoc: "Use XxxUseCase instead"
- [ ] Remove NO_LONGER_NEEDED_Service.java files
- [ ] Build succeeds: `mvn clean compile`
- [ ] No "symbol not found" errors

---

## PHASE 7: Testing, Documentation & Full Integration

### Task 7.1 Completion Checklist

- [ ] ArchUnit dependency added to pom.xml
- [ ] Test class created: `ArchitectureComplianceTest.java`
- [ ] Rules implemented:
  - [ ] No JPA imports in domain layer
  - [ ] Services only depend on ports
  - [ ] Adapters implement ports
  - [ ] No cyclic dependencies
  - [ ] (other rules as needed)
- [ ] All architecture tests pass: `mvn test -Dtest=ArchitectureComplianceTest`
- [ ] Tests fail if violations are introduced (enforces compliance)

**Sample Rule Verification:**
```bash
# Rule: No JPA in domain
mvn test -Dtest=ArchitectureComplianceTest

# If passes:
# ✓ Architecture rules enforced
```

---

### Task 7.2 Completion Checklist

- [ ] Integration test package created: `src/test/java/.../integration/`
- [ ] 10+ integration tests created
- [ ] Each test covers:
  - [ ] Happy path (successful flow)
  - [ ] Error scenario
- [ ] Tests use `@SpringBootTest` + real database (H2 test DB)
- [ ] Tests use `MockMvc` for HTTP requests
- [ ] Mock external services (email, file storage) but test real domain logic
- [ ] All integration tests pass: `mvn test -Dtest=*IntegrationTest`
- [ ] Tests are fast (< 1 second each)

**Test Scenarios Covered:**
- [ ] Create Equipo flow (HTTP → DB)
- [ ] Create Mantenimiento flow
- [ ] Create Custodio flow
- [ ] Update operations
- [ ] Delete operations
- [ ] Error cases (validation, duplicates, not found)
- [ ] Side effects (notifications mocked)

---

### Task 7.3 Completion Checklist

**SOLID Documentation:**

- [ ] Document created: `.planning/SOLID_COMPLIANCE_REPORT.md`
- [ ] Each SOLID principle verified:
  - [ ] **SRP:** Classes have one reason to change (evidence: file listing by concern)
  - [ ] **OCP:** Can add new adapters without modifying existing code (evidence: example)
  - [ ] **LSP:** Implementations substitute for abstractions (evidence: test with swapped adapter)
  - [ ] **ISP:** Small, focused interfaces (evidence: port method lists)
  - [ ] **DIP:** Depend on abstractions, not concretions (evidence: grep results, import analysis)

**Report Contents:**
- [ ] Architecture diagram (ASCII art showing layers and dependencies)
- [ ] Evidence for each SOLID principle
- [ ] Grep results showing compliance
- [ ] Code examples demonstrating each principle
- [ ] Metrics:
  - [ ] Cyclomatic complexity (if available)
  - [ ] Test coverage by layer (target: Domain 80%+, App 75%+, Infra 60%+)
  - [ ] Number of classes per layer
  - [ ] Dependency count before/after refactoring

**Metrics Collection:**
```bash
# Test coverage (if using JaCoCo)
mvn clean test jacoco:report

# Line count per layer
echo "Domain:"
find src/main/java -path "*/dominio/*" -name "*.java" -type f -exec wc -l {} + | tail -1

echo "Application:"
find src/main/java -path "*/aplicacion/*" -name "*.java" -type f -exec wc -l {} + | tail -1

echo "Infrastructure:"
find src/main/java -path "*/infraestructura/*" -name "*.java" -type f -exec wc -l {} + | tail -1
```

---

### Final Verification Checklist

**All Phases Complete?**
- [ ] Phase 1: Blueprint done
- [ ] Phase 2: Ports + domain entities done
- [ ] Phase 3: Use cases implemented
- [ ] Phase 4: Adapters created
- [ ] Phase 5: Specialization adapters done
- [ ] Phase 6: JPA separation done
- [ ] Phase 7: Tests + documentation done

**Code Quality:**
- [ ] `mvn clean test` passes (all tests)
- [ ] `mvn clean compile` succeeds (no warnings)
- [ ] No "symbol not found" errors
- [ ] No "No qualifying bean" errors on startup
- [ ] Application starts successfully: `mvn spring-boot:run`

**Architecture Validation:**
- [ ] Zero JPA imports in domain/application layers
  ```bash
  grep -r "jakarta.persistence\|javax.persistence" src/main/java/com/uisrael/gestionactivosapi/dominio/ src/main/java/com/uisrael/gestionactivosapi/aplicacion/
  # Should return 0
  ```
- [ ] Zero direct JPA repository imports in use cases
  ```bash
  grep -r "IEquiposJpaRepositorio\|IMantenimientosJpaRepositorio" src/main/java/com/uisrael/gestionactivosapi/aplicacion/
  # Should return 0
  ```
- [ ] All services depend on ports
  ```bash
  grep -r "RepositorioPuerto" src/main/java/com/uisrael/gestionactivosapi/aplicacion/casosuso/impl/
  # Should see many matches
  ```

**Documentation Complete:**
- [ ] `HEXAGONAL_REFACTOR_PLAN.md` (this plan)
- [ ] `PHASE3_DETAILED_EXAMPLE.md` (refactoring example)
- [ ] `SOLID_COMPLIANCE_REPORT.md` (compliance evidence)
- [ ] Architecture blueprint in Phase 1 output
- [ ] Code comments/JavaDoc on key classes

**Success Criteria Met:**
- [ ] Architecture easily understood by new team member (< 2 hours)
- [ ] Adding new feature requires no architectural changes
- [ ] Unit tests pass (mocked, no DB)
- [ ] Integration tests pass (with test DB)
- [ ] ArchUnit tests pass (architecture enforced)
- [ ] Test coverage: Domain 80%+, App 75%+, Infra 60%+
- [ ] Application runs without errors
- [ ] Team feels confident in the architecture
- [ ] Ready for production use

---

## Troubleshooting Guide

### "No qualifying bean" error on startup
**Cause:** Port not registered as Spring bean.
**Solution:**
```java
// Option 1: Add @Bean method in config
@Bean
public EquipoRepositorioPuerto equipoRepo(...) {
    return new EquipoRepositorioAdapter(...);
}

// Option 2: Make adapter a @Repository/@Component
@Repository
public class EquipoRepositorioAdapter implements EquipoRepositorioPuerto { ... }
```

### "Symbol not found" compilation error
**Cause:** Old service or class not deleted/updated.
**Solution:**
```bash
# Find and remove orphaned references
grep -r "OldServiceName" src/
# Delete file or update import
```

### Test fails: "Cannot invoke use case - port is null"
**Cause:** Port not mocked in test.
**Solution:**
```java
@MockBean
private EquipoRepositorioPuerto equipoRepo;  // Add this

// In test
when(equipoRepo.guardar(any())).thenReturn(...);
```

### MapStruct mapper not generating
**Cause:** Processor not configured in Maven.
**Solution:**
```xml
<!-- pom.xml -->
<annotationProcessorPaths>
    <path>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct-processor</artifactId>
        <version>1.6.3</version>
    </path>
</annotationProcessorPaths>
```

### Adapter converts null values incorrectly
**Cause:** MapStruct not configured for null handling.
**Solution:**
```java
@Mapper(componentModel = "spring", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface EquipoMapperJpa { ... }
```

---

## Completion Checklist (for PM/Team Lead)

- [ ] All 7 phases executed
- [ ] All code written and tested
- [ ] Architecture compliance verified (ArchUnit)
- [ ] Team trained on new architecture
- [ ] Code reviewed by senior developer
- [ ] Performance tested (same or better)
- [ ] Deployment plan documented
- [ ] Rollback plan documented
- [ ] Production deployment completed
- [ ] Post-deployment monitoring active
- [ ] Team feedback collected
- [ ] Lessons learned documented
- [ ] Architecture decision recorded (in ADR or wiki)

---

**Refactoring Complete! 🎉**

Your gestionactivosapi is now a clean, SOLID-compliant hexagonal architecture, ready for future growth.

Next: Schedule team training on the new architecture and patterns.

