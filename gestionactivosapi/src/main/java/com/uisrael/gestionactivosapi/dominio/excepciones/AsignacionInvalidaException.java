package com.uisrael.gestionactivosapi.dominio.excepciones;

/**
 * Excepción lanzada cuando una asignación de equipo a custodio es inválida.
 */
public class AsignacionInvalidaException extends ValidacionNegocioException {
    
    public AsignacionInvalidaException(String razon) {
        super("Asignación inválida: " + razon);
    }
    
    public AsignacionInvalidaException(Integer equipoId, Integer custodioId, String razon) {
        super(String.format("No se puede asignar equipo %d a custodio %d: %s", 
            equipoId, custodioId, razon));
    }
}
