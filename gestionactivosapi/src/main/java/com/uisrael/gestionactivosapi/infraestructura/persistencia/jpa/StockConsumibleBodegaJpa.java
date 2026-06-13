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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "stock_consumible_bodega",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_bodega", "id_consumible"}))
public class StockConsumibleBodegaJpa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_stock_consumible_bodega")
    private Integer idStockConsumibleBodega;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bodega", nullable = false)
    private BodegaJpa bodega;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_consumible", nullable = false)
    private ConsumibleJpa consumible;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad = 0;
}
