package com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa;

import java.io.Serializable;

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
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Cambio relevante detectado en una corrida de sincronizacion de empleados.
 * Tipos: CREADO, ACTUALIZADO, INACTIVADO, REACTIVADO, ADVERTENCIA, ALERTA_ACTIVOS.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "sync_empleados_cambio")
public class SyncEmpleadosCambioJpa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cambio")
    private Integer idCambio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ejecucion", nullable = false)
    private SyncEmpleadosEjecucionJpa ejecucion;

    @Column(name = "cedula", length = 20, nullable = false)
    private String cedula;

    @Column(name = "tipo", length = 30, nullable = false)
    private String tipo;

    @Column(name = "detalle", length = 500)
    private String detalle;

    @Column(name = "id_custodio")
    private Integer idCustodio;
}
