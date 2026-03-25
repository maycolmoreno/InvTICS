package com.uisrael.gestionactivosapi.dominio.puertos.servicios;

/**
 * Puerto para la generación de reportes en formato PDF.
 * Define el contrato para operaciones de generación de PDF sin dependencias de infraestructura.
 */
public interface GeneradorPdfPuerto {
    
    /**
     * Genera un reporte PDF de un mantenimiento.
     * 
     * @param mantenimientoId el ID del mantenimiento
     * @return byte[] con el contenido del PDF
     * @throws IllegalArgumentException si el mantenimiento no existe
     */
    byte[] generarReporteMantenimiento(Integer mantenimientoId);
    
    /**
     * Genera un reporte PDF de etiqueta de equipo.
     * 
     * @param equipoId el ID del equipo
     * @return byte[] con la etiqueta en PDF
     */
    byte[] generarEtiquetaEquipo(Integer equipoId);
    
    /**
     * Genera un reporte consolidado con imagen base64 embebida.
     * 
     * @param datos mapa with reportData
     * @return byte[] del PDF generado
     */
    byte[] generarReporteConImagenes(java.util.Map<String, Object> datos);
    
    /**
     * Verifica que la generación de PDF sea posible (dependencias disponibles).
     * 
     * @return true si el generador está disponible
     */
    boolean isDisponible();
}
