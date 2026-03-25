# Code Example: Refactoring Equipos Use Case (Phase 3 Reference)

This document shows **concrete code examples** for refactoring the `Equipos` functionality, which is a pivot aggregate. Use this as a template for other entities.

## Current State (Before Refactoring)

### Old Mixed Service: `EquiposService.java`
```java
@Service
@RequiredArgsConstructor
public class EquiposService {
    private final IEquiposJpaRepositorio equiposJpaRepo;  // ❌ CONCRETE INFRA DEPENDENCY
    private final ICustodiosJpaRepositorio custodiosRepo;
    private final JavaMailSender mailSender;  // ❌ MIXED CONCERNS
    private final CorreoService correoService;
    
    public EquipoDTO crearEquipo(CrearEquipoRequestDTO request) {
        // Validation mixed with business logic
        if (request.getNombre() == null) {
            throw new IllegalArgumentException("Name required");
        }
        
        // Domain logic in service
        if (equiposJpaRepo.existsByNombreIgnoreCase(request.getNombre())) {
            throw new RuntimeException("Already exists");  // ❌ GENERIC EXCEPTION
        }
        
        // Direct JPA entity creation (domain leakage)
        EquipoJpa equipoJpa = new EquipoJpa();
        equipoJpa.setNombre(request.getNombre());
        equipoJpa.setModelo(request.getModelo());
        // ... more setters
        
        // Direct persistence
        EquipoJpa saved = equiposJpaRepo.save(equipoJpa);
        
        // Side effect inline (hard to test)
        try {
            correoService.notificar("Equipo creado", "admin@example.com");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        // Return DTO (no mapping)
        return new EquipoDTO(saved.getId(), saved.getNombre(), ...);
    }
    
    public List<EquipoDTO> obtenerTodos() {
        return equiposJpaRepo.findAll()
            .stream()
            .map(this::convertirADTO)  // ❌ MANUAL MAPPING
            .collect(Collectors.toList());
    }
    
    private EquipoDTO convertirADTO(EquipoJpa jpa) { /*...*/ }
}
```

### Issues
1. ✗ Depends on concrete `IEquiposJpaRepositorio` (JPA implementation)
2. ✗ Domain logic mixed with infrastructure (persistence checks)
3. ✗ DTO conversion manual (not MapStruct)
4. ✗ Side effects (email) inline and hard to test/mock
5. ✗ Generic RuntimeException (not domain-specific)
6. ✗ Hard to test (needs real database or complex mocking)
7. ✗ **Violates DIP, SRP, Interface Segregation, Open/Closed**

---

## Target State (After Refactoring)

### Layer 1: Domain Entity (PURE POJO)
**File:** `src/main/java/com/uisrael/gestionactivosapi/dominio/entidades/Equipo.java`

```java
package com.uisrael.gestionactivosapi.dominio.entidades;

import java.time.LocalDateTime;
import com.uisrael.gestionactivosapi.dominio.valoresobjeto.EstadoEquipo;
import com.uisrael.gestionactivosapi.dominio.excepciones.ValidacionNuegoException;

/**
 * Domain entity: Equipo (Asset).
 * Represents an asset with business rules.
 * NO Spring annotations, NO JPA annotations, NO infrastructure concerns.
 */
public class Equipo {
    
    private Integer id;
    private String nombre;
    private String descripcion;
    private String serial;
    private String modelo;
    private EstadoEquipo estado;
    private LocalDateTime fechaAdquisicion;
    private LocalDateTime fechaProximoMantenimiento;
    private Integer idCustodioActual;
    
    // Constructor: enforces invariants
    public Equipo(String nombre, String descripcion, String modelo) {
        this.nombre = validarNombre(nombre);
        this.descripcion = validarDescripcion(descripcion);
        this.modelo = validarModelo(modelo);
        this.estado = EstadoEquipo.DISPONIBLE;
        this.fechaAdquisicion = LocalDateTime.now();
    }
    
    // Full constructor with ID (for loading from persistence)
    public Equipo(Integer id, String nombre, String descripcion, String modelo, 
                  EstadoEquipo estado, LocalDateTime fechaAdquisicion) {
        this(nombre, descripcion, modelo);
        this.id = id;
        this.estado = estado;
        this.fechaAdquisicion = fechaAdquisicion;
    }
    
    // Domain validation methods
    private String validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new ValidacionNuegoException("El nombre del equipo es requerido");
        }
        if (nombre.length() < 3 || nombre.length() > 100) {
            throw new ValidacionNuegoException("El nombre debe tener entre 3 y 100 caracteres");
        }
        return nombre.trim();
    }
    
    private String validarDescripcion(String descripcion) {
        if (descripcion != null && descripcion.length() > 500) {
            throw new ValidacionNuegoException("La descripción no puede exceder 500 caracteres");
        }
        return descripcion;
    }
    
    private String validarModelo(String modelo) {
        if (modelo != null && modelo.length() > 100) {
            throw new ValidacionNuegoException("El modelo no puede exceder 100 caracteres");
        }
        return modelo;
    }
    
    // Domain business methods (core logic)
    
    /**
     * Assigns the equipment to a custodian.
     * Enforces business rule: only available equipment can be assigned.
     */
    public void asignarAlCustodio(Integer idCustodio) {
        if (estado != EstadoEquipo.DISPONIBLE) {
            throw new EquipoNoDisponibleException(
                "El equipo está en estado " + estado + " y no puede asignarse. " +
                "Solo equipos disponibles pueden asignarse.");
        }
        if (idCustodio == null || idCustodio <= 0) {
            throw new ValidacionNuegoException("ID de custodio inválido");
        }
        this.idCustodioActual = idCustodio;
        this.estado = EstadoEquipo.ASIGNADO;
    }
    
    /**
     * Returns equipment to available state.
     * Only assigned or maintenance equipment can be returned.
     */
    public void devolver() {
        if (estado != EstadoEquipo.ASIGNADO && estado != EstadoEquipo.MANTENIMIENTO) {
            throw new TransicionEstadoIlegalException(
                "Equipo en estado " + estado + " no puede devolverse.");
        }
        this.idCustodioActual = null;
        this.estado = EstadoEquipo.DISPONIBLE;
    }
    
    /**
     * Marks equipment for maintenance.
     * Removes current custodian assignment.
     */
    public void marcarParaMantenimiento() {
        if (estado == EstadoEquipo.DESCARTADO) {
            throw new TransicionEstadoIlegalException("Equipo descartado no puede mantenerse.");
        }
        this.idCustodioActual = null;
        this.estado = EstadoEquipo.MANTENIMIENTO;
    }
    
    /**
     * Changes equipment state based on business rules.
     * Validates allowed state transitions.
     */
    public void cambiarEstado(EstadoEquipo nuevoEstado) {
        if (!puedeTransicionarA(nuevoEstado)) {
            throw new TransicionEstadoIlegalException(
                "No se puede cambiar de estado " + estado + " a " + nuevoEstado);
        }
        if (nuevoEstado == EstadoEquipo.DISPONIBLE) {
            this.idCustodioActual = null;  // Clear custodian on return
        }
        this.estado = nuevoEstado;
    }
    
    /**
     * Determines if state transition is allowed.
     * Implements state machine rules.
     */
    private boolean puedeTransicionarA(EstadoEquipo nuevoEstado) {
        return switch (estado) {
            case DISPONIBLE -> 
                nuevoEstado == EstadoEquipo.ASIGNADO || 
                nuevoEstado == EstadoEquipo.MANTENIMIENTO || 
                nuevoEstado == EstadoEquipo.DESCARTADO;
            case ASIGNADO -> 
                nuevoEstado == EstadoEquipo.DISPONIBLE || 
                nuevoEstado == EstadoEquipo.MANTENIMIENTO || 
                nuevoEstado == EstadoEquipo.DESCARTADO;
            case MANTENIMIENTO -> 
                nuevoEstado == EstadoEquipo.DISPONIBLE || 
                nuevoEstado == EstadoEquipo.DESCARTADO;
            case DESCARTADO -> false;  // Terminal state
        };
    }
    
    /**
     * Determines if equipment requires manager approval for creation.
     * Business rule: equipment above certain value requires approval.
     */
    public boolean requiereAprobacion() {
        // Could depend on equipment cost, category, etc.
        return false;  // Adjust based on actual business rules
    }
    
    /**
     * Checks if equipment is ready for use.
     * Not assigned, not in maintenance, not discarded.
     */
    public boolean estaDisponible() {
        return estado == EstadoEquipo.DISPONIBLE;
    }
    
    // Getters (minimal, for reading - no setters allow direct modification)
    
    public Integer getId() {
        return id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public String getModelo() {
        return modelo;
    }
    
    public String getSerial() {
        return serial;
    }
    
    public EstadoEquipo getEstado() {
        return estado;
    }
    
    public LocalDateTime getFechaAdquisicion() {
        return fechaAdquisicion;
    }
    
    public LocalDateTime getFechaProximoMantenimiento() {
        return fechaProximoMantenimiento;
    }
    
    public Integer getIdCustodioActual() {
        return idCustodioActual;
    }
    
    // Setters only for persistence (used by adapter when loading from DB)
    // Marked as package-protected; not for business logic use
    
    void setId(Integer id) {
        this.id = id;
    }
    
    void setFechaProximoMantenimiento(LocalDateTime fecha) {
        this.fechaProximoMantenimiento = fecha;
    }
}
```

**Key points:**
- ✓ Pure Java POJO (no Spring, no JPA annotations)
- ✓ Business rules enforced in constructor and methods
- ✓ Domain-specific exceptions (not generic RuntimeException)
- ✓ State machine logic encapsulated
- ✓ Getters expose only necessary data
- ✓ Setters are package-protected (only adapter uses for persistence)

---

### Layer 2: Domain Repository Port
**File:** `src/main/java/com/uisrael/gestionactivosapi/dominio/puertos/EquipoRepositorioPuerto.java`

```java
package com.uisrael.gestionactivosapi.dominio.puertos;

import java.util.List;
import java.util.Optional;
import com.uisrael.gestionactivosapi.dominio.entidades.Equipo;

/**
 * Port (interface) for Equipo persistence.
 * Abstraction that domain and application depend on.
 * Infrastructure implements this port.
 * 
 * Depends on DOMAIN entities only, never on infrastructure.
 */
public interface EquipoRepositorioPuerto {
    
    /**
     * Saves a new Equipo or updates existing.
     * 
     * @param equipo Domain entity to persist
     * @return Saved entity with ID assigned (if new)
     * @throws IllegalArgumentException if entity violates domain rules
     * @throws RepositorioPersistenciaException if database error occurs
     */
    Equipo guardar(Equipo equipo);
    
    /**
     * Retrieves equipment by ID.
     * 
     * @param id Equipment ID
     * @return Equipo if found, empty Optional otherwise
     */
    Optional<Equipo> obtenerPorId(Integer id);
    
    /**
     * Retrieves all equipment.
     * 
     * @return List of all Equipo entities (may be empty)
     */
    List<Equipo> obtenerTodos();
    
    /**
     * Retrieves equipment by name (case-insensitive).
     * Useful for duplicate checking.
     * 
     * @param nombre Equipment name
     * @return Optional containing Equipo if found
     */
    Optional<Equipo> obtenerPorNombre(String nombre);
    
    /**
     * Checks if equipment with given name exists (case-insensitive).
     * 
     * @param nombre Equipment name
     * @return true if exists, false otherwise
     */
    boolean existePorNombre(String nombre);
    
    /**
     * Checks if equipment with given serial number exists.
     * 
     * @param serial Serial number
     * @return true if exists, false otherwise
     */
    boolean existePorSerial(String serial);
    
    /**
     * Retrieves all equipment in given state.
     * 
     * @param estado Equipment state
     * @return List of Equipo in that state
     */
    List<Equipo> obtenerPorEstado(EstadoEquipo estado);
    
    /**
     * Retrieves all equipment assigned to a custodian.
     * 
     * @param idCustodio Custodian ID
     * @return List of Equipo assigned to custodian
     */
    List<Equipo> obtenerPorCustodio(Integer idCustodio);
    
    /**
     * Deletes equipment by ID.
     * Cascades or handles related data per business rules.
     * 
     * @param id Equipment ID to delete
     * @throws EquipoNoEncontradoException if ID doesn't exist
     */
    void eliminar(Integer id);
    
    /**
     * Deletes an equipment entity.
     * 
     * @param equipo Entity to delete
     */
    void eliminarEntidad(Equipo equipo);
}
```

**Key points:**
- ✓ No Spring annotations (not an interface—it's a pure contract)
- ✓ Takes and returns domain entities (`Equipo`, not `EquipoJpa`)
- ✓ Methods are minimal and focused (Interface Segregation)
- ✓ Domain exception types in contracts
- ✓ Clear documentation of each method's responsibility

---

### Layer 3: Notification Port (Specialized)
**File:** `src/main/java/com/uisrael/gestionactivosapi/dominio/puertos/NotificacionPuerto.java`

```java
package com.uisrael.gestionactivosapi.dominio.puertos;

import com.uisrael.gestionactivosapi.dominio.entidades.Equipo;
import com.uisrael.gestionactivosapi.dominio.entidades.Usuario;

/**
 * Port for sending notifications.
 * Abstracts away email/SMS details from domain.
 * Dependencies flow IN to this port (adapters implement it).
 */
public interface NotificacionPuerto {
    
    /**
     * Notifies when new equipment is created.
     * 
     * @param equipo Created equipment
     * @param creadorId ID of user who created it
     */
    void notificarEquipoCreado(Equipo equipo, Integer creadorId);
    
    /**
     * Notifies when equipment is assigned to custodian.
     * 
     * @param equipo Assigned equipment
     * @param custodio Custodian user
     */
    void notificarEquipoAsignado(Equipo equipo, Usuario custodio);
    
    /**
     * Notifies when equipment is returned.
     * 
     * @param equipo Returned equipment
     */
    void notificarEquipoDevuelto(Equipo equipo);
    
    /**
     * Notifies when equipment needs maintenance.
     * 
     * @param equipo Equipment needing maintenance
     */
    void notificarMantenimientoRequerido(Equipo equipo);
}
```

---

### Layer 4: Use Case Implementation
**File:** `src/main/java/com/uisrael/gestionactivosapi/aplicacion/casosuso/impl/CrearEquipoUseCaseImpl.java`

```java
package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.ICrearEquipoUseCase;
import com.uisrael.gestionactivosapi.dominio.entidades.Equipo;
import com.uisrael.gestionactivosapi.dominio.puertos.EquipoRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.NotificacionPuerto;
import com.uisrael.gestionactivosapi.presentacion.dto.request.CrearEquipoRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.EquipoResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.mapeadores.EquipoMapper;

/**
 * Use Case: Create Equipment
 * 
 * Implements the workflow for creating new equipment.
 * - Validates input
 * - Creates domain entity
 * - Enforces business rules
 * - Persists via port
 * - Triggers side effects (notifications)
 * - Returns response
 * 
 * DEPENDS ON:
 * - EquipoRepositorioPuerto (port abstraction - NOT concrete JPA)
 * - NotificacionPuerto (port abstraction)
 * - EquipoMapper (MapStruct for DTO conversion)
 * 
 * Does NOT depend on JPA, databases, or infrastructure details.
 */
@Component  // Spring component
@RequiredArgsConstructor  // Constructor injection via Lombok
@Transactional  // Manages transaction for entire use case
public class CrearEquipoUseCaseImpl implements ICrearEquipoUseCase {
    
    // Dependencies injected via constructor (final, required)
    // NOTE: These are PORTS (abstractions), not concrete implementations
    private final EquipoRepositorioPuerto equipoRepo;
    private final NotificacionPuerto notificacion;
    private final EquipoMapper equipoMapper;  // MapStruct
    
    /**
     * Execute the use case: Create Equipment
     * 
     * @param request Input DTO with equipment data
     * @return Response DTO with created equipment
     * @throws ValidacionNuegoException if input violated business rules
     * @throws EquipoYaExisteException if name/serial already exists
     */
    @Override
    public EquipoResponseDTO ejecutar(CrearEquipoRequestDTO request) {
        // STEP 1: Validate input (application layer responsibility)
        validarSolicitud(request);
        
        // STEP 2: Check business rule: name must be unique
        if (equipoRepo.existePorNombre(request.getNombre())) {
            throw new EquipoYaExisteException(
                "Ya existe un equipo con el nombre: " + request.getNombre());
        }
        
        // STEP 3: Create domain entity
        // Constructor validates invariants
        Equipo equipoNuevo = new Equipo(
            request.getNombre(),
            request.getDescripcion(),
            request.getModelo()
        );
        
        // STEP 4: Apply business rules (domain logic)
        if (equipoNuevo.requiereAprobacion()) {
            // Could mark for approval (if needed)
            // equipoNuevo.marcarPendienteAprobacion();
        }
        
        // STEP 5: Persist via PORT abstraction (never direct JPA)
        Equipo equipoGuardado = equipoRepo.guardar(equipoNuevo);
        
        // STEP 6: Side effects via PORT abstraction
        try {
            // Only notifies if save succeeds; failure propagates
            notificacion.notificarEquipoCreado(equipoGuardado, obtenerIdUsuarioActual());
        } catch (Exception e) {
            // Log but don't crash use case (notification is secondary)
            // In production, use coordination ports or async events
            System.err.println("Error sending notification: " + e.getMessage());
        }
        
        // STEP 7: Convert domain entity to response DTO
        // Uses MapStruct mapper for clean separation
        return equipoMapper.toResponseDTO(equipoGuardado);
    }
    
    /**
     * Validates that the request meets requirements.
     * 
     * @param request Input DTO
     * @throws ValidacionNuegoException if invalid
     */
    private void validarSolicitud(CrearEquipoRequestDTO request) {
        if (request == null) {
            throw new ValidacionNuegoException("La solicitud no puede ser nula");
        }
        
        if (request.getNombre() == null || request.getNombre().isBlank()) {
            throw new ValidacionNuegoException("El nombre es requerido");
        }
        
        if (request.getNombre().length() < 3) {
            throw new ValidacionNuegoException("El nombre debe tener al menos 3 caracteres");
        }
    }
    
    /**
     * Placeholder to get current user ID from security context.
     * In real implementation, inject SecurityContext or CurrentUserService.
     */
    private Integer obtenerIdUsuarioActual() {
        // TODO: Implement with Spring Security
        return 1;  // Placeholder
    }
}
```

**Key points:**
- ✓ Implements use case interface (from Phase 2)
- ✓ `@Transactional` on the entire use case method (not scattered)
- ✓ Injects `EquipoRepositorioPuerto` (port), NOT `IEquiposJpaRepositorio` (concrete JPA)
- ✓ Clear workflow: Validate → Create → Apply rules → Persist → Notify → Return
- ✓ Domain entity creation with validation in constructor
- ✓ Uses MapStruct mapper for DTO conversion
- ✓ Handles exceptions gracefully
- ✓ Side effects through ports (not inline services)

---

### Layer 5: Persistence Adapter
**File:** `src/main/java/com/uisrael/gestionactivosapi/infraestructura/adaptadores/EquipoRepositorioAdapter.java`

```java
package com.uisrael.gestionactivosapi.infraestructura.adaptadores;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;

import com.uisrael.gestionactivosapi.dominio.entidades.Equipo;
import com.uisrael.gestionactivosapi.dominio.puertos.EquipoRepositorioPuerto;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.EquipoJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.EquipoMapperJpa;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IEquiposJpaRepositorio;

/**
 * Adapter: Implements EquipoRepositorioPuerto using Spring Data JPA.
 * 
 * This adapter:
 * - Implements the port contract (EquipoRepositorioPuerto)
 * - Abstracts JPA details from domain/application
 * - Converts between Domain entity (Equipo) and JPA entity (EquipoJpa)
 * - Uses MapStruct mapper for clean conversion
 * 
 * Dependencies:
 * - IEquiposJpaRepositorio (Spring Data JPA interface)
 * - EquipoMapperJpa (MapStruct mapper)
 * 
 * Important: Domain layer does NOT know this adapter exists.
 * Only Spring configuration wires the port to this adapter.
 * Can be swapped with MongoDB adapter, In-Memory adapter, etc. without changing domain.
 */
@Repository  // Spring component for persistence
@RequiredArgsConstructor  // Constructor injection
public class EquipoRepositorioAdapter implements EquipoRepositorioPuerto {
    
    // JPA repository (Spring Data) - infrastructure detail
    private final IEquiposJpaRepositorio jpaRepository;
    
    // Mapper: converts Equipo <-> EquipoJpa
    private final EquipoMapperJpa mapperJpa;
    
    @Override
    public Equipo guardar(Equipo equipo) {
        // Convert domain entity to JPA entity
        EquipoJpa jpaEntity = mapperJpa.toJpa(equipo);
        
        // Save to database via Spring Data JPA
        EquipoJpa guardado = jpaRepository.save(jpaEntity);
        
        // Convert back to domain entity
        return mapperJpa.toDomain(guardado);
    }
    
    @Override
    public Optional<Equipo> obtenerPorId(Integer id) {
        return jpaRepository.findById(id)
            .map(mapperJpa::toDomain);  // Convert if found
    }
    
    @Override
    public List<Equipo> obtenerTodos() {
        return jpaRepository.findAll()
            .stream()
            .map(mapperJpa::toDomain)  // Convert each JPA entity
            .toList();
    }
    
    @Override
    public Optional<Equipo> obtenerPorNombre(String nombre) {
        return jpaRepository.findByNombreIgnoreCase(nombre)
            .map(mapperJpa::toDomain);
    }
    
    @Override
    public boolean existePorNombre(String nombre) {
        return jpaRepository.existsByNombreIgnoreCase(nombre);
    }
    
    @Override
    public boolean existePorSerial(String serial) {
        return jpaRepository.existsBySerialIgnoreCase(serial);
    }
    
    @Override
    public List<Equipo> obtenerPorEstado(EstadoEquipo estado) {
        // JPA repository method name (assumes it exists)
        return jpaRepository.findByEstado(estado.name())  // JPA entity state is String
            .stream()
            .map(mapperJpa::toDomain)
            .toList();
    }
    
    @Override
    public List<Equipo> obtenerPorCustodio(Integer idCustodio) {
        return jpaRepository.findByIdCustodioActual(idCustodio)
            .stream()
            .map(mapperJpa::toDomain)
            .toList();
    }
    
    @Override
    public void eliminar(Integer id) {
        if (!jpaRepository.existsById(id)) {
            throw new EquipoNoEncontradoException("Equipo no encontrado: " + id);
        }
        jpaRepository.deleteById(id);
    }
    
    @Override
    public void eliminarEntidad(Equipo equipo) {
        EquipoJpa jpaEntity = mapperJpa.toJpa(equipo);
        jpaRepository.delete(jpaEntity);
    }
}
```

**Key points:**
- ✓ Implements `EquipoRepositorioPuerto` interface (the port contract)
- ✓ `@Repository` marks it as Spring component
- ✓ Converts between Domain `Equipo` and JPA `EquipoJpa`
- ✓ Injects both `IEquiposJpaRepositorio` (Spring Data) and `EquipoMapperJpa` (MapStruct)
- ✓ No business logic (only mapping and JPA calls)
- ✓ Can be replaced with other adapters (MongoDB, In-Memory, etc.) without affecting domain/application

---

### Layer 6: JPA Entity (Persistence)
**File:** `src/main/java/com/uisrael/gestionactivosapi/infraestructura/persistencia/jpa/EquipoJpa.java`

```java
package com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * JPA Entity: Equipo (database representation).
 * 
 * This is the persistence-specific twin of the domain Equipo entity.
 * Contains ONLY JPA/database annotations.
 * NO business logic (that's in domain Equipo).
 * 
 * Mappers convert between this (EquipoJpa) and domain Equipo.
 */
@Entity
@Table(name = "equipos", 
       indexes = {
           @Index(name = "idx_nombre", columnList = "nombre"),
           @Index(name = "idx_serial", columnList = "serial"),
           @Index(name = "idx_estado", columnList = "estado")
       })
@Data  // Lombok: generates getters, setters, toString, equals, hashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EquipoJpa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEquipo;
    
    @Column(nullable = false, length = 100, unique = true)
    private String nombre;
    
    @Column(length = 500)
    private String descripcion;
    
    @Column(length = 100)
    private String serial;
    
    @Column(length = 100)
    private String modelo;
    
    @Column(nullable = false, length = 20)
    private String estado;  // Store as String; enum mapping in mapper
    
    @Column(name = "fecha_adquisicion")
    private LocalDateTime fechaAdquisicion;
    
    @Column(name = "fecha_proximo_mantenimiento")
    private LocalDateTime fechaProximoMantenimiento;
    
    @Column(name = "id_custodio_actual")
    private Integer idCustodioActual;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
    
    // Relationships (optional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_custodio_actual", insertable = false, updatable = false)
    private CustodioJpa custodioActual;
    
    // No business logic here - pure data container for ORM
}
```

**Key points:**
- ✓ JPA annotations ONLY (no domain classes imported)
- ✓ Represents database schema structure
- ✓ No business methods (getters/setters via Lombok)
- ✓ Parallel to domain `Equipo` (different structure for persistence concerns)
- ✓ Mappers handle `Equipo ↔ EquipoJpa` conversion

---

### Layer 7: MapStruct Mapper (DTO and Entity)
**File:** `src/main/java/com/uisrael/gestionactivosapi/presentacion/mapeadores/EquipoMapper.java`

```java
package com.uisrael.gestionactivosapi.presentacion.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.uisrael.gestionactivosapi.dominio.entidades.Equipo;
import com.uisrael.gestionactivosapi.presentacion.dto.request.CrearEquipoRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.EquipoResponseDTO;

/**
 * MapStruct mapper: Domain Equipo <-> DTO
 * Converts between domain entity and API request/response DTOs.
 */
@Mapper(componentModel = "spring")
public interface EquipoMapper {
    
    /**
     * Maps create request DTO to domain entity.
     * Note: In use case, we create entity via constructor instead.
     * This is kept for reference; use case does explicit constructor call.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", ignore = true)  // Default in constructor
    @Mapping(target = "fechaAdquisicion", ignore = true)
    Equipo toEntity(CrearEquipoRequestDTO dto);
    
    /**
     * Maps domain entity to response DTO.
     * Called after saving to return user-friendly response.
     */
    EquipoResponseDTO toResponseDTO(Equipo equipo);
}
```

**And the JPA entity mapper:**

**File:** `src/main/java/com/uisrael/gestionactivosapi/infraestructura/persistencia/mapeadores/EquipoMapperJpa.java`

```java
package com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.uisrael.gestionactivosapi.dominio.entidades.Equipo;
import com.uisrael.gestionactivosapi.dominio.valoresobjeto.EstadoEquipo;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.EquipoJpa;

/**
 * MapStruct mapper: Domain Equipo <-> JPA EquipoJpa
 * Converts between domain entity (logic) and JPA entity (persistence).
 */
@Mapper(componentModel = "spring")
public interface EquipoMapperJpa {
    
    /**
     * Maps domain entity to JPA entity for storage.
     */
    @Mapping(target = "idEquipo", source = "id")
    @Mapping(target = "estado", expression = "java(equipo.getEstado().name())")
    EquipoJpa toJpa(Equipo equipo);
    
    /**
     * Maps JPA entity to domain entity after loading.
     */
    @Mapping(target = "id", source = "idEquipo")
    @Mapping(target = "estado", expression = "java(EstadoEquipo.valueOf(equipoJpa.getEstado()))")
    Equipo toDomain(EquipoJpa equipoJpa);
}
```

**Key points:**
- ✓ MapStruct generates implementation automatically
- ✓ Keeps converters separate for DTOs and JPA entities
- ✓ Clean mapping rules using `@Mapping` annotations
- ✓ Handles enum conversions (e.g., `EstadoEquipo.DISPONIBLE` ↔ String "DISPONIBLE")

---

### Layer 8: Spring Configuration (Wiring)
**File:** `src/main/java/com/uisrael/gestionactivosapi/infraestructura/configuracion/PersistenciaConfig.java`

```java
package com.uisrael.gestionactivosapi.infraestructura.configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.uisrael.gestionactivosapi.dominio.puertos.EquipoRepositorioPuerto;
import com.uisrael.gestionactivosapi.infraestructura.adaptadores.EquipoRepositorioAdapter;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.EquipoMapperJpa;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IEquiposJpaRepositorio;

/**
 * Spring configuration for persistence layer.
 * 
 * Registers adapters as implementations of domain ports.
 * This is where DEPENDENCY INJECTION is configured.
 * 
 * Key benefit: Use cases never know adapters exist.
 * If you want to swap JPA for MongoDB, change ONLY this config.
 */
@Configuration
public class PersistenciaConfig {
    
    /**
     * Provides EquipoRepositorioPuerto as a Spring bean.
     * Spring resolves the port to the adapter when use cases ask for it.
     */
    @Bean
    public EquipoRepositorioPuerto equipoRepositorioPuerto(
            IEquiposJpaRepositorio jpaRepository,
            EquipoMapperJpa mapperJpa) {
        return new EquipoRepositorioAdapter(jpaRepository, mapperJpa);
    }
    
    // Repeat for other ports...
    // @Bean
    // public OtroRepositorioPuerto otroRepositorioPuerto(...) { ... }
}
```

**Alternative (preferred in modern Spring):**  
Just mark the adapter with `@Repository` and let Spring auto-discover it:

```java
@Repository
@RequiredArgsConstructor
public class EquipoRepositorioAdapter implements EquipoRepositorioPuerto {
    // Spring automatically registers this as a bean
}
```

---

### Layer 9: Use Case Interface (kept from old code)
**File:** `src/main/java/com/uisrael/gestionactivosapi/aplicacion/casosuso/entradas/ICrearEquipoUseCase.java`

```java
package com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas;

import com.uisrael.gestionactivosapi.presentacion.dto.request.CrearEquipoRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.EquipoResponseDTO;

/**
 * Use case interface: Create Equipment
 * Kept from application layer.
 * Implementation moved to `impl` package and refactored to use ports.
 */
public interface ICrearEquipoUseCase {
    EquipoResponseDTO ejecutar(CrearEquipoRequestDTO request);
}
```

---

### Layer 10: Controller (Updated)
**File:** `src/main/java/com/uisrael/gestionactivosapi/presentacion/controladores/EquiposController.java`

```java
package com.uisrael.gestionactivosapi.presentacion.controladores;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.ICrearEquipoUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IObtenerEquiposUseCase;
import com.uisrael.gestionactivosapi.dominio.excepciones.*;
import com.uisrael.gestionactivosapi.presentacion.dto.request.CrearEquipoRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.EquipoResponseDTO;

/**
 * REST Controller for Equipment endpoints.
 * 
 * Updated to use new use cases instead of old mixed service.
 * Injects use case interfaces (which are implemented by refactored use cases).
 */
@RestController
@RequestMapping("/api/equipos")
@RequiredArgsConstructor
@Slf4j
public class EquiposController {
    
    // Inject use case interfaces (not services)
    private final ICrearEquipoUseCase crearEquipo;
    private final IObtenerEquiposUseCase obtenerEquipos;
    
    /**
     * POST /api/equipos - Create new equipment
     * 
     * Uses ICrearEquipoUseCase to execute the workflow.
     */
    @PostMapping
    public ResponseEntity<EquipoResponseDTO> crear(@RequestBody CrearEquipoRequestDTO request) {
        try {
            EquipoResponseDTO resultado = crearEquipo.ejecutar(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
        } catch (ValidacionNuegoException e) {
            log.warn("Validation error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (EquipoYaExisteException e) {
            log.warn("Equipment already exists: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            log.error("Unexpected error creating equipment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * GET /api/equipos - List all equipment
     */
    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            return ResponseEntity.ok(obtenerEquipos.ejecutar());
        } catch (Exception e) {
            log.error("Error listing equipment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
```

**Key points:**
- ✓ Injects use case **interfaces** (ICrearEquipoUseCase)
- ✓ Calls use case via `ejecutar()` method
- ✓ Handles domain exceptions and maps to HTTP status codes
- ✓ No direct service injection (old pattern removed)

---

### Layer 11: Unit Test for Use Case
**File:** `src/test/java/com/uisrael/gestionactivosapi/aplicacion/casosuso/impl/CrearEquipoUseCaseTest.java`

```java
package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.CrearEquipoUseCaseImpl;
import com.uisrael.gestionactivosapi.dominio.entidades.Equipo;
import com.uisrael.gestionactivosapi.dominio.excepciones.*;
import com.uisrael.gestionactivosapi.dominio.puertos.EquipoRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.NotificacionPuerto;
import com.uisrael.gestionactivosapi.presentacion.dto.request.CrearEquipoRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.EquipoResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.mapeadores.EquipoMapper;

/**
 * Unit tests for CrearEquipoUseCase.
 * 
 * Tests the use case in isolation by mocking dependencies (ports).
 * Does NOT require a database (all persistence is mocked).
 */
@SpringBootTest
@Transactional
@DisplayName("CrearEquipoUseCase Tests")
class CrearEquipoUseCaseTest {
    
    // Mock the port dependencies (not real implementations)
    @MockBean
    private EquipoRepositorioPuerto equipoRepo;
    
    @MockBean
    private NotificacionPuerto notificacion;
    
    @MockBean
    private EquipoMapper mapper;
    
    // The use case being tested (real implementation)
    @Autowired
    private CrearEquipoUseCaseImpl useCase;
    
    @Test
    @DisplayName("Should create equipment when valid request is provided")
    void testCrearEquipoValido() {
        // Arrange
        CrearEquipoRequestDTO request = new CrearEquipoRequestDTO(
            "HP Laptop",
            "Business class laptop",
            "HP 650"
        );
        
        Equipo equipoEsperado = new Equipo("HP Laptop", "Business class laptop", "HP 650");
        equipoEsperado.setId(1);  // After persistence
        
        EquipoResponseDTO responseEsperada = new EquipoResponseDTO(1, "HP Laptop", "HP 650", "DISPONIBLE");
        
        // Mock behavior
        when(equipoRepo.existePorNombre("HP Laptop")).thenReturn(false);
        when(equipoRepo.guardar(any(Equipo.class))).thenReturn(equipoEsperado);
        when(mapper.toResponseDTO(equipoEsperado)).thenReturn(responseEsperada);
        
        // Act
        EquipoResponseDTO resultado = useCase.ejecutar(request);
        
        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1);
        assertThat(resultado.getNombre()).isEqualTo("HP Laptop");
        
        // Verify interactions
        verify(equipoRepo).existePorNombre("HP Laptop");
        verify(equipoRepo).guardar(any(Equipo.class));
        verify(notificacion).notificarEquipoCreado(equipoEsperado, any());
        verify(mapper).toResponseDTO(equipoEsperado);
    }
    
    @Test
    @DisplayName("Should throw exception when equipment name is null")
    void testCrearEquipoNombreNull() {
        // Arrange
        CrearEquipoRequestDTO request = new CrearEquipoRequestDTO(null, "Desc", "Model");
        
        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(request))
            .isInstanceOf(ValidacionNuegoException.class)
            .hasMessageContaining("nombre")
            .hasMessageContaining("requerido");
        
        // Verify no persistence was attempted
        verify(equipoRepo, never()).guardar(any());
        verify(equipoRepo, never()).existePorNombre(any());
    }
    
    @Test
    @DisplayName("Should throw exception when equipment already exists")
    void testCrearEquipoYaExiste() {
        // Arrange
        CrearEquipoRequestDTO request = new CrearEquipoRequestDTO("HP Laptop", "Desc", "Model");
        when(equipoRepo.existePorNombre("HP Laptop")).thenReturn(true);
        
        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(request))
            .isInstanceOf(EquipoYaExisteException.class)
            .hasMessageContaining("HP Laptop");
        
        // Verify save was never called
        verify(equipoRepo, never()).guardar(any());
    }
    
    @Test
    @DisplayName("Should notify after successful creation")
    void testNotificacionEnviadaAlCrear() {
        // Arrange
        CrearEquipoRequestDTO request = new CrearEquipoRequestDTO("HP Laptop", "Desc", "Model");
        Equipo equipoGuardado = new Equipo("HP Laptop", "Desc", "Model");
        equipoGuardado.setId(1);
        
        when(equipoRepo.existePorNombre("HP Laptop")).thenReturn(false);
        when(equipoRepo.guardar(any(Equipo.class))).thenReturn(equipoGuardado);
        when(mapper.toResponseDTO(equipoGuardado)).thenReturn(new EquipoResponseDTO(...));
        
        // Act
        useCase.ejecutar(request);
        
        // Assert
        verify(notificacion, times(1)).notificarEquipoCreado(equipoGuardado, any());
    }
}
```

**Key points:**
- ✓ Mocks `EquipoRepositorioPuerto` (not JPA repository)
- ✓ Mocks `NotificacionPuerto`
- ✓ Tests use case logic without database
- ✓ Fast, isolated tests
- ✓ Verifies mock interactions (call expectations)

---

### Layer 12: Integration Test
**File:** `src/test/java/com/uisrael/gestionactivosapi/apresentacion/controladores/CrearEquipoIntegrationTest.java`

```java
package com.uisrael.gestionactivosapi.presentacion.controladores;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.notNullValue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uisrael.gestionactivosapi.dominio.puertos.EquipoRepositorioPuerto;
import com.uisrael.gestionactivosapi.presentacion.dto.request.CrearEquipoRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.EquipoResponseDTO;

/**
 * Integration test: End-to-end flow from HTTP request to database.
 * 
 * This test:
 * - Sends actual HTTP request (via MockMvc)
 * - Controller → Use case → Domain → Adapter → Database
 * - Verifies response and database state
 * 
 * Uses real Spring context (Spring Boot Test), real database (test-h2).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional  // Rollback after test
@DisplayName("CrearEquipo Integration Tests")
class CrearEquipoIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private EquipoRepositorioPuerto equipoRepo;  // Use the port (injected adapter)
    
    @Test
    @DisplayName("Should create equipment via HTTP POST")
    void testCrearEquipoViaHttp() throws Exception {
        // Arrange
        CrearEquipoRequestDTO request = new CrearEquipoRequestDTO(
            "Dell XPS",
            "High-performance laptop",
            "Dell 13"
        );
        
        String requestJson = objectMapper.writeValueAsString(request);
        
        // Act & Assert
        MvcResult result = mockMvc.perform(post("/api/equipos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", notNullValue()))
            .andExpect(jsonPath("$.nombre", is("Dell XPS")))
            .andExpect(jsonPath("$.estado", is("DISPONIBLE")))
            .andReturn();
        
        // Parse response
        String responseJson = result.getResponse().getContentAsString();
        EquipoResponseDTO response = objectMapper.readValue(responseJson, EquipoResponseDTO.class);
        
        // Verify in database via port
        var equipoEnDb = equipoRepo.obtenerPorId(response.getId());
        assertThat(equipoEnDb).isPresent();
        assertThat(equipoEnDb.get().getNombre()).isEqualTo("Dell XPS");
        assertThat(equipoEnDb.get().estaDisponible()).isTrue();
    }
    
    @Test
    @DisplayName("Should reject equipment with duplicate name")
    void testCrearEquipoDuplicado() throws Exception {
        // Arrange: Create first equipment
        CrearEquipoRequestDTO request1 = new CrearEquipoRequestDTO(
            "HP Laptop", "Desc", "Model"
        );
        mockMvc.perform(post("/api/equipos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request1)))
            .andExpect(status().isCreated());
        
        // Act & Assert: Try to create duplicate
        CrearEquipoRequestDTO request2 = new CrearEquipoRequestDTO(
            "HP Laptop", "Different", "Different"
        );
        mockMvc.perform(post("/api/equipos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request2)))
            .andExpect(status().isConflict());  // HTTP 409
    }
}
```

**Key points:**
- ✓ Full Spring context (real beans)
- ✓ Real HTTP request/response (MockMvc)
- ✓ Real database (H2 test database)
- ✓ Verifies complete flow: HTTP → Use case → Domain → Adapter → DB
- ✓ Tests both happy path and error scenarios
- ✓ Faster than manual testing; catches regressions

---

### Layer 13: Exception Classes
**File:** `src/main/java/com/uisrael/gestionactivosapi/dominio/excepciones/`

```java
package com.uisrael.gestionactivosapi.dominio.excepciones;

/**
 * Base exception for domain violations.
 * Thrown when business rules are violated.
 */
public class ExcepcionDominio extends RuntimeException {
    public ExcepcionDominio(String mensaje) {
        super(mensaje);
    }
    
    public ExcepcionDominio(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

// Specific exceptions
public class ValidacionNuegoException extends ExcepcionDominio {
    public ValidacionNuegoException(String mensaje) {
        super(mensaje);
    }
}

public class EquipoNoEncontradoException extends ExcepcionDominio {
    public EquipoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}

public class EquipoYaExisteException extends ExcepcionDominio {
    public EquipoYaExisteException(String mensaje) {
        super(mensaje);
    }
}

public class EquipoNoDisponibleException extends ExcepcionDominio {
    public EquipoNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}

public class TransicionEstadoIlegalException extends ExcepcionDominio {
    public TransicionEstadoIlegalException(String mensaje) {
        super(mensaje);
    }
}
```

---

## Architecture Summary (Refactored)

```
HTTP Request (POST /api/equipos)
    ↓
EquiposController (Presentation)
    ↓ injects (interface)
ICrearEquipoUseCase (Application boundary)
    ↓ injects (interface [port])
EquipoRepositorioPuerto (Domain)
    ↓ implemented by (Spring wiring)
EquipoRepositorioAdapter (Infrastructure)
    ↓ uses
IEquiposJpaRepositorio (Spring Data JPA)
    ↓ maps to/from
EquipoJpa (JPA Entity)
    ↓
PostgreSQL Database

Domain Entity (Equipo) = Pure logic, testable without infrastructure
JPA Entity (EquipoJpa) = Persistence only, database schema representation
Port Interface = Contract, dependency direction
Adapter = Implementation, infrastructure detail
```

---

## Key Refactoring Benefits

| Aspect | Before | After |
|--------|--------|-------|
| **Dependencies** | Service → Concrete JPA repo | Use case → Port interface |
| **Testing** | Needs database, complex setup | Mocks ports, fast & isolated |
| **Changes** | Database change = code change everywhere | Swap adapter only, domain/app unchanged |
| **Responsibilities** | Mixed (validation, logic, persistence) | Clear: domain logic, use case workflow, adapter mechanics |
| **Exception Handling** | Generic RuntimeException | Domain-specific exceptions |
| **Testability** | Hard (infrastructure coupled) | Easy (interfaces to mock) |
| **Scalability** | New feature = modify service | New feature = new use case + wire |
| **Team Understanding** | Unclear flow, mixed concerns | Clear layering, obvious dependencies |

---

## Next Steps for Implementation (Phase 3)

1. **Create domain entities** with business logic (copy Equipo pattern above)
2. **Define all ports** (repository + service ports)
3. **Implement use cases** following CrearEquipoUseCaseImpl pattern
4. **Create adapters** following EquipoRepositorioAdapter pattern
5. **Write tests** for each layer (unit + integration)
6. **Wire in Spring configuration** (register adapters as port implementations)
7. **Update controllers** to inject use cases instead of services
8. **Verify** no JPA imports in domain/application layers

---

**This example is your template.** Repeat for all 15-20 entities and adapt as needed for your specific domain rules.

Good luck! 🚀
