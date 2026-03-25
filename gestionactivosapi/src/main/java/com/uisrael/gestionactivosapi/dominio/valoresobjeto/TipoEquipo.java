package com.uisrael.gestionactivosapi.dominio.valoresobjeto;

import com.uisrael.gestionactivosapi.dominio.excepciones.ValidacionNegocioException;

/**
 * Value object que representa los tipos de equipos posibles.
 */
public enum TipoEquipo {
    COMPUTADORA("Computadora"),
    LAPTOP("Laptop"),
    SERVIDOR("Servidor"),
    PRINTER("Impresora"),
    SCANNER("Escáner"),
    ROUTER("Router"),
    SWITCH("Switch"),
    TELEFONO("Teléfono"),
    MONITOR("Monitor"),
    OTRO("Otro"),
    PROYECTOR("Proyector"),
    MOUSE("Mouse"),
    TECLADO("Teclado"),
    AUDIFONO("Audífono"),
    WEBCAM("Webcam");
    
    private final String descripcion;
    
    TipoEquipo(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
