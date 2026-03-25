package com.uisrael.gestionactivosapi.dominio.excepciones;

/**
 * Excepción base para todas las excepciones del dominio.
 * Representa errores en lógica de negocio.
 */
public class ExcepcionDominio extends RuntimeException {
    
    public ExcepcionDominio(String mensaje) {
        super(mensaje);
    }
    
    public ExcepcionDominio(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
