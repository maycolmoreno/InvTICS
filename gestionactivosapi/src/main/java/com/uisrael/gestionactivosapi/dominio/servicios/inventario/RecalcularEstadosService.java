package com.uisrael.gestionactivosapi.dominio.servicios.inventario;

import java.util.List;

import com.uisrael.gestionactivosapi.dominio.entidades.inventario.EstadoOrdenCompra;
import com.uisrael.gestionactivosapi.dominio.entidades.inventario.EstadoOrdenCompraDetalle;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.OrdenCompraDetalleJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.OrdenCompraJpa;

public class RecalcularEstadosService {

    private final OrdenCompraDetalleStateMachine detalleStateMachine;
    private final OrdenCompraStateMachine ordenStateMachine;

    public RecalcularEstadosService() {
        this(new OrdenCompraDetalleStateMachine(), new OrdenCompraStateMachine());
    }

    public RecalcularEstadosService(OrdenCompraDetalleStateMachine detalleStateMachine,
                                    OrdenCompraStateMachine ordenStateMachine) {
        this.detalleStateMachine = detalleStateMachine;
        this.ordenStateMachine = ordenStateMachine;
    }

    public EstadoOrdenCompraDetalle recalcularDetalle(OrdenCompraDetalleJpa detalle) {
        EstadoOrdenCompraDetalle estado = detalleStateMachine.calcularEstado(
                detalle.getCantidadSolicitada(),
                detalle.getCantidadRecibida(),
                detalle.getEstado());
        detalle.setEstado(estado);
        return estado;
    }

    public EstadoOrdenCompra recalcularOrden(OrdenCompraJpa orden, List<OrdenCompraDetalleJpa> detalles) {
        EstadoOrdenCompra estadoActual = orden.getEstado();
        if (estadoActual == EstadoOrdenCompra.BORRADOR
                || estadoActual == EstadoOrdenCompra.CANCELADA
                || estadoActual == EstadoOrdenCompra.RECIBIDA) {
            return estadoActual;
        }

        for (OrdenCompraDetalleJpa detalle : detalles) {
            recalcularDetalle(detalle);
        }

        boolean todosCerrados = !detalles.isEmpty() && detalles.stream().allMatch(this::estaCerrado);
        if (todosCerrados) {
            ordenStateMachine.validarTransicion(orden.getEstado(), EstadoOrdenCompra.RECIBIDA);
            orden.setEstado(EstadoOrdenCompra.RECIBIDA);
            return EstadoOrdenCompra.RECIBIDA;
        }

        boolean hayRecepcionParcial = detalles.stream()
                .anyMatch(d -> d.getEstado() == EstadoOrdenCompraDetalle.PARCIAL);
        if (hayRecepcionParcial) {
            ordenStateMachine.validarTransicion(orden.getEstado(), EstadoOrdenCompra.RECEPCION_PARCIAL);
            orden.setEstado(EstadoOrdenCompra.RECEPCION_PARCIAL);
            return EstadoOrdenCompra.RECEPCION_PARCIAL;
        }

        if (orden.getEstado() == EstadoOrdenCompra.EMITIDA) {
            return EstadoOrdenCompra.EMITIDA;
        }

        return orden.getEstado();
    }

    private boolean estaCerrado(OrdenCompraDetalleJpa detalle) {
        return detalle.getEstado() == EstadoOrdenCompraDetalle.COMPLETO
                || detalle.getEstado() == EstadoOrdenCompraDetalle.CANCELADO;
    }
}
