package com.uisrael.gestionactivosapi.dominio.excepciones;

/**
 * Excepción lanzada cuando se intenta una operación que viola la integridad de datos.
 */
public class IntegridadDatosException extends ExcepcionDominio {
    
    public IntegridadDatosException(String mensaje) {
        super(mensaje);
    }
    
    public IntegridadDatosException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
