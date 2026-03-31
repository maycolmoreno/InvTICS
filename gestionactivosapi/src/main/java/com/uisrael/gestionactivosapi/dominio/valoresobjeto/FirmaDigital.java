package com.uisrael.gestionactivosapi.dominio.valoresobjeto;

import java.time.LocalDateTime;

/**
 * Value Object que encapsula los datos de una firma digital de mantenimiento.
 * Inmutable por diseño (Java record).
 */
public record FirmaDigital(
        Integer firmadoPorId,
        String tipoFirma,
        String firmaBase64,
        LocalDateTime firmadoEn,
        String ipOrigen
) {

    public FirmaDigital {
        if (firmadoPorId == null) {
            throw new IllegalArgumentException("firmadoPorId no puede ser nulo");
        }
        if (tipoFirma == null || tipoFirma.isBlank()) {
            throw new IllegalArgumentException("tipoFirma no puede ser nulo o vacío");
        }
        if (firmaBase64 == null || firmaBase64.isBlank()) {
            throw new IllegalArgumentException("firmaBase64 no puede ser nula o vacía");
        }
    }

    public boolean esValida() {
        return firmaBase64 != null && !firmaBase64.isBlank()
                && firmadoPorId != null
                && tipoFirma != null;
    }
}
