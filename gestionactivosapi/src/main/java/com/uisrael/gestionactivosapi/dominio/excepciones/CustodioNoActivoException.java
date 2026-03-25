package com.uisrael.gestionactivosapi.dominio.excepciones;

/**
 * Excepción lanzada cuando un custodio no está activo.
 */
public class CustodioNoActivoException extends ValidacionNegocioException {
    
    private final Integer custodioId;
    
    public CustodioNoActivoException(Integer custodioId) {
        super(String.format("Custodio %d no está activo", custodioId));
        this.custodioId = custodioId;
    }
    
    public Integer getCustodioId() {
        return custodioId;
    }
}
