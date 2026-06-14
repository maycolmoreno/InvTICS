package com.uisrael.gestionactivosapi.dominio.servicios.inventario;

import com.uisrael.gestionactivosapi.dominio.entidades.inventario.EstadoOrdenCompraDetalle;
import com.uisrael.gestionactivosapi.dominio.excepciones.CantidadExcedidaException;
import com.uisrael.gestionactivosapi.dominio.excepciones.CantidadInvalidaException;

public class OrdenCompraDetalleStateMachine {

    public EstadoOrdenCompraDetalle calcularEstado(
            Integer cantidadSolicitada,
            Integer cantidadRecibida,
            EstadoOrdenCompraDetalle estadoActual) {
        if (estadoActual == EstadoOrdenCompraDetalle.CANCELADO) {
            return EstadoOrdenCompraDetalle.CANCELADO;
        }
        int solicitada = cantidadSolicitada == null ? 0 : cantidadSolicitada;
        int recibida = cantidadRecibida == null ? 0 : cantidadRecibida;

        if (solicitada <= 0) {
            throw new CantidadInvalidaException("La cantidad solicitada debe ser mayor a cero");
        }
        if (recibida < 0) {
            throw new CantidadInvalidaException("La cantidad recibida no puede ser negativa");
        }
        if (recibida > solicitada) {
            throw new CantidadExcedidaException("La cantidad recibida excede la cantidad solicitada");
        }
        if (recibida == 0) {
            return EstadoOrdenCompraDetalle.PENDIENTE;
        }
        if (recibida < solicitada) {
            return EstadoOrdenCompraDetalle.PARCIAL;
        }
        return EstadoOrdenCompraDetalle.COMPLETO;
    }
}
