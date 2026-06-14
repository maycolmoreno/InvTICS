package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import com.uisrael.gestionactivosapi.dominio.entidades.inventario.EstadoOrdenCompra;
import com.uisrael.gestionactivosapi.dominio.entidades.inventario.EstadoOrdenCompraDetalle;
import com.uisrael.gestionactivosapi.dominio.entidades.inventario.EstadoRecepcionLote;
import com.uisrael.gestionactivosapi.dominio.entidades.inventario.TipoItemInventario;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrdenCompraRecepcionSmokeTest {

    @Test
    void estadoOrdenCompra_tieneValoresRequeridos() {
        assertThat(EstadoOrdenCompra.BORRADOR).isNotNull();
        assertThat(EstadoOrdenCompra.RECEPCION_PARCIAL).isNotNull();
        assertThat(EstadoOrdenCompra.EMITIDA).isNotNull();
        assertThat(EstadoOrdenCompra.RECIBIDA).isNotNull();
        assertThat(EstadoOrdenCompra.CANCELADA).isNotNull();
    }

    @Test
    void tipoItemInventario_tieneStock() {
        assertThat(TipoItemInventario.STOCK).isNotNull();
        assertThat(TipoItemInventario.ACTIVO).isNotNull();
        assertThat(TipoItemInventario.CONSUMIBLE).isNotNull();
    }

    @Test
    void estadoOrdenCompraDetalle_valoresCompletos() {
        assertThat(EstadoOrdenCompraDetalle.values()).containsExactlyInAnyOrder(
                EstadoOrdenCompraDetalle.PENDIENTE,
                EstadoOrdenCompraDetalle.PARCIAL,
                EstadoOrdenCompraDetalle.COMPLETO,
                EstadoOrdenCompraDetalle.CANCELADO
        );
    }

    @Test
    void estadoRecepcionLote_valoresCompletos() {
        assertThat(EstadoRecepcionLote.values()).containsExactlyInAnyOrder(
                EstadoRecepcionLote.REGISTRADO,
                EstadoRecepcionLote.APLICADO,
                EstadoRecepcionLote.ANULADO
        );
    }
}
