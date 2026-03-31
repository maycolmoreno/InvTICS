package com.uisrael.gestionactivosapi.aplicacion.casosuso;

import java.util.List;

import com.uisrael.gestionactivosapi.aplicacion.dto.ActividadChecklistConCategoriasDTO;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.ActividadChecklistJpa;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IActividadChecklistJpaRepositorio;

import lombok.RequiredArgsConstructor;

/**
 * Caso de uso: Obtener actividades de checklist filtradas por categoría
 * usando la relación normalizada checklist_categoria en lugar del campo
 * de texto libre 'categoria' (ya eliminado).
 */
@RequiredArgsConstructor
public class ObtenerActividadesPorCategoriaUseCase {

    private final IActividadChecklistJpaRepositorio actividadRepo;

    /**
     * Obtiene actividades activas por categoría de equipo.
     *
     * @param idCategoria ID de la categoría de equipo
     * @return lista de actividades con sus categorías asociadas
     */
    public List<ActividadChecklistConCategoriasDTO> ejecutar(Integer idCategoria) {
        List<ActividadChecklistJpa> actividades = actividadRepo.findActivasPorCategoria(idCategoria);
        return actividades.stream().map(this::toDTO).toList();
    }

    /**
     * Obtiene todas las actividades activas con sus categorías.
     */
    public List<ActividadChecklistConCategoriasDTO> ejecutarTodas() {
        List<ActividadChecklistJpa> actividades = actividadRepo.findAllByEstadoTrueOrderByOrdenAsc();
        return actividades.stream().map(this::toDTO).toList();
    }

    private ActividadChecklistConCategoriasDTO toDTO(ActividadChecklistJpa jpa) {
        var categorias = jpa.getCategorias().stream()
                .map(c -> new ActividadChecklistConCategoriasDTO.CategoriaDTO(
                        c.getIdCategoria(), c.getNombre()))
                .toList();

        return new ActividadChecklistConCategoriasDTO(
                jpa.getIdActividad(),
                jpa.getNombre(),
                jpa.getOrden(),
                jpa.getEstado(),
                categorias
        );
    }
}
