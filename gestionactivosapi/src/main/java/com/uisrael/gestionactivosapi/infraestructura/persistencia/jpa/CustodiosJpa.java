package com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa;

import java.io.Serializable;
import java.time.LocalDate;

import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.base.AuditableEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "custodios")
@SQLRestriction("deleted_at IS NULL")
public class CustodiosJpa extends AuditableEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_custodio")
    private int idCustodio;

    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    @Column(name = "cedula", length = 20, nullable = false)
    private String cedula;

    @Column(name = "correo", length = 150)
    private String correo;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "fecha_ingreso")
    private LocalDate fechaIngreso;

    private boolean estado;

    @ManyToOne
    @JoinColumn(name = "fk_cargo")
    private CargosJpa fkCargo;

    @ManyToOne
    @JoinColumn(name = "fk_ubicacion")
    private UbicacionesJpa fkUbicacion;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private UsuariosJpa fkUsuario;

    /** true si el registro proviene de la sincronizacion de empleados. */
    @Column(name = "origen_sync", nullable = false)
    private boolean origenSync;

    /** Ultima vez que la sincronizacion vio a este empleado en la fuente. */
    @Column(name = "sincronizado_en")
    private java.time.LocalDateTime sincronizadoEn;

    /** Cargo tal cual viene del directorio institucional externo (texto libre, sin catalogo). */
    @Column(name = "cargo_directorio", length = 150)
    private String cargoDirectorio;

    /** Departamento tal cual viene del directorio institucional externo (texto libre, sin catalogo). */
    @Column(name = "departamento_directorio", length = 150)
    private String departamentoDirectorio;
}
