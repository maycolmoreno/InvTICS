package com.uisrael.gestionactivosapi.dominio.excepciones;

/**
 * Excepción lanzada cuando una operación requiere aprobación pero no ha sido aprobada.
 */
public class AprobadonRequeridaException extends ValidacionNegocioException {
    
    public AprobadonRequeridaException(String tipoOperacion) {
        super(String.format("Operación '%s' requiere aprobación", tipoOperacion));
    }
    
    public AprobadonRequeridaException(String tipoOperacion, String detalles) {
        super(String.format("Operación '%s' requiere aprobación: %s", tipoOperacion, detalles));
    }
}
