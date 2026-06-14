package com.uisrael.gestionactivosapi.dominio.entidades.inventario;

public enum EstadoOrdenCompra {
    BORRADOR,
    EMITIDA,
    RECEPCION_PARCIAL,
    /** @deprecated Migrado a RECEPCION_PARCIAL via V22. Conservado para compatibilidad con datos legados. */
    @Deprecated
    RECIBIDA_PARCIAL,
    RECIBIDA,
    CANCELADA
}
