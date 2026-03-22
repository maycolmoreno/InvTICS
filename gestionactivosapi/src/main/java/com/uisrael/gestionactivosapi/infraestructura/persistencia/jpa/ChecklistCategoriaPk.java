package com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa;

import java.io.Serializable;
import java.util.Objects;

public class ChecklistCategoriaPk implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idActividad;
    private Integer idCategoria;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ChecklistCategoriaPk other)) {
            return false;
        }
        return Objects.equals(idActividad, other.idActividad)
                && Objects.equals(idCategoria, other.idCategoria);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idActividad, idCategoria);
    }
}
