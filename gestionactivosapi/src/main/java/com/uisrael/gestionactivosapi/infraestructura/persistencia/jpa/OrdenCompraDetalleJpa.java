package com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa;

import java.io.Serializable;
import java.math.BigDecimal;

import com.uisrael.gestionactivosapi.dominio.entidades.inventario.EstadoOrdenCompraDetalle;
import com.uisrael.gestionactivosapi.dominio.entidades.inventario.TipoItemInventario;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.base.AuditableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ordenes_compra_detalle")
public class OrdenCompraDetalleJpa extends AuditableEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_orden_compra_detalle")
    private Integer idOrdenCompraDetalle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_orden_compra", nullable = false)
    private OrdenCompraJpa ordenCompra;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_item", length = 20, nullable = false)
    private TipoItemInventario tipoItem;

    @Column(name = "descripcion", length = 255, nullable = false)
    private String descripcion;

    @Column(name = "modelo", length = 120)
    private String modelo;

    @Column(name = "cantidad_solicitada", nullable = false)
    private Integer cantidadSolicitada;

    @Column(name = "cantidad_recibida")
    private Integer cantidadRecibida = 0;

    @Column(name = "precio_unitario", precision = 14, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "unidad_medida", length = 30)
    private String unidadMedida;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 30, nullable = false)
    private EstadoOrdenCompraDetalle estado = EstadoOrdenCompraDetalle.PENDIENTE;

    @Column(name = "observacion", columnDefinition = "TEXT")
    private String observacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria")
    private CategoriaEquiposJpa categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_marca")
    private MarcasJpa marca;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_consumible")
    private ConsumibleJpa consumible;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;
}
