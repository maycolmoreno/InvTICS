# Documentation Index - Hexagonal Refactoring Plan

## Purpose of This Directory

This `.planning/` directory contains **complete, executable specifications** for refactoring `gestionactivosapi` from a partially-compliant hexagonal architecture to a **clean, SOLID-compliant** design over 7 weeks.

---

## What You Have Here

### 📋 Main Planning Documents

| Document | Purpose | Read Time | Audience |
|----------|---------|-----------|----------|
| **[README.md](README.md)** | Quick reference, getting started | 5 min | Everyone |
| **[HEXAGONAL_REFACTOR_PLAN.md](HEXAGONAL_REFACTOR_PLAN.md)** | Complete 7-phase plan with all details | 30 min | Architects, Senior Devs |
| **[PHASE3_DETAILED_EXAMPLE.md](PHASE3_DETAILED_EXAMPLE.md)** | Concrete code example (before → after) | 20 min | All Developers |
| **[PHASE_CHECKLISTS.md](PHASE_CHECKLISTS.md)** | Validation checkpoints for each phase | 15 min | QA, Team Leads |

---

## How to Use These Documents

### Scenario 1: "I need to understand the plan quickly"
1. Read [README.md](README.md) (5 minutes)
2. Skim [HEXAGONAL_REFACTOR_PLAN.md](HEXAGONAL_REFACTOR_PLAN.md) sections 1-3 (10 minutes)
3. Look at the timeline table and architecture diagram

### Scenario 2: "I'm going to implement Phase X"
1. Read the relevant Phase section in [HEXAGONAL_REFACTOR_PLAN.md](HEXAGONAL_REFACTOR_PLAN.md)
2. Reference [PHASE3_DETAILED_EXAMPLE.md](PHASE3_DETAILED_EXAMPLE.md) for code patterns
3. Use [PHASE_CHECKLISTS.md](PHASE_CHECKLISTS.md) to validate completion
4. Run validation commands from checklist

### Scenario 3: "I need to verify the architecture is correct"
1. Run validation commands from [PHASE_CHECKLISTS.md](PHASE_CHECKLISTS.md)
2. Check [PHASE3_DETAILED_EXAMPLE.md](PHASE3_DETAILED_EXAMPLE.md) for expected patterns
3. Compare your code against the patterns shown

### Scenario 4: "Something is broken"
1. Find the issue in [PHASE_CHECKLISTS.md](PHASE_CHECKLISTS.md) → Troubleshooting Guide
2. Or find your phase section in [HEXAGONAL_REFACTOR_PLAN.md](HEXAGONAL_REFACTOR_PLAN.md) → FAQ

---

## Document Descriptions

### [README.md](README.md)
**Quick reference guide.** Start here.
- 7-week timeline overview
- Architecture layers
- File structure
- Command reference
- Success indicators

**When to use:** When you need a quick overview or command reference.

---

### [HEXAGONAL_REFACTOR_PLAN.md](HEXAGONAL_REFACTOR_PLAN.md)
**Complete plan with all details.**
- Executive summary
- 7 phases explained (Week 1-7)
- Each phase has 2-3 tasks
- Each task has objective, action steps, outputs, verification
- Implementation guidelines (code patterns, naming conventions, etc.)
- FAQ & common pitfalls
- Success metrics

**Structure:**
```
1. Executive Summary
2. Phase 1: Design & Analysis (Week 1)
   - Objectives
   - Task 1.1, 1.2, 1.3
   - Success Criteria
3. Phase 2: Ports & Domain (Week 2)
   ...
4. Phase 7: Testing & Documentation (Week 7)
5. Timeline & Phasing Summary
6. Implementation Guidelines
7. Validation Checkpoints
8. FAQ
9. Success Metrics
```

**When to use:** When implementing a phase or understanding the complete picture.

---

### [PHASE3_DETAILED_EXAMPLE.md](PHASE3_DETAILED_EXAMPLE.md)
**Concrete code example showing the refactoring.**
- Before: Old mixed service (problematic)
- After: Refactored to hexagonal (clean)
- 13 layers of the architecture shown with full code:
  1. Domain entity (pure POJO)
  2. Domain port interface
  3. Notification port (specialized)
  4. Use case implementation
  5. Persistence adapter
  6. JPA entity
  7. MapStruct mappers (2x)
  8. Spring configuration
  9. Use case interface
  10. Controller (updated)
  11. Unit test
  12. Integration test
  13. Domain exceptions

- Architecture summary diagram
- Benefits table (before vs after)
- Next steps

**When to use:** Copy the patterns here for implementing each entity/use case.

---

### [PHASE_CHECKLISTS.md](PHASE_CHECKLISTS.md)
**Phase-by-phase validation checkpoints.**
- One checklist per phase (7 total)
- Each task has a "Completion Checklist"
- Validation commands (bash)
- Success criteria per phase
- Troubleshooting guide (10+ common issues)
- Final verification checklist for all phases

**When to use:** After completing a phase, verify with this checklist.

---

## File Navigation

### By Role

**Architect/Tech Lead:**
→ Read README.md, skim HEXAGONAL_REFACTOR_PLAN.md sections 1-3

**Senior Developer (Phase Implementation):**
→ Read relevant phase in HEXAGONAL_REFACTOR_PLAN.md, reference PHASE3_DETAILED_EXAMPLE.md

**QA/Testing:**
→ Use PHASE_CHECKLISTS.md to validate each phase

**New Team Member:**
→ Start with README.md, then PHASE3_DETAILED_EXAMPLE.md to see patterns

---

### By Phase

**Phase 1 (Week 1) - Design:**
- See: HEXAGONAL_REFACTOR_PLAN.md → Phase 1
- Deliverables: Analysis documents
- Validation: README.md key decisions

**Phase 2 (Week 2) - Ports & Domain:**
- See: HEXAGONAL_REFACTOR_PLAN.md → Phase 2
- Patterns: PHASE3_DETAILED_EXAMPLE.md → Layer 1-3
- Validate: PHASE_CHECKLISTS.md → Phase 2

**Phase 3 (Week 3) - Use Cases:**
- See: HEXAGONAL_REFACTOR_PLAN.md → Phase 3
- Patterns: PHASE3_DETAILED_EXAMPLE.md → Layer 4, 9, 10
- Validate: PHASE_CHECKLISTS.md → Phase 3
- **THIS IS THE CORE OF THE REFACTORING**

**Phase 4 (Week 4) - Adapters:**
- See: HEXAGONAL_REFACTOR_PLAN.md → Phase 4
- Patterns: PHASE3_DETAILED_EXAMPLE.md → Layer 5, 7, 8
- Validate: PHASE_CHECKLISTS.md → Phase 4

**Phase 5 (Week 5) - Specialization:**
- See: HEXAGONAL_REFACTOR_PLAN.md → Phase 5
- Quick checklist: PHASE_CHECKLISTS.md → Phase 5

**Phase 6 (Week 6) - JPA Separation:**
- See: HEXAGONAL_REFACTOR_PLAN.md → Phase 6
- Patterns: PHASE3_DETAILED_EXAMPLE.md → Layer 6
- Validate: PHASE_CHECKLISTS.md → Phase 6
- **HIGH RISK PHASE**

**Phase 7 (Week 7) - Testing & Docs:**
- See: HEXAGONAL_REFACTOR_PLAN.md → Phase 7
- Patterns: PHASE3_DETAILED_EXAMPLE.md → Layer 11, 12
- Validate: PHASE_CHECKLISTS.md → Phase 7

---

## Key Metrics & Success Criteria

### Code Quality
- **Zero JPA imports in domain/application layers**
  ```bash
  grep -r "jakarta.persistence" src/main/java/*/dominio/ src/main/java/*/aplicacion/
  # Should return 0
  ```

- **All services depend on ports (not concrete JPA repos)**
  ```bash
  grep -r "RepositorioPuerto" src/main/java/*/aplicacion/casosuso/impl/
  # Should return many
  ```

- **Test Coverage:**
  - Domain: 80%+
  - Application: 75%+
  - Infrastructure: 60%+

### Architecture
- ✓ 4 clear layers (Domain, Application, Infrastructure, Presentation)
- ✓ Dependency Inversion enforced
- ✓ Single Responsibility per class
- ✓ Interface Segregation (small, focused ports)

### Team Readiness
- ✓ New developer understands architecture in < 2 hours
- ✓ Can add new feature without architecture changes
- ✓ Can swap storage (JPA → MongoDB) without code changes

---

## Quick Reference: What Belongs Where

### Domain Layer (`dominio/`)
- ✓ Entities (with business logic)
- ✓ Value objects (Email, Estado, etc.)
- ✓ Ports (interfaces that domain defines)
- ✓ Domain exceptions
- ✓ Domain services (complex logic)

**❌ NOT in Domain:**
- JPA annotations
- Spring annotations
- Infrastructure details
- Database queries

### Application Layer (`aplicacion/`)
- ✓ Use case interfaces (from old `casosuso/entradas/`)
- ✓ Use case implementations (in `casosuso/impl/`)
- ✓ Application services
- ✓ DTOs (request/response)
- ✓ Application exceptions

**❌ NOT in Application:**
- JPA entities
- Database queries
- Spring Data repositories
- Spring @Repository beans

### Infrastructure Layer (`infraestructura/`)
- ✓ Adapters (implement ports)
- ✓ JPA entities
- ✓ JPA repositories (Spring Data)
- ✓ MapStruct mappers
- ✓ External service integrations
- ✓ Configuration beans

**✓ CAN be in Infrastructure:**
- Spring annotations (@Repository, @Component, @Configuration)
- JPA annotations (@Entity, @Column)
- Database queries
- Framework-specific code

### Presentation Layer (`presentacion/`)
- ✓ Controllers
- ✓ Request/Response DTOs
- ✓ Presentation validation
- ✓ DTO mappers

---

## Document Maintenance

### After Phase 1
- Add `phases/01-architecture-design/01-SUMMARY.md` with findings

### After Phase 2
- Add `phases/02-ports-domain/02-SUMMARY.md` listing created ports/entities

### After Phase 3
- Add `phases/03-use-cases/03-SUMMARY.md` listing refactored use cases

### etc., for each phase

---

## Troubleshooting by Symptom

| Symptom | Check | Fix |
|---------|-------|-----|
| "No qualifying bean" error | PHASE_CHECKLISTS.md → Task 4.3 | Register adapter in Spring config |
| MapStruct not generating | README.md → pom.xml | Add `mapstruct-processor` |
| SOLID rules failing | PHASE3_DETAILED_EXAMPLE.md | Follow patterns exactly |
| Test setup confusing | PHASE3_DETAILED_EXAMPLE.md → Layer 11 | Copy test pattern |
| Entity mapping broken | PHASE3_DETAILED_EXAMPLE.md → Layer 7 | Check @Mapping annotations |
| Can't understand architecture | README.md | Read, then PHASE3_DETAILED_EXAMPLE.md |

---

## Learning Path for New Team Members

1. **Day 1:** Read README.md
2. **Day 1:** Look at PHASE3_DETAILED_EXAMPLE.md diagrams
3. **Day 2:** Deep dive on PHASE3_DETAILED_EXAMPLE.md code
4. **Day 3:** Run through PHASE_CHECKLISTS.md validation commands
5. **Thereafter:** Reference HEXAGONAL_REFACTOR_PLAN.md as questions arise

**Result:** New team member is productive by Day 3.

---

## Document Update Strategy

These documents are **FROZEN for the refactoring duration** to ensure consistency. If changes needed:

1. Discuss with team
2. Update relevant document
3. Annotate with date and reason
4. Re-distribute

After Phase 7 completion:
- Archive planning docs
- Create final Architecture Decision Record (ADR)
- Create team wiki/training materials
- Document lessons learned

---

## FAQ About These Documents

**Q: Are these documents complete?**
A: Yes. They contain everything needed to execute the refactoring. No external docs required.

**Q: Can I skip a phase?**
A: No. Phases build on each other. Phase 3 depends on Phase 2, etc.

**Q: How long should each phase take?**
A: 1 week (5 working days) assuming full-time effort. Adjust based on your throughput.

**Q: What if I find an error in the documentation?**
A: Note it down, continue execution, fix after phase completion.

**Q: Can I refactor all services at once instead of the waves?**
A: Not recommended. Waves reduce risk and allow testing between phases.

**Q: Do I need to read all documents?**
A: No. Architects: README + HEXAGONAL_REFACTOR_PLAN.md (sections 1-3). Devs: README + PHASE3_DETAILED_EXAMPLE.md + relevant phase. QA: PHASE_CHECKLISTS.md.

---

## Links to Read

**Inside this folder:**
- [README.md](README.md) — Start here
- [HEXAGONAL_REFACTOR_PLAN.md](HEXAGONAL_REFACTOR_PLAN.md) — Full plan
- [PHASE3_DETAILED_EXAMPLE.md](PHASE3_DETAILED_EXAMPLE.md) — Code example
- [PHASE_CHECKLISTS.md](PHASE_CHECKLISTS.md) — Validation

**External (for reference):**
- Hexagonal Architecture: Alistair Cockburn
- SOLID Principles: Uncle Bob (Robert C. Martin)
- Spring Best Practices: spring.io
- MapStruct: mapstruct.org

---

## Next Step

**👉 Start here:** [README.md](README.md)

Then pick your role and follow the guidance in "How to Use These Documents" above.

---

**Version:** 1.0
**Created:** March 24, 2026
**Status:** Ready for execution
**Estimated Duration:** 7 weeks
**Risk Level:** Medium (Managed with structured phases)

