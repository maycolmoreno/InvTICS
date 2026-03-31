package com.uisrael.gestionactivosapi.dominio.valoresobjeto;

/**
 * Value Object inmutable que representa la ubicación activa de un equipo.
 * Se determina por la custodia activa (estado = true AND fecha_fin IS NULL).
 *
 * @param idUbicacion   ID de la ubicación
 * @param nombre        nombre de la ubicación
 * @param agencia       agencia o sucursal
 * @param custodioNombre nombre del custodio actual
 * @param idCustodia    ID de la custodia activa
 */
public record UbicacionActiva(
    Integer idUbicacion,
    String nombre,
    String agencia,
    String custodioNombre,
    Integer idCustodia
) {

    /**
     * Indica si el equipo tiene una ubicación asignada.
     */
    public boolean tieneUbicacion() {
        return idUbicacion != null;
    }
}
