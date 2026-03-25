package com.uisrael.gestionactivosapi.dominio.excepciones;

/**
 * Excepción lanzada cuando no se cumplen las reglas de validación de negocio.
 */
public class ValidacionNegocioException extends ExcepcionDominio {
    
    public ValidacionNegocioException(String mensaje) {
        super(mensaje);
    }
    
    public ValidacionNegocioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
