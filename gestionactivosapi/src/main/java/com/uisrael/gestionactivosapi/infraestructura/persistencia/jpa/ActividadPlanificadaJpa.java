package com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.SQLRestriction;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.base.AuditableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "actividades_planificadas")
@SQLRestriction("deleted_at IS NULL")
public class ActividadPlanificadaJpa extends AuditableEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_actividad_planificada")
    private Long idActividadPlanificada;

    @Column(name = "tecnico_id", nullable = false)
    private Integer tecnicoId;

    @Column(name = "creado_por_id", nullable = false)
    private Integer creadoPorId;

    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "tipo_actividad", nullable = false, length = 50)
    private String tipoActividad;

    @Column(name = "prioridad", nullable = false, length = 20)
    private String prioridad;

    @Column(name = "estado", nullable = false, length = 30)
    private String estado;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "fecha_completada")
    private LocalDateTime fechaCompletada;

    @Column(name = "tiempo_estimado_minutos")
    private Integer tiempoEstimadoMinutos;

    @Column(name = "tiempo_real_minutos")
    private Integer tiempoRealMinutos;

    @Column(name = "referencia_mantenimiento_id")
    private Integer referenciaMantenimientoId;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tecnico_id", insertable = false, updatable = false)
    private UsuariosJpa fkTecnico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por_id", insertable = false, updatable = false)
    private UsuariosJpa fkCreadoPor;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "referencia_mantenimiento_id", insertable = false, updatable = false)
    private MantenimientosJpa fkMantenimiento;

    @Column(name = "fk_mantenimiento_prog")
    private Long fkMantenimientoProgId;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "fk_mantenimiento_prog", insertable = false, updatable = false)
    private MantenimientoProgramadoJpa fkMantenimientoProgramado;

    @Column(name = "fk_equipo")
    private Integer fkEquipoId;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "fk_equipo", insertable = false, updatable = false)
    private EquiposJpa fkEquipo;

    /** Farmacia objetivo cuando el mantenimiento programado es general (sin equipo). */
    @Column(name = "fk_ubicacion")
    private Integer fkUbicacionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "fk_ubicacion", insertable = false, updatable = false)
    private UbicacionesJpa fkUbicacion;
}
