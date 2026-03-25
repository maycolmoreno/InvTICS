package com.uisrael.gestionactivosapi.dominio.puertos.servicios;

/**
 * Puerto para el almacenamiento y recuperación de archivos.
 * Define el contrato para operaciones de persistencia de archivos sin dependencias concretas.
 */
public interface AlmacenadorArchivosPuerto {
    
    /**
     * Guarda un archivo en el almacenamiento.
     * 
     * @param nombreArchivo nombre del archivo (incluye extensión)
     * @param contenido byte[] del archivo
     * @param rutaCarpeta carpeta destino (ej: "mantenimientos", "equipos")
     * @return ruta del archivo guardado
     * @throws IllegalArgumentException si no se puede guardar
     */
    String guardarArchivo(String nombreArchivo, byte[] contenido, String rutaCarpeta);
    
    /**
     * Obtiene un archivo del almacenamiento.
     * 
     * @param rutaArchivo ruta completa del archivo
     * @return byte[] del contenido del archivo
     * @throws IllegalArgumentException si el archivo no existe
     */
    byte[] obtenerArchivo(String rutaArchivo);
    
    /**
     * Elimina un archivo del almacenamiento.
     * 
     * @param rutaArchivo ruta del archivo a eliminar
     * @return true si se eliminó exitosamente
     */
    boolean eliminarArchivo(String rutaArchivo);
    
    /**
     * Verifica si un archivo existe en el almacenamiento.
     * 
     * @param rutaArchivo ruta del archivo
     * @return true si existe
     */
    boolean archivoExiste(String rutaArchivo);
    
    /**
     * Obtiene la ruta base de almacenamiento.
     * 
     * @return String con la ruta base
     */
    String obtenerRutaBase();
}
