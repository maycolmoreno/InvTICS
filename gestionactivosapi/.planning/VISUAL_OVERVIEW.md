# Visual Phase Overview

## 7-Week Refactoring Timeline

```
Week 1              Week 2              Week 3              Week 4
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   PHASE 1   │    │   PHASE 2   │    │   PHASE 3   │    │   PHASE 4   │
│  Analysis   │───→│   Ports &   │───→│  Use Cases  │───→│ Adapters &  │
│ & Design    │    │   Domain    │    │ & Testing   │    │   Mappers   │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
   3 tasks           3 tasks            3 tasks            3 tasks


Week 5              Week 6              Week 7
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   PHASE 5   │    │   PHASE 6   │    │   PHASE 7   │
│ Special     │───→│  JPA Entity │───→│ Testing &   │
│ Adapters    │    │ Separation  │    │Documentation
└─────────────┘    └─────────────┘    └─────────────┘
   2-3 tasks        2 tasks            3 tasks


Legend:
────→  Dependency flow
│      Phase boundary
```

---

## Architecture Transformation

### BEFORE (Current - 40% Hexagonal, DIP Violated)

```
                    ┌─────────────────┐
                    │  Presentation   │
                    │  (Controllers)  │
                    └────────┬────────┘
                             │
                    ┌────────▼────────┐
                    │  Application    │
                    │  (Mixed         │
                    │   Services)     │  ❌ Violates DIP:
                    └────────┬────────┘     Depends on
                             │              concrete repos
                    ┌────────▼────────┐ 
                    │ Infrastructure  │
                    │ (JPA Repos,     │
                    │  Entities)      │
                    └─────────────────┘

Problems:
- Services inject concrete JPA repositories
- Mixed responsibilities (domain + persistence logic)
- Hard to test (needs database)
- Hard to change (JPA couples domain)
```

### AFTER (Target - Clean Hexagonal, SOLID Compliant)

```
Presentation Layer
    │ (Controllers, DTOs)
    │
    ▼
Application Layer
    │ (Use Cases with clean workflow)
    │
    ▼
Domain Layer ◄────────── Owns these contracts (Ports)
    │ (Entities + Business Logic)
    │
    ├─ Port Interfaces (abstractions)  ◄─┐
    │                                     │ Adapters implement
    └─────────────────────────────────┐   │ these ports
                                      │   │
Infrastructure Layer                 │   │
    │ (Adapters implement ports) ─────┘   │
    │ (JPA Entities, Repositories)        │
    │ (External Services) ─────────────────

Benefits:
✓ Services depend on abstractions (ports), not concrete repos
✓ Domain logic separated from persistence
✓ Easy to test (mock ports)
✓ Easy to change storage (JPA → MongoDB)
✓ SOLID principles enforced
```

---

## Work Breakdown Structure (WBS)

```
PHASE 1 (Week 1) - Design
├── 1.1 Architecture Analysis
│   └── Output: ARCHITECTURE_ANALYSIS.md, COUPLING_MAP.xlsx
├── 1.2 Target Blueprint
│   └── Output: ARCHITECTURE_BLUEPRINT.md
└── 1.3 Roadmap & Dependency Graph
    └── Output: ROADMAP.md, DEPENDENCY_GRAPH.txt

PHASE 2 (Week 2) - Foundation
├── 2.1 Create Port Interfaces
│   └── Output: 15+ port files in dominio/puertos/
├── 2.2 Enhance Domain Entities
│   └── Output: Updated entidades/ with business logic
└── 2.3 Domain Exceptions
    └── Output: 10+ exception classes

PHASE 3 (Week 3) - Core Refactoring ⭐ MOST CRITICAL
├── 3.1 Use Case Implementations
│   └── Output: 15+ use cases in aplicacion/casosuso/impl/
├── 3.2 Update Controllers
│   └── Output: Controllers wired to use cases
└── 3.3 Unit Tests
    └── Output: 10+ test cases per use case

PHASE 4 (Week 4) - Persistence
├── 4.1 Adapter Implementations
│   └── Output: 15+ adapters in infraestructura/adaptadores/
├── 4.2 MapStruct Mappers
│   └── Output: 10+ mappers for entity conversion
└── 4.3 Spring Configuration
    └── Output: PersistenciaConfig.java wiring

PHASE 5 (Week 5) - Specialization
├── 5.1 Notification Adapter
│   └── Output: NotificacionAdapter (email/SMS)
├── 5.2 File Generation Adapter
│   └── Output: ArchivoGeneradorAdapter (PDF/Excel)
└── 5.3 Remaining Adapters
    └── Output: Schedulers, Audit, Storage adapters

PHASE 6 (Week 6) - JPA Separation ⚠️ HIGH RISK
├── 6.1 Create Twin JPA Entities
│   └── Output: EquipoJpa, MantenimientoJpa, ... (parallel files)
└── 6.2 Remove Old Code
    └── Output: Cleanup unused services

PHASE 7 (Week 7) - Validation
├── 7.1 ArchUnit Tests
│   └── Output: Architecture compliance automated
├── 7.2 Integration Tests
│   └── Output: 10+ E2E flow tests
└── 7.3 Documentation
    └── Output: SOLID_COMPLIANCE_REPORT.md
```

---

## Dependency Management

### What Depends on What

```
Phase Sequence (Blocking Dependencies):

1.1 ─┐
1.2 ─┼─→ Phase 1 complete
1.3 ─┘
     │
     ▼
2.1 ─┐
2.2 ─┼─→ Phase 2 complete (Ports and Domain)
2.3 ─┘
     │
     ▼
3.1 ─┐   (Depends on Phase 2 ports)
3.2 ─┼─→ Phase 3 complete (Use Cases)
3.3 ─┘
     │
     ├─→ 4.1 ─┐
     │       ├─→ Phase 4 complete (Adapters)
     │       └─→ 4.3
     │
     ▼
5.1 ─┐
5.2 ─┼─→ Phase 5 complete (Specialization)
5.3 ─┘
     │
     ▼
6.1 ─┐
6.2 ─┴─→ Phase 6 complete (JPA Separation)
     │
     ▼
7.1 ─┐
7.2 ─┼─→ Phase 7 complete (Testing)
7.3 ─┘

Critical Path:
Phase 1 → Phase 2 → Phase 3 → Phase 4 → Phase 5 → Phase 6 → Phase 7
(No parallel phases; sequential execution)
```

### File Dependencies

```
                  Domain Layer
                  (Owns ports)
                       │
                       ├─ Entities (business logic)
                       ├─ Ports (abstractions)
                       └─ Exceptions (domain-specific)
                       
Application Layer        │
(Depends on domain)      ▼
├─ Use Cases (implement port consumers)
├─ Services
└─ Mappers

Infrastructure Layer     │
(Implements domain)      ▼
├─ Adapters (implement ports)
├─ JPA Entities (persistence)
├─ Repositories (Spring Data)
└─ Configuration (wire beans)

Presentation Layer
(Depends on app)
├─ Controllers (inject use cases)
├─ DTOs
└─ Mappers
```

---

## Risk Assessment

| Phase | Risk | Mitigation | Effort |
|-------|------|-----------|--------|
| 1 | Low (design only, no code changes) | Team review of blueprint | 2 days |
| 2 | Low (new files, no breaking changes) | Pre-plan all ports | 3 days |
| 3 | **HIGH** (refactoring core services) | Use waves, keep old code, test thoroughly | 5 days |
| 4 | Medium (new adapters, might break tests) | Run full suite after each adapter | 4 days |
| 5 | Low (new specialized adapters) | Mock externals in tests | 2 days |
| 6 | **HIGH** (JPA entity separation) | High test coverage, careful mapping | 3 days |
| 7 | Low (testing & docs) | Automated ArchUnit rules | 3 days |

**Total Risk:** MEDIUM (Structured phases mitigate risk of high-risk phases)

---

## Effort Estimation

| Phase | Tasks | Hours | Notes |
|-------|-------|-------|-------|
| 1 | 3 | 15 | Pure analysis & design |
| 2 | 3 | 25 | Creating interfaces & entities |
| 3 | 3 | 40 | **Core refactoring (bulk of work)** |
| 4 | 3 | 30 | Creating adapters & mappers |
| 5 | 2-3 | 15 | Specialized adapters |
| 6 | 2 | 20 | **JPA entity separation** |
| 7 | 3 | 20 | Testing & documentation |
| **Total** | **19-20** | **165** | ~1 week full-time per phase |

---

## Success Metrics by Phase

### Phase 1
- [ ] Architecture blueprint documented
- [ ] All 15+ ports designed
- [ ] Refactoring roadmap clear
- [ ] Team agrees on approach

### Phase 2
- [ ] 15+ port interfaces created
- [ ] 5+ domain entities enhanced with logic
- [ ] 10+ domain exceptions defined
- [ ] Zero JPA imports in domain layer

### Phase 3
- [ ] 15+ use cases implemented
- [ ] Controllers wired to use cases
- [ ] 10+ test cases per use case
- [ ] Build passes: `mvn clean test`

### Phase 4
- [ ] 15+ adapters implement ports
- [ ] 10+ MapStruct mappers created
- [ ] Spring config registers all adapters
- [ ] No "No qualifying bean" errors

### Phase 5
- [ ] Notification adapter sends emails
- [ ] File generation adapter creates PDF/Excel
- [ ] All ports have implementations
- [ ] Use cases can call all ports

### Phase 6
- [ ] Domain entities have NO JPA annotations
- [ ] JPA entities parallel to domain
- [ ] All mappers convert correctly
- [ ] Full test suite passes

### Phase 7
- [ ] ArchUnit tests pass
- [ ] 10+ integration tests pass
- [ ] SOLID compliance documented
- [ ] Architecture ready for team handoff

---

## Files & Deliverables by Phase

```
Phase 1 Outputs:
  └─ .planning/
     ├── ARCHITECTURE_ANALYSIS.md
     ├── COUPLING_MAP.xlsx
     ├── ARCHITECTURE_BLUEPRINT.md
     ├── ROADMAP.md
     └── REFACTORING_WAVES.md

Phase 2 Outputs:
  └─ src/main/java/dominio/
     ├── puertos/ (15+ port interfaces)
     ├── entidades/ (enhanced entities)
     └── excepciones/ (domain exceptions)

Phase 3 Outputs:
  └─ src/main/java/aplicacion/casosuso/
     ├── entradas/ (use case interfaces - updated)
     ├── impl/ (use case implementations - NEW)
     └── src/test/ (unit tests)

Phase 4 Outputs:
  └─ src/main/java/infraestructura/
     ├── adaptadores/ (15+ adapters)
     ├── persistencia/mapeadores/ (10+ mappers)
     └── configuracion/ (Spring bean registration)

Phase 5 Outputs:
  └─ src/main/java/infraestructura/adaptadores/
     ├── NotificacionAdapter.java
     ├── ArchivoGeneradorAdapter.java
     └── [Other specialized adapters]

Phase 6 Outputs:
  └─ src/main/java/infraestructura/persistencia/jpa/
     ├── EquipoJpa.java (parallel to Equipo)
     ├── MantenimientoJpa.java (parallel to Mantenimiento)
     └── [etc.]

Phase 7 Outputs:
  ├─ .planning/SOLID_COMPLIANCE_REPORT.md
  ├─ src/test/ (ArchUnit tests, integration tests)
  └─ Documentation complete
```

---

## Key Milestones

```
March 24 ──→  Day 5  (Phase 1 complete)
              ↓
       March 30 ──→  Day 10 (Phase 2 complete)
                     ↓
              April 6 ──→  Day 15 (Phase 3 complete) ⭐ CRITICAL
                          ↓
                   April 13 ──→  Day 20 (Phase 4 complete)
                                 ↓
                          April 20 ──→  Day 25 (Phase 5 complete)
                                        ↓
                                 April 27 ──→  Day 30 (Phase 6 complete) ⚠️ RISKY
                                               ↓
                                        May 4 ──→  Day 35 (Phase 7 complete) ✅ DONE
```

---

## Phase Dependencies Matrix

| Phase | Depends On | Blocks | Parallel? |
|-------|------------|--------|-----------|
| 1 | Nothing | All | No |
| 2 | 1 | 3 | No |
| 3 | 2 | 4 | No |
| 4 | 3 | 5 | No (5 can wait for 4) |
| 5 | 4 | 6 | No |
| 6 | 5 | 7 | No |
| 7 | 6 | None | No |

**Note:** All phases are sequential. No parallelization possible due to design dependencies.

---

## Quality Gates

### Before proceeding from Phase X to Phase X+1:

**Phase 1 → 2:**
- [ ] Architecture blueprint approved by team

**Phase 2 → 3:**
- [ ] All ports created (15+)
- [ ] Build: `mvn clean compile` ✓
- [ ] Zero JPA imports in domain ✓

**Phase 3 → 4:**
- [ ] Use cases implemented (15+)
- [ ] Build: `mvn clean test` ✓
- [ ] Test coverage: 75%+ ✓

**Phase 4 → 5:**
- [ ] Adapters created (15+)
- [ ] Spring config wires all beans ✓
- [ ] Build: `mvn spring-boot:run` starts ✓

**Phase 5 → 6:**
- [ ] All ports have implementations ✓
- [ ] Use cases can call all ports ✓

**Phase 6 → 7:**
- [ ] JPA entities parallel to domain ✓
- [ ] Full test suite passes ✓
- [ ] Zero regressions ✓

**Phase 7 → Production:**
- [ ] ArchUnit tests pass ✓
- [ ] Integration tests pass ✓
- [ ] SOLID compliance documented ✓
- [ ] Team trained ✓

---

## Comparison: Before vs After

| Aspect | Before | After |
|--------|--------|-------|
| **Layers** | 3 (mixed) | 4 (clean) |
| **Dependencies** | Services → Concrete repos | Use cases → Port interfaces |
| **Testing** | Needs database | Mocks ports, fast |
| **New features** | Modify service + adapter | New use case |
| **Storage change** | Code everywhere | Swap adapter only |
| **Responsibilities** | Mixed (logic + persistence) | Clear separation |
| **Interface quality** | Large (50+ methods) | Small (5-8 methods) |
| **Code Duplication** | Some | Minimal (MapStruct) |

---

## Go/No-Go Checklist

### Before starting Phase 1:
- [ ] Team understands plan
- [ ] Timeline allocated (7 weeks)
- [ ] Resources assigned
- [ ] Stakeholder buy-in obtained

### After Phase 1 (before Phase 2):
- [ ] Blueprint approved
- [ ] All pivot entities identified
- [ ] Refactoring waves agreed

### After Phase 3 (before Phase 4):
- [ ] Use cases tested (10+ tests)
- [ ] Controllers working with new use cases
- [ ] No regressions in old code

### After Phase 6 (before Phase 7):
- [ ] All test suite passing
- [ ] No broken queries
- [ ] Database operations verified

### After Phase 7:
- [ ] ArchUnit validates architecture
- [ ] Team trained
- [ ] Ready for production

---

## Communication Plan

**Weekly Standup:**
- Progress on current phase
- Blockers & risks
- Validation results

**Phase Completion Review:**
- Demonstrate code
- Show test results
- Document decisions

**Final Presentation:**
- Architecture walkthrough
- Live demo (create equipo → use case flow)
- Team Q&A

---

## Next Action Items

1. **Today:**
   - Review this visual overview
   - Read INDEX.md for navigation

2. **This Week (Phase 1):**
   - Assign Phase 1 tasks
   - Start architecture analysis (Task 1.1)

3. **Before Phase 2:**
   - Get blueprint approval
   - Schedule Phase 2 kickoff

---

**Status:** Ready for execution
**Target Start:** Next available week
**Estimated Completion:** 7 weeks from start

Let's build a clean, SOLID-compliant architecture! 🚀

