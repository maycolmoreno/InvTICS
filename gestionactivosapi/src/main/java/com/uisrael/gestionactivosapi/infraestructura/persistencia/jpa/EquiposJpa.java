package com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.FetchType;
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
@Table(name = "equipos")
@SQLRestriction("deleted_at IS NULL")
public class EquiposJpa extends AuditableEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_equipo")
    private int idEquipo;

    @Column(name = "codigo_sap", length = 20)
    private String codigoSap;

    @Column(length = 100)
    private String modelo;

    @Column(length = 100)
    private String serial;

    @Column(length = 100)
    private String procesador;

    @Column(name = "memoria_ram_gb")
    private Integer memoriaRamGb;

    @Column(name = "capacidad_almacenamiento_gb")
    private Integer capacidadAlmacenamientoGb;

    @Column(name = "licencia_windows_activada")
    private Boolean licenciaWindowsActivada;

    @Column(length = 100)
    private String mac;

    @Column(name = "fecha_compra")
    private LocalDate fechaCompra;

    @Column(name = "precio_compra", precision = 10, scale = 2)
    private BigDecimal precioCompra;

    @Column(name = "estado_equipo", length = 50)
    private String estadoEquipo;

    @Column(name = "observacion_equipo", columnDefinition = "TEXT")
    private String observacionEquipo;

    private boolean estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_marca", nullable = false)
    private MarcasJpa fkMarcas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false)
    private CategoriaEquiposJpa fkCategoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_ubicacion")
    private UbicacionesJpa fkUbicacion;

}
