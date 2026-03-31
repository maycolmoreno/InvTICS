package com.uisrael.gestionactivosapi.dominio.valoresobjeto;

/**
 * Value Object inmutable que agrupa los datos de snapshot del equipo
 * capturados al momento de crear un mantenimiento.
 *
 * <p>Estos campos son una "foto" del equipo en ese instante para que
 * los reportes históricos muestren los datos correctos aunque el equipo
 * haya sido modificado posteriormente.</p>
 *
 * @param serie           número de serie del equipo al momento del mantenimiento
 * @param codigoInterno   código interno (ex sine_snapshot) - código SINE o código SAP
 * @param year            año de referencia del snapshot
 */
public record SnapshotEquipo(
    String serie,
    String codigoInterno,
    Integer year
) {

    /**
     * Verifica que el snapshot tenga al menos la serie o el código interno.
     */
    public boolean esValido() {
        return (serie != null && !serie.isBlank())
            || (codigoInterno != null && !codigoInterno.isBlank());
    }
}
