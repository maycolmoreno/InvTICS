package com.uisrael.gestionactivosapi.dominio.puertos.repositorios;

import com.uisrael.gestionactivosapi.dominio.entidades.ImagenMantenimiento;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de repositorio para la entidad ImagenMantenimiento.
 * Define el contrato para operaciones de persistencia de imágenes de mantenimiento.
 */
public interface ImagenMantenimientoRepositorioPuerto {
    
    /**
     * Guarda una nueva imagen de mantenimiento.
     * 
     * @param imagen la imagen a guardar
     * @return la imagen guardada con ID asignado
     */
    ImagenMantenimiento guardar(ImagenMantenimiento imagen);
    
    /**
     * Obtiene una imagen por su ID.
     * 
     * @param id el ID de la imagen
     * @return Optional con la imagen si existe
     */
    Optional<ImagenMantenimiento> obtenerPorId(Integer id);
    
    /**
     * Obtiene todas las imágenes.
     * 
     * @return lista de todas las imágenes
     */
    List<ImagenMantenimiento> obtenerTodas();
    
    /**
     * Actualiza una imagen.
     * 
     * @param imagen la imagen con datos actualizados
     */
    void actualizar(ImagenMantenimiento imagen);
    
    /**
     * Elimina una imagen.
     * 
     * @param id el ID de la imagen a eliminar
     */
    void eliminar(Integer id);
    
    /**
     * Obtiene imágenes de un mantenimiento específico.
     * 
     * @param mantenimientoId el ID del mantenimiento
     * @return lista de imágenes del mantenimiento
     */
    List<ImagenMantenimiento> obtenerPorMantenimiento(Integer mantenimientoId);
    
    /**
     * Obtiene imágenes de una actividad específica.
     * 
     * @param actividadId el ID de la actividad
     * @return lista de imágenes de la actividad
     */
    List<ImagenMantenimiento> obtenerPorActividad(Integer actividadId);
    
    /**
     * Obtiene el número de imágenes para un mantenimiento.
     * 
     * @param mantenimientoId el ID del mantenimiento
     * @return cantidad de imágenes
     */
    Integer obtenerCountPorMantenimiento(Integer mantenimientoId);
}
