package com.uisrael.gestionactivosapi.dominio.puertos.repositorios;

import com.uisrael.gestionactivosapi.dominio.entidades.VisitaTecnica;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de repositorio para la entidad VisitaTecnica.
 * Define el contrato para operaciones de persistencia de visitas técnicas.
 */
public interface VisitaTecnicaRepositorioPuerto {
    
    /**
     * Guarda una nueva visita técnica.
     * 
     * @param visita la visita a guardar
     * @return la visita guardada con ID asignado
     */
    VisitaTecnica guardar(VisitaTecnica visita);
    
    /**
     * Obtiene una visita técnica por su ID.
     * 
     * @param id el ID de la visita
     * @return Optional con la visita si existe
     */
    Optional<VisitaTecnica> obtenerPorId(Integer id);
    
    /**
     * Obtiene todas las visitas técnicas.
     * 
     * @return lista de todas las visitas
     */
    List<VisitaTecnica> obtenerTodas();
    
    /**
     * Actualiza una visita técnica.
     * 
     * @param visita la visita con datos actualizados
     */
    void actualizar(VisitaTecnica visita);
    
    /**
     * Elimina una visita técnica.
     * 
     * @param id el ID de la visita a eliminar
     */
    void eliminar(Integer id);
    
    /**
     * Obtiene visitas técnicas pendientes.
     * 
     * @return lista de visitas pendientes
     */
    List<VisitaTecnica> obtenerPendientes();
    
    /**
     * Obtiene visitas técnicas de un equipo.
     * 
     * @param equipoId el ID del equipo
     * @return lista de visitas del equipo
     */
    List<VisitaTecnica> obtenerPorEquipo(Integer equipoId);
    
    /**
     * Obtiene visitas técnicas asignadas a un técnico.
     * 
     * @param tecnicoId el ID del técnico
     * @return lista de visitas asignadas
     */
    List<VisitaTecnica> obtenerPorTecnico(Integer tecnicoId);
    
    /**
     * Obtiene visitas técnicas en un rango de fechas.
     * 
     * @param desde fecha de inicio
     * @param hasta fecha de fin
     * @return lista de visitas en el rango
     */
    List<VisitaTecnica> obtenerPorRangoFechas(LocalDateTime desde, LocalDateTime hasta);
}
