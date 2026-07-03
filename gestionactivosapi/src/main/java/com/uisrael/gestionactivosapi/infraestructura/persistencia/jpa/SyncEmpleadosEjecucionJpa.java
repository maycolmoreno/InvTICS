package com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Registro de una corrida de sincronizacion de empleados: fecha, origen
 * (MANUAL, URL, ARCHIVO) y contadores de resultado.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "sync_empleados_ejecucion")
public class SyncEmpleadosEjecucionJpa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ejecucion")
    private Integer idEjecucion;

    @Column(name = "ejecutado_en", nullable = false)
    private LocalDateTime ejecutadoEn;

    @Column(name = "origen", length = 20, nullable = false)
    private String origen;

    @Column(name = "ejecutado_por", length = 100)
    private String ejecutadoPor;

    @Column(name = "total_recibidos", nullable = false)
    private int totalRecibidos;

    @Column(name = "creados", nullable = false)
    private int creados;

    @Column(name = "actualizados", nullable = false)
    private int actualizados;

    @Column(name = "inactivados", nullable = false)
    private int inactivados;

    @Column(name = "reactivados", nullable = false)
    private int reactivados;

    @Column(name = "sin_cambios", nullable = false)
    private int sinCambios;

    @Column(name = "advertencias", nullable = false)
    private int advertencias;
}
