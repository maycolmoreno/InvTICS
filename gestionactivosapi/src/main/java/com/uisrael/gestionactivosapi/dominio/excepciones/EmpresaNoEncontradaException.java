package com.uisrael.gestionactivosapi.dominio.excepciones;

/**
 * Excepción lanzada cuando no se encuentra una empresa por su ID o RUC.
 */
public class EmpresaNoEncontradaException extends RecursoNoEncontradoException {

    public EmpresaNoEncontradaException(Integer id) {
        super("Empresa", id);
    }

    public EmpresaNoEncontradaException(String ruc) {
        super("Empresa", ruc);
    }
}
