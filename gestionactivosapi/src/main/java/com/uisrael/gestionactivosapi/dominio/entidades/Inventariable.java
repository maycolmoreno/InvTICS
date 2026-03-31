package com.uisrael.gestionactivosapi.dominio.entidades;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Interfaz de dominio que define el contrato común entre activos físicos.
 *
 * <h3>Análisis: ¿Fusionar o componer activos y equipos?</h3>
 *
 * <p><b>Opción descartada — Fusión:</b> No es viable porque {@code equipos} tiene campos
 * altamente especializados (procesador, RAM, almacenamiento, SO, licencias Office, IP, MAC,
 * dominio) que no aplican a activos generales (mobiliario, vehículos, etc.).
 * Fusionar crearía una tabla con decenas de columnas nullable.</p>
 *
 * <p><b>Opción elegida — Composición con interfaz común:</b>
 * Ambas tablas representan activos inventariables pero con diferente nivel de detalle.
 * {@code activos} = bienes generales (mobiliario, vehículos).
 * {@code equipos} = activos tecnológicos con campos especializados.
 * La interfaz {@code Inventariable} unifica los campos comunes.</p>
 *
 * <p><b>Por qué no herencia JPA:</b> {@code @Inheritance(JOINED)} requeriría cambiar
 * las PKs de ambas tablas para compartir secuencia, rompiendo todas las FKs existentes.
 * El costo de migración supera el beneficio.</p>
 *
 * <p><b>Acción adicional:</b> Se normaliza {@code activos.ubicacion} (varchar) a FK
 * referenciando {@code ubicaciones} en el script V9.</p>
 */
public interface Inventariable {

    /** Identificador único del activo. */
    Integer getId();

    /** Modelo del activo. */
    String getModelo();

    /** Número de serie o serial. */
    String getSerie();

    /** Estado actual del activo (ACTIVO, INACTIVO, BAJA, etc.). */
    String getEstado();

    /** Fecha de adquisición o compra. */
    LocalDate getFechaAdquisicion();

    /** Valor actual del activo. */
    BigDecimal getValorActual();

    /** Nombre o descripción corta. */
    String getDescripcion();
}
