package com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.uisrael.gestionactivosapi.dominio.entidades.inventario.EstadoRecepcionLote;
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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "recepciones_lote")
public class RecepcionLoteJpa extends AuditableEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recepcion_lote")
    private Integer idRecepcionLote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_orden_compra", nullable = false)
    private OrdenCompraJpa ordenCompra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_orden_compra_detalle", nullable = false)
    private OrdenCompraDetalleJpa ordenCompraDetalle;

    @Column(name = "numero_lote", length = 60)
    private String numeroLote;

    @Column(name = "fecha_recepcion", nullable = false)
    private LocalDateTime fechaRecepcion;

    @Column(name = "cantidad_recibida", nullable = false)
    private Integer cantidadRecibida;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_item", length = 20, nullable = false)
    private TipoItemInventario tipoItem;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 30, nullable = false)
    private EstadoRecepcionLote estado = EstadoRecepcionLote.REGISTRADO;

    @Column(name = "observacion", columnDefinition = "TEXT")
    private String observacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bodega_destino")
    private BodegaJpa bodegaDestino;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_custodio_receptor")
    private CustodiosJpa custodioReceptor;

    @Column(name = "uuid", length = 60, nullable = false, unique = true)
    private String uuid;

    @Column(name = "recepcionado_por", length = 100, nullable = false)
    private String recepcionadoPor;

    @Column(name = "recepcionado_en", nullable = false)
    private LocalDateTime recepcionadoEn;
}
