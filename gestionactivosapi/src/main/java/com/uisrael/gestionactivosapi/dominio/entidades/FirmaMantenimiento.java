package com.uisrael.gestionactivosapi.dominio.entidades;

import java.time.LocalDateTime;

public record FirmaMantenimiento(
        Integer id,
        Integer idMantenimiento,
        TipoFirma tipoFirma,
        String firmaBase64,
        LocalDateTime firmadoEn,
        String ipOrigen) {
}
