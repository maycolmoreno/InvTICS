package com.uisrael.gestionactivosapi.dominio.excepciones;

/**
 * Excepción lanzada cuando un custodio no está autorizado para una operación.
 */
public class CustodioNoAutorizadoException extends ValidacionNegocioException {
    
    private final Integer custodioId;
    private final String razon;
    
    public CustodioNoAutorizadoException(Integer custodioId, String razon) {
        super(String.format("Custodio %d no autorizado: %s", custodioId, razon));
        this.custodioId = custodioId;
        this.razon = razon;
    }
    
    public Integer getCustodioId() {
        return custodioId;
    }
    
    public String getRazon() {
        return razon;
    }
}
