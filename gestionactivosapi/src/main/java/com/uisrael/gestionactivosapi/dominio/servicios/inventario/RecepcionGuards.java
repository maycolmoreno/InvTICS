package com.uisrael.gestionactivosapi.dominio.servicios.inventario;

import com.uisrael.gestionactivosapi.dominio.entidades.inventario.EstadoOrdenCompra;
import com.uisrael.gestionactivosapi.dominio.entidades.inventario.EstadoOrdenCompraDetalle;
import com.uisrael.gestionactivosapi.dominio.excepciones.CantidadExcedidaException;
import com.uisrael.gestionactivosapi.dominio.excepciones.CantidadInvalidaException;
import com.uisrael.gestionactivosapi.dominio.excepciones.RecepcionNoPermitidaException;

public class RecepcionGuards {

    public void validarOrdenRecibible(EstadoOrdenCompra estado) {
        if (estado == EstadoOrdenCompra.EMITIDA || estado == EstadoOrdenCompra.RECEPCION_PARCIAL) {
            return;
        }
        throw new RecepcionNoPermitidaException("La orden de compra no permite recepcion en estado " + estado);
    }

    public void validarDetalleRecibible(EstadoOrdenCompraDetalle estado) {
        if (estado == EstadoOrdenCompraDetalle.PENDIENTE || estado == EstadoOrdenCompraDetalle.PARCIAL) {
            return;
        }
        throw new RecepcionNoPermitidaException("El detalle de orden no permite recepcion en estado " + estado);
    }

    public void validarCantidad(Integer cantidad, Integer cantidadSolicitada, Integer cantidadRecibida) {
        int solicitada = cantidadSolicitada == null ? 0 : cantidadSolicitada;
        int recibida = cantidadRecibida == null ? 0 : cantidadRecibida;
        int recibidaNueva = cantidad == null ? 0 : cantidad;

        if (recibidaNueva <= 0) {
            throw new CantidadInvalidaException("La cantidad a recibir debe ser mayor a cero");
        }
        if (solicitada <= 0) {
            throw new CantidadInvalidaException("La cantidad solicitada debe ser mayor a cero");
        }
        if (recibida < 0) {
            throw new CantidadInvalidaException("La cantidad recibida acumulada no puede ser negativa");
        }

        int pendiente = solicitada - recibida;
        if (recibidaNueva > pendiente) {
            throw new CantidadExcedidaException("La cantidad a recibir excede la cantidad pendiente");
        }
    }
}
