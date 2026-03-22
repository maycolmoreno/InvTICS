package com.uisrael.gestionactivosapi.infraestructura.persistencia.adaptadores;

import java.util.List;

import com.uisrael.gestionactivosapi.dominio.entidades.ActividadChecklist;
import com.uisrael.gestionactivosapi.dominio.repositorios.IActividadChecklistRepository;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.ActividadChecklistJpa;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IActividadChecklistJpaRepositorio;

public class ActividadChecklistRepositorioImpl implements IActividadChecklistRepository {

    private final IActividadChecklistJpaRepositorio jpaRepositorio;

    public ActividadChecklistRepositorioImpl(IActividadChecklistJpaRepositorio jpaRepositorio) {
        this.jpaRepositorio = jpaRepositorio;
    }

    @Override
    public List<ActividadChecklist> listarActivas() {
        return jpaRepositorio.findAllByEstadoTrueOrderByCategoriaAscOrdenAsc()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<ActividadChecklist> listarActivasPorCategoria(Integer idCategoria) {
        return jpaRepositorio.findActivasPorCategoria(idCategoria)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private ActividadChecklist toDomain(ActividadChecklistJpa entity) {
        ActividadChecklist d = new ActividadChecklist();
        d.setIdActividad(entity.getIdActividad());
        d.setNombre(entity.getNombre());
        d.setCategoria(entity.getCategoria());
        d.setOrden(entity.getOrden());
        d.setEstado(Boolean.TRUE.equals(entity.getEstado()));
        return d;
    }
}
